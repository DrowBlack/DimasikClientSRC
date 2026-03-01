package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.MathHelper;

public class BoatModel
extends SegmentedModel<BoatEntity> {
    private final ModelRenderer[] paddles = new ModelRenderer[2];
    private final ModelRenderer noWater;
    private final ImmutableList<ModelRenderer> field_228243_f_;

    public BoatModel() {
        ModelRenderer[] amodelrenderer = new ModelRenderer[]{new ModelRenderer(this, 0, 0).setTextureSize(128, 64), new ModelRenderer(this, 0, 19).setTextureSize(128, 64), new ModelRenderer(this, 0, 27).setTextureSize(128, 64), new ModelRenderer(this, 0, 35).setTextureSize(128, 64), new ModelRenderer(this, 0, 43).setTextureSize(128, 64)};
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int i1 = 28;
        amodelrenderer[0].addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        amodelrenderer[0].setRotationPoint(0.0f, 3.0f, 1.0f);
        amodelrenderer[1].addBox(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f, 0.0f);
        amodelrenderer[1].setRotationPoint(-15.0f, 4.0f, 4.0f);
        amodelrenderer[2].addBox(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f, 0.0f);
        amodelrenderer[2].setRotationPoint(15.0f, 4.0f, 0.0f);
        amodelrenderer[3].addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        amodelrenderer[3].setRotationPoint(0.0f, 4.0f, -9.0f);
        amodelrenderer[4].addBox(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        amodelrenderer[4].setRotationPoint(0.0f, 4.0f, 9.0f);
        amodelrenderer[0].rotateAngleX = 1.5707964f;
        amodelrenderer[1].rotateAngleY = 4.712389f;
        amodelrenderer[2].rotateAngleY = 1.5707964f;
        amodelrenderer[3].rotateAngleY = (float)Math.PI;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setRotationPoint(3.0f, -5.0f, 9.0f);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setRotationPoint(3.0f, -5.0f, -9.0f);
        this.paddles[1].rotateAngleY = (float)Math.PI;
        this.paddles[0].rotateAngleZ = 0.19634955f;
        this.paddles[1].rotateAngleZ = 0.19634955f;
        this.noWater = new ModelRenderer(this, 0, 0).setTextureSize(128, 64);
        this.noWater.addBox(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        this.noWater.setRotationPoint(0.0f, -3.0f, 1.0f);
        this.noWater.rotateAngleX = 1.5707964f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(amodelrenderer));
        builder.addAll(Arrays.asList(this.paddles));
        this.field_228243_f_ = builder.build();
    }

    @Override
    public void setRotationAngles(BoatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.func_228244_a_(entityIn, 0, limbSwing);
        this.func_228244_a_(entityIn, 1, limbSwing);
    }

    public ImmutableList<ModelRenderer> getParts() {
        return this.field_228243_f_;
    }

    public ModelRenderer func_228245_c_() {
        return this.noWater;
    }

    protected ModelRenderer makePaddle(boolean p_187056_1_) {
        ModelRenderer modelrenderer = new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelrenderer.addBox(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f);
        modelrenderer.addBox(p_187056_1_ ? -1.001f : 0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f);
        return modelrenderer;
    }

    protected void func_228244_a_(BoatEntity p_228244_1_, int p_228244_2_, float p_228244_3_) {
        float f = p_228244_1_.getRowingTime(p_228244_2_, p_228244_3_);
        ModelRenderer modelrenderer = this.paddles[p_228244_2_];
        modelrenderer.rotateAngleX = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        modelrenderer.rotateAngleY = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (p_228244_2_ == 1) {
            modelrenderer.rotateAngleY = (float)Math.PI - modelrenderer.rotateAngleY;
        }
    }
}
