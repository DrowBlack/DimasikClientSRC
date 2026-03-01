package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SlimeRenderer
extends MobRenderer<SlimeEntity, SlimeModel<SlimeEntity>> {
    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

    public SlimeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SlimeModel(16), 0.25f);
        this.addLayer(new SlimeGelLayer<SlimeEntity>(this));
    }

    @Override
    public void render(SlimeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        this.shadowSize = 0.25f * (float)entityIn.getSlimeSize();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void preRenderCallback(SlimeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.999f;
        matrixStackIn.scale(0.999f, 0.999f, 0.999f);
        matrixStackIn.translate(0.0, 0.001f, 0.0);
        float f1 = entitylivingbaseIn.getSlimeSize();
        float f2 = MathHelper.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor) / (f1 * 0.5f + 1.0f);
        float f3 = 1.0f / (f2 + 1.0f);
        matrixStackIn.scale(f3 * f1, 1.0f / f3 * f1, f3 * f1);
    }

    @Override
    public ResourceLocation getEntityTexture(SlimeEntity entity) {
        return SLIME_TEXTURES;
    }
}
