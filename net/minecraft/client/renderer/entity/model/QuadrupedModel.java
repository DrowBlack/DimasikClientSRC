package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class QuadrupedModel<T extends Entity>
extends AgeableModel<T> {
    protected ModelRenderer headModel = new ModelRenderer(this, 0, 0);
    protected ModelRenderer body;
    protected ModelRenderer legBackRight;
    protected ModelRenderer legBackLeft;
    protected ModelRenderer legFrontRight;
    protected ModelRenderer legFrontLeft;

    public QuadrupedModel(int p_i225948_1_, float p_i225948_2_, boolean p_i225948_3_, float p_i225948_4_, float p_i225948_5_, float p_i225948_6_, float p_i225948_7_, int p_i225948_8_) {
        super(p_i225948_3_, p_i225948_4_, p_i225948_5_, p_i225948_6_, p_i225948_7_, p_i225948_8_);
        this.headModel.addBox(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, p_i225948_2_);
        this.headModel.setRotationPoint(0.0f, 18 - p_i225948_1_, -6.0f);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, p_i225948_2_);
        this.body.setRotationPoint(0.0f, 17 - p_i225948_1_, 2.0f);
        this.legBackRight = new ModelRenderer(this, 0, 16);
        this.legBackRight.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)p_i225948_1_, 4.0f, p_i225948_2_);
        this.legBackRight.setRotationPoint(-3.0f, 24 - p_i225948_1_, 7.0f);
        this.legBackLeft = new ModelRenderer(this, 0, 16);
        this.legBackLeft.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)p_i225948_1_, 4.0f, p_i225948_2_);
        this.legBackLeft.setRotationPoint(3.0f, 24 - p_i225948_1_, 7.0f);
        this.legFrontRight = new ModelRenderer(this, 0, 16);
        this.legFrontRight.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)p_i225948_1_, 4.0f, p_i225948_2_);
        this.legFrontRight.setRotationPoint(-3.0f, 24 - p_i225948_1_, -5.0f);
        this.legFrontLeft = new ModelRenderer(this, 0, 16);
        this.legFrontLeft.addBox(-2.0f, 0.0f, -2.0f, 4.0f, (float)p_i225948_1_, 4.0f, p_i225948_2_);
        this.legFrontLeft.setRotationPoint(3.0f, 24 - p_i225948_1_, -5.0f);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.headModel);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.headModel.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.headModel.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.body.rotateAngleX = 1.5707964f;
        this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
    }
}
