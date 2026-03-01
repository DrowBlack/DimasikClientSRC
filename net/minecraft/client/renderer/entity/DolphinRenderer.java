package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.DolphinCarriedItemLayer;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.ResourceLocation;

public class DolphinRenderer
extends MobRenderer<DolphinEntity, DolphinModel<DolphinEntity>> {
    private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

    public DolphinRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new DolphinModel(), 0.7f);
        this.addLayer(new DolphinCarriedItemLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(DolphinEntity entity) {
        return DOLPHIN_LOCATION;
    }
}
