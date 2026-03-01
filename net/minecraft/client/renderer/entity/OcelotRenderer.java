package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.OcelotModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.ResourceLocation;

public class OcelotRenderer
extends MobRenderer<OcelotEntity, OcelotModel<OcelotEntity>> {
    private static final ResourceLocation OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/ocelot.png");

    public OcelotRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new OcelotModel(0.0f), 0.4f);
    }

    @Override
    public ResourceLocation getEntityTexture(OcelotEntity entity) {
        return OCELOT_TEXTURES;
    }
}
