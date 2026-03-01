package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.model.AbstractTropicalFishModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class TropicalFishRenderer
extends MobRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
    private final TropicalFishAModel<TropicalFishEntity> aModel = new TropicalFishAModel(0.0f);
    private final TropicalFishBModel<TropicalFishEntity> bModel = new TropicalFishBModel(0.0f);

    public TropicalFishRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new TropicalFishAModel(0.0f), 0.15f);
        this.addLayer(new TropicalFishPatternLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(TropicalFishEntity entity) {
        return entity.getBodyTexture();
    }

    @Override
    public void render(TropicalFishEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        AbstractTropicalFishModel abstracttropicalfishmodel;
        this.entityModel = abstracttropicalfishmodel = entityIn.getSize() == 0 ? this.aModel : this.bModel;
        float[] afloat = entityIn.func_204219_dC();
        abstracttropicalfishmodel.func_228257_a_(afloat[0], afloat[1], afloat[2]);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        abstracttropicalfishmodel.func_228257_a_(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void applyRotations(TropicalFishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        float f = 4.3f * MathHelper.sin(0.6f * ageInTicks);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
        if (!entityLiving.isInWater()) {
            matrixStackIn.translate(0.2f, 0.1f, 0.0);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0f));
        }
    }
}
