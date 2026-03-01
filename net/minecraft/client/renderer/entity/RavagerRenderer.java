package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.ResourceLocation;

public class RavagerRenderer
extends MobRenderer<RavagerEntity, RavagerModel> {
    private static final ResourceLocation RAVAGER_TEXTURES = new ResourceLocation("textures/entity/illager/ravager.png");

    public RavagerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new RavagerModel(), 1.1f);
    }

    @Override
    public ResourceLocation getEntityTexture(RavagerEntity entity) {
        return RAVAGER_TEXTURES;
    }
}
