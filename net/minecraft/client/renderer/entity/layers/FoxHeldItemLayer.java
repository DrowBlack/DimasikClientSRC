package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class FoxHeldItemLayer
extends LayerRenderer<FoxEntity, FoxModel<FoxEntity>> {
    public FoxHeldItemLayer(IEntityRenderer<FoxEntity, FoxModel<FoxEntity>> p_i50938_1_) {
        super(p_i50938_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, FoxEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = entitylivingbaseIn.isSleeping();
        boolean flag1 = entitylivingbaseIn.isChild();
        matrixStackIn.push();
        if (flag1) {
            float f = 0.75f;
            matrixStackIn.scale(0.75f, 0.75f, 0.75f);
            matrixStackIn.translate(0.0, 0.5, 0.209375f);
        }
        matrixStackIn.translate(((FoxModel)this.getEntityModel()).head.rotationPointX / 16.0f, ((FoxModel)this.getEntityModel()).head.rotationPointY / 16.0f, ((FoxModel)this.getEntityModel()).head.rotationPointZ / 16.0f);
        float f1 = entitylivingbaseIn.func_213475_v(partialTicks);
        matrixStackIn.rotate(Vector3f.ZP.rotation(f1));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));
        if (entitylivingbaseIn.isChild()) {
            if (flag) {
                matrixStackIn.translate(0.4f, 0.26f, 0.15f);
            } else {
                matrixStackIn.translate(0.06f, 0.26f, -0.5);
            }
        } else if (flag) {
            matrixStackIn.translate(0.46f, 0.26f, 0.22f);
        } else {
            matrixStackIn.translate(0.06f, 0.27f, -0.5);
        }
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
        if (flag) {
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0f));
        }
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }
}
