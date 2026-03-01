package net.minecraft.client.renderer.tileentity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DragonHeadModel
extends GenericHeadModel {
    private final ModelRenderer head;
    private final ModelRenderer jaw;

    public DragonHeadModel(float p_i46588_1_) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        float f = -16.0f;
        this.head = new ModelRenderer(this);
        this.head.addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, p_i46588_1_, 176, 44);
        this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, p_i46588_1_, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, p_i46588_1_, 0, 0);
        this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, p_i46588_1_, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, p_i46588_1_, 0, 0);
        this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, p_i46588_1_, 112, 0);
        this.jaw = new ModelRenderer(this);
        this.jaw.setRotationPoint(0.0f, 4.0f, -8.0f);
        this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, p_i46588_1_, 176, 65);
        this.head.addChild(this.jaw);
    }

    @Override
    public void func_225603_a_(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
        this.jaw.rotateAngleX = (float)(Math.sin(p_225603_1_ * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.rotateAngleY = p_225603_2_ * ((float)Math.PI / 180);
        this.head.rotateAngleX = p_225603_3_ * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0, -0.374375f, 0.0);
        matrixStackIn.scale(0.75f, 0.75f, 0.75f);
        this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }
}
