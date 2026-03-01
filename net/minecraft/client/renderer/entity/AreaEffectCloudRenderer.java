package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.ResourceLocation;

public class AreaEffectCloudRenderer
extends EntityRenderer<AreaEffectCloudEntity> {
    public AreaEffectCloudRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getEntityTexture(AreaEffectCloudEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
