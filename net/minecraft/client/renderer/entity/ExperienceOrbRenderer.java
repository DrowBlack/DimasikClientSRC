package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.CustomColors;

public class ExperienceOrbRenderer
extends EntityRenderer<ExperienceOrbEntity> {
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.getItemEntityTranslucentCull(EXPERIENCE_ORB_TEXTURES);

    public ExperienceOrbRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    @Override
    protected int getBlockLight(ExperienceOrbEntity entityIn, BlockPos partialTicks) {
        return MathHelper.clamp(super.getBlockLight(entityIn, partialTicks) + 7, 0, 15);
    }

    @Override
    public void render(ExperienceOrbEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int l1;
        matrixStackIn.push();
        int i = entityIn.getTextureByXP();
        float f = (float)(i % 4 * 16 + 0) / 64.0f;
        float f1 = (float)(i % 4 * 16 + 16) / 64.0f;
        float f2 = (float)(i / 4 * 16 + 0) / 64.0f;
        float f3 = (float)(i / 4 * 16 + 16) / 64.0f;
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;
        float f7 = 255.0f;
        float f8 = ((float)entityIn.xpColor + partialTicks) / 2.0f;
        if (Config.isCustomColors()) {
            f8 = CustomColors.getXpOrbTimer(f8);
        }
        int j = (int)((MathHelper.sin(f8 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int k = 255;
        int l = (int)((MathHelper.sin(f8 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        matrixStackIn.translate(0.0, 0.1f, 0.0);
        matrixStackIn.rotate(this.renderManager.getCameraOrientation());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
        float f9 = 0.3f;
        matrixStackIn.scale(0.3f, 0.3f, 0.3f);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        int i1 = j;
        int j1 = 255;
        int k1 = l;
        if (Config.isCustomColors() && (l1 = CustomColors.getXpOrbColor(f8)) >= 0) {
            i1 = l1 >> 16 & 0xFF;
            j1 = l1 >> 8 & 0xFF;
            k1 = l1 >> 0 & 0xFF;
        }
        ExperienceOrbRenderer.vertex(ivertexbuilder, matrix4f, matrix3f, -0.5f, -0.25f, i1, j1, k1, f, f3, packedLightIn);
        ExperienceOrbRenderer.vertex(ivertexbuilder, matrix4f, matrix3f, 0.5f, -0.25f, i1, j1, k1, f1, f3, packedLightIn);
        ExperienceOrbRenderer.vertex(ivertexbuilder, matrix4f, matrix3f, 0.5f, 0.75f, i1, j1, k1, f1, f2, packedLightIn);
        ExperienceOrbRenderer.vertex(ivertexbuilder, matrix4f, matrix3f, -0.5f, 0.75f, i1, j1, k1, f, f2, packedLightIn);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void vertex(IVertexBuilder bufferIn, Matrix4f matrixIn, Matrix3f matrixNormalIn, float x, float y, int red, int green, int blue, float texU, float texV, int packedLight) {
        bufferIn.pos(matrixIn, x, y, 0.0f).color(red, green, blue, 128).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormalIn, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getEntityTexture(ExperienceOrbEntity entity) {
        return EXPERIENCE_ORB_TEXTURES;
    }
}
