package net.minecraft.tileentity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BlastFurnaceTileEntity
extends AbstractFurnaceTileEntity {
    public BlastFurnaceTileEntity() {
        super(TileEntityType.BLAST_FURNACE, IRecipeType.BLASTING);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.blast_furnace");
    }

    @Override
    protected int getBurnTime(ItemStack fuel) {
        return super.getBurnTime(fuel) / 2;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new BlastFurnaceContainer(id, player, this, this.furnaceData);
    }
}
