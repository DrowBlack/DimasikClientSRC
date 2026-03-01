package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class VillagerRenderer
extends MobRenderer<VillagerEntity, VillagerModel<VillagerEntity>> {
    private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/villager.png");

    public VillagerRenderer(EntityRendererManager renderManagerIn, IReloadableResourceManager resourceManagerIn) {
        super(renderManagerIn, new VillagerModel(0.0f), 0.5f);
        this.addLayer(new HeadLayer<VillagerEntity, VillagerModel<VillagerEntity>>(this));
        this.addLayer(new VillagerLevelPendantLayer<VillagerEntity, VillagerModel<VillagerEntity>>(this, resourceManagerIn, "villager"));
        this.addLayer(new CrossedArmsItemLayer<VillagerEntity, VillagerModel<VillagerEntity>>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(VillagerEntity entity) {
        return VILLAGER_TEXTURES;
    }

    @Override
    protected void preRenderCallback(VillagerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375f;
        if (entitylivingbaseIn.isChild()) {
            f = (float)((double)f * 0.5);
            this.shadowSize = 0.25f;
        } else {
            this.shadowSize = 0.5f;
        }
        matrixStackIn.scale(f, f, f);
    }
}
