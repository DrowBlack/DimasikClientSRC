package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public abstract class ArrowRenderer<T extends AbstractArrowEntity>
extends EntityRenderer<T> {
    public ArrowRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, ((AbstractArrowEntity)entityIn).prevRotationYaw, ((AbstractArrowEntity)entityIn).rotationYaw) - 90.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, ((AbstractArrowEntity)entityIn).prevRotationPitch, ((AbstractArrowEntity)entityIn).rotationPitch)));
        boolean i = false;
        float f = 0.0f;
        float f1 = 0.5f;
        float f2 = 0.0f;
        float f3 = 0.15625f;
        float f4 = 0.0f;
        float f5 = 0.15625f;
        float f6 = 0.15625f;
        float f7 = 0.3125f;
        float f8 = 0.05625f;
        float f9 = (float)((AbstractArrowEntity)entityIn).arrowShake - partialTicks;
        if (f9 > 0.0f) {
            float f10 = -MathHelper.sin(f9 * 3.0f) * f9;
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f10));
        }
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(45.0f));
        matrixStackIn.scale(0.05625f, 0.05625f, 0.05625f);
        matrixStackIn.translate(-4.0, 0.0, 0.0);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(this.getEntityTexture(entityIn)));
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0f, 0.15625f, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0f, 0.3125f, -1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0f, 0.15625f, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, packedLightIn);
        this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0f, 0.3125f, 1, 0, 0, packedLightIn);
        for (int j = 0; j < 4; ++j) {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, packedLightIn);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, packedLightIn);
        }
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, IVertexBuilder vertexBuilder, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int p_229039_9_, int p_229039_10_, int p_229039_11_, int packedLightIn) {
        vertexBuilder.pos(matrix, offsetX, offsetY, offsetZ).color(255, 255, 255, 255).tex(textureX, textureY).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(normals, p_229039_9_, p_229039_11_, p_229039_10_).endVertex();
    }
}
