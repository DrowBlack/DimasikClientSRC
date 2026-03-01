package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.IHeadToggle;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.util.math.MathHelper;

public class VillagerModel<T extends Entity>
extends SegmentedModel<T>
implements IHasHead,
IHeadToggle {
    protected ModelRenderer villagerHead;
    protected ModelRenderer hat;
    protected final ModelRenderer hatBrim;
    protected final ModelRenderer villagerBody;
    protected final ModelRenderer clothing;
    protected final ModelRenderer villagerArms;
    protected final ModelRenderer rightVillagerLeg;
    protected final ModelRenderer leftVillagerLeg;
    protected final ModelRenderer villagerNose;

    public VillagerModel(float scale) {
        this(scale, 64, 64);
    }

    public VillagerModel(float p_i51059_1_, int p_i51059_2_, int p_i51059_3_) {
        float f = 0.5f;
        this.villagerHead = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.villagerHead.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, p_i51059_1_);
        this.hat = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.hat.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.hat.setTextureOffset(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, p_i51059_1_ + 0.5f);
        this.villagerHead.addChild(this.hat);
        this.hatBrim = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.hatBrim.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.hatBrim.setTextureOffset(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, p_i51059_1_);
        this.hatBrim.rotateAngleX = -1.5707964f;
        this.hat.addChild(this.hatBrim);
        this.villagerNose = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.villagerNose.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, p_i51059_1_);
        this.villagerHead.addChild(this.villagerNose);
        this.villagerBody = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.villagerBody.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, p_i51059_1_);
        this.clothing = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.clothing.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.clothing.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, p_i51059_1_ + 0.5f);
        this.villagerBody.addChild(this.clothing);
        this.villagerArms = new ModelRenderer(this).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.villagerArms.setRotationPoint(0.0f, 2.0f, 0.0f);
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, p_i51059_1_);
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, p_i51059_1_, true);
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, p_i51059_1_);
        this.rightVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.rightVillagerLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
        this.rightVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51059_1_);
        this.leftVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(p_i51059_2_, p_i51059_3_);
        this.leftVillagerLeg.mirror = true;
        this.leftVillagerLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        this.leftVillagerLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51059_1_);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.villagerHead, this.villagerBody, this.rightVillagerLeg, this.leftVillagerLeg, this.villagerArms);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = false;
        if (entityIn instanceof AbstractVillagerEntity) {
            flag = ((AbstractVillagerEntity)entityIn).getShakeHeadTicks() > 0;
        }
        this.villagerHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.villagerHead.rotateAngleX = headPitch * ((float)Math.PI / 180);
        if (flag) {
            this.villagerHead.rotateAngleZ = 0.3f * MathHelper.sin(0.45f * ageInTicks);
            this.villagerHead.rotateAngleX = 0.4f;
        } else {
            this.villagerHead.rotateAngleZ = 0.0f;
        }
        this.villagerArms.rotationPointY = 3.0f;
        this.villagerArms.rotationPointZ = -1.0f;
        this.villagerArms.rotateAngleX = -0.75f;
        this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount * 0.5f;
        this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount * 0.5f;
        this.rightVillagerLeg.rotateAngleY = 0.0f;
        this.leftVillagerLeg.rotateAngleY = 0.0f;
    }

    @Override
    public ModelRenderer getModelHead() {
        return this.villagerHead;
    }

    @Override
    public void func_217146_a(boolean p_217146_1_) {
        this.villagerHead.showModel = p_217146_1_;
        this.hat.showModel = p_217146_1_;
        this.hatBrim.showModel = p_217146_1_;
    }
}
