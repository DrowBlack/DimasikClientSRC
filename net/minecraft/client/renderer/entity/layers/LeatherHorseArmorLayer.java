package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

public class LeatherHorseArmorLayer
extends LayerRenderer<HorseEntity, HorseModel<HorseEntity>> {
    private final HorseModel<HorseEntity> field_215341_a = new HorseModel(0.1f);

    public LeatherHorseArmorLayer(IEntityRenderer<HorseEntity, HorseModel<HorseEntity>> p_i50937_1_) {
        super(p_i50937_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, HorseEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = entitylivingbaseIn.func_213803_dV();
        if (itemstack.getItem() instanceof HorseArmorItem) {
            float f2;
            float f1;
            float f;
            HorseArmorItem horsearmoritem = (HorseArmorItem)itemstack.getItem();
            ((HorseModel)this.getEntityModel()).copyModelAttributesTo(this.field_215341_a);
            this.field_215341_a.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.field_215341_a.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            if (horsearmoritem instanceof DyeableHorseArmorItem) {
                int i = ((DyeableHorseArmorItem)horsearmoritem).getColor(itemstack);
                f = (float)(i >> 16 & 0xFF) / 255.0f;
                f1 = (float)(i >> 8 & 0xFF) / 255.0f;
                f2 = (float)(i & 0xFF) / 255.0f;
            } else {
                f = 1.0f;
                f1 = 1.0f;
                f2 = 1.0f;
            }
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(horsearmoritem.getArmorTexture()));
            this.field_215341_a.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0f);
        }
    }
}
