package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.utils.client.ChatUtils;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public class HWHelper
extends Module {
    private final BindOption stanKey = new BindOption("\u041a\u043d\u043e\u043f\u043a\u0430 \u0441\u0442\u0430\u043d\u0430", -1);
    private final BindOption trapKey = new BindOption("\u041a\u043d\u043e\u043f\u043a\u0430 \u0442\u0440\u0430\u043f\u043a\u0438", -1);
    private final SwapHelpers.Hand3 handUtil = new SwapHelpers.Hand3();
    long delay;
    boolean trapThrow;
    boolean stanThrow;
    private final EventListener<EventReceivePacket> input = this::onPacket;
    private final EventListener<EventInput> event = this::onKey;
    private final EventListener<EventUpdate> event2 = this::onUpdate;

    public HWHelper() {
        super("HWHelper", Category.MISC);
        this.settings(this.stanKey, this.trapKey);
    }

    private void onKey(EventInput e) {
        if (e.getKey() == Integer.valueOf(this.stanKey.getKey()).intValue()) {
            this.stanThrow = true;
        }
        if (e.getKey() == Integer.valueOf(this.trapKey.getKey()).intValue()) {
            this.trapThrow = true;
        }
    }

    private void onUpdate(EventUpdate e) {
        int slot;
        int old;
        int invSlot;
        int hbSlot;
        if (this.stanThrow) {
            hbSlot = this.getItemForName("\u0441\u0442\u0430\u043d", true);
            invSlot = this.getItemForName("\u0441\u0442\u0430\u043d", false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0421\u0442\u0430\u043d \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                this.stanThrow = false;
                return;
            }
            if (!HWHelper.mc.player.getCooldownTracker().hasCooldown(Items.NETHER_STAR)) {
                ChatUtils.addClientMessage("\u0417\u0430\u044e\u0437\u0430\u043b \u0421\u0442\u0430\u043d");
                old = HWHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    HWHelper.mc.playerController.pickItem(slot);
                }
                if (SwapHelpers.findEmptySlot(true) != -1 && HWHelper.mc.player.inventory.currentItem != old) {
                    HWHelper.mc.player.inventory.currentItem = old;
                }
            }
            this.stanThrow = false;
        }
        if (this.trapThrow) {
            hbSlot = this.getItemForName("\u0432\u0437\u0440\u044b\u0432\u043d\u0430\u044f \u0442\u0440\u0430\u043f\u043a\u0430", true);
            invSlot = this.getItemForName("\u0432\u0437\u0440\u044b\u0432\u043d\u0430\u044f \u0442\u0440\u0430\u043f\u043a\u0430", false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0412\u0437\u0440\u044b\u0432\u043d\u0430\u044f \u0442\u0440\u0430\u043f\u043a\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430");
                this.trapThrow = false;
                return;
            }
            if (!HWHelper.mc.player.getCooldownTracker().hasCooldown(Items.PRISMARINE_SHARD)) {
                ChatUtils.addClientMessage("\u0417\u0430\u044e\u0437\u0430\u043b \u0432\u0437\u0440\u044b\u0432\u043d\u0443\u044e \u0442\u0440\u0430\u043f\u043a\u0443");
                old = HWHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    HWHelper.mc.playerController.pickItem(slot);
                }
                if (SwapHelpers.findEmptySlot(true) != -1 && HWHelper.mc.player.inventory.currentItem != old) {
                    HWHelper.mc.player.inventory.currentItem = old;
                }
            }
            this.trapThrow = false;
        }
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    private void onPacket(EventReceivePacket e) {
        this.handUtil.onEventPacket(e);
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(HWHelper.mc.player.inventory.currentItem);
            HWHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            HWHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            HWHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            this.handUtil.setOriginalSlot(HWHelper.mc.player.inventory.currentItem);
            HWHelper.mc.playerController.pickItem(invSlot);
            HWHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            HWHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    @Override
    public void onDisabled() {
        this.trapThrow = false;
        this.delay = 0L;
    }

    private int getItemForName(String name, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            String displayName;
            ItemStack itemStack = HWHelper.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem || (displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString())) == null || !displayName.toLowerCase().contains(name)) continue;
            return i;
        }
        return -1;
    }
}
