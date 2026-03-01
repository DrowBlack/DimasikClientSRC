package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WitchModel<T extends Entity>
extends VillagerModel<T> {
    private boolean holdingItem;
    private final ModelRenderer mole = new ModelRenderer(this).setTextureSize(64, 128);

    public WitchModel(float scale) {
        super(scale, 64, 128);
        this.mole.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.mole.setTextureOffset(0, 0).addBox(0.0f, 3.0f, -6.75f, 1.0f, 1.0f, 1.0f, -0.25f);
        this.villagerNose.addChild(this.mole);
        this.villagerHead = new ModelRenderer(this).setTextureSize(64, 128);
        this.villagerHead.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale);
        this.hat = new ModelRenderer(this).setTextureSize(64, 128);
        this.hat.setRotationPoint(-5.0f, -10.03125f, -5.0f);
        this.hat.setTextureOffset(0, 64).addBox(0.0f, 0.0f, 0.0f, 10.0f, 2.0f, 10.0f);
        this.villagerHead.addChild(this.hat);
        this.villagerHead.addChild(this.villagerNose);
        ModelRenderer modelrenderer = new ModelRenderer(this).setTextureSize(64, 128);
        modelrenderer.setRotationPoint(1.75f, -4.0f, 2.0f);
        modelrenderer.setTextureOffset(0, 76).addBox(0.0f, 0.0f, 0.0f, 7.0f, 4.0f, 7.0f);
        modelrenderer.rotateAngleX = -0.05235988f;
        modelrenderer.rotateAngleZ = 0.02617994f;
        this.hat.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this).setTextureSize(64, 128);
        modelrenderer1.setRotationPoint(1.75f, -4.0f, 2.0f);
        modelrenderer1.setTextureOffset(0, 87).addBox(0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f);
        modelrenderer1.rotateAngleX = -0.10471976f;
        modelrenderer1.rotateAngleZ = 0.05235988f;
        modelrenderer.addChild(modelrenderer1);
        ModelRenderer modelrenderer2 = new ModelRenderer(this).setTextureSize(64, 128);
        modelrenderer2.setRotationPoint(1.75f, -2.0f, 2.0f);
        modelrenderer2.setTextureOffset(0, 95).addBox(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f, 0.25f);
        modelrenderer2.rotateAngleX = -0.20943952f;
        modelrenderer2.rotateAngleZ = 0.10471976f;
        modelrenderer1.addChild(modelrenderer2);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.villagerNose.setRotationPoint(0.0f, -2.0f, 0.0f);
        float f = 0.01f * (float)(((Entity)entityIn).getEntityId() % 10);
        this.villagerNose.rotateAngleX = MathHelper.sin((float)((Entity)entityIn).ticksExisted * f) * 4.5f * ((float)Math.PI / 180);
        this.villagerNose.rotateAngleY = 0.0f;
        this.villagerNose.rotateAngleZ = MathHelper.cos((float)((Entity)entityIn).ticksExisted * f) * 2.5f * ((float)Math.PI / 180);
        if (this.holdingItem) {
            this.villagerNose.setRotationPoint(0.0f, 1.0f, -1.5f);
            this.villagerNose.rotateAngleX = -0.9f;
        }
    }

    public ModelRenderer func_205073_b() {
        return this.villagerNose;
    }

    public void func_205074_a(boolean p_205074_1_) {
        this.holdingItem = p_205074_1_;
    }
}
