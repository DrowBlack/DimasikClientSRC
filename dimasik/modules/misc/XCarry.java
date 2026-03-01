package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import net.minecraft.network.play.client.CCloseWindowPacket;

public class XCarry
extends Module {
    private final EventListener<EventReceivePacket> receive = this::packet;

    public XCarry() {
        super("XCarry", Category.MISC);
    }

    public void packet(EventReceivePacket event) {
        if (event.getPacket() instanceof CCloseWindowPacket) {
            event.setCancelled(true);
        }
    }
}
