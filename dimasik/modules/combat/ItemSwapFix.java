package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.util.Hand;

public class ItemSwapFix
extends Module {
    private final EventListener<EventReceivePacket> receive = this::receive;

    public ItemSwapFix() {
        super("Item Swap Fix", Category.COMBAT);
    }

    public void receive(EventReceivePacket eventReceivePacket) {
        IPacket<?> iPacket = eventReceivePacket.getPacket();
        if (iPacket instanceof SHeldItemChangePacket) {
            SHeldItemChangePacket packetHeldItemChange = (SHeldItemChangePacket)iPacket;
            if (ItemSwapFix.mc.player.isHandActive() && ItemSwapFix.mc.player.getActiveHand() == Hand.OFF_HAND) {
                ItemSwapFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(ItemSwapFix.mc.player.inventory.currentItem % 8 + 1));
                ItemSwapFix.mc.player.connection.sendPacket(new CHeldItemChangePacket(ItemSwapFix.mc.player.inventory.currentItem));
                eventReceivePacket.setCancelled(true);
            }
        }
    }
}
