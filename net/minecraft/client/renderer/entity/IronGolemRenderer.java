package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCracksLayer;
import net.minecraft.client.renderer.entity.layers.IronGolenFlowerLayer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class IronGolemRenderer
extends MobRenderer<IronGolemEntity, IronGolemModel<IronGolemEntity>> {
    private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    public IronGolemRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new IronGolemModel(), 0.7f);
        this.addLayer(new IronGolemCracksLayer(this));
        this.addLayer(new IronGolenFlowerLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(IronGolemEntity entity) {
        return IRON_GOLEM_TEXTURES;
    }

    @Override
    protected void applyRotations(IronGolemEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        if (!((double)entityLiving.limbSwingAmount < 0.01)) {
            float f = 13.0f;
            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0f - partialTicks) + 6.0f;
            float f2 = (Math.abs(f1 % 13.0f - 6.5f) - 3.25f) / 3.25f;
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(6.5f * f2));
        }
    }
}
