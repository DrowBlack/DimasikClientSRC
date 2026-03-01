package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MagmaCubeRenderer
extends MobRenderer<MagmaCubeEntity, MagmaCubeModel<MagmaCubeEntity>> {
    private static final ResourceLocation MAGMA_CUBE_TEXTURES = new ResourceLocation("textures/entity/slime/magmacube.png");

    public MagmaCubeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MagmaCubeModel(), 0.25f);
    }

    @Override
    protected int getBlockLight(MagmaCubeEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public ResourceLocation getEntityTexture(MagmaCubeEntity entity) {
        return MAGMA_CUBE_TEXTURES;
    }

    @Override
    protected void preRenderCallback(MagmaCubeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        int i = entitylivingbaseIn.getSlimeSize();
        float f = MathHelper.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor) / ((float)i * 0.5f + 1.0f);
        float f1 = 1.0f / (f + 1.0f);
        matrixStackIn.scale(f1 * (float)i, 1.0f / f1 * (float)i, f1 * (float)i);
    }
}
