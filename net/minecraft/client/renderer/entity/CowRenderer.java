package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;

public class CowRenderer
extends MobRenderer<CowEntity, CowModel<CowEntity>> {
    private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

    public CowRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CowModel(), 0.7f);
    }

    @Override
    public ResourceLocation getEntityTexture(CowEntity entity) {
        return COW_TEXTURES;
    }
}
