package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3f;

public class WitchHeldItemLayer<T extends LivingEntity>
extends CrossedArmsItemLayer<T, WitchModel<T>> {
    public WitchHeldItemLayer(IEntityRenderer<T, WitchModel<T>> p_i50916_1_) {
        super(p_i50916_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = ((LivingEntity)entitylivingbaseIn).getHeldItemMainhand();
        matrixStackIn.push();
        if (itemstack.getItem() == Items.POTION) {
            ((WitchModel)this.getEntityModel()).getModelHead().translateRotate(matrixStackIn);
            ((WitchModel)this.getEntityModel()).func_205073_b().translateRotate(matrixStackIn);
            matrixStackIn.translate(0.0625, 0.25, 0.0);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(140.0f));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(10.0f));
            matrixStackIn.translate(0.0, -0.4f, 0.4f);
        }
        super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        matrixStackIn.pop();
    }
}
