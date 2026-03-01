package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class BookModel
extends Model {
    private final ModelRenderer coverRight = new ModelRenderer(64, 32, 0, 0).addBox(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelRenderer coverLeft = new ModelRenderer(64, 32, 16, 0).addBox(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelRenderer pagesRight;
    private final ModelRenderer pagesLeft;
    private final ModelRenderer flippingPageRight;
    private final ModelRenderer flippingPageLeft;
    private final ModelRenderer bookSpine = new ModelRenderer(64, 32, 12, 0).addBox(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f);
    private final List<ModelRenderer> bookParts;

    public BookModel() {
        super(RenderType::getEntitySolid);
        this.pagesRight = new ModelRenderer(64, 32, 0, 10).addBox(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f);
        this.pagesLeft = new ModelRenderer(64, 32, 12, 10).addBox(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f);
        this.flippingPageRight = new ModelRenderer(64, 32, 24, 10).addBox(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.flippingPageLeft = new ModelRenderer(64, 32, 24, 10).addBox(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.bookParts = ImmutableList.of(this.coverRight, this.coverLeft, this.bookSpine, this.pagesRight, this.pagesLeft, this.flippingPageRight, this.flippingPageLeft);
        this.coverRight.setRotationPoint(0.0f, 0.0f, -1.0f);
        this.coverLeft.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.bookSpine.rotateAngleY = 1.5707964f;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.renderAll(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderAll(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.bookParts.forEach(p_228248_8_ -> p_228248_8_.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
    }

    public void setBookState(float p_228247_1_, float rightPageFlipAmount, float leftPageFlipAmount, float bookOpenAmount) {
        float f = (MathHelper.sin(p_228247_1_ * 0.02f) * 0.1f + 1.25f) * bookOpenAmount;
        this.coverRight.rotateAngleY = (float)Math.PI + f;
        this.coverLeft.rotateAngleY = -f;
        this.pagesRight.rotateAngleY = f;
        this.pagesLeft.rotateAngleY = -f;
        this.flippingPageRight.rotateAngleY = f - f * 2.0f * rightPageFlipAmount;
        this.flippingPageLeft.rotateAngleY = f - f * 2.0f * leftPageFlipAmount;
        this.pagesRight.rotationPointX = MathHelper.sin(f);
        this.pagesLeft.rotationPointX = MathHelper.sin(f);
        this.flippingPageRight.rotationPointX = MathHelper.sin(f);
        this.flippingPageLeft.rotationPointX = MathHelper.sin(f);
    }
}
