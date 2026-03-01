package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.CodModel;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class CodRenderer
extends MobRenderer<CodEntity, CodModel<CodEntity>> {
    private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

    public CodRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CodModel(), 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(CodEntity entity) {
        return COD_LOCATION;
    }

    @Override
    protected void applyRotations(CodEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        float f = 4.3f * MathHelper.sin(0.6f * ageInTicks);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
        if (!entityLiving.isInWater()) {
            matrixStackIn.translate(0.1f, 0.1f, -0.1f);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0f));
        }
    }
}
