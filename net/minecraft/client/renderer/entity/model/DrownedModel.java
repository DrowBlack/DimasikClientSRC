package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class DrownedModel<T extends ZombieEntity>
extends ZombieModel<T> {
    public DrownedModel(float p_i48915_1_, float p_i48915_2_, int p_i48915_3_, int p_i48915_4_) {
        super(p_i48915_1_, p_i48915_2_, p_i48915_3_, p_i48915_4_);
        this.bipedRightArm = new ModelRenderer(this, 32, 48);
        this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i48915_1_);
        this.bipedRightArm.setRotationPoint(-5.0f, 2.0f + p_i48915_2_, 0.0f);
        this.bipedRightLeg = new ModelRenderer(this, 16, 48);
        this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i48915_1_);
        this.bipedRightLeg.setRotationPoint(-1.9f, 12.0f + p_i48915_2_, 0.0f);
    }

    public DrownedModel(float p_i49398_1_, boolean p_i49398_2_) {
        super(p_i49398_1_, 0.0f, 64, p_i49398_2_ ? 32 : 64);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = BipedModel.ArmPose.EMPTY;
        this.leftArmPose = BipedModel.ArmPose.EMPTY;
        ItemStack itemstack = ((LivingEntity)entityIn).getHeldItem(Hand.MAIN_HAND);
        if (itemstack.getItem() == Items.TRIDENT && ((MobEntity)entityIn).isAggressive()) {
            if (((MobEntity)entityIn).getPrimaryHand() == HandSide.RIGHT) {
                this.rightArmPose = BipedModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = BipedModel.ArmPose.THROW_SPEAR;
            }
        }
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR) {
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - (float)Math.PI;
            this.bipedLeftArm.rotateAngleY = 0.0f;
        }
        if (this.rightArmPose == BipedModel.ArmPose.THROW_SPEAR) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - (float)Math.PI;
            this.bipedRightArm.rotateAngleY = 0.0f;
        }
        if (this.swimAnimation > 0.0f) {
            this.bipedRightArm.rotateAngleX = this.rotLerpRad(this.swimAnimation, this.bipedRightArm.rotateAngleX, -2.5132742f) + this.swimAnimation * 0.35f * MathHelper.sin(0.1f * ageInTicks);
            this.bipedLeftArm.rotateAngleX = this.rotLerpRad(this.swimAnimation, this.bipedLeftArm.rotateAngleX, -2.5132742f) - this.swimAnimation * 0.35f * MathHelper.sin(0.1f * ageInTicks);
            this.bipedRightArm.rotateAngleZ = this.rotLerpRad(this.swimAnimation, this.bipedRightArm.rotateAngleZ, -0.15f);
            this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(this.swimAnimation, this.bipedLeftArm.rotateAngleZ, 0.15f);
            this.bipedLeftLeg.rotateAngleX -= this.swimAnimation * 0.55f * MathHelper.sin(0.1f * ageInTicks);
            this.bipedRightLeg.rotateAngleX += this.swimAnimation * 0.55f * MathHelper.sin(0.1f * ageInTicks);
            this.bipedHead.rotateAngleX = 0.0f;
        }
    }
}
