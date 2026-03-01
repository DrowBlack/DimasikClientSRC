package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public class HuskRenderer
extends ZombieRenderer {
    private static final ResourceLocation HUSK_ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/husk.png");

    public HuskRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void preRenderCallback(ZombieEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 1.0625f;
        matrixStackIn.scale(1.0625f, 1.0625f, 1.0625f);
        super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
    }

    @Override
    public ResourceLocation getEntityTexture(ZombieEntity entity) {
        return HUSK_ZOMBIE_TEXTURES;
    }
}
