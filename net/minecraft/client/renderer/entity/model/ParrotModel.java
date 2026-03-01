package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

public class ParrotModel
extends SegmentedModel<ParrotEntity> {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer wingLeft;
    private final ModelRenderer wingRight;
    private final ModelRenderer head;
    private final ModelRenderer head2;
    private final ModelRenderer beak1;
    private final ModelRenderer beak2;
    private final ModelRenderer feather;
    private final ModelRenderer legLeft;
    private final ModelRenderer legRight;

    public ParrotModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.body = new ModelRenderer(this, 2, 8);
        this.body.addBox(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f);
        this.body.setRotationPoint(0.0f, 16.5f, -3.0f);
        this.tail = new ModelRenderer(this, 22, 1);
        this.tail.addBox(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f);
        this.tail.setRotationPoint(0.0f, 21.07f, 1.16f);
        this.wingLeft = new ModelRenderer(this, 19, 8);
        this.wingLeft.addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f);
        this.wingLeft.setRotationPoint(1.5f, 16.94f, -2.76f);
        this.wingRight = new ModelRenderer(this, 19, 8);
        this.wingRight.addBox(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f);
        this.wingRight.setRotationPoint(-1.5f, 16.94f, -2.76f);
        this.head = new ModelRenderer(this, 2, 2);
        this.head.addBox(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f);
        this.head.setRotationPoint(0.0f, 15.69f, -2.76f);
        this.head2 = new ModelRenderer(this, 10, 0);
        this.head2.addBox(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f);
        this.head2.setRotationPoint(0.0f, -2.0f, -1.0f);
        this.head.addChild(this.head2);
        this.beak1 = new ModelRenderer(this, 11, 7);
        this.beak1.addBox(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.beak1.setRotationPoint(0.0f, -0.5f, -1.5f);
        this.head.addChild(this.beak1);
        this.beak2 = new ModelRenderer(this, 16, 7);
        this.beak2.addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.beak2.setRotationPoint(0.0f, -1.75f, -2.45f);
        this.head.addChild(this.beak2);
        this.feather = new ModelRenderer(this, 2, 18);
        this.feather.addBox(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f);
        this.feather.setRotationPoint(0.0f, -2.15f, 0.15f);
        this.head.addChild(this.feather);
        this.legLeft = new ModelRenderer(this, 14, 18);
        this.legLeft.addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.legLeft.setRotationPoint(1.0f, 22.0f, -1.05f);
        this.legRight = new ModelRenderer(this, 14, 18);
        this.legRight.addBox(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.legRight.setRotationPoint(-1.0f, 22.0f, -1.05f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.body, this.wingLeft, this.wingRight, this.tail, this.head, this.legLeft, this.legRight);
    }

    @Override
    public void setRotationAngles(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setRotationAngles(ParrotModel.getParrotState(entityIn), entityIn.ticksExisted, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void setLivingAnimations(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.setLivingAnimations(ParrotModel.getParrotState(entityIn));
    }

    public void renderOnShoulder(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float p_228284_5_, float p_228284_6_, float p_228284_7_, float p_228284_8_, int p_228284_9_) {
        this.setLivingAnimations(State.ON_SHOULDER);
        this.setRotationAngles(State.ON_SHOULDER, p_228284_9_, p_228284_5_, p_228284_6_, 0.0f, p_228284_7_, p_228284_8_);
        this.getParts().forEach(p_228285_4_ -> p_228285_4_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn));
    }

    private void setRotationAngles(State p_217162_1_, int p_217162_2_, float p_217162_3_, float p_217162_4_, float p_217162_5_, float p_217162_6_, float p_217162_7_) {
        this.head.rotateAngleX = p_217162_7_ * ((float)Math.PI / 180);
        this.head.rotateAngleY = p_217162_6_ * ((float)Math.PI / 180);
        this.head.rotateAngleZ = 0.0f;
        this.head.rotationPointX = 0.0f;
        this.body.rotationPointX = 0.0f;
        this.tail.rotationPointX = 0.0f;
        this.wingRight.rotationPointX = -1.5f;
        this.wingLeft.rotationPointX = 1.5f;
        switch (p_217162_1_) {
            case SITTING: {
                break;
            }
            case PARTY: {
                float f = MathHelper.cos(p_217162_2_);
                float f1 = MathHelper.sin(p_217162_2_);
                this.head.rotationPointX = f;
                this.head.rotationPointY = 15.69f + f1;
                this.head.rotateAngleX = 0.0f;
                this.head.rotateAngleY = 0.0f;
                this.head.rotateAngleZ = MathHelper.sin(p_217162_2_) * 0.4f;
                this.body.rotationPointX = f;
                this.body.rotationPointY = 16.5f + f1;
                this.wingLeft.rotateAngleZ = -0.0873f - p_217162_5_;
                this.wingLeft.rotationPointX = 1.5f + f;
                this.wingLeft.rotationPointY = 16.94f + f1;
                this.wingRight.rotateAngleZ = 0.0873f + p_217162_5_;
                this.wingRight.rotationPointX = -1.5f + f;
                this.wingRight.rotationPointY = 16.94f + f1;
                this.tail.rotationPointX = f;
                this.tail.rotationPointY = 21.07f + f1;
                break;
            }
            case STANDING: {
                this.legLeft.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662f) * 1.4f * p_217162_4_;
                this.legRight.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662f + (float)Math.PI) * 1.4f * p_217162_4_;
            }
            default: {
                float f2 = p_217162_5_ * 0.3f;
                this.head.rotationPointY = 15.69f + f2;
                this.tail.rotateAngleX = 1.015f + MathHelper.cos(p_217162_3_ * 0.6662f) * 0.3f * p_217162_4_;
                this.tail.rotationPointY = 21.07f + f2;
                this.body.rotationPointY = 16.5f + f2;
                this.wingLeft.rotateAngleZ = -0.0873f - p_217162_5_;
                this.wingLeft.rotationPointY = 16.94f + f2;
                this.wingRight.rotateAngleZ = 0.0873f + p_217162_5_;
                this.wingRight.rotationPointY = 16.94f + f2;
                this.legLeft.rotationPointY = 22.0f + f2;
                this.legRight.rotationPointY = 22.0f + f2;
            }
        }
    }

    private void setLivingAnimations(State p_217160_1_) {
        this.feather.rotateAngleX = -0.2214f;
        this.body.rotateAngleX = 0.4937f;
        this.wingLeft.rotateAngleX = -0.69813174f;
        this.wingLeft.rotateAngleY = (float)(-Math.PI);
        this.wingRight.rotateAngleX = -0.69813174f;
        this.wingRight.rotateAngleY = (float)(-Math.PI);
        this.legLeft.rotateAngleX = -0.0299f;
        this.legRight.rotateAngleX = -0.0299f;
        this.legLeft.rotationPointY = 22.0f;
        this.legRight.rotationPointY = 22.0f;
        this.legLeft.rotateAngleZ = 0.0f;
        this.legRight.rotateAngleZ = 0.0f;
        switch (p_217160_1_) {
            case SITTING: {
                float f = 1.9f;
                this.head.rotationPointY = 17.59f;
                this.tail.rotateAngleX = 1.5388988f;
                this.tail.rotationPointY = 22.97f;
                this.body.rotationPointY = 18.4f;
                this.wingLeft.rotateAngleZ = -0.0873f;
                this.wingLeft.rotationPointY = 18.84f;
                this.wingRight.rotateAngleZ = 0.0873f;
                this.wingRight.rotationPointY = 18.84f;
                this.legLeft.rotationPointY += 1.0f;
                this.legRight.rotationPointY += 1.0f;
                this.legLeft.rotateAngleX += 1.0f;
                this.legRight.rotateAngleX += 1.0f;
                break;
            }
            case PARTY: {
                this.legLeft.rotateAngleZ = -0.34906584f;
                this.legRight.rotateAngleZ = 0.34906584f;
            }
            default: {
                break;
            }
            case FLYING: {
                this.legLeft.rotateAngleX += 0.69813174f;
                this.legRight.rotateAngleX += 0.69813174f;
            }
        }
    }

    private static State getParrotState(ParrotEntity p_217158_0_) {
        if (p_217158_0_.isPartying()) {
            return State.PARTY;
        }
        if (p_217158_0_.isSleeping()) {
            return State.SITTING;
        }
        return p_217158_0_.isFlying() ? State.FLYING : State.STANDING;
    }

    public static enum State {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}
