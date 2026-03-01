package net.minecraft.entity;

import net.minecraft.util.SoundCategory;

public interface IShearable {
    public void shear(SoundCategory var1);

    public boolean isShearable();
}
