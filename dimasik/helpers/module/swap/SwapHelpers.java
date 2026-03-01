package dimasik.helpers.module.swap;

import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.interfaces.IFastAccess;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class SwapHelpers
implements IFastAccess {
    private int originalSlot = -1;
    public static boolean isEnabled;
    private boolean isChangingItem;

    public int find(int id) {
        int slot = -1;
        block0: for (int i = 0; i < 36; ++i) {
            for (EffectInstance potion : PotionUtils.getEffectsFromStack(SwapHelpers.mc.player.inventory.getStackInSlot(i))) {
                if (potion.getPotion() != Effect.get(id) || SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != Items.SPLASH_POTION) continue;
                slot = i;
                continue block0;
            }
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public static int containerSlot(Item item) {
        for (int i = 0; i < SwapHelpers.mc.player.openContainer.inventorySlots.size(); ++i) {
            Slot slot = SwapHelpers.mc.player.openContainer.getSlot(i);
            if (!slot.getHasStack() || slot.getStack().getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int getHotBarSlot(Item input) {
        for (int i = 0; i < 9; ++i) {
            if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != input) continue;
            return i;
        }
        return -1;
    }

    public static void inventorySwapClick(Item item) {
        if (InventoryHelper.getItemIndex(item) != -1) {
            int i;
            if (SwapHelpers.doesHotbarHaveItem(item)) {
                for (i = 0; i < 9; ++i) {
                    if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
                    boolean propusk = false;
                    if (i != SwapHelpers.mc.player.inventory.currentItem) {
                        SwapHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                        propusk = true;
                    }
                    SwapHelpers.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    if (!propusk) break;
                    SwapHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(SwapHelpers.mc.player.inventory.currentItem));
                    break;
                }
            }
            if (!SwapHelpers.doesHotbarHaveItem(item)) {
                for (i = 0; i < 36; ++i) {
                    if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
                    SwapHelpers.mc.playerController.windowClick(0, i, SwapHelpers.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, SwapHelpers.mc.player);
                    SwapHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(SwapHelpers.mc.player.inventory.currentItem % 8 + 1));
                    SwapHelpers.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    SwapHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(SwapHelpers.mc.player.inventory.currentItem));
                    SwapHelpers.mc.playerController.windowClick(0, i, SwapHelpers.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, SwapHelpers.mc.player);
                    break;
                }
            }
        }
    }

    public static boolean doesHotbarHaveItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public static int getItemSlot(Item input) {
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != input) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public static int findEmptySlot(boolean inHotBar) {
        int start = inHotBar ? 0 : 9;
        int end = inHotBar ? 9 : 45;
        for (int i = start; i < end; ++i) {
            if (!SwapHelpers.mc.player.inventory.getStackInSlot(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public ItemStack findWithSlot(int slot) {
        return SwapHelpers.mc.player.inventory.getStackInSlot(slot);
    }

    public static void clickSlotId(int slot, int button, ClickType clickType, boolean packet) {
        if (packet) {
            SwapHelpers.mc.player.connection.sendPacket(new CClickWindowPacket(SwapHelpers.mc.player.openContainer.windowId, slot, button, clickType, ItemStack.EMPTY, SwapHelpers.mc.player.openContainer.getNextTransactionID(SwapHelpers.mc.player.inventory)));
        } else {
            SwapHelpers.mc.playerController.windowClick(SwapHelpers.mc.player.openContainer.windowId, slot, button, clickType, SwapHelpers.mc.player);
        }
    }

    public static int getItemInHotBar(Item item) {
        return IntStream.range(0, 9).filter(i -> SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem().equals(item)).findFirst().orElse(-1);
    }

    public void handleItemChange(boolean resetItem) {
        if (this.isChangingItem && this.originalSlot != -1) {
            isEnabled = true;
            SwapHelpers.mc.player.inventory.currentItem = this.originalSlot;
            if (resetItem) {
                this.isChangingItem = false;
                this.originalSlot = -1;
                isEnabled = false;
            }
        }
    }

    public static void antipolet(Item item, boolean rotation) {
        if (InventoryHelper.getItemIndex(item) != -1) {
            for (int i = 0; i < SwapHelpers.mc.player.inventory.getSizeInventory(); ++i) {
                if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
                final int originalSlot = i;
                SwapHelpers.mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0, ClickType.PICKUP, SwapHelpers.mc.player);
                SwapHelpers.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, SwapHelpers.mc.player);
                SwapHelpers.mc.playerController.windowClick(0, i < 9 ? 36 + i : i, 0, ClickType.PICKUP, SwapHelpers.mc.player);
                new Timer().schedule(new TimerTask(){

                    @Override
                    public void run() {
                        if (!IFastAccess.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            IFastAccess.mc.gameSettings.keyBindSneak.setPressed(true);
                        }
                    }
                }, 20L);
                new Timer().schedule(new TimerTask(){

                    @Override
                    public void run() {
                        if (IFastAccess.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            IFastAccess.mc.gameSettings.keyBindSneak.setPressed(false);
                        }
                    }
                }, 60L);
                new Timer().schedule(new TimerTask(){

                    @Override
                    public void run() {
                        IFastAccess.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, IFastAccess.mc.player);
                        IFastAccess.mc.playerController.windowClick(0, originalSlot < 9 ? 36 + originalSlot : originalSlot, 0, ClickType.PICKUP, IFastAccess.mc.player);
                    }
                }, 110L);
                SwapHelpers.mc.playerController.windowClick(0, originalSlot < 9 ? 36 + originalSlot : originalSlot, 0, ClickType.PICKUP, SwapHelpers.mc.player);
                break;
            }
        }
    }

    public int find(Item item) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != item) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int findTotem() {
        int normalSlot = -1;
        int enchantedSlot = -1;
        Item item = Items.TOTEM_OF_UNDYING;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != item) continue;
            if (!stack.isEnchanted()) {
                return -2;
            }
            enchantedSlot = -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            if (!stack.isEnchanted()) {
                normalSlot = i;
                break;
            }
            if (enchantedSlot != -1) continue;
            enchantedSlot = i;
        }
        if (normalSlot != -1) {
            return normalSlot < 9 ? normalSlot + 36 : normalSlot;
        }
        if (enchantedSlot != -1) {
            if (enchantedSlot == -2) {
                return -2;
            }
            return enchantedSlot < 9 ? enchantedSlot + 36 : enchantedSlot;
        }
        return -1;
    }

    public int findtal() {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != Items.TOTEM_OF_UNDYING || stack.getEnchantmentTagList().isEmpty()) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != Items.TOTEM_OF_UNDYING || stack.getEnchantmentTagList().isEmpty()) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int find(UseAction action) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getUseAction() != action) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getUseAction() != action) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public void setOriginalSlot(int slot) {
        this.originalSlot = slot;
    }

    public void moveItem(Container container, int from, int to) {
        SwapHelpers.mc.playerController.windowClick(container.windowId, from, to, ClickType.SWAP, SwapHelpers.mc.player);
    }

    public int find() {
        RayTraceResult rayTraceResult = SwapHelpers.mc.objectMouseOver;
        if (rayTraceResult instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
            Block block = SwapHelpers.mc.world.getBlockState(blockRayTraceResult.getPos()).getBlock();
            int bestSlot = -1;
            float bestSpeed = 1.0f;
            for (int slot = 0; slot < 9; ++slot) {
                ItemStack stack = SwapHelpers.mc.player.inventory.getStackInSlot(slot);
                float speed = stack.getDestroySpeed(block.getDefaultState());
                if (!(speed > bestSpeed)) continue;
                bestSpeed = speed;
                bestSlot = slot;
            }
            return bestSlot;
        }
        return -1;
    }

    public int find(ItemStack itemStack) {
        int slot = -1;
        for (ItemStack stack : SwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack != itemStack) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack != itemStack) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public boolean haveHotBar(Item item) {
        for (int i = 0; i < 9; ++i) {
            SwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (SwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public boolean haveHotBar(int index) {
        return index >= 36 && index <= 44;
    }

    public int format(int slot) {
        return slot - 36;
    }

    public int find(String name) {
        int slot = -1;
        ContainerScreen containerScreen = (ContainerScreen)SwapHelpers.mc.currentScreen;
        for (int i = 0; i < ((Container)containerScreen.getContainer()).inventorySlots.size(); ++i) {
            String itemName = ((Container)containerScreen.getContainer()).inventorySlots.get(i).getStack().getDisplayName().getString();
            if (!itemName.contains(name)) continue;
            return i;
        }
        return slot;
    }

    public static class Hand3 {
        public static boolean isEnabled;
        private boolean isChangingItem;
        private int originalSlot = -1;

        public void onEventPacket(EventReceivePacket eventPacket) {
            if (eventPacket.getPacket() instanceof SHeldItemChangePacket) {
                this.isChangingItem = true;
            }
        }

        public void handleItemChange(boolean resetItem) {
            if (this.isChangingItem && this.originalSlot != -1) {
                isEnabled = true;
                Minecraft var10000 = IFastAccess.mc;
                IFastAccess.mc.player.inventory.currentItem = this.originalSlot;
                if (resetItem) {
                    this.isChangingItem = false;
                    this.originalSlot = -1;
                    isEnabled = false;
                }
            }
        }

        public void setOriginalSlot(int slot) {
            this.originalSlot = slot;
        }
    }
}
