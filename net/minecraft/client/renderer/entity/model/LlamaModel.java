package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.util.math.MathHelper;

public class LlamaModel<T extends AbstractChestedHorseEntity>
extends EntityModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer legBackRight;
    private final ModelRenderer legBackLeft;
    private final ModelRenderer legFrontRight;
    private final ModelRenderer legFrontLeft;
    private final ModelRenderer chest1;
    private final ModelRenderer chest2;

    public LlamaModel(float p_i47226_1_) {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, p_i47226_1_);
        this.head.setRotationPoint(0.0f, 7.0f, -6.0f);
        this.head.setTextureOffset(0, 14).addBox(-4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, p_i47226_1_);
        this.head.setTextureOffset(17, 0).addBox(-4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, p_i47226_1_);
        this.head.setTextureOffset(17, 0).addBox(1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, p_i47226_1_);
        this.body = new ModelRenderer(this, 29, 0);
        this.body.addBox(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, p_i47226_1_);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        this.chest1 = new ModelRenderer(this, 45, 28);
        this.chest1.addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, p_i47226_1_);
        this.chest1.setRotationPoint(-8.5f, 3.0f, 3.0f);
        this.chest1.rotateAngleY = 1.5707964f;
        this.chest2 = new ModelRenderer(this, 45, 41);
        this.chest2.addBox(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, p_i47226_1_);
        this.chest2.setRotationPoint(5.5f, 3.0f, 3.0f);
        this.chest2.rotateAngleY = 1.5707964f;
        int i = 4;
        int j = 14;
        this.legBackRight = new ModelRenderer(this, 29, 29);
        this.legBackRight.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, p_i47226_1_);
        this.legBackRight.setRotationPoint(-2.5f, 10.0f, 6.0f);
        this.legBackLeft = new ModelRenderer(this, 29, 29);
        this.legBackLeft.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, p_i47226_1_);
        this.legBackLeft.setRotationPoint(2.5f, 10.0f, 6.0f);
        this.legFrontRight = new ModelRenderer(this, 29, 29);
        this.legFrontRight.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, p_i47226_1_);
        this.legFrontRight.setRotationPoint(-2.5f, 10.0f, -4.0f);
        this.legFrontLeft = new ModelRenderer(this, 29, 29);
        this.legFrontLeft.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, p_i47226_1_);
        this.legFrontLeft.setRotationPoint(2.5f, 10.0f, -4.0f);
        this.legBackRight.rotationPointX -= 1.0f;
        this.legBackLeft.rotationPointX += 1.0f;
        this.legBackRight.rotationPointZ += 0.0f;
        this.legBackLeft.rotationPointZ += 0.0f;
        this.legFrontRight.rotationPointX -= 1.0f;
        this.legFrontLeft.rotationPointX += 1.0f;
        this.legFrontRight.rotationPointZ -= 1.0f;
        this.legFrontLeft.rotationPointZ -= 1.0f;
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag;
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.body.rotateAngleX = 1.5707964f;
        this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.chest1.showModel = flag = !((AgeableEntity)entityIn).isChild() && ((AbstractChestedHorseEntity)entityIn).hasChest();
        this.chest2.showModel = flag;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.isChild) {
            float f = 2.0f;
            matrixStackIn.push();
            float f1 = 0.7f;
            matrixStackIn.scale(0.71428573f, 0.64935064f, 0.7936508f);
            matrixStackIn.translate(0.0, 1.3125, 0.22f);
            this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStackIn.pop();
            matrixStackIn.push();
            float f2 = 1.1f;
            matrixStackIn.scale(0.625f, 0.45454544f, 0.45454544f);
            matrixStackIn.translate(0.0, 2.0625, 0.0);
            this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrixStackIn.translate(0.0, 2.0625, 0.0);
            ImmutableList.of(this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.chest1, this.chest2).forEach(p_228280_8_ -> p_228280_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
            matrixStackIn.pop();
        } else {
            ImmutableList.of(this.head, this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.chest1, this.chest2).forEach(p_228279_8_ -> p_228279_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha));
        }
    }
}
