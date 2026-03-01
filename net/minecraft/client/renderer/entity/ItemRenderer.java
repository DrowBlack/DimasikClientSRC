package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ItemRenderer
extends EntityRenderer<ItemEntity> {
    private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    private int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    @Override
    public void render(ItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        ItemStack itemstack = entityIn.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
        this.random.setSeed(i);
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entityIn.world, null);
        boolean flag = ibakedmodel.isGui3d();
        int j = this.getModelCount(itemstack);
        float f = 0.25f;
        float f1 = MathHelper.sin(((float)entityIn.getAge() + partialTicks) / 10.0f + entityIn.hoverStart) * 0.1f + 0.1f;
        if (!this.shouldBob()) {
            f1 = 0.0f;
        }
        float f2 = ibakedmodel.getItemCameraTransforms().getTransform((ItemCameraTransforms.TransformType)ItemCameraTransforms.TransformType.GROUND).scale.getY();
        matrixStackIn.translate(0.0, f1 + 0.25f * f2, 0.0);
        float f3 = entityIn.getItemHover(partialTicks);
        matrixStackIn.rotate(Vector3f.YP.rotation(f3));
        float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.getX();
        float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.getY();
        float f6 = ibakedmodel.getItemCameraTransforms().ground.scale.getZ();
        if (!flag) {
            float f7 = -0.0f * (float)(j - 1) * 0.5f * f4;
            float f8 = -0.0f * (float)(j - 1) * 0.5f * f5;
            float f9 = -0.09375f * (float)(j - 1) * 0.5f * f6;
            matrixStackIn.translate(f7, f8, f9);
        }
        for (int k = 0; k < j; ++k) {
            matrixStackIn.push();
            if (k > 0) {
                if (flag) {
                    float f11 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f13 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f10 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    if (!this.shouldSpreadItems()) {
                        f11 = 0.0f;
                        f13 = 0.0f;
                    }
                    matrixStackIn.translate(f11, f13, f10);
                } else {
                    float f12 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    float f14 = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrixStackIn.translate(f12, f14, 0.0);
                }
            }
            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrixStackIn.pop();
            if (flag) continue;
            matrixStackIn.translate(0.0f * f4, 0.0f * f5, 0.09375f * f6);
        }
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ItemEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    public boolean shouldSpreadItems() {
        return true;
    }

    public boolean shouldBob() {
        return true;
    }
}
