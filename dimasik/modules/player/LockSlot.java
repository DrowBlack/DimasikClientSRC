package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;

public class LockSlot
extends Module {
    public final MultiOption lockSlots = new MultiOption("\u0411\u043b\u043e\u043a\u0430\u0442\u044c", new MultiOptionValue("\u0421\u043b\u043e\u0442 1", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 2", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 3", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 4", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 5", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 6", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 7", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 8", false), new MultiOptionValue("\u0421\u043b\u043e\u0442 9", false));
    private boolean wasDropPressed = false;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSendPacket> send = this::send;

    public LockSlot() {
        super("LockSlot", Category.PLAYER);
        this.settings(this.lockSlots);
    }

    public void update(EventUpdate e) {
        if (LockSlot.mc.player == null) {
            return;
        }
        if (LockSlot.mc.gameSettings.keyBindDrop.isKeyDown()) {
            if (!this.wasDropPressed) {
                this.wasDropPressed = true;
                int currentSlot = LockSlot.mc.player.inventory.currentItem;
                if (this.isSlotLocked(currentSlot)) {
                    LockSlot.mc.gameSettings.keyBindDrop.setPressed(false);
                    this.updateSlot(currentSlot);
                }
            }
        } else {
            this.wasDropPressed = false;
        }
    }

    public void send(EventSendPacket e) {
        int currentSlot;
        CPlayerDiggingPacket packet;
        if (LockSlot.mc.player == null) {
            return;
        }
        if (e.getPacket() instanceof CPlayerDiggingPacket && ((packet = (CPlayerDiggingPacket)e.getPacket()).getAction() == CPlayerDiggingPacket.Action.DROP_ITEM || packet.getAction() == CPlayerDiggingPacket.Action.DROP_ALL_ITEMS) && this.isSlotLocked(currentSlot = LockSlot.mc.player.inventory.currentItem)) {
            e.setCancelled(true);
            LockSlot.mc.gameSettings.keyBindDrop.setPressed(false);
            this.updateSlot(currentSlot);
        }
    }

    private void updateSlot(int slot) {
        if (LockSlot.mc.player != null && LockSlot.mc.player.openContainer != null) {
            LockSlot.mc.player.connection.sendPacket(new CClickWindowPacket(LockSlot.mc.player.openContainer.windowId, 36 + slot, 0, ClickType.PICKUP, LockSlot.mc.player.inventory.getStackInSlot(slot), LockSlot.mc.player.openContainer.getNextTransactionID(LockSlot.mc.player.inventory)));
            LockSlot.mc.player.connection.sendPacket(new CClickWindowPacket(LockSlot.mc.player.openContainer.windowId, 36 + slot, 0, ClickType.PICKUP, LockSlot.mc.player.inventory.getStackInSlot(slot), LockSlot.mc.player.openContainer.getNextTransactionID(LockSlot.mc.player.inventory)));
        }
    }

    private boolean isSlotLocked(int slot) {
        return this.lockSlots.getSelected("\u0421\u043b\u043e\u0442 " + (slot + 1));
    }
}
