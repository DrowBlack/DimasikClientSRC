package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class SnowmanHeadLayer
extends LayerRenderer<SnowGolemEntity, SnowManModel<SnowGolemEntity>> {
    public SnowmanHeadLayer(IEntityRenderer<SnowGolemEntity, SnowManModel<SnowGolemEntity>> p_i50922_1_) {
        super(p_i50922_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, SnowGolemEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isPumpkinEquipped()) {
            matrixStackIn.push();
            ((SnowManModel)this.getEntityModel()).func_205070_a().translateRotate(matrixStackIn);
            float f = 0.625f;
            matrixStackIn.translate(0.0, -0.34375, 0.0);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
            matrixStackIn.scale(0.625f, -0.625f, -0.625f);
            ItemStack itemstack = new ItemStack(Blocks.CARVED_PUMPKIN);
            Minecraft.getInstance().getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, entitylivingbaseIn.world, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0f));
            matrixStackIn.pop();
        }
    }
}
