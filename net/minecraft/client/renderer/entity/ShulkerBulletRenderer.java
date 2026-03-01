package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ShulkerBulletRenderer
extends EntityRenderer<ShulkerBulletEntity> {
    private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType field_229123_e_ = RenderType.getEntityTranslucent(SHULKER_SPARK_TEXTURE);
    private final ShulkerBulletModel<ShulkerBulletEntity> model = new ShulkerBulletModel();

    public ShulkerBulletRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    protected int getBlockLight(ShulkerBulletEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public void render(ShulkerBulletEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        float f = MathHelper.rotLerp(entityIn.prevRotationYaw, entityIn.rotationYaw, partialTicks);
        float f1 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        float f2 = (float)entityIn.ticksExisted + partialTicks;
        matrixStackIn.translate(0.0, 0.15f, 0.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.sin(f2 * 0.1f) * 180.0f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.cos(f2 * 0.1f) * 180.0f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(f2 * 0.15f) * 360.0f));
        matrixStackIn.scale(-0.5f, -0.5f, 0.5f);
        this.model.setRotationAngles(entityIn, 0.0f, 0.0f, 0.0f, f, f1);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(SHULKER_SPARK_TEXTURE));
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStackIn.scale(1.5f, 1.5f, 1.5f);
        IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(field_229123_e_);
        this.model.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.15f);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ShulkerBulletEntity entity) {
        return SHULKER_SPARK_TEXTURE;
    }
}
