package net.minecraft.tileentity;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TrappedChestTileEntity
extends ChestTileEntity {
    public TrappedChestTileEntity() {
        super(TileEntityType.TRAPPED_CHEST);
    }

    @Override
    protected void onOpenOrClose() {
        super.onOpenOrClose();
        this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
    }
}
