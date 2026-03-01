package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SilverfishModel;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.ResourceLocation;

public class SilverfishRenderer
extends MobRenderer<SilverfishEntity, SilverfishModel<SilverfishEntity>> {
    private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

    public SilverfishRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SilverfishModel(), 0.3f);
    }

    @Override
    protected float getDeathMaxRotation(SilverfishEntity entityLivingBaseIn) {
        return 180.0f;
    }

    @Override
    public ResourceLocation getEntityTexture(SilverfishEntity entity) {
        return SILVERFISH_TEXTURES;
    }
}
