package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.HandSide;

public interface IHasArm {
    public void translateHand(HandSide var1, MatrixStack var2);
}
