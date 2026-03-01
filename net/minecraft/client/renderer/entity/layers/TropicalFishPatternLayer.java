package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.AbstractTropicalFishModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;

public class TropicalFishPatternLayer
extends LayerRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishAModel<TropicalFishEntity> modelA = new TropicalFishAModel(0.008f);
    private final TropicalFishBModel<TropicalFishEntity> modelB = new TropicalFishBModel(0.008f);

    public TropicalFishPatternLayer(IEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> p_i50918_1_) {
        super(p_i50918_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, TropicalFishEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        AbstractTropicalFishModel entitymodel = entitylivingbaseIn.getSize() == 0 ? this.modelA : this.modelB;
        float[] afloat = entitylivingbaseIn.func_204222_dD();
        TropicalFishPatternLayer.renderCopyCutoutModel(this.getEntityModel(), entitymodel, entitylivingbaseIn.getPatternTexture(), matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, afloat[0], afloat[1], afloat[2]);
    }
}
