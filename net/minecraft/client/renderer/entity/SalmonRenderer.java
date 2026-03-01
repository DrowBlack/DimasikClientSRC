package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SalmonModel;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SalmonRenderer
extends MobRenderer<SalmonEntity, SalmonModel<SalmonEntity>> {
    private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

    public SalmonRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SalmonModel(), 0.4f);
    }

    @Override
    public ResourceLocation getEntityTexture(SalmonEntity entity) {
        return SALMON_LOCATION;
    }

    @Override
    protected void applyRotations(SalmonEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        float f = 1.0f;
        float f1 = 1.0f;
        if (!entityLiving.isInWater()) {
            f = 1.3f;
            f1 = 1.7f;
        }
        float f2 = f * 4.3f * MathHelper.sin(f1 * 0.6f * ageInTicks);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f2));
        matrixStackIn.translate(0.0, 0.0, -0.4f);
        if (!entityLiving.isInWater()) {
            matrixStackIn.translate(0.2f, 0.1f, 0.0);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0f));
        }
    }
}
