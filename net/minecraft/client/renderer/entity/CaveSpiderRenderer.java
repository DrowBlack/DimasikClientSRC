package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.util.ResourceLocation;

public class CaveSpiderRenderer
extends SpiderRenderer<CaveSpiderEntity> {
    private static final ResourceLocation CAVE_SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/cave_spider.png");

    public CaveSpiderRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize *= 0.7f;
    }

    @Override
    protected void preRenderCallback(CaveSpiderEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.7f, 0.7f, 0.7f);
    }

    @Override
    public ResourceLocation getEntityTexture(CaveSpiderEntity entity) {
        return CAVE_SPIDER_TEXTURES;
    }
}
