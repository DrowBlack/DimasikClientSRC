package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class ModelHelper {
    public static void func_239104_a_(ModelRenderer rightArm, ModelRenderer leftArm, ModelRenderer head, boolean leftHanded) {
        ModelRenderer modelrenderer = leftHanded ? rightArm : leftArm;
        ModelRenderer modelrenderer1 = leftHanded ? leftArm : rightArm;
        modelrenderer.rotateAngleY = (leftHanded ? -0.3f : 0.3f) + head.rotateAngleY;
        modelrenderer1.rotateAngleY = (leftHanded ? 0.6f : -0.6f) + head.rotateAngleY;
        modelrenderer.rotateAngleX = -1.5707964f + head.rotateAngleX + 0.1f;
        modelrenderer1.rotateAngleX = -1.5f + head.rotateAngleX;
    }

    public static void func_239102_a_(ModelRenderer rightArm, ModelRenderer leftArm, LivingEntity entity, boolean leftHanded) {
        ModelRenderer modelrenderer = leftHanded ? rightArm : leftArm;
        ModelRenderer modelrenderer1 = leftHanded ? leftArm : rightArm;
        modelrenderer.rotateAngleY = leftHanded ? -0.8f : 0.8f;
        modelrenderer1.rotateAngleX = modelrenderer.rotateAngleX = -0.97079635f;
        float f = CrossbowItem.getChargeTime(entity.getActiveItemStack());
        float f1 = MathHelper.clamp((float)entity.getItemInUseMaxCount(), 0.0f, f);
        float f2 = f1 / f;
        modelrenderer1.rotateAngleY = MathHelper.lerp(f2, 0.4f, 0.85f) * (float)(leftHanded ? 1 : -1);
        modelrenderer1.rotateAngleX = MathHelper.lerp(f2, modelrenderer1.rotateAngleX, -1.5707964f);
    }

    public static <T extends MobEntity> void func_239103_a_(ModelRenderer rightArm, ModelRenderer leftArm, T entity, float swingProgress, float ageInTicks) {
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * (float)Math.PI);
        rightArm.rotateAngleZ = 0.0f;
        leftArm.rotateAngleZ = 0.0f;
        rightArm.rotateAngleY = 0.15707964f;
        leftArm.rotateAngleY = -0.15707964f;
        if (entity.getPrimaryHand() == HandSide.RIGHT) {
            rightArm.rotateAngleX = -1.8849558f + MathHelper.cos(ageInTicks * 0.09f) * 0.15f;
            leftArm.rotateAngleX = -0.0f + MathHelper.cos(ageInTicks * 0.19f) * 0.5f;
            rightArm.rotateAngleX += f * 2.2f - f1 * 0.4f;
            leftArm.rotateAngleX += f * 1.2f - f1 * 0.4f;
        } else {
            rightArm.rotateAngleX = -0.0f + MathHelper.cos(ageInTicks * 0.19f) * 0.5f;
            leftArm.rotateAngleX = -1.8849558f + MathHelper.cos(ageInTicks * 0.09f) * 0.15f;
            rightArm.rotateAngleX += f * 1.2f - f1 * 0.4f;
            leftArm.rotateAngleX += f * 2.2f - f1 * 0.4f;
        }
        ModelHelper.func_239101_a_(rightArm, leftArm, ageInTicks);
    }

    public static void func_239101_a_(ModelRenderer rightArm, ModelRenderer leftArm, float ageInTicks) {
        rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
        rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
        leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067f) * 0.05f;
    }

    public static void func_239105_a_(ModelRenderer leftArm, ModelRenderer rightArm, boolean isAggresive, float swingProgress, float ageInTicks) {
        float f2;
        float f = MathHelper.sin(swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin((1.0f - (1.0f - swingProgress) * (1.0f - swingProgress)) * (float)Math.PI);
        rightArm.rotateAngleZ = 0.0f;
        leftArm.rotateAngleZ = 0.0f;
        rightArm.rotateAngleY = -(0.1f - f * 0.6f);
        leftArm.rotateAngleY = 0.1f - f * 0.6f;
        rightArm.rotateAngleX = f2 = (float)(-Math.PI) / (isAggresive ? 1.5f : 2.25f);
        leftArm.rotateAngleX = f2;
        rightArm.rotateAngleX += f * 1.2f - f1 * 0.4f;
        leftArm.rotateAngleX += f * 1.2f - f1 * 0.4f;
        ModelHelper.func_239101_a_(rightArm, leftArm, ageInTicks);
    }
}
