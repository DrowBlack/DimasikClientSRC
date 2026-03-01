package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class BipedModel<T extends LivingEntity>
extends AgeableModel<T>
implements IHasArm,
IHasHead {
    public ModelRenderer bipedHead;
    public ModelRenderer bipedHeadwear;
    public ModelRenderer bipedBody;
    public ModelRenderer bipedRightArm;
    public ModelRenderer bipedLeftArm;
    public ModelRenderer bipedRightLeg;
    public ModelRenderer bipedLeftLeg;
    public ArmPose leftArmPose = ArmPose.EMPTY;
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public boolean isSneak;
    public float swimAnimation;

    public BipedModel(float modelSize) {
        this(RenderType::getEntityCutoutNoCull, modelSize, 0.0f, 64, 32);
    }

    protected BipedModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
        this(RenderType::getEntityCutoutNoCull, modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
    }

    public BipedModel(Function<ResourceLocation, RenderType> renderTypeIn, float modelSizeIn, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
        super(renderTypeIn, true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f);
        this.textureWidth = textureWidthIn;
        this.textureHeight = textureHeightIn;
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, modelSizeIn);
        this.bipedHead.setRotationPoint(0.0f, 0.0f + yOffsetIn, 0.0f);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, modelSizeIn + 0.5f);
        this.bipedHeadwear.setRotationPoint(0.0f, 0.0f + yOffsetIn, 0.0f);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, modelSizeIn);
        this.bipedBody.setRotationPoint(0.0f, 0.0f + yOffsetIn, 0.0f);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, modelSizeIn);
        this.bipedRightArm.setRotationPoint(-5.0f, 2.0f + yOffsetIn, 0.0f);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, modelSizeIn);
        this.bipedLeftArm.setRotationPoint(5.0f, 2.0f + yOffsetIn, 0.0f);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, modelSizeIn);
        this.bipedRightLeg.setRotationPoint(-1.9f, 12.0f + yOffsetIn, 0.0f);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, modelSizeIn);
        this.bipedLeftLeg.setRotationPoint(1.9f, 12.0f + yOffsetIn, 0.0f);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.bipedHead);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg, this.bipedHeadwear);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.swimAnimation = ((LivingEntity)entityIn).getSwimAnimation(partialTick);
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag3;
        boolean flag = ((LivingEntity)entityIn).getTicksElytraFlying() > 4;
        boolean flag1 = ((LivingEntity)entityIn).isActualySwimming();
        this.bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.bipedHead.rotateAngleX = flag ? -0.7853982f : (this.swimAnimation > 0.0f ? (flag1 ? this.rotLerpRad(this.swimAnimation, this.bipedHead.rotateAngleX, -0.7853982f) : this.rotLerpRad(this.swimAnimation, this.bipedHead.rotateAngleX, headPitch * ((float)Math.PI / 180))) : headPitch * ((float)Math.PI / 180));
        this.bipedBody.rotateAngleY = 0.0f;
        this.bipedRightArm.rotationPointZ = 0.0f;
        this.bipedRightArm.rotationPointX = -5.0f;
        this.bipedLeftArm.rotationPointZ = 0.0f;
        this.bipedLeftArm.rotationPointX = 5.0f;
        float f = 1.0f;
        if (flag) {
            f = (float)((Entity)entityIn).getMotion().lengthSquared();
            f /= 0.2f;
            f = f * f * f;
        }
        if (f < 1.0f) {
            f = 1.0f;
        }
        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 2.0f * limbSwingAmount * 0.5f / f;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.5f / f;
        this.bipedRightArm.rotateAngleZ = 0.0f;
        this.bipedLeftArm.rotateAngleZ = 0.0f;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount / f;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount / f;
        this.bipedRightLeg.rotateAngleY = 0.0f;
        this.bipedLeftLeg.rotateAngleY = 0.0f;
        this.bipedRightLeg.rotateAngleZ = 0.0f;
        this.bipedLeftLeg.rotateAngleZ = 0.0f;
        if (this.isSitting) {
            this.bipedRightArm.rotateAngleX += -0.62831855f;
            this.bipedLeftArm.rotateAngleX += -0.62831855f;
            this.bipedRightLeg.rotateAngleX = -1.4137167f;
            this.bipedRightLeg.rotateAngleY = 0.31415927f;
            this.bipedRightLeg.rotateAngleZ = 0.07853982f;
            this.bipedLeftLeg.rotateAngleX = -1.4137167f;
            this.bipedLeftLeg.rotateAngleY = -0.31415927f;
            this.bipedLeftLeg.rotateAngleZ = -0.07853982f;
        }
        this.bipedRightArm.rotateAngleY = 0.0f;
        this.bipedLeftArm.rotateAngleY = 0.0f;
        boolean flag2 = ((LivingEntity)entityIn).getPrimaryHand() == HandSide.RIGHT;
        boolean bl = flag3 = flag2 ? this.leftArmPose.func_241657_a_() : this.rightArmPose.func_241657_a_();
        if (flag2 != flag3) {
            this.func_241655_c_(entityIn);
            this.func_241654_b_(entityIn);
        } else {
            this.func_241654_b_(entityIn);
            this.func_241655_c_(entityIn);
        }
        this.func_230486_a_(entityIn, ageInTicks);
        if (this.isSneak) {
            this.bipedBody.rotateAngleX = 0.5f;
            this.bipedRightArm.rotateAngleX += 0.4f;
            this.bipedLeftArm.rotateAngleX += 0.4f;
            this.bipedRightLeg.rotationPointZ = 4.0f;
            this.bipedLeftLeg.rotationPointZ = 4.0f;
            this.bipedRightLeg.rotationPointY = 12.2f;
            this.bipedLeftLeg.rotationPointY = 12.2f;
            this.bipedHead.rotationPointY = 4.2f;
            this.bipedBody.rotationPointY = 3.2f;
            this.bipedLeftArm.rotationPointY = 5.2f;
            this.bipedRightArm.rotationPointY = 5.2f;
        } else {
            this.bipedBody.rotateAngleX = 0.0f;
            this.bipedRightLeg.rotationPointZ = 0.1f;
            this.bipedLeftLeg.rotationPointZ = 0.1f;
            this.bipedRightLeg.rotationPointY = 12.0f;
            this.bipedLeftLeg.rotationPointY = 12.0f;
            this.bipedHead.rotationPointY = 0.0f;
            this.bipedBody.rotationPointY = 0.0f;
            this.bipedLeftArm.rotationPointY = 2.0f;
            this.bipedRightArm.rotationPointY = 2.0f;
        }
        ModelHelper.func_239101_a_(this.bipedRightArm, this.bipedLeftArm, ageInTicks);
        if (this.swimAnimation > 0.0f) {
            float f3;
            float f1 = limbSwing % 26.0f;
            HandSide handside = this.getMainHand(entityIn);
            float f2 = handside == HandSide.RIGHT && this.swingProgress > 0.0f ? 0.0f : this.swimAnimation;
            float f4 = f3 = handside == HandSide.LEFT && this.swingProgress > 0.0f ? 0.0f : this.swimAnimation;
            if (f1 < 14.0f) {
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleX, 0.0f);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleX, 0.0f);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleY, (float)Math.PI);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleZ, (float)Math.PI + 1.8707964f * this.getArmAngleSq(f1) / this.getArmAngleSq(14.0f));
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleZ, (float)Math.PI - 1.8707964f * this.getArmAngleSq(f1) / this.getArmAngleSq(14.0f));
            } else if (f1 >= 14.0f && f1 < 22.0f) {
                float f6 = (f1 - 14.0f) / 8.0f;
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleX, 1.5707964f * f6);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleX, 1.5707964f * f6);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleY, (float)Math.PI);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleZ, 5.012389f - 1.8707964f * f6);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleZ, 1.2707963f + 1.8707964f * f6);
            } else if (f1 >= 22.0f && f1 < 26.0f) {
                float f42 = (f1 - 22.0f) / 4.0f;
                this.bipedLeftArm.rotateAngleX = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleX, 1.5707964f - 1.5707964f * f42);
                this.bipedRightArm.rotateAngleX = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleX, 1.5707964f - 1.5707964f * f42);
                this.bipedLeftArm.rotateAngleY = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleY, (float)Math.PI);
                this.bipedRightArm.rotateAngleY = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleY, (float)Math.PI);
                this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(f3, this.bipedLeftArm.rotateAngleZ, (float)Math.PI);
                this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f2, this.bipedRightArm.rotateAngleZ, (float)Math.PI);
            }
            float f7 = 0.3f;
            float f5 = 0.33333334f;
            this.bipedLeftLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedLeftLeg.rotateAngleX, 0.3f * MathHelper.cos(limbSwing * 0.33333334f + (float)Math.PI));
            this.bipedRightLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedRightLeg.rotateAngleX, 0.3f * MathHelper.cos(limbSwing * 0.33333334f));
        }
        this.bipedHeadwear.copyModelAngles(this.bipedHead);
    }

    private void func_241654_b_(T p_241654_1_) {
        switch (this.rightArmPose) {
            case EMPTY: {
                this.bipedRightArm.rotateAngleY = 0.0f;
                break;
            }
            case BLOCK: {
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - 0.9424779f;
                this.bipedRightArm.rotateAngleY = -0.5235988f;
                break;
            }
            case ITEM: {
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - 0.31415927f;
                this.bipedRightArm.rotateAngleY = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5f - (float)Math.PI;
                this.bipedRightArm.rotateAngleY = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.bipedRightArm.rotateAngleY = -0.1f + this.bipedHead.rotateAngleY;
                this.bipedLeftArm.rotateAngleY = 0.1f + this.bipedHead.rotateAngleY + 0.4f;
                this.bipedRightArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                this.bipedLeftArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                break;
            }
            case CROSSBOW_CHARGE: {
                ModelHelper.func_239102_a_(this.bipedRightArm, this.bipedLeftArm, p_241654_1_, true);
                break;
            }
            case CROSSBOW_HOLD: {
                ModelHelper.func_239104_a_(this.bipedRightArm, this.bipedLeftArm, this.bipedHead, true);
            }
        }
    }

    private void func_241655_c_(T p_241655_1_) {
        switch (this.leftArmPose) {
            case EMPTY: {
                this.bipedLeftArm.rotateAngleY = 0.0f;
                break;
            }
            case BLOCK: {
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - 0.9424779f;
                this.bipedLeftArm.rotateAngleY = 0.5235988f;
                break;
            }
            case ITEM: {
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - 0.31415927f;
                this.bipedLeftArm.rotateAngleY = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5f - (float)Math.PI;
                this.bipedLeftArm.rotateAngleY = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.bipedRightArm.rotateAngleY = -0.1f + this.bipedHead.rotateAngleY - 0.4f;
                this.bipedLeftArm.rotateAngleY = 0.1f + this.bipedHead.rotateAngleY;
                this.bipedRightArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                this.bipedLeftArm.rotateAngleX = -1.5707964f + this.bipedHead.rotateAngleX;
                break;
            }
            case CROSSBOW_CHARGE: {
                ModelHelper.func_239102_a_(this.bipedRightArm, this.bipedLeftArm, p_241655_1_, false);
                break;
            }
            case CROSSBOW_HOLD: {
                ModelHelper.func_239104_a_(this.bipedRightArm, this.bipedLeftArm, this.bipedHead, false);
            }
        }
    }

    protected void func_230486_a_(T p_230486_1_, float p_230486_2_) {
        if (!(this.swingProgress <= 0.0f)) {
            HandSide handside = this.getMainHand(p_230486_1_);
            ModelRenderer modelrenderer = this.getArmForSide(handside);
            float f = this.swingProgress;
            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2)) * 0.2f;
            if (handside == HandSide.LEFT) {
                this.bipedBody.rotateAngleY *= -1.0f;
            }
            this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0f;
            this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0f;
            this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0f;
            this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0f;
            this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
            this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;
            f = 1.0f - this.swingProgress;
            f *= f;
            f *= f;
            f = 1.0f - f;
            float f1 = MathHelper.sin(f * (float)Math.PI);
            float f2 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7f) * 0.75f;
            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f1 * 1.2 + (double)f2));
            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0f;
            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4f;
        }
    }

    protected float rotLerpRad(float angleIn, float maxAngleIn, float mulIn) {
        float f = (mulIn - maxAngleIn) % ((float)Math.PI * 2);
        if (f < (float)(-Math.PI)) {
            f += (float)Math.PI * 2;
        }
        if (f >= (float)Math.PI) {
            f -= (float)Math.PI * 2;
        }
        return maxAngleIn + angleIn * f;
    }

    private float getArmAngleSq(float limbSwing) {
        return -65.0f * limbSwing + limbSwing * limbSwing;
    }

    public void setModelAttributes(BipedModel<T> modelIn) {
        super.copyModelAttributesTo(modelIn);
        modelIn.leftArmPose = this.leftArmPose;
        modelIn.rightArmPose = this.rightArmPose;
        modelIn.isSneak = this.isSneak;
        modelIn.bipedHead.copyModelAngles(this.bipedHead);
        modelIn.bipedHeadwear.copyModelAngles(this.bipedHeadwear);
        modelIn.bipedBody.copyModelAngles(this.bipedBody);
        modelIn.bipedRightArm.copyModelAngles(this.bipedRightArm);
        modelIn.bipedLeftArm.copyModelAngles(this.bipedLeftArm);
        modelIn.bipedRightLeg.copyModelAngles(this.bipedRightLeg);
        modelIn.bipedLeftLeg.copyModelAngles(this.bipedLeftLeg);
    }

    public void setVisible(boolean visible) {
        this.bipedHead.showModel = visible;
        this.bipedHeadwear.showModel = visible;
        this.bipedBody.showModel = visible;
        this.bipedRightArm.showModel = visible;
        this.bipedLeftArm.showModel = visible;
        this.bipedRightLeg.showModel = visible;
        this.bipedLeftLeg.showModel = visible;
    }

    @Override
    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        this.getArmForSide(sideIn).translateRotate(matrixStackIn);
    }

    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }

    @Override
    public ModelRenderer getModelHead() {
        return this.bipedHead;
    }

    protected HandSide getMainHand(T entityIn) {
        HandSide handside = ((LivingEntity)entityIn).getPrimaryHand();
        return ((LivingEntity)entityIn).swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }

    public static enum ArmPose {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true);

        private final boolean field_241656_h_;

        private ArmPose(boolean p_i241257_3_) {
            this.field_241656_h_ = p_i241257_3_;
        }

        public boolean func_241657_a_() {
            return this.field_241656_h_;
        }
    }
}
