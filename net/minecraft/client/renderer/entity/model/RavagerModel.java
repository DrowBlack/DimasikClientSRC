package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.math.MathHelper;

public class RavagerModel
extends SegmentedModel<RavagerEntity> {
    private final ModelRenderer head;
    private final ModelRenderer jaw;
    private final ModelRenderer body;
    private final ModelRenderer legBackRight;
    private final ModelRenderer legBackLeft;
    private final ModelRenderer legFrontRight;
    private final ModelRenderer legFrontLeft;
    private final ModelRenderer neck;

    public RavagerModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        int i = 16;
        float f = 0.0f;
        this.neck = new ModelRenderer(this);
        this.neck.setRotationPoint(0.0f, -7.0f, -1.5f);
        this.neck.setTextureOffset(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f, 0.0f);
        this.head = new ModelRenderer(this);
        this.head.setRotationPoint(0.0f, 16.0f, -17.0f);
        this.head.setTextureOffset(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f, 0.0f);
        ModelRenderer modelrenderer = new ModelRenderer(this);
        modelrenderer.setRotationPoint(-10.0f, -14.0f, -8.0f);
        modelrenderer.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelrenderer.rotateAngleX = 1.0995574f;
        this.head.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this);
        modelrenderer1.mirror = true;
        modelrenderer1.setRotationPoint(8.0f, -14.0f, -8.0f);
        modelrenderer1.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelrenderer1.rotateAngleX = 1.0995574f;
        this.head.addChild(modelrenderer1);
        this.jaw = new ModelRenderer(this);
        this.jaw.setRotationPoint(0.0f, -2.0f, 2.0f);
        this.jaw.setTextureOffset(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f, 0.0f);
        this.head.addChild(this.jaw);
        this.neck.addChild(this.head);
        this.body = new ModelRenderer(this);
        this.body.setTextureOffset(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f, 0.0f);
        this.body.setTextureOffset(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f, 0.0f);
        this.body.setRotationPoint(0.0f, 1.0f, 2.0f);
        this.legBackRight = new ModelRenderer(this, 96, 0);
        this.legBackRight.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.legBackRight.setRotationPoint(-8.0f, -13.0f, 18.0f);
        this.legBackLeft = new ModelRenderer(this, 96, 0);
        this.legBackLeft.mirror = true;
        this.legBackLeft.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.legBackLeft.setRotationPoint(8.0f, -13.0f, 18.0f);
        this.legFrontRight = new ModelRenderer(this, 64, 0);
        this.legFrontRight.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.legFrontRight.setRotationPoint(-8.0f, -13.0f, -5.0f);
        this.legFrontLeft = new ModelRenderer(this, 64, 0);
        this.legFrontLeft.mirror = true;
        this.legFrontLeft.addBox(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.legFrontLeft.setRotationPoint(8.0f, -13.0f, -5.0f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.neck, this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft);
    }

    @Override
    public void setRotationAngles(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.body.rotateAngleX = 1.5707964f;
        float f = 0.4f * limbSwingAmount;
        this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * f;
        this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * f;
        this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * f;
        this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * f;
    }

    @Override
    public void setLivingAnimations(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        int i = entityIn.func_213684_dX();
        int j = entityIn.func_213687_eg();
        int k = 20;
        int l = entityIn.func_213683_l();
        int i1 = 10;
        if (l > 0) {
            float f = MathHelper.func_233021_e_((float)l - partialTick, 10.0f);
            float f1 = (1.0f + f) * 0.5f;
            float f2 = f1 * f1 * f1 * 12.0f;
            float f3 = f2 * MathHelper.sin(this.neck.rotateAngleX);
            this.neck.rotationPointZ = -6.5f + f2;
            this.neck.rotationPointY = -7.0f - f3;
            float f4 = MathHelper.sin(((float)l - partialTick) / 10.0f * (float)Math.PI * 0.25f);
            this.jaw.rotateAngleX = 1.5707964f * f4;
            this.jaw.rotateAngleX = l > 5 ? MathHelper.sin(((float)(-4 + l) - partialTick) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - partialTick) / 10.0f);
        } else {
            float f5 = -1.0f;
            float f6 = -1.0f * MathHelper.sin(this.neck.rotateAngleX);
            this.neck.rotationPointX = 0.0f;
            this.neck.rotationPointY = -7.0f - f6;
            this.neck.rotationPointZ = 5.5f;
            boolean flag = i > 0;
            this.neck.rotateAngleX = flag ? 0.2199115f : 0.0f;
            this.jaw.rotateAngleX = (float)Math.PI * (flag ? 0.05f : 0.01f);
            if (flag) {
                double d0 = (double)i / 40.0;
                this.neck.rotationPointX = (float)Math.sin(d0 * 10.0) * 3.0f;
            } else if (j > 0) {
                float f7 = MathHelper.sin(((float)(20 - j) - partialTick) / 20.0f * (float)Math.PI * 0.25f);
                this.jaw.rotateAngleX = 1.5707964f * f7;
            }
        }
    }
}
