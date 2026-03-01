package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.model.CatModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class CatRenderer
extends MobRenderer<CatEntity, CatModel<CatEntity>> {
    public CatRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new CatModel(0.0f), 0.4f);
        this.addLayer(new CatCollarLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(CatEntity entity) {
        return entity.getCatTypeName();
    }

    @Override
    protected void preRenderCallback(CatEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
        matrixStackIn.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void applyRotations(CatEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        float f = entityLiving.func_213408_v(partialTicks);
        if (f > 0.0f) {
            matrixStackIn.translate(0.4f * f, 0.15f * f, 0.1f * f);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.interpolateAngle(f, 0.0f, 90.0f)));
            BlockPos blockpos = entityLiving.getPosition();
            for (PlayerEntity playerentity : entityLiving.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(blockpos).grow(2.0, 2.0, 2.0))) {
                if (!playerentity.isSleeping()) continue;
                matrixStackIn.translate(0.15f * f, 0.0, 0.0);
                break;
            }
        }
    }
}
