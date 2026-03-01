package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.vector.Vector3f;

public class IronGolenFlowerLayer
extends LayerRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> {
    public IronGolenFlowerLayer(IEntityRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> p_i50935_1_) {
        super(p_i50935_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, IronGolemEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.getHoldRoseTick() != 0) {
            matrixStackIn.push();
            ModelRenderer modelrenderer = ((IronGolemModel)this.getEntityModel()).getArmHoldingRose();
            modelrenderer.translateRotate(matrixStackIn);
            matrixStackIn.translate(-1.1875, 1.0625, -0.9375);
            matrixStackIn.translate(0.5, 0.5, 0.5);
            float f = 0.5f;
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0f));
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Blocks.POPPY.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.pop();
        }
    }
}
