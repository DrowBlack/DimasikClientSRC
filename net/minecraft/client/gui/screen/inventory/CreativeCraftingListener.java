package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeCraftingListener
implements IContainerListener {
    private final Minecraft mc;

    public CreativeCraftingListener(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        this.mc.playerController.sendSlotPacket(stack, slotInd);
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
    }
}
