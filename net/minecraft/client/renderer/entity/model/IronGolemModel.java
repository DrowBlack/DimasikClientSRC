package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MathHelper;

public class IronGolemModel<T extends IronGolemEntity>
extends SegmentedModel<T> {
    private final ModelRenderer ironGolemHead;
    private final ModelRenderer ironGolemBody;
    private final ModelRenderer ironGolemRightArm;
    private final ModelRenderer ironGolemLeftArm;
    private final ModelRenderer ironGolemLeftLeg;
    private final ModelRenderer ironGolemRightLeg;

    public IronGolemModel() {
        int i = 128;
        int j = 128;
        this.ironGolemHead = new ModelRenderer(this).setTextureSize(128, 128);
        this.ironGolemHead.setRotationPoint(0.0f, -7.0f, -2.0f);
        this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f, 0.0f);
        this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f, 0.0f);
        this.ironGolemBody = new ModelRenderer(this).setTextureSize(128, 128);
        this.ironGolemBody.setRotationPoint(0.0f, -7.0f, 0.0f);
        this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f, 0.0f);
        this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, 0.5f);
        this.ironGolemRightArm = new ModelRenderer(this).setTextureSize(128, 128);
        this.ironGolemRightArm.setRotationPoint(0.0f, -7.0f, 0.0f);
        this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f, 0.0f);
        this.ironGolemLeftArm = new ModelRenderer(this).setTextureSize(128, 128);
        this.ironGolemLeftArm.setRotationPoint(0.0f, -7.0f, 0.0f);
        this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f, 0.0f);
        this.ironGolemLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        this.ironGolemLeftLeg.setRotationPoint(-4.0f, 11.0f, 0.0f);
        this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, 0.0f);
        this.ironGolemRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        this.ironGolemRightLeg.mirror = true;
        this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0f, 11.0f, 0.0f);
        this.ironGolemRightLeg.addBox(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f, 0.0f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.ironGolemHead, this.ironGolemBody, this.ironGolemLeftLeg, this.ironGolemRightLeg, this.ironGolemRightArm, this.ironGolemLeftArm);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.ironGolemHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.ironGolemHead.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.ironGolemLeftLeg.rotateAngleX = -1.5f * MathHelper.func_233021_e_(limbSwing, 13.0f) * limbSwingAmount;
        this.ironGolemRightLeg.rotateAngleX = 1.5f * MathHelper.func_233021_e_(limbSwing, 13.0f) * limbSwingAmount;
        this.ironGolemLeftLeg.rotateAngleY = 0.0f;
        this.ironGolemRightLeg.rotateAngleY = 0.0f;
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        int i = ((IronGolemEntity)entityIn).getAttackTimer();
        if (i > 0) {
            this.ironGolemRightArm.rotateAngleX = -2.0f + 1.5f * MathHelper.func_233021_e_((float)i - partialTick, 10.0f);
            this.ironGolemLeftArm.rotateAngleX = -2.0f + 1.5f * MathHelper.func_233021_e_((float)i - partialTick, 10.0f);
        } else {
            int j = ((IronGolemEntity)entityIn).getHoldRoseTick();
            if (j > 0) {
                this.ironGolemRightArm.rotateAngleX = -0.8f + 0.025f * MathHelper.func_233021_e_(j, 70.0f);
                this.ironGolemLeftArm.rotateAngleX = 0.0f;
            } else {
                this.ironGolemRightArm.rotateAngleX = (-0.2f + 1.5f * MathHelper.func_233021_e_(limbSwing, 13.0f)) * limbSwingAmount;
                this.ironGolemLeftArm.rotateAngleX = (-0.2f - 1.5f * MathHelper.func_233021_e_(limbSwing, 13.0f)) * limbSwingAmount;
            }
        }
    }

    public ModelRenderer getArmHoldingRose() {
        return this.ironGolemRightArm;
    }
}
