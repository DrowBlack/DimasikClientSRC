package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HumanoidHeadModel
extends GenericHeadModel {
    private final ModelRenderer head = new ModelRenderer(this, 32, 0);

    public HumanoidHeadModel() {
        super(0, 0, 64, 64);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.25f);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void func_225603_a_(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
        super.func_225603_a_(p_225603_1_, p_225603_2_, p_225603_3_);
        this.head.rotateAngleY = this.field_217105_a.rotateAngleY;
        this.head.rotateAngleX = this.field_217105_a.rotateAngleX;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
