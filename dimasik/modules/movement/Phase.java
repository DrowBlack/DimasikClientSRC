package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.time.TimerUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;

public class Phase
extends Module {
    private final CopyOnWriteArrayList<IPacket<?>> packets = new CopyOnWriteArrayList();
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSendPacket> packet = this::packet;
    protected boolean updating = false;
    protected TimerUtils timerUtils = new TimerUtils();

    public Phase() {
        super("Phase", Category.MOVEMENT);
    }

    public void update(EventUpdate eventUpdate) {
        if (!this.updating) {
            this.updating = true;
            if (this.timerUtils.hasTimeElapsed(10L)) {
                Vector3d tp = Minecraft.getInstance().player.getLook(1.0f).mul(1.0, 0.0, 1.0);
                Minecraft.getInstance().player.setPosition(Phase.mc.player.getPosX() + tp.getX(), Phase.mc.player.getPosY(), Phase.mc.player.getPosZ() + tp.getZ());
                this.timerUtils.reset();
            }
            this.packets.forEach((Consumer<IPacket<?>>)((Consumer<IPacket>)packets -> Phase.mc.player.connection.getNetworkManager().sendPacketWithoutEvent((IPacket<?>)packets)));
            this.packets.clear();
            Phase.mc.player.connection.getNetworkManager().sendPacketWithoutEvent(new CPlayerPacket.PositionRotationPacket(Phase.mc.player.getPosX(), Phase.mc.player.getPosY(), Phase.mc.player.getPosZ(), Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, Phase.mc.player.isOnGround()));
        }
    }

    public void packet(EventSendPacket event) {
        if (this.updating && event.getPacket() instanceof CPlayerPacket) {
            this.packets.add(event.getPacket());
            event.setCancelled(true);
            this.updating = false;
        }
    }
}
