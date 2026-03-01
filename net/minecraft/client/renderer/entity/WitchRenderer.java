package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.util.ResourceLocation;

public class WitchRenderer
extends MobRenderer<WitchEntity, WitchModel<WitchEntity>> {
    private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

    public WitchRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new WitchModel(0.0f), 0.5f);
        this.addLayer(new WitchHeldItemLayer<WitchEntity>(this));
    }

    @Override
    public void render(WitchEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        ((WitchModel)this.entityModel).func_205074_a(!entityIn.getHeldItemMainhand().isEmpty());
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(WitchEntity entity) {
        return WITCH_TEXTURES;
    }

    @Override
    protected void preRenderCallback(WitchEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375f;
        matrixStackIn.scale(0.9375f, 0.9375f, 0.9375f);
    }
}
