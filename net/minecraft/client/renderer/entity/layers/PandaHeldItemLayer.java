package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class PandaHeldItemLayer
extends LayerRenderer<PandaEntity, PandaModel<PandaEntity>> {
    public PandaHeldItemLayer(IEntityRenderer<PandaEntity, PandaModel<PandaEntity>> p_i50930_1_) {
        super(p_i50930_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, PandaEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        if (entitylivingbaseIn.func_213556_dX() && !entitylivingbaseIn.func_213566_eo()) {
            float f = -0.6f;
            float f1 = 1.4f;
            if (entitylivingbaseIn.func_213578_dZ()) {
                f -= 0.2f * MathHelper.sin(ageInTicks * 0.6f) + 0.2f;
                f1 -= 0.09f * MathHelper.sin(ageInTicks * 0.6f);
            }
            matrixStackIn.push();
            matrixStackIn.translate(0.1f, f1, f);
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
    }
}
