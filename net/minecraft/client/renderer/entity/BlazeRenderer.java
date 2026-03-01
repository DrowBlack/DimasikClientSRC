package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class BlazeRenderer
extends MobRenderer<BlazeEntity, BlazeModel<BlazeEntity>> {
    private static final ResourceLocation BLAZE_TEXTURES = new ResourceLocation("textures/entity/blaze.png");

    public BlazeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BlazeModel(), 0.5f);
    }

    @Override
    protected int getBlockLight(BlazeEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    @Override
    public ResourceLocation getEntityTexture(BlazeEntity entity) {
        return BLAZE_TEXTURES;
    }
}
