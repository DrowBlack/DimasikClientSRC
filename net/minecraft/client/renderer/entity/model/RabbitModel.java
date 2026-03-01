package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

public class RabbitModel<T extends RabbitEntity>
extends EntityModel<T> {
    private final ModelRenderer rabbitLeftFoot = new ModelRenderer(this, 26, 24);
    private final ModelRenderer rabbitRightFoot;
    private final ModelRenderer rabbitLeftThigh;
    private final ModelRenderer rabbitRightThigh;
    private final ModelRenderer rabbitBody;
    private final ModelRenderer rabbitLeftArm;
    private final ModelRenderer rabbitRightArm;
    private final ModelRenderer rabbitHead;
    private final ModelRenderer rabbitRightEar;
    private final ModelRenderer rabbitLeftEar;
    private final ModelRenderer rabbitTail;
    private final ModelRenderer rabbitNose;
    private float jumpRotation;

    public RabbitModel() {
        this.rabbitLeftFoot.addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.rabbitLeftFoot.setRotationPoint(3.0f, 17.5f, 3.7f);
        this.rabbitLeftFoot.mirror = true;
        this.setRotationOffset(this.rabbitLeftFoot, 0.0f, 0.0f, 0.0f);
        this.rabbitRightFoot = new ModelRenderer(this, 8, 24);
        this.rabbitRightFoot.addBox(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.rabbitRightFoot.setRotationPoint(-3.0f, 17.5f, 3.7f);
        this.rabbitRightFoot.mirror = true;
        this.setRotationOffset(this.rabbitRightFoot, 0.0f, 0.0f, 0.0f);
        this.rabbitLeftThigh = new ModelRenderer(this, 30, 15);
        this.rabbitLeftThigh.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.rabbitLeftThigh.setRotationPoint(3.0f, 17.5f, 3.7f);
        this.rabbitLeftThigh.mirror = true;
        this.setRotationOffset(this.rabbitLeftThigh, -0.34906584f, 0.0f, 0.0f);
        this.rabbitRightThigh = new ModelRenderer(this, 16, 15);
        this.rabbitRightThigh.addBox(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.rabbitRightThigh.setRotationPoint(-3.0f, 17.5f, 3.7f);
        this.rabbitRightThigh.mirror = true;
        this.setRotationOffset(this.rabbitRightThigh, -0.34906584f, 0.0f, 0.0f);
        this.rabbitBody = new ModelRenderer(this, 0, 0);
        this.rabbitBody.addBox(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f);
        this.rabbitBody.setRotationPoint(0.0f, 19.0f, 8.0f);
        this.rabbitBody.mirror = true;
        this.setRotationOffset(this.rabbitBody, -0.34906584f, 0.0f, 0.0f);
        this.rabbitLeftArm = new ModelRenderer(this, 8, 15);
        this.rabbitLeftArm.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.rabbitLeftArm.setRotationPoint(3.0f, 17.0f, -1.0f);
        this.rabbitLeftArm.mirror = true;
        this.setRotationOffset(this.rabbitLeftArm, -0.17453292f, 0.0f, 0.0f);
        this.rabbitRightArm = new ModelRenderer(this, 0, 15);
        this.rabbitRightArm.addBox(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.rabbitRightArm.setRotationPoint(-3.0f, 17.0f, -1.0f);
        this.rabbitRightArm.mirror = true;
        this.setRotationOffset(this.rabbitRightArm, -0.17453292f, 0.0f, 0.0f);
        this.rabbitHead = new ModelRenderer(this, 32, 0);
        this.rabbitHead.addBox(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f);
        this.rabbitHead.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.rabbitHead.mirror = true;
        this.setRotationOffset(this.rabbitHead, 0.0f, 0.0f, 0.0f);
        this.rabbitRightEar = new ModelRenderer(this, 52, 0);
        this.rabbitRightEar.addBox(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.rabbitRightEar.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.rabbitRightEar.mirror = true;
        this.setRotationOffset(this.rabbitRightEar, 0.0f, -0.2617994f, 0.0f);
        this.rabbitLeftEar = new ModelRenderer(this, 58, 0);
        this.rabbitLeftEar.addBox(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.rabbitLeftEar.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.rabbitLeftEar.mirror = true;
        this.setRotationOffset(this.rabbitLeftEar, 0.0f, 0.2617994f, 0.0f);
        this.rabbitTail = new ModelRenderer(this, 52, 6);
        this.rabbitTail.addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f);
        this.rabbitTail.setRotationPoint(0.0f, 20.0f, 7.0f);
        this.rabbitTail.mirror = true;
        this.setRotationOffset(this.rabbitTail, -0.3490659f, 0.0f, 0.0f);
        this.rabbitNose = new ModelRenderer(this, 32, 9);
        this.rabbitNose.addBox(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f);
        this.rabbitNose.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.rabbitNose.mirror = true;
        this.setRotationOffset(this.rabbitNose, 0.0f, 0.0f, 0.0f);
    }

    private void setRotationOffset(ModelRenderer renderer, float x, float y, float z) {
        renderer.rotateAngleX = x;
        renderer.rotateAngleY = y;
        renderer.rotateAngleZ = z;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            float f = 1.5f;
            matrixStackIn.push();
            matrixStackIn.scale(0.56666666f, 0.56666666f, 0.56666666f);
            matrixStackIn.translate(0.0, 1.375, 0.125);
            ImmutableList.of(this.rabbitHead, this.rabbitLeftEar, this.rabbitRightEar, this.rabbitNose).forEach(p_228292_8_ -> p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.scale(0.4f, 0.4f, 0.4f);
            matrixStackIn.translate(0.0, 2.25, 0.0);
            ImmutableList.of(this.rabbitLeftFoot, this.rabbitRightFoot, this.rabbitLeftThigh, this.rabbitRightThigh, this.rabbitBody, this.rabbitLeftArm, this.rabbitRightArm, this.rabbitTail).forEach(p_228291_8_ -> p_228291_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.pop();
        } else {
            matrixStackIn.push();
            matrixStackIn.scale(0.6f, 0.6f, 0.6f);
            matrixStackIn.translate(0.0, 1.0, 0.0);
            ImmutableList.of(this.rabbitLeftFoot, this.rabbitRightFoot, this.rabbitLeftThigh, this.rabbitRightThigh, this.rabbitBody, this.rabbitLeftArm, this.rabbitRightArm, this.rabbitHead, this.rabbitRightEar, this.rabbitLeftEar, this.rabbitTail, this.rabbitNose, new ModelRenderer[0]).forEach(p_228290_8_ -> p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.pop();
        }
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = ageInTicks - (float)((RabbitEntity)entityIn).ticksExisted;
        this.rabbitNose.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.rabbitHead.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.rabbitRightEar.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.rabbitLeftEar.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.rabbitNose.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.rabbitHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.rabbitRightEar.rotateAngleY = this.rabbitNose.rotateAngleY - 0.2617994f;
        this.rabbitLeftEar.rotateAngleY = this.rabbitNose.rotateAngleY + 0.2617994f;
        this.jumpRotation = MathHelper.sin(((RabbitEntity)entityIn).getJumpCompletion(f) * (float)Math.PI);
        this.rabbitLeftThigh.rotateAngleX = (this.jumpRotation * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.rabbitRightThigh.rotateAngleX = (this.jumpRotation * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.rabbitLeftFoot.rotateAngleX = this.jumpRotation * 50.0f * ((float)Math.PI / 180);
        this.rabbitRightFoot.rotateAngleX = this.jumpRotation * 50.0f * ((float)Math.PI / 180);
        this.rabbitLeftArm.rotateAngleX = (this.jumpRotation * -40.0f - 11.0f) * ((float)Math.PI / 180);
        this.rabbitRightArm.rotateAngleX = (this.jumpRotation * -40.0f - 11.0f) * ((float)Math.PI / 180);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.jumpRotation = MathHelper.sin(((RabbitEntity)entityIn).getJumpCompletion(partialTick) * (float)Math.PI);
    }
}
