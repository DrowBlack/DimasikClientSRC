package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class SpriteRenderer<T extends Entity>
extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean field_229126_f_;

    public SpriteRenderer(EntityRendererManager p_i226035_1_, ItemRenderer p_i226035_2_, float p_i226035_3_, boolean p_i226035_4_) {
        super(p_i226035_1_);
        this.itemRenderer = p_i226035_2_;
        this.scale = p_i226035_3_;
        this.field_229126_f_ = p_i226035_4_;
    }

    public SpriteRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
        this(renderManagerIn, itemRendererIn, 1.0f, false);
    }

    @Override
    protected int getBlockLight(T entityIn, BlockPos partialTicks) {
        return this.field_229126_f_ ? 15 : super.getBlockLight(entityIn, partialTicks);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (((Entity)entityIn).ticksExisted >= 2 || !(this.renderManager.info.getRenderViewEntity().getDistanceSq((Entity)entityIn) < 12.25)) {
            matrixStackIn.push();
            matrixStackIn.scale(this.scale, this.scale, this.scale);
            matrixStackIn.rotate(this.renderManager.getCameraOrientation());
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f));
            this.itemRenderer.renderItem(((IRendersAsItem)entityIn).getItem(), ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
            matrixStackIn.pop();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
