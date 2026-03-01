package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.item.ArmorStandEntity;

public class ArmorStandArmorModel
extends BipedModel<ArmorStandEntity> {
    public ArmorStandArmorModel(float modelSize) {
        this(modelSize, 64, 32);
    }

    protected ArmorStandArmorModel(float modelSize, int textureWidthIn, int textureHeightIn) {
        super(modelSize, 0.0f, textureWidthIn, textureHeightIn);
    }

    @Override
    public void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bipedHead.rotateAngleX = (float)Math.PI / 180 * entityIn.getHeadRotation().getX();
        this.bipedHead.rotateAngleY = (float)Math.PI / 180 * entityIn.getHeadRotation().getY();
        this.bipedHead.rotateAngleZ = (float)Math.PI / 180 * entityIn.getHeadRotation().getZ();
        this.bipedHead.setRotationPoint(0.0f, 1.0f, 0.0f);
        this.bipedBody.rotateAngleX = (float)Math.PI / 180 * entityIn.getBodyRotation().getX();
        this.bipedBody.rotateAngleY = (float)Math.PI / 180 * entityIn.getBodyRotation().getY();
        this.bipedBody.rotateAngleZ = (float)Math.PI / 180 * entityIn.getBodyRotation().getZ();
        this.bipedLeftArm.rotateAngleX = (float)Math.PI / 180 * entityIn.getLeftArmRotation().getX();
        this.bipedLeftArm.rotateAngleY = (float)Math.PI / 180 * entityIn.getLeftArmRotation().getY();
        this.bipedLeftArm.rotateAngleZ = (float)Math.PI / 180 * entityIn.getLeftArmRotation().getZ();
        this.bipedRightArm.rotateAngleX = (float)Math.PI / 180 * entityIn.getRightArmRotation().getX();
        this.bipedRightArm.rotateAngleY = (float)Math.PI / 180 * entityIn.getRightArmRotation().getY();
        this.bipedRightArm.rotateAngleZ = (float)Math.PI / 180 * entityIn.getRightArmRotation().getZ();
        this.bipedLeftLeg.rotateAngleX = (float)Math.PI / 180 * entityIn.getLeftLegRotation().getX();
        this.bipedLeftLeg.rotateAngleY = (float)Math.PI / 180 * entityIn.getLeftLegRotation().getY();
        this.bipedLeftLeg.rotateAngleZ = (float)Math.PI / 180 * entityIn.getLeftLegRotation().getZ();
        this.bipedLeftLeg.setRotationPoint(1.9f, 11.0f, 0.0f);
        this.bipedRightLeg.rotateAngleX = (float)Math.PI / 180 * entityIn.getRightLegRotation().getX();
        this.bipedRightLeg.rotateAngleY = (float)Math.PI / 180 * entityIn.getRightLegRotation().getY();
        this.bipedRightLeg.rotateAngleZ = (float)Math.PI / 180 * entityIn.getRightLegRotation().getZ();
        this.bipedRightLeg.setRotationPoint(-1.9f, 11.0f, 0.0f);
        this.bipedHeadwear.copyModelAngles(this.bipedHead);
    }
}
