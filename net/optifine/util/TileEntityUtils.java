package net.optifine.util;

import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.optifine.reflect.Reflector;
import net.optifine.util.IntegratedServerUtils;

public class TileEntityUtils {
    public static String getTileEntityName(IBlockReader blockAccess, BlockPos blockPos) {
        TileEntity tileentity = blockAccess.getTileEntity(blockPos);
        return TileEntityUtils.getTileEntityName(tileentity);
    }

    public static String getTileEntityName(TileEntity te) {
        if (!(te instanceof INameable)) {
            return null;
        }
        INameable inameable = (INameable)((Object)te);
        TileEntityUtils.updateTileEntityName(te);
        return !inameable.hasCustomName() ? null : inameable.getCustomName().getUnformattedComponentText();
    }

    public static void updateTileEntityName(TileEntity te) {
        BlockPos blockpos = te.getPos();
        ITextComponent itextcomponent = TileEntityUtils.getTileEntityRawName(te);
        if (itextcomponent == null) {
            ITextComponent itextcomponent1 = TileEntityUtils.getServerTileEntityRawName(blockpos);
            if (itextcomponent1 == null) {
                itextcomponent1 = new StringTextComponent("");
            }
            TileEntityUtils.setTileEntityRawName(te, itextcomponent1);
        }
    }

    public static ITextComponent getServerTileEntityRawName(BlockPos blockPos) {
        TileEntity tileentity = IntegratedServerUtils.getTileEntity(blockPos);
        return tileentity == null ? null : TileEntityUtils.getTileEntityRawName(tileentity);
    }

    public static ITextComponent getTileEntityRawName(TileEntity te) {
        if (te instanceof INameable) {
            return ((INameable)((Object)te)).getCustomName();
        }
        return te instanceof BeaconTileEntity ? (ITextComponent)Reflector.getFieldValue(te, Reflector.TileEntityBeacon_customName) : null;
    }

    public static boolean setTileEntityRawName(TileEntity te, ITextComponent name) {
        if (te instanceof LockableTileEntity) {
            ((LockableTileEntity)te).setCustomName(name);
            return true;
        }
        if (te instanceof BannerTileEntity) {
            ((BannerTileEntity)te).setName(name);
            return true;
        }
        if (te instanceof EnchantingTableTileEntity) {
            ((EnchantingTableTileEntity)te).setCustomName(name);
            return true;
        }
        if (te instanceof BeaconTileEntity) {
            ((BeaconTileEntity)te).setCustomName(name);
            return true;
        }
        return false;
    }
}
