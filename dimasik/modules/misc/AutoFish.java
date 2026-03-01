package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.client.StringUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;

public class AutoFish
extends Module {
    private final CheckboxOption saveRod = new CheckboxOption("Save F.Rod", false);
    private final CheckboxOption autoFix = new CheckboxOption("Repair F.Rod", false);
    private final CheckboxOption throwTrash = new CheckboxOption("Throw Trash", false);
    private final CheckboxOption autoSell = new CheckboxOption("Auto Sell", false);
    private final SliderOption codCount = new SliderOption("Cod Count", 700.0f, 100.0f, 10000.0f).increment(50.0f).visible(() -> (Boolean)this.autoSell.getValue());
    private final SliderOption salmCount = new SliderOption("Salmon Count", 700.0f, 100.0f, 10000.0f).increment(50.0f).increment(50.0f).visible(() -> (Boolean)this.autoSell.getValue());
    private final SliderOption tropCount = new SliderOption("Trop.Fish Count", 700.0f, 100.0f, 10000.0f).increment(50.0f).increment(50.0f).visible(() -> (Boolean)this.autoSell.getValue());
    private final SliderOption pufferCount = new SliderOption("Puf.Fish Count", 700.0f, 100.0f, 10000.0f).increment(50.0f).increment(50.0f).visible(() -> (Boolean)this.autoSell.getValue());
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventReceivePacket> packet = this::packet;
    private TimerUtils timerUtils = new TimerUtils();
    private TimerUtils containerCD = new TimerUtils();
    private SwapHelpers swapHelpers = new SwapHelpers();
    private boolean isHooked = false;
    private boolean needToHook = false;

    public AutoFish() {
        super("AutoFish", Category.MISC);
        this.settings(this.saveRod, this.autoFix, this.throwTrash, this.autoSell, this.codCount, this.salmCount, this.tropCount, this.pufferCount);
    }

    public void update(EventUpdate eventUpdate) {
        ItemStack mainHand;
        if (this.timerUtils.isReached(600L) && this.isHooked) {
            AutoFish.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            this.isHooked = false;
            this.needToHook = true;
            this.timerUtils.reset();
        }
        if (this.timerUtils.isReached(300L) && this.needToHook) {
            AutoFish.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            this.needToHook = false;
            this.timerUtils.reset();
        }
        if (((Boolean)this.saveRod.getValue()).booleanValue() && (mainHand = AutoFish.mc.player.getHeldItemMainhand()).getItem() == Items.FISHING_ROD && (double)mainHand.getDamage() < (double)mainHand.getDamage() * 0.15) {
            this.needToHook = false;
            this.isHooked = false;
        }
        if (((Boolean)this.autoFix.getValue()).booleanValue() && StringUtils.priority(StringUtils.getDonate(AutoFish.mc.player)) >= 4 && (mainHand = AutoFish.mc.player.getHeldItemMainhand()).getItem() == Items.FISHING_ROD && (double)mainHand.getDamage() < (double)mainHand.getDamage() * 0.15) {
            AutoFish.mc.player.sendChatMessage("/fix");
        }
        if (((Boolean)this.throwTrash.getValue()).booleanValue()) {
            if (AutoFish.mc.player == null || AutoFish.mc.world == null) {
                return;
            }
            for (int i = 0; i < 36; ++i) {
                ItemStack stack = AutoFish.mc.player.inventory.getStackInSlot(i);
                if (!this.isFishingTrash(stack)) continue;
                AutoFish.mc.playerController.windowClick(AutoFish.mc.player.openContainer.windowId, i < 9 ? i + 36 : i, 1, ClickType.THROW, AutoFish.mc.player);
                break;
            }
        }
        if (((Boolean)this.autoSell.getValue()).booleanValue()) {
            int cod = this.swapHelpers.find(Items.COD);
            int salmon = this.swapHelpers.find(Items.SALMON);
            int tropical_fish = this.swapHelpers.find(Items.TROPICAL_FISH);
            int pufferfish = this.swapHelpers.find(Items.PUFFERFISH);
            if (cod != -1) {
                this.swapHelpers.moveItem(AutoFish.mc.player.openContainer, cod, AutoFish.mc.player.inventory.currentItem);
                AutoFish.mc.player.sendChatMessage("/market sell " + ((Float)this.codCount.getValue()).intValue());
            }
            if (salmon != -1) {
                this.swapHelpers.moveItem(AutoFish.mc.player.openContainer, salmon, AutoFish.mc.player.inventory.currentItem);
                AutoFish.mc.player.sendChatMessage("/market sell " + ((Float)this.salmCount.getValue()).intValue());
            }
            if (tropical_fish != -1) {
                this.swapHelpers.moveItem(AutoFish.mc.player.openContainer, tropical_fish, AutoFish.mc.player.inventory.currentItem);
                AutoFish.mc.player.sendChatMessage("/market sell " + ((Float)this.tropCount.getValue()).intValue());
            }
            if (pufferfish != -1) {
                this.swapHelpers.moveItem(AutoFish.mc.player.openContainer, pufferfish, AutoFish.mc.player.inventory.currentItem);
                AutoFish.mc.player.sendChatMessage("/market sell " + ((Float)this.pufferCount.getValue()).intValue());
            }
        }
    }

    public void packet(EventReceivePacket eventReceivePacket) {
        SPlaySoundEffectPacket p;
        IPacket<?> iPacket = eventReceivePacket.getPacket();
        if (iPacket instanceof SPlaySoundEffectPacket && (p = (SPlaySoundEffectPacket)iPacket).getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
            this.isHooked = true;
            this.timerUtils.reset();
        }
    }

    private boolean isFishingTrash(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item == Items.BOWL || item == Items.LEATHER_BOOTS || item == Items.LEATHER || item == Items.ROTTEN_FLESH || item == Items.BONE || item == Items.STRING || item == Items.STICK || item == Items.INK_SAC || item == Items.TRIPWIRE_HOOK || item == Items.LILY_PAD;
    }
}
