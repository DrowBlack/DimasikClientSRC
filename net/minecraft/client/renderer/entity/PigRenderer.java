package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;

public class PigRenderer
extends MobRenderer<PigEntity, PigModel<PigEntity>> {
    private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");

    public PigRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PigModel(), 0.7f);
        this.addLayer(new SaddleLayer(this, new PigModel(0.5f), new ResourceLocation("textures/entity/pig/pig_saddle.png")));
    }

    @Override
    public ResourceLocation getEntityTexture(PigEntity entity) {
        return PIG_TEXTURES;
    }
}
