package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.util.ResourceLocation;

public class WanderingTraderRenderer
extends MobRenderer<WanderingTraderEntity, VillagerModel<WanderingTraderEntity>> {
    private static final ResourceLocation field_217780_a = new ResourceLocation("textures/entity/wandering_trader.png");

    public WanderingTraderRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VillagerModel(0.0f), 0.5f);
        this.addLayer(new HeadLayer<WanderingTraderEntity, VillagerModel<WanderingTraderEntity>>(this));
        this.addLayer(new CrossedArmsItemLayer<WanderingTraderEntity, VillagerModel<WanderingTraderEntity>>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(WanderingTraderEntity entity) {
        return field_217780_a;
    }

    @Override
    protected void preRenderCallback(WanderingTraderEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375f;
        matrixStackIn.scale(0.9375f, 0.9375f, 0.9375f);
    }
}
