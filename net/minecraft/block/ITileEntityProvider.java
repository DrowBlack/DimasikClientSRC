package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface ITileEntityProvider {
    @Nullable
    public TileEntity createNewTileEntity(IBlockReader var1);
}
