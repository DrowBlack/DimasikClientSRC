package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public abstract class MobRenderer<T extends MobEntity, M extends EntityModel<T>>
extends LivingRenderer<T, M> {
    public MobRenderer(EntityRendererManager renderManagerIn, M entityModelIn, float shadowSizeIn) {
        super(renderManagerIn, entityModelIn, shadowSizeIn);
    }

    @Override
    protected boolean canRenderName(T entity) {
        return super.canRenderName(entity) && (((LivingEntity)entity).getAlwaysRenderNameTagForRender() || ((Entity)entity).hasCustomName() && entity == this.renderManager.pointedEntity);
    }

    @Override
    public boolean shouldRender(T livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        }
        Entity entity = ((MobEntity)livingEntityIn).getLeashHolder();
        return entity != null ? camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox()) : false;
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        Entity entity = ((MobEntity)entityIn).getLeashHolder();
        if (entity != null) {
            this.renderLeash(entityIn, partialTicks, matrixStackIn, bufferIn, entity);
        }
    }

    private <E extends Entity> void renderLeash(T entityLivingIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, E leashHolder) {
        if (!Config.isShaders() || !Shaders.isShadowPass) {
            matrixStackIn.push();
            Vector3d vector3d = leashHolder.getLeashPosition(partialTicks);
            double d0 = (double)(MathHelper.lerp(partialTicks, ((MobEntity)entityLivingIn).renderYawOffset, ((MobEntity)entityLivingIn).prevRenderYawOffset) * ((float)Math.PI / 180)) + 1.5707963267948966;
            Vector3d vector3d1 = ((Entity)entityLivingIn).func_241205_ce_();
            double d1 = Math.cos(d0) * vector3d1.z + Math.sin(d0) * vector3d1.x;
            double d2 = Math.sin(d0) * vector3d1.z - Math.cos(d0) * vector3d1.x;
            double d3 = MathHelper.lerp((double)partialTicks, ((MobEntity)entityLivingIn).prevPosX, ((Entity)entityLivingIn).getPosX()) + d1;
            double d4 = MathHelper.lerp((double)partialTicks, ((MobEntity)entityLivingIn).prevPosY, ((Entity)entityLivingIn).getPosY()) + vector3d1.y;
            double d5 = MathHelper.lerp((double)partialTicks, ((MobEntity)entityLivingIn).prevPosZ, ((Entity)entityLivingIn).getPosZ()) + d2;
            matrixStackIn.translate(d1, vector3d1.y, d2);
            float f = (float)(vector3d.x - d3);
            float f1 = (float)(vector3d.y - d4);
            float f2 = (float)(vector3d.z - d5);
            float f3 = 0.025f;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLeash());
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            float f4 = MathHelper.fastInvSqrt(f * f + f2 * f2) * 0.025f / 2.0f;
            float f5 = f2 * f4;
            float f6 = f * f4;
            BlockPos blockpos = new BlockPos(((Entity)entityLivingIn).getEyePosition(partialTicks));
            BlockPos blockpos1 = new BlockPos(leashHolder.getEyePosition(partialTicks));
            int i = this.getBlockLight(entityLivingIn, blockpos);
            int j = this.renderManager.getRenderer(leashHolder).getBlockLight(leashHolder, blockpos1);
            int k = ((MobEntity)entityLivingIn).world.getLightFor(LightType.SKY, blockpos);
            int l = ((MobEntity)entityLivingIn).world.getLightFor(LightType.SKY, blockpos1);
            if (Config.isShaders()) {
                Shaders.beginLeash();
            }
            MobRenderer.renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025f, 0.025f, f5, f6);
            MobRenderer.renderSide(ivertexbuilder, matrix4f, f, f1, f2, i, j, k, l, 0.025f, 0.0f, f5, f6);
            if (Config.isShaders()) {
                Shaders.endLeash();
            }
            matrixStackIn.pop();
        }
    }

    public static void renderSide(IVertexBuilder bufferIn, Matrix4f matrixIn, float p_229119_2_, float p_229119_3_, float p_229119_4_, int blockLight, int holderBlockLight, int skyLight, int holderSkyLight, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
        int i = 24;
        for (int j = 0; j < 24; ++j) {
            float f = (float)j / 23.0f;
            int k = (int)MathHelper.lerp(f, blockLight, holderBlockLight);
            int l = (int)MathHelper.lerp(f, skyLight, holderSkyLight);
            int i1 = LightTexture.packLight(k, l);
            MobRenderer.addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j, false, p_229119_11_, p_229119_12_);
            MobRenderer.addVertexPair(bufferIn, matrixIn, i1, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, j + 1, true, p_229119_11_, p_229119_12_);
        }
    }

    public static void addVertexPair(IVertexBuilder bufferIn, Matrix4f matrixIn, int packedLight, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
        float f = 0.5f;
        float f1 = 0.4f;
        float f2 = 0.3f;
        if (p_229120_9_ % 2 == 0) {
            f *= 0.7f;
            f1 *= 0.7f;
            f2 *= 0.7f;
        }
        float f3 = (float)p_229120_9_ / (float)p_229120_8_;
        float f4 = p_229120_3_ * f3;
        float f5 = p_229120_4_ > 0.0f ? p_229120_4_ * f3 * f3 : p_229120_4_ - p_229120_4_ * (1.0f - f3) * (1.0f - f3);
        float f6 = p_229120_5_ * f3;
        if (!p_229120_10_) {
            bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0f).lightmap(packedLight).endVertex();
        }
        bufferIn.pos(matrixIn, f4 - p_229120_11_, f5 + p_229120_7_, f6 + p_229120_12_).color(f, f1, f2, 1.0f).lightmap(packedLight).endVertex();
        if (p_229120_10_) {
            bufferIn.pos(matrixIn, f4 + p_229120_11_, f5 + p_229120_6_ - p_229120_7_, f6 - p_229120_12_).color(f, f1, f2, 1.0f).lightmap(packedLight).endVertex();
        }
    }
}
