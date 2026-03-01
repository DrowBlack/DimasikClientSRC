package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherAuraLayer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class WitherRenderer
extends MobRenderer<WitherEntity, WitherModel<WitherEntity>> {
    private static final ResourceLocation INVULNERABLE_WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_TEXTURES = new ResourceLocation("textures/entity/wither/wither.png");

    public WitherRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new WitherModel(0.0f), 1.0f);
        this.addLayer(new WitherAuraLayer(this));
    }

    @Override
    protected int getBlockLight(WitherEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public ResourceLocation getEntityTexture(WitherEntity entity) {
        int i = entity.getInvulTime();
        return i > 0 && (i > 80 || i / 5 % 2 != 1) ? INVULNERABLE_WITHER_TEXTURES : WITHER_TEXTURES;
    }

    @Override
    protected void preRenderCallback(WitherEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 2.0f;
        int i = entitylivingbaseIn.getInvulTime();
        if (i > 0) {
            f -= ((float)i - partialTickTime) / 220.0f * 0.5f;
        }
        matrixStackIn.scale(f, f, f);
    }
}
