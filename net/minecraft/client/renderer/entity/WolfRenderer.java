package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ResourceLocation;

public class WolfRenderer
extends MobRenderer<WolfEntity, WolfModel<WolfEntity>> {
    private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf.png");
    private static final ResourceLocation TAMED_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    private static final ResourceLocation ANGRY_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

    public WolfRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new WolfModel(), 0.5f);
        this.addLayer(new WolfCollarLayer(this));
    }

    @Override
    protected float handleRotationFloat(WolfEntity livingBase, float partialTicks) {
        return livingBase.getTailRotation();
    }

    @Override
    public void render(WolfEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.isWolfWet()) {
            float f = entityIn.getShadingWhileWet(partialTicks);
            ((WolfModel)this.entityModel).setTint(f, f, f);
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entityIn.isWolfWet()) {
            ((WolfModel)this.entityModel).setTint(1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(WolfEntity entity) {
        if (entity.isTamed()) {
            return TAMED_WOLF_TEXTURES;
        }
        return entity.func_233678_J__() ? ANGRY_WOLF_TEXTURES : WOLF_TEXTURES;
    }
}
