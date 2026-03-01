package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class DragonFireballRenderer
extends EntityRenderer<DragonFireballEntity> {
    private static final ResourceLocation DRAGON_FIREBALL_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderType field_229044_e_ = RenderType.getEntityCutoutNoCull(DRAGON_FIREBALL_TEXTURE);

    public DragonFireballRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected int getBlockLight(DragonFireballEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public void render(DragonFireballEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.scale(2.0f, 2.0f, 2.0f);
        matrixStackIn.rotate(this.renderManager.getCameraOrientation());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(field_229044_e_);
        DragonFireballRenderer.func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0f, 0, 0, 1);
        DragonFireballRenderer.func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0f, 0, 1, 1);
        DragonFireballRenderer.func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0f, 1, 1, 0);
        DragonFireballRenderer.func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0f, 1, 0, 0);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void func_229045_a_(IVertexBuilder p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        p_229045_0_.pos(p_229045_1_, p_229045_4_ - 0.5f, (float)p_229045_5_ - 0.25f, 0.0f).color(255, 255, 255, 255).tex(p_229045_6_, p_229045_7_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229045_3_).normal(p_229045_2_, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getEntityTexture(DragonFireballEntity entity) {
        return DRAGON_FIREBALL_TEXTURE;
    }
}
