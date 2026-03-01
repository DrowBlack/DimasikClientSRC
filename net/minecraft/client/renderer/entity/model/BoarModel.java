package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IFlinging;
import net.minecraft.util.math.MathHelper;

public class BoarModel<T extends MobEntity>
extends AgeableModel<T> {
    private final ModelRenderer field_239106_a_;
    private final ModelRenderer field_239107_b_;
    private final ModelRenderer field_239108_f_;
    private final ModelRenderer field_239109_g_;
    private final ModelRenderer field_239110_h_;
    private final ModelRenderer field_239111_i_;
    private final ModelRenderer field_239112_j_;
    private final ModelRenderer field_239113_k_;
    private final ModelRenderer field_239114_l_;

    public BoarModel() {
        super(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.field_239109_g_ = new ModelRenderer(this);
        this.field_239109_g_.setRotationPoint(0.0f, 7.0f, 0.0f);
        this.field_239109_g_.setTextureOffset(1, 1).addBox(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f);
        this.field_239114_l_ = new ModelRenderer(this);
        this.field_239114_l_.setRotationPoint(0.0f, -14.0f, -5.0f);
        this.field_239114_l_.setTextureOffset(90, 33).addBox(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, 0.001f);
        this.field_239109_g_.addChild(this.field_239114_l_);
        this.field_239106_a_ = new ModelRenderer(this);
        this.field_239106_a_.setRotationPoint(0.0f, 2.0f, -12.0f);
        this.field_239106_a_.setTextureOffset(61, 1).addBox(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f);
        this.field_239107_b_ = new ModelRenderer(this);
        this.field_239107_b_.setRotationPoint(-6.0f, -2.0f, -3.0f);
        this.field_239107_b_.setTextureOffset(1, 1).addBox(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f);
        this.field_239107_b_.rotateAngleZ = -0.69813174f;
        this.field_239106_a_.addChild(this.field_239107_b_);
        this.field_239108_f_ = new ModelRenderer(this);
        this.field_239108_f_.setRotationPoint(6.0f, -2.0f, -3.0f);
        this.field_239108_f_.setTextureOffset(1, 6).addBox(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f);
        this.field_239108_f_.rotateAngleZ = 0.69813174f;
        this.field_239106_a_.addChild(this.field_239108_f_);
        ModelRenderer modelrenderer = new ModelRenderer(this);
        modelrenderer.setRotationPoint(-7.0f, 2.0f, -12.0f);
        modelrenderer.setTextureOffset(10, 13).addBox(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f);
        this.field_239106_a_.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this);
        modelrenderer1.setRotationPoint(7.0f, 2.0f, -12.0f);
        modelrenderer1.setTextureOffset(1, 13).addBox(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f);
        this.field_239106_a_.addChild(modelrenderer1);
        this.field_239106_a_.rotateAngleX = 0.87266463f;
        int i = 14;
        int j = 11;
        this.field_239110_h_ = new ModelRenderer(this);
        this.field_239110_h_.setRotationPoint(-4.0f, 10.0f, -8.5f);
        this.field_239110_h_.setTextureOffset(66, 42).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f);
        this.field_239111_i_ = new ModelRenderer(this);
        this.field_239111_i_.setRotationPoint(4.0f, 10.0f, -8.5f);
        this.field_239111_i_.setTextureOffset(41, 42).addBox(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f);
        this.field_239112_j_ = new ModelRenderer(this);
        this.field_239112_j_.setRotationPoint(-5.0f, 13.0f, 10.0f);
        this.field_239112_j_.setTextureOffset(21, 45).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f);
        this.field_239113_k_ = new ModelRenderer(this);
        this.field_239113_k_.setRotationPoint(5.0f, 13.0f, 10.0f);
        this.field_239113_k_.setTextureOffset(0, 45).addBox(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.field_239106_a_);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.field_239109_g_, this.field_239110_h_, this.field_239111_i_, this.field_239112_j_, this.field_239113_k_);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.field_239107_b_.rotateAngleZ = -0.69813174f - limbSwingAmount * MathHelper.sin(limbSwing);
        this.field_239108_f_.rotateAngleZ = 0.69813174f + limbSwingAmount * MathHelper.sin(limbSwing);
        this.field_239106_a_.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        int i = ((IFlinging)entityIn).func_230290_eL_();
        float f = 1.0f - (float)MathHelper.abs(10 - 2 * i) / 10.0f;
        this.field_239106_a_.rotateAngleX = MathHelper.lerp(f, 0.87266463f, -0.34906584f);
        if (((LivingEntity)entityIn).isChild()) {
            this.field_239106_a_.rotationPointY = MathHelper.lerp(f, 2.0f, 5.0f);
            this.field_239114_l_.rotationPointZ = -3.0f;
        } else {
            this.field_239106_a_.rotationPointY = 2.0f;
            this.field_239114_l_.rotationPointZ = -7.0f;
        }
        float f1 = 1.2f;
        this.field_239110_h_.rotateAngleX = MathHelper.cos(limbSwing) * 1.2f * limbSwingAmount;
        this.field_239112_j_.rotateAngleX = this.field_239111_i_.rotateAngleX = MathHelper.cos(limbSwing + (float)Math.PI) * 1.2f * limbSwingAmount;
        this.field_239113_k_.rotateAngleX = this.field_239110_h_.rotateAngleX;
    }
}
