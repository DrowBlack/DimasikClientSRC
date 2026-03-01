package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class TNTRenderer
extends EntityRenderer<TNTEntity> {
    public TNTRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5f;
    }

    @Override
    public void render(TNTEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.5, 0.0);
        if ((float)entityIn.getFuse() - partialTicks + 1.0f < 10.0f) {
            float f = 1.0f - ((float)entityIn.getFuse() - partialTicks + 1.0f) / 10.0f;
            f = MathHelper.clamp(f, 0.0f, 1.0f);
            f *= f;
            f *= f;
            float f1 = 1.0f + f * 0.3f;
            matrixStackIn.scale(f1, f1, f1);
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0f));
        matrixStackIn.translate(-0.5, -0.5, 0.5);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f));
        TNTMinecartRenderer.renderTntFlash(Blocks.TNT.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, entityIn.getFuse() / 5 % 2 == 0);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(TNTEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
