package dimasik.helpers.module.elytraswap;

import dimasik.helpers.interfaces.IFastAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ElytraSwapHelpers
implements IFastAccess {
    public int getSlot(Item item) {
        int slot = -1;
        for (ItemStack stack : ElytraSwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack.getItem() != item) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = ElytraSwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public int getSlot(ItemStack itemStack) {
        int slot = -1;
        for (ItemStack stack : ElytraSwapHelpers.mc.player.getArmorInventoryList()) {
            if (stack != itemStack) continue;
            return -2;
        }
        for (int i = 0; i < 36; ++i) {
            ItemStack stack;
            stack = ElytraSwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (stack != itemStack) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public boolean haveHotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            ElytraSwapHelpers.mc.player.inventory.getStackInSlot(i);
            if (ElytraSwapHelpers.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }
}
