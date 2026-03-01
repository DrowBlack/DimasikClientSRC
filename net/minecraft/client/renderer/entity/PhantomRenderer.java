package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class PhantomRenderer
extends MobRenderer<PhantomEntity, PhantomModel<PhantomEntity>> {
    private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

    public PhantomRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PhantomModel(), 0.75f);
        this.addLayer(new PhantomEyesLayer<PhantomEntity>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(PhantomEntity entity) {
        return PHANTOM_LOCATION;
    }

    @Override
    protected void preRenderCallback(PhantomEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        int i = entitylivingbaseIn.getPhantomSize();
        float f = 1.0f + 0.15f * (float)i;
        matrixStackIn.scale(f, f, f);
        matrixStackIn.translate(0.0, 1.3125, 0.1875);
    }

    @Override
    protected void applyRotations(PhantomEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityLiving.rotationPitch));
    }
}
