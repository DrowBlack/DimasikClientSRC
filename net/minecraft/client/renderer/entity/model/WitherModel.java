package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

public class WitherModel<T extends WitherEntity>
extends SegmentedModel<T> {
    private final ModelRenderer[] upperBodyParts;
    private final ModelRenderer[] heads;
    private final ImmutableList<ModelRenderer> field_228297_f_;

    public WitherModel(float p_i46302_1_) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.upperBodyParts = new ModelRenderer[3];
        this.upperBodyParts[0] = new ModelRenderer(this, 0, 16);
        this.upperBodyParts[0].addBox(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, p_i46302_1_);
        this.upperBodyParts[1] = new ModelRenderer(this).setTextureSize(this.textureWidth, this.textureHeight);
        this.upperBodyParts[1].setRotationPoint(-2.0f, 6.9f, -0.5f);
        this.upperBodyParts[1].setTextureOffset(0, 22).addBox(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, p_i46302_1_);
        this.upperBodyParts[1].setTextureOffset(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, p_i46302_1_);
        this.upperBodyParts[1].setTextureOffset(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, p_i46302_1_);
        this.upperBodyParts[1].setTextureOffset(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, p_i46302_1_);
        this.upperBodyParts[2] = new ModelRenderer(this, 12, 22);
        this.upperBodyParts[2].addBox(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, p_i46302_1_);
        this.heads = new ModelRenderer[3];
        this.heads[0] = new ModelRenderer(this, 0, 0);
        this.heads[0].addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, p_i46302_1_);
        this.heads[1] = new ModelRenderer(this, 32, 0);
        this.heads[1].addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, p_i46302_1_);
        this.heads[1].rotationPointX = -8.0f;
        this.heads[1].rotationPointY = 4.0f;
        this.heads[2] = new ModelRenderer(this, 32, 0);
        this.heads[2].addBox(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, p_i46302_1_);
        this.heads[2].rotationPointX = 10.0f;
        this.heads[2].rotationPointY = 4.0f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(this.heads));
        builder.addAll(Arrays.asList(this.upperBodyParts));
        this.field_228297_f_ = builder.build();
    }

    public ImmutableList<ModelRenderer> getParts() {
        return this.field_228297_f_;
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = MathHelper.cos(ageInTicks * 0.1f);
        this.upperBodyParts[1].rotateAngleX = (0.065f + 0.05f * f) * (float)Math.PI;
        this.upperBodyParts[2].setRotationPoint(-2.0f, 6.9f + MathHelper.cos(this.upperBodyParts[1].rotateAngleX) * 10.0f, -0.5f + MathHelper.sin(this.upperBodyParts[1].rotateAngleX) * 10.0f);
        this.upperBodyParts[2].rotateAngleX = (0.265f + 0.1f * f) * (float)Math.PI;
        this.heads[0].rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.heads[0].rotateAngleX = headPitch * ((float)Math.PI / 180);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        for (int i = 1; i < 3; ++i) {
            this.heads[i].rotateAngleY = (((WitherEntity)entityIn).getHeadYRotation(i - 1) - ((WitherEntity)entityIn).renderYawOffset) * ((float)Math.PI / 180);
            this.heads[i].rotateAngleX = ((WitherEntity)entityIn).getHeadXRotation(i - 1) * ((float)Math.PI / 180);
        }
    }
}
