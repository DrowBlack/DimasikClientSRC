package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.movement.MovingEvent;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.time.TimerUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;

public final class Blink
extends Module {
    private final CopyOnWriteArrayList<IPacket> packets = new CopyOnWriteArrayList();
    private CheckboxOption delay = new CheckboxOption("Bravo exploit", false);
    private SliderOption delayS = new SliderOption("Delay", 100.0f, 50.0f, 1000.0f).increment(50.0f).visible(() -> (Boolean)this.delay.getValue());
    private long started;
    private final EventListener<EventSendPacket> update = this::send;
    private final EventListener<MovingEvent> move = this::sync;
    float animation;
    public TimerUtils timerUtil = new TimerUtils();
    Vector3d lastPos = new Vector3d(0.0, 0.0, 0.0);

    public Blink() {
        super("Blink", Category.MISC);
        this.settings(this.delay, this.delayS);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        this.started = System.currentTimeMillis();
        this.lastPos = Blink.mc.player.getPositionVec();
    }

    public void send(EventSendPacket e) {
        if (Blink.mc.player != null && Blink.mc.world != null && !mc.isSingleplayer() && !Blink.mc.player.getShouldBeDead()) {
            this.packets.add(e.getPacket());
            e.setCancelled(true);
        } else {
            this.toggle();
        }
    }

    public void sync(MovingEvent e) {
        if (System.currentTimeMillis() - this.started >= 29900L) {
            this.toggle();
        }
        if (((Boolean)this.delay.getValue()).booleanValue() && this.timerUtil.hasTimeElapsed(((Float)this.delayS.getValue()).longValue())) {
            for (IPacket packet : this.packets) {
                Blink.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(packet);
            }
            this.packets.clear();
            this.started = System.currentTimeMillis();
            this.timerUtil.reset();
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        for (IPacket packet : this.packets) {
            Blink.mc.player.connection.sendPacket(packet);
        }
        this.packets.clear();
    }
}
