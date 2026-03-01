package net.optifine.render;

import net.minecraft.util.math.AxisAlignedBB;

public interface ICamera {
    public void setCameraPosition(double var1, double var3, double var5);

    public boolean isBoundingBoxInFrustum(AxisAlignedBB var1);

    public boolean isBoxInFrustumFully(double var1, double var3, double var5, double var7, double var9, double var11);
}
