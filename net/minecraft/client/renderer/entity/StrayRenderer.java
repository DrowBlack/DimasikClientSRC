package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.StayClothingLayer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;

public class StrayRenderer
extends SkeletonRenderer {
    private static final ResourceLocation STRAY_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray.png");

    public StrayRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.addLayer(new StayClothingLayer<AbstractSkeletonEntity, SkeletonModel<AbstractSkeletonEntity>>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractSkeletonEntity entity) {
        return STRAY_SKELETON_TEXTURES;
    }
}
