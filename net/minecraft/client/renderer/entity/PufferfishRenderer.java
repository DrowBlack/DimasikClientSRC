package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PufferfishRenderer
extends MobRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
    private static final ResourceLocation PUFFERFISH_TEXTURES = new ResourceLocation("textures/entity/fish/pufferfish.png");
    private int lastPuffState = 3;
    private final PufferFishSmallModel<PufferfishEntity> modelSmall = new PufferFishSmallModel();
    private final PufferFishMediumModel<PufferfishEntity> modelMedium = new PufferFishMediumModel();
    private final PufferFishBigModel<PufferfishEntity> modelLarge = new PufferFishBigModel();

    public PufferfishRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PufferFishBigModel(), 0.2f);
    }

    @Override
    public ResourceLocation getEntityTexture(PufferfishEntity entity) {
        return PUFFERFISH_TEXTURES;
    }

    @Override
    public void render(PufferfishEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int i = entityIn.getPuffState();
        if (i != this.lastPuffState) {
            this.entityModel = i == 0 ? this.modelSmall : (i == 1 ? this.modelMedium : this.modelLarge);
        }
        this.lastPuffState = i;
        this.shadowSize = 0.1f + 0.1f * (float)i;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void applyRotations(PufferfishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        matrixStackIn.translate(0.0, MathHelper.cos(ageInTicks * 0.05f) * 0.08f, 0.0);
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }
}
