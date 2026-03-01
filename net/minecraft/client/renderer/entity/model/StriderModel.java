package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

public class StriderModel<T extends StriderEntity>
extends SegmentedModel<T> {
    private final ModelRenderer field_239118_a_;
    private final ModelRenderer field_239119_b_;
    private final ModelRenderer field_239120_f_;
    private final ModelRenderer field_239121_g_;
    private final ModelRenderer field_239122_h_;
    private final ModelRenderer field_239123_i_;
    private final ModelRenderer field_239124_j_;
    private final ModelRenderer field_239125_k_;
    private final ModelRenderer field_239126_l_;

    public StriderModel() {
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.field_239118_a_ = new ModelRenderer(this, 0, 32);
        this.field_239118_a_.setRotationPoint(-4.0f, 8.0f, 0.0f);
        this.field_239118_a_.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.field_239119_b_ = new ModelRenderer(this, 0, 55);
        this.field_239119_b_.setRotationPoint(4.0f, 8.0f, 0.0f);
        this.field_239119_b_.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f, 0.0f);
        this.field_239120_f_ = new ModelRenderer(this, 0, 0);
        this.field_239120_f_.setRotationPoint(0.0f, 1.0f, 0.0f);
        this.field_239120_f_.addBox(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f, 0.0f);
        this.field_239121_g_ = new ModelRenderer(this, 16, 65);
        this.field_239121_g_.setRotationPoint(-8.0f, 4.0f, -8.0f);
        this.field_239121_g_.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.func_239127_a_(this.field_239121_g_, 0.0f, 0.0f, -1.2217305f);
        this.field_239122_h_ = new ModelRenderer(this, 16, 49);
        this.field_239122_h_.setRotationPoint(-8.0f, -1.0f, -8.0f);
        this.field_239122_h_.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.func_239127_a_(this.field_239122_h_, 0.0f, 0.0f, -1.134464f);
        this.field_239123_i_ = new ModelRenderer(this, 16, 33);
        this.field_239123_i_.setRotationPoint(-8.0f, -5.0f, -8.0f);
        this.field_239123_i_.addBox(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f, true);
        this.func_239127_a_(this.field_239123_i_, 0.0f, 0.0f, -0.87266463f);
        this.field_239124_j_ = new ModelRenderer(this, 16, 33);
        this.field_239124_j_.setRotationPoint(8.0f, -6.0f, -8.0f);
        this.field_239124_j_.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.func_239127_a_(this.field_239124_j_, 0.0f, 0.0f, 0.87266463f);
        this.field_239125_k_ = new ModelRenderer(this, 16, 49);
        this.field_239125_k_.setRotationPoint(8.0f, -2.0f, -8.0f);
        this.field_239125_k_.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.func_239127_a_(this.field_239125_k_, 0.0f, 0.0f, 1.134464f);
        this.field_239126_l_ = new ModelRenderer(this, 16, 65);
        this.field_239126_l_.setRotationPoint(8.0f, 3.0f, -8.0f);
        this.field_239126_l_.addBox(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, 0.0f);
        this.func_239127_a_(this.field_239126_l_, 0.0f, 0.0f, 1.2217305f);
        this.field_239120_f_.addChild(this.field_239121_g_);
        this.field_239120_f_.addChild(this.field_239122_h_);
        this.field_239120_f_.addChild(this.field_239123_i_);
        this.field_239120_f_.addChild(this.field_239124_j_);
        this.field_239120_f_.addChild(this.field_239125_k_);
        this.field_239120_f_.addChild(this.field_239126_l_);
    }

    @Override
    public void setRotationAngles(StriderEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        limbSwingAmount = Math.min(0.25f, limbSwingAmount);
        if (entityIn.getPassengers().size() <= 0) {
            this.field_239120_f_.rotateAngleX = headPitch * ((float)Math.PI / 180);
            this.field_239120_f_.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        } else {
            this.field_239120_f_.rotateAngleX = 0.0f;
            this.field_239120_f_.rotateAngleY = 0.0f;
        }
        float f = 1.5f;
        this.field_239120_f_.rotateAngleZ = 0.1f * MathHelper.sin(limbSwing * 1.5f) * 4.0f * limbSwingAmount;
        this.field_239120_f_.rotationPointY = 2.0f;
        this.field_239120_f_.rotationPointY -= 2.0f * MathHelper.cos(limbSwing * 1.5f) * 2.0f * limbSwingAmount;
        this.field_239119_b_.rotateAngleX = MathHelper.sin(limbSwing * 1.5f * 0.5f) * 2.0f * limbSwingAmount;
        this.field_239118_a_.rotateAngleX = MathHelper.sin(limbSwing * 1.5f * 0.5f + (float)Math.PI) * 2.0f * limbSwingAmount;
        this.field_239119_b_.rotateAngleZ = 0.17453292f * MathHelper.cos(limbSwing * 1.5f * 0.5f) * limbSwingAmount;
        this.field_239118_a_.rotateAngleZ = 0.17453292f * MathHelper.cos(limbSwing * 1.5f * 0.5f + (float)Math.PI) * limbSwingAmount;
        this.field_239119_b_.rotationPointY = 8.0f + 2.0f * MathHelper.sin(limbSwing * 1.5f * 0.5f + (float)Math.PI) * 2.0f * limbSwingAmount;
        this.field_239118_a_.rotationPointY = 8.0f + 2.0f * MathHelper.sin(limbSwing * 1.5f * 0.5f) * 2.0f * limbSwingAmount;
        this.field_239121_g_.rotateAngleZ = -1.2217305f;
        this.field_239122_h_.rotateAngleZ = -1.134464f;
        this.field_239123_i_.rotateAngleZ = -0.87266463f;
        this.field_239124_j_.rotateAngleZ = 0.87266463f;
        this.field_239125_k_.rotateAngleZ = 1.134464f;
        this.field_239126_l_.rotateAngleZ = 1.2217305f;
        float f1 = MathHelper.cos(limbSwing * 1.5f + (float)Math.PI) * limbSwingAmount;
        this.field_239121_g_.rotateAngleZ += f1 * 1.3f;
        this.field_239122_h_.rotateAngleZ += f1 * 1.2f;
        this.field_239123_i_.rotateAngleZ += f1 * 0.6f;
        this.field_239124_j_.rotateAngleZ += f1 * 0.6f;
        this.field_239125_k_.rotateAngleZ += f1 * 1.2f;
        this.field_239126_l_.rotateAngleZ += f1 * 1.3f;
        float f2 = 1.0f;
        float f3 = 1.0f;
        this.field_239121_g_.rotateAngleZ += 0.05f * MathHelper.sin(ageInTicks * 1.0f * -0.4f);
        this.field_239122_h_.rotateAngleZ += 0.1f * MathHelper.sin(ageInTicks * 1.0f * 0.2f);
        this.field_239123_i_.rotateAngleZ += 0.1f * MathHelper.sin(ageInTicks * 1.0f * 0.4f);
        this.field_239124_j_.rotateAngleZ += 0.1f * MathHelper.sin(ageInTicks * 1.0f * 0.4f);
        this.field_239125_k_.rotateAngleZ += 0.1f * MathHelper.sin(ageInTicks * 1.0f * 0.2f);
        this.field_239126_l_.rotateAngleZ += 0.05f * MathHelper.sin(ageInTicks * 1.0f * -0.4f);
    }

    public void func_239127_a_(ModelRenderer p_239127_1_, float p_239127_2_, float p_239127_3_, float p_239127_4_) {
        p_239127_1_.rotateAngleX = p_239127_2_;
        p_239127_1_.rotateAngleY = p_239127_3_;
        p_239127_1_.rotateAngleZ = p_239127_4_;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.field_239120_f_, this.field_239119_b_, this.field_239118_a_);
    }
}
