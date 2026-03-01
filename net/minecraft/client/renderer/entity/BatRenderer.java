package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class BatRenderer
extends MobRenderer<BatEntity, BatModel> {
    private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

    public BatRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BatModel(), 0.25f);
    }

    @Override
    public ResourceLocation getEntityTexture(BatEntity entity) {
        return BAT_TEXTURES;
    }

    @Override
    protected void preRenderCallback(BatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void applyRotations(BatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (entityLiving.getIsBatHanging()) {
            matrixStackIn.translate(0.0, -0.1f, 0.0);
        } else {
            matrixStackIn.translate(0.0, MathHelper.cos(ageInTicks * 0.3f) * 0.1f, 0.0);
        }
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }
}
