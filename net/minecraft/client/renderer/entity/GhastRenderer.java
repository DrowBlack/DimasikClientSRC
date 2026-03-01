package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.ResourceLocation;

public class GhastRenderer
extends MobRenderer<GhastEntity, GhastModel<GhastEntity>> {
    private static final ResourceLocation GHAST_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public GhastRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new GhastModel(), 1.5f);
    }

    @Override
    public ResourceLocation getEntityTexture(GhastEntity entity) {
        return entity.isAttacking() ? GHAST_SHOOTING_TEXTURES : GHAST_TEXTURES;
    }

    @Override
    protected void preRenderCallback(GhastEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 1.0f;
        float f1 = 4.5f;
        float f2 = 4.5f;
        matrixStackIn.scale(4.5f, 4.5f, 4.5f);
    }
}
