package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.math.vector.Vector3f;

public class HeldBlockLayer
extends LayerRenderer<EndermanEntity, EndermanModel<EndermanEntity>> {
    public HeldBlockLayer(IEntityRenderer<EndermanEntity, EndermanModel<EndermanEntity>> p_i50949_1_) {
        super(p_i50949_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EndermanEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BlockState blockstate = entitylivingbaseIn.getHeldBlockState();
        if (blockstate != null) {
            matrixStackIn.push();
            matrixStackIn.translate(0.0, 0.6875, -0.75);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(20.0f));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45.0f));
            matrixStackIn.translate(0.25, 0.1875, 0.25);
            float f = 0.5f;
            matrixStackIn.scale(-0.5f, -0.5f, 0.5f);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f));
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }
    }
}
