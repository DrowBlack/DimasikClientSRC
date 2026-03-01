package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class EvokerFangsRenderer
extends EntityRenderer<EvokerFangsEntity> {
    private static final ResourceLocation EVOKER_ILLAGER_FANGS = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsModel<EvokerFangsEntity> model = new EvokerFangsModel();

    public EvokerFangsRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(EvokerFangsEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        float f = entityIn.getAnimationProgress(partialTicks);
        if (f != 0.0f) {
            float f1 = 2.0f;
            if (f > 0.9f) {
                f1 = (float)((double)f1 * ((1.0 - (double)f) / (double)0.1f));
            }
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f - entityIn.rotationYaw));
            matrixStackIn.scale(-f1, -f1, f1);
            float f2 = 0.03125f;
            matrixStackIn.translate(0.0, -0.626f, 0.0);
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            this.model.setRotationAngles(entityIn, f, 0.0f, 0.0f, entityIn.rotationYaw, entityIn.rotationPitch);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(EVOKER_ILLAGER_FANGS));
            this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            matrixStackIn.pop();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EvokerFangsEntity entity) {
        return EVOKER_ILLAGER_FANGS;
    }
}
