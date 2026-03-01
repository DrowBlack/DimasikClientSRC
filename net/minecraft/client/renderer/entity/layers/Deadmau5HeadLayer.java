package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class Deadmau5HeadLayer
extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    public Deadmau5HeadLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50945_1_) {
        super(p_i50945_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if ("deadmau5".equals(entitylivingbaseIn.getName().getString()) && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntitySolid(entitylivingbaseIn.getLocationSkin()));
            int i = LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0f);
            for (int j = 0; j < 2; ++j) {
                float f = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRotationYaw, entitylivingbaseIn.rotationYaw) - MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset);
                float f1 = MathHelper.lerp(partialTicks, entitylivingbaseIn.prevRotationPitch, entitylivingbaseIn.rotationPitch);
                matrixStackIn.push();
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1));
                matrixStackIn.translate(0.375f * (float)(j * 2 - 1), 0.0, 0.0);
                matrixStackIn.translate(0.0, -0.375, 0.0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f1));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
                float f2 = 1.3333334f;
                matrixStackIn.scale(1.3333334f, 1.3333334f, 1.3333334f);
                ((PlayerModel)this.getEntityModel()).renderEars(matrixStackIn, ivertexbuilder, packedLightIn, i);
                matrixStackIn.pop();
            }
        }
    }
}
