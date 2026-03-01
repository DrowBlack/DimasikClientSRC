package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class ArmorStandModel
extends ArmorStandArmorModel {
    private final ModelRenderer standRightSide;
    private final ModelRenderer standLeftSide;
    private final ModelRenderer standWaist;
    private final ModelRenderer standBase;

    public ArmorStandModel() {
        this(0.0f);
    }

    public ArmorStandModel(float modelSize) {
        super(modelSize, 64, 64);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f, modelSize);
        this.bipedHead.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedBody = new ModelRenderer(this, 0, 26);
        this.bipedBody.addBox(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f, modelSize);
        this.bipedBody.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedRightArm = new ModelRenderer(this, 24, 0);
        this.bipedRightArm.addBox(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, modelSize);
        this.bipedRightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.bipedLeftArm = new ModelRenderer(this, 32, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, modelSize);
        this.bipedLeftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.bipedRightLeg = new ModelRenderer(this, 8, 0);
        this.bipedRightLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, modelSize);
        this.bipedRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.bipedLeftLeg = new ModelRenderer(this, 40, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, modelSize);
        this.bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.standRightSide = new ModelRenderer(this, 16, 0);
        this.standRightSide.addBox(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, modelSize);
        this.standRightSide.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.standRightSide.showModel = true;
        this.standLeftSide = new ModelRenderer(this, 48, 16);
        this.standLeftSide.addBox(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, modelSize);
        this.standLeftSide.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.standWaist = new ModelRenderer(this, 0, 48);
        this.standWaist.addBox(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f, modelSize);
        this.standWaist.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.standBase = new ModelRenderer(this, 0, 32);
        this.standBase.addBox(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f, modelSize);
        this.standBase.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.bipedHeadwear.showModel = false;
    }

    @Override
    public void setLivingAnimations(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.standBase.rotateAngleX = 0.0f;
        this.standBase.rotateAngleY = (float)Math.PI / 180 * -MathHelper.interpolateAngle(partialTick, entityIn.prevRotationYaw, entityIn.rotationYaw);
        this.standBase.rotateAngleZ = 0.0f;
    }

    @Override
    public void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.bipedLeftArm.showModel = entityIn.getShowArms();
        this.bipedRightArm.showModel = entityIn.getShowArms();
        this.standBase.showModel = !entityIn.hasNoBasePlate();
        this.bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.bipedRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.standRightSide.rotateAngleX = (float)Math.PI / 180 * entityIn.getBodyRotation().getX();
        this.standRightSide.rotateAngleY = (float)Math.PI / 180 * entityIn.getBodyRotation().getY();
        this.standRightSide.rotateAngleZ = (float)Math.PI / 180 * entityIn.getBodyRotation().getZ();
        this.standLeftSide.rotateAngleX = (float)Math.PI / 180 * entityIn.getBodyRotation().getX();
        this.standLeftSide.rotateAngleY = (float)Math.PI / 180 * entityIn.getBodyRotation().getY();
        this.standLeftSide.rotateAngleZ = (float)Math.PI / 180 * entityIn.getBodyRotation().getZ();
        this.standWaist.rotateAngleX = (float)Math.PI / 180 * entityIn.getBodyRotation().getX();
        this.standWaist.rotateAngleY = (float)Math.PI / 180 * entityIn.getBodyRotation().getY();
        this.standWaist.rotateAngleZ = (float)Math.PI / 180 * entityIn.getBodyRotation().getZ();
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.standRightSide, this.standLeftSide, this.standWaist, this.standBase));
    }

    @Override
    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        ModelRenderer modelrenderer = this.getArmForSide(sideIn);
        boolean flag = modelrenderer.showModel;
        modelrenderer.showModel = true;
        super.translateHand(sideIn, matrixStackIn);
        modelrenderer.showModel = flag;
    }
}
