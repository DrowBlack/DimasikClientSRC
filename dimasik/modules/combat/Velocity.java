package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.time.TimerUtils;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;

public class Velocity
extends Module {
    private final TimerUtils timer = new TimerUtils();
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Matrix"), new SelectOptionValue("ReallyWorld"));
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventReceivePacket> receive = this::receive;
    private final EventListener<EventSendPacket> send = this::send;

    public Velocity() {
        super("Velocity", Category.COMBAT);
        this.settings(this.mode);
    }

    public void update(EventUpdate event) {
        if (Velocity.mc.player.getDistance(Load.getInstance().getHooks().getModuleManagers().getAura().getTarget()) <= ((Float)Load.getInstance().getHooks().getModuleManagers().getAura().getDistance().getValue()).floatValue() && Load.getInstance().getHooks().getModuleManagers().getAura().isToggled() && this.mode.getSelected("ReallyWorld")) {
            this.timer.reset();
        }
    }

    public void receive(EventReceivePacket event) {
        SEntityVelocityPacket packet;
        IPacket<?> iPacket;
        if (this.mode.getSelected("Matrix") && (iPacket = event.getPacket()) instanceof SEntityVelocityPacket && (packet = (SEntityVelocityPacket)iPacket).getEntityID() == Velocity.mc.player.getEntityId()) {
            event.setCancelled(true);
        }
        if (this.timer.hasTimeElapsed(400L) && this.mode.getSelected("ReallyWorld")) {
            iPacket = event.getPacket();
            if (iPacket instanceof SEntityVelocityPacket && (packet = (SEntityVelocityPacket)iPacket).getEntityID() == Velocity.mc.player.getEntityId()) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof SConfirmTransactionPacket) {
                event.setCancelled(true);
            }
        }
    }

    public void send(EventSendPacket event) {
        if (event.getPacket() instanceof CConfirmTransactionPacket && this.timer.hasTimeElapsed(400L) && this.mode.getSelected("ReallyWorld")) {
            event.setCancelled(true);
        }
    }
}
