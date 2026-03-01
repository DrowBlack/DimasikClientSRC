package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class GuardianRenderer
extends MobRenderer<GuardianEntity, GuardianModel> {
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType field_229107_h_ = RenderType.getEntityCutoutNoCull(GUARDIAN_BEAM_TEXTURE);

    public GuardianRenderer(EntityRendererManager renderManagerIn) {
        this(renderManagerIn, 0.5f);
    }

    protected GuardianRenderer(EntityRendererManager p_i50968_1_, float p_i50968_2_) {
        super(p_i50968_1_, new GuardianModel(), p_i50968_2_);
    }

    @Override
    public boolean shouldRender(GuardianEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        LivingEntity livingentity;
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        }
        if (livingEntityIn.hasTargetedEntity() && (livingentity = livingEntityIn.getTargetedEntity()) != null) {
            Vector3d vector3d = this.getPosition(livingentity, (double)livingentity.getHeight() * 0.5, 1.0f);
            Vector3d vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0f);
            return camera.isBoundingBoxInFrustum(new AxisAlignedBB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z));
        }
        return false;
    }

    private Vector3d getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
        double d0 = MathHelper.lerp((double)p_177110_4_, entityLivingBaseIn.lastTickPosX, entityLivingBaseIn.getPosX());
        double d1 = MathHelper.lerp((double)p_177110_4_, entityLivingBaseIn.lastTickPosY, entityLivingBaseIn.getPosY()) + p_177110_2_;
        double d2 = MathHelper.lerp((double)p_177110_4_, entityLivingBaseIn.lastTickPosZ, entityLivingBaseIn.getPosZ());
        return new Vector3d(d0, d1, d2);
    }

    @Override
    public void render(GuardianEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        LivingEntity livingentity = entityIn.getTargetedEntity();
        if (livingentity != null) {
            float f = entityIn.getAttackAnimationScale(partialTicks);
            float f1 = (float)entityIn.world.getGameTime() + partialTicks;
            float f2 = f1 * 0.5f % 1.0f;
            float f3 = entityIn.getEyeHeight();
            matrixStackIn.push();
            matrixStackIn.translate(0.0, f3, 0.0);
            Vector3d vector3d = this.getPosition(livingentity, (double)livingentity.getHeight() * 0.5, partialTicks);
            Vector3d vector3d1 = this.getPosition(entityIn, f3, partialTicks);
            Vector3d vector3d2 = vector3d.subtract(vector3d1);
            float f4 = (float)(vector3d2.length() + 1.0);
            vector3d2 = vector3d2.normalize();
            float f5 = (float)Math.acos(vector3d2.y);
            float f6 = (float)Math.atan2(vector3d2.z, vector3d2.x);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((1.5707964f - f6) * 57.295776f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * 57.295776f));
            boolean i = true;
            float f7 = f1 * 0.05f * -1.5f;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0f);
            int k = 32 + (int)(f8 * 191.0f);
            int l = 128 - (int)(f8 * 64.0f);
            float f9 = 0.2f;
            float f10 = 0.282f;
            float f11 = MathHelper.cos(f7 + 2.3561945f) * 0.282f;
            float f12 = MathHelper.sin(f7 + 2.3561945f) * 0.282f;
            float f13 = MathHelper.cos(f7 + 0.7853982f) * 0.282f;
            float f14 = MathHelper.sin(f7 + 0.7853982f) * 0.282f;
            float f15 = MathHelper.cos(f7 + 3.926991f) * 0.282f;
            float f16 = MathHelper.sin(f7 + 3.926991f) * 0.282f;
            float f17 = MathHelper.cos(f7 + 5.4977875f) * 0.282f;
            float f18 = MathHelper.sin(f7 + 5.4977875f) * 0.282f;
            float f19 = MathHelper.cos(f7 + (float)Math.PI) * 0.2f;
            float f20 = MathHelper.sin(f7 + (float)Math.PI) * 0.2f;
            float f21 = MathHelper.cos(f7 + 0.0f) * 0.2f;
            float f22 = MathHelper.sin(f7 + 0.0f) * 0.2f;
            float f23 = MathHelper.cos(f7 + 1.5707964f) * 0.2f;
            float f24 = MathHelper.sin(f7 + 1.5707964f) * 0.2f;
            float f25 = MathHelper.cos(f7 + 4.712389f) * 0.2f;
            float f26 = MathHelper.sin(f7 + 4.712389f) * 0.2f;
            float f27 = 0.0f;
            float f28 = 0.4999f;
            float f29 = -1.0f + f2;
            float f30 = f4 * 2.5f + f29;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(field_229107_h_);
            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999f, f30);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0f, f20, j, k, l, 0.4999f, f29);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0f, f22, j, k, l, 0.0f, f29);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0f, f30);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999f, f30);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0f, f24, j, k, l, 0.4999f, f29);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0f, f26, j, k, l, 0.0f, f29);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0f, f30);
            float f31 = 0.0f;
            if (entityIn.ticksExisted % 2 == 0) {
                f31 = 0.5f;
            }
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5f, f31 + 0.5f);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0f, f31 + 0.5f);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0f, f31);
            GuardianRenderer.func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5f, f31);
            matrixStackIn.pop();
        }
    }

    private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.pos(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).tex(p_229108_9_, p_229108_10_).overlay(OverlayTexture.NO_OVERLAY).lightmap(0xF000F0).normal(p_229108_2_, 0.0f, 1.0f, 0.0f).endVertex();
    }

    @Override
    public ResourceLocation getEntityTexture(GuardianEntity entity) {
        return GUARDIAN_TEXTURE;
    }
}
