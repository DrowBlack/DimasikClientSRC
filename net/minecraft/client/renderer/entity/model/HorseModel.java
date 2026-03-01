package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;

public class HorseModel<T extends AbstractHorseEntity>
extends AgeableModel<T> {
    protected final ModelRenderer body;
    protected final ModelRenderer head;
    private final ModelRenderer field_228262_f_;
    private final ModelRenderer field_228263_g_;
    private final ModelRenderer field_228264_h_;
    private final ModelRenderer field_228265_i_;
    private final ModelRenderer field_228266_j_;
    private final ModelRenderer field_228267_k_;
    private final ModelRenderer field_228268_l_;
    private final ModelRenderer field_228269_m_;
    private final ModelRenderer field_217133_j;
    private final ModelRenderer[] field_217134_k;
    private final ModelRenderer[] field_217135_l;

    public HorseModel(float p_i51065_1_) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this, 0, 32);
        this.body.addBox(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, 0.05f);
        this.body.setRotationPoint(0.0f, 11.0f, 5.0f);
        this.head = new ModelRenderer(this, 0, 35);
        this.head.addBox(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f);
        this.head.rotateAngleX = 0.5235988f;
        ModelRenderer modelrenderer = new ModelRenderer(this, 0, 13);
        modelrenderer.addBox(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, p_i51065_1_);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 56, 36);
        modelrenderer1.addBox(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, p_i51065_1_);
        ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 25);
        modelrenderer2.addBox(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, p_i51065_1_);
        this.head.addChild(modelrenderer);
        this.head.addChild(modelrenderer1);
        this.head.addChild(modelrenderer2);
        this.func_199047_a(this.head);
        this.field_228262_f_ = new ModelRenderer(this, 48, 21);
        this.field_228262_f_.mirror = true;
        this.field_228262_f_.addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, p_i51065_1_);
        this.field_228262_f_.setRotationPoint(4.0f, 14.0f, 7.0f);
        this.field_228263_g_ = new ModelRenderer(this, 48, 21);
        this.field_228263_g_.addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, p_i51065_1_);
        this.field_228263_g_.setRotationPoint(-4.0f, 14.0f, 7.0f);
        this.field_228264_h_ = new ModelRenderer(this, 48, 21);
        this.field_228264_h_.mirror = true;
        this.field_228264_h_.addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, p_i51065_1_);
        this.field_228264_h_.setRotationPoint(4.0f, 6.0f, -12.0f);
        this.field_228265_i_ = new ModelRenderer(this, 48, 21);
        this.field_228265_i_.addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, p_i51065_1_);
        this.field_228265_i_.setRotationPoint(-4.0f, 6.0f, -12.0f);
        float f = 5.5f;
        this.field_228266_j_ = new ModelRenderer(this, 48, 21);
        this.field_228266_j_.mirror = true;
        this.field_228266_j_.addBox(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, p_i51065_1_, p_i51065_1_ + 5.5f, p_i51065_1_);
        this.field_228266_j_.setRotationPoint(4.0f, 14.0f, 7.0f);
        this.field_228267_k_ = new ModelRenderer(this, 48, 21);
        this.field_228267_k_.addBox(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, p_i51065_1_, p_i51065_1_ + 5.5f, p_i51065_1_);
        this.field_228267_k_.setRotationPoint(-4.0f, 14.0f, 7.0f);
        this.field_228268_l_ = new ModelRenderer(this, 48, 21);
        this.field_228268_l_.mirror = true;
        this.field_228268_l_.addBox(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, p_i51065_1_, p_i51065_1_ + 5.5f, p_i51065_1_);
        this.field_228268_l_.setRotationPoint(4.0f, 6.0f, -12.0f);
        this.field_228269_m_ = new ModelRenderer(this, 48, 21);
        this.field_228269_m_.addBox(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, p_i51065_1_, p_i51065_1_ + 5.5f, p_i51065_1_);
        this.field_228269_m_.setRotationPoint(-4.0f, 6.0f, -12.0f);
        this.field_217133_j = new ModelRenderer(this, 42, 36);
        this.field_217133_j.addBox(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, p_i51065_1_);
        this.field_217133_j.setRotationPoint(0.0f, -5.0f, 2.0f);
        this.field_217133_j.rotateAngleX = 0.5235988f;
        this.body.addChild(this.field_217133_j);
        ModelRenderer modelrenderer3 = new ModelRenderer(this, 26, 0);
        modelrenderer3.addBox(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, 0.5f);
        this.body.addChild(modelrenderer3);
        ModelRenderer modelrenderer4 = new ModelRenderer(this, 29, 5);
        modelrenderer4.addBox(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, p_i51065_1_);
        this.head.addChild(modelrenderer4);
        ModelRenderer modelrenderer5 = new ModelRenderer(this, 29, 5);
        modelrenderer5.addBox(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, p_i51065_1_);
        this.head.addChild(modelrenderer5);
        ModelRenderer modelrenderer6 = new ModelRenderer(this, 32, 2);
        modelrenderer6.addBox(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, p_i51065_1_);
        modelrenderer6.rotateAngleX = -0.5235988f;
        this.head.addChild(modelrenderer6);
        ModelRenderer modelrenderer7 = new ModelRenderer(this, 32, 2);
        modelrenderer7.addBox(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, p_i51065_1_);
        modelrenderer7.rotateAngleX = -0.5235988f;
        this.head.addChild(modelrenderer7);
        ModelRenderer modelrenderer8 = new ModelRenderer(this, 1, 1);
        modelrenderer8.addBox(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, 0.2f);
        this.head.addChild(modelrenderer8);
        ModelRenderer modelrenderer9 = new ModelRenderer(this, 19, 0);
        modelrenderer9.addBox(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, 0.2f);
        this.head.addChild(modelrenderer9);
        this.field_217134_k = new ModelRenderer[]{modelrenderer3, modelrenderer4, modelrenderer5, modelrenderer8, modelrenderer9};
        this.field_217135_l = new ModelRenderer[]{modelrenderer6, modelrenderer7};
    }

    protected void func_199047_a(ModelRenderer p_199047_1_) {
        ModelRenderer modelrenderer = new ModelRenderer(this, 19, 16);
        modelrenderer.addBox(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 19, 16);
        modelrenderer1.addBox(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        p_199047_1_.addChild(modelrenderer);
        p_199047_1_.addChild(modelrenderer1);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = ((AbstractHorseEntity)entityIn).isHorseSaddled();
        boolean flag1 = ((Entity)entityIn).isBeingRidden();
        for (ModelRenderer modelrenderer : this.field_217134_k) {
            modelrenderer.showModel = flag;
        }
        for (ModelRenderer modelrenderer1 : this.field_217135_l) {
            modelrenderer1.showModel = flag1 && flag;
        }
        this.body.rotationPointY = 11.0f;
    }

    @Override
    public Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body, this.field_228262_f_, this.field_228263_g_, this.field_228264_h_, this.field_228265_i_, this.field_228266_j_, this.field_228267_k_, this.field_228268_l_, this.field_228269_m_);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        float f = MathHelper.rotLerp(((AbstractHorseEntity)entityIn).prevRenderYawOffset, ((AbstractHorseEntity)entityIn).renderYawOffset, partialTick);
        float f1 = MathHelper.rotLerp(((AbstractHorseEntity)entityIn).prevRotationYawHead, ((AbstractHorseEntity)entityIn).rotationYawHead, partialTick);
        float f2 = MathHelper.lerp(partialTick, ((AbstractHorseEntity)entityIn).prevRotationPitch, ((AbstractHorseEntity)entityIn).rotationPitch);
        float f3 = f1 - f;
        float f4 = f2 * ((float)Math.PI / 180);
        if (f3 > 20.0f) {
            f3 = 20.0f;
        }
        if (f3 < -20.0f) {
            f3 = -20.0f;
        }
        if (limbSwingAmount > 0.2f) {
            f4 += MathHelper.cos(limbSwing * 0.4f) * 0.15f * limbSwingAmount;
        }
        float f5 = ((AbstractHorseEntity)entityIn).getGrassEatingAmount(partialTick);
        float f6 = ((AbstractHorseEntity)entityIn).getRearingAmount(partialTick);
        float f7 = 1.0f - f6;
        float f8 = ((AbstractHorseEntity)entityIn).getMouthOpennessAngle(partialTick);
        boolean flag = ((AbstractHorseEntity)entityIn).tailCounter != 0;
        float f9 = (float)((AbstractHorseEntity)entityIn).ticksExisted + partialTick;
        this.head.rotationPointY = 4.0f;
        this.head.rotationPointZ = -12.0f;
        this.body.rotateAngleX = 0.0f;
        this.head.rotateAngleX = 0.5235988f + f4;
        this.head.rotateAngleY = f3 * ((float)Math.PI / 180);
        float f10 = ((Entity)entityIn).isInWater() ? 0.2f : 1.0f;
        float f11 = MathHelper.cos(f10 * limbSwing * 0.6662f + (float)Math.PI);
        float f12 = f11 * 0.8f * limbSwingAmount;
        float f13 = (1.0f - Math.max(f6, f5)) * (0.5235988f + f4 + f8 * MathHelper.sin(f9) * 0.05f);
        this.head.rotateAngleX = f6 * (0.2617994f + f4) + f5 * (2.1816616f + MathHelper.sin(f9) * 0.05f) + f13;
        this.head.rotateAngleY = f6 * f3 * ((float)Math.PI / 180) + (1.0f - Math.max(f6, f5)) * this.head.rotateAngleY;
        this.head.rotationPointY = f6 * -4.0f + f5 * 11.0f + (1.0f - Math.max(f6, f5)) * this.head.rotationPointY;
        this.head.rotationPointZ = f6 * -4.0f + f5 * -12.0f + (1.0f - Math.max(f6, f5)) * this.head.rotationPointZ;
        this.body.rotateAngleX = f6 * -0.7853982f + f7 * this.body.rotateAngleX;
        float f14 = 0.2617994f * f6;
        float f15 = MathHelper.cos(f9 * 0.6f + (float)Math.PI);
        this.field_228264_h_.rotationPointY = 2.0f * f6 + 14.0f * f7;
        this.field_228264_h_.rotationPointZ = -6.0f * f6 - 10.0f * f7;
        this.field_228265_i_.rotationPointY = this.field_228264_h_.rotationPointY;
        this.field_228265_i_.rotationPointZ = this.field_228264_h_.rotationPointZ;
        float f16 = (-1.0471976f + f15) * f6 + f12 * f7;
        float f17 = (-1.0471976f - f15) * f6 - f12 * f7;
        this.field_228262_f_.rotateAngleX = f14 - f11 * 0.5f * limbSwingAmount * f7;
        this.field_228263_g_.rotateAngleX = f14 + f11 * 0.5f * limbSwingAmount * f7;
        this.field_228264_h_.rotateAngleX = f16;
        this.field_228265_i_.rotateAngleX = f17;
        this.field_217133_j.rotateAngleX = 0.5235988f + limbSwingAmount * 0.75f;
        this.field_217133_j.rotationPointY = -5.0f + limbSwingAmount;
        this.field_217133_j.rotationPointZ = 2.0f + limbSwingAmount * 2.0f;
        this.field_217133_j.rotateAngleY = flag ? MathHelper.cos(f9 * 0.7f) : 0.0f;
        this.field_228266_j_.rotationPointY = this.field_228262_f_.rotationPointY;
        this.field_228266_j_.rotationPointZ = this.field_228262_f_.rotationPointZ;
        this.field_228266_j_.rotateAngleX = this.field_228262_f_.rotateAngleX;
        this.field_228267_k_.rotationPointY = this.field_228263_g_.rotationPointY;
        this.field_228267_k_.rotationPointZ = this.field_228263_g_.rotationPointZ;
        this.field_228267_k_.rotateAngleX = this.field_228263_g_.rotateAngleX;
        this.field_228268_l_.rotationPointY = this.field_228264_h_.rotationPointY;
        this.field_228268_l_.rotationPointZ = this.field_228264_h_.rotationPointZ;
        this.field_228268_l_.rotateAngleX = this.field_228264_h_.rotateAngleX;
        this.field_228269_m_.rotationPointY = this.field_228265_i_.rotationPointY;
        this.field_228269_m_.rotationPointZ = this.field_228265_i_.rotationPointZ;
        this.field_228269_m_.rotateAngleX = this.field_228265_i_.rotateAngleX;
        boolean flag1 = ((AgeableEntity)entityIn).isChild();
        this.field_228262_f_.showModel = !flag1;
        this.field_228263_g_.showModel = !flag1;
        this.field_228264_h_.showModel = !flag1;
        this.field_228265_i_.showModel = !flag1;
        this.field_228266_j_.showModel = flag1;
        this.field_228267_k_.showModel = flag1;
        this.field_228268_l_.showModel = flag1;
        this.field_228269_m_.showModel = flag1;
        this.body.rotationPointY = flag1 ? 10.8f : 0.0f;
    }
}
