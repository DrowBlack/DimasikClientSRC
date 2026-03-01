package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public abstract class LayerRenderer<T extends Entity, M extends EntityModel<T>> {
    private final IEntityRenderer<T, M> entityRenderer;

    public LayerRenderer(IEntityRenderer<T, M> entityRendererIn) {
        this.entityRenderer = entityRendererIn;
    }

    protected static <T extends LivingEntity> void renderCopyCutoutModel(EntityModel<T> modelParentIn, EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue) {
        if (!entityIn.isInvisible()) {
            modelParentIn.copyModelAttributesTo(modelIn);
            modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            LayerRenderer.renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
        }
    }

    protected static <T extends LivingEntity> void renderCutoutModel(EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float red, float green, float blue) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(textureLocationIn));
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0f), red, green, blue, 1.0f);
    }

    public M getEntityModel() {
        return this.entityRenderer.getEntityModel();
    }

    protected ResourceLocation getEntityTexture(T entityIn) {
        return this.entityRenderer.getEntityTexture(entityIn);
    }

    public abstract void render(MatrixStack var1, IRenderTypeBuffer var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}
