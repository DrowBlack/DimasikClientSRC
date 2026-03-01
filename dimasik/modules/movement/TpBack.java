package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.player.EventDeath;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.time.TimerUtils;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;

public class TpBack
extends Module {
    private final TimerUtils timer = new TimerUtils();
    private boolean work = false;
    private final EventListener<EventDeath> death = this::death;
    private final EventListener<EventReceivePacket> receive = this::receive;

    public TpBack() {
        super("TpBack", Category.MOVEMENT);
    }

    public void death(EventDeath event) {
        this.work = true;
        if (this.timer.hasTimeElapsed(300L)) {
            TpBack.mc.player.sendChatMessage("/sethome death");
            TpBack.mc.player.respawnPlayer();
        }
        if (this.timer.hasTimeElapsed(800L)) {
            TpBack.mc.player.sendChatMessage("/home death");
        }
        this.work = false;
    }

    public void receive(EventReceivePacket event) {
        SChatPacket packet;
        IPacket<?> iPacket = event.getPacket();
        if (iPacket instanceof SChatPacket && ((packet = (SChatPacket)iPacket).getChatComponent().getString().contains("death") || packet.getChatComponent().getString().contains("\u0434\u043e\u043c")) && this.work) {
            event.setCancelled(true);
        }
    }
}
