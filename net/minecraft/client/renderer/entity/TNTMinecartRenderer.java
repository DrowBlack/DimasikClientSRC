package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class TNTMinecartRenderer
extends MinecartRenderer<TNTMinecartEntity> {
    public TNTMinecartRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void renderBlockState(TNTMinecartEntity entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        int i = entityIn.getFuseTicks();
        if (i > -1 && (float)i - partialTicks + 1.0f < 10.0f) {
            float f = 1.0f - ((float)i - partialTicks + 1.0f) / 10.0f;
            f = MathHelper.clamp(f, 0.0f, 1.0f);
            f *= f;
            f *= f;
            float f1 = 1.0f + f * 0.3f;
            matrixStackIn.scale(f1, f1, f1);
        }
        TNTMinecartRenderer.renderTntFlash(stateIn, matrixStackIn, bufferIn, packedLightIn, i > -1 && i / 5 % 2 == 0);
    }

    public static void renderTntFlash(BlockState blockStateIn, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int combinedLight, boolean doFullBright) {
        int i = doFullBright ? OverlayTexture.getPackedUV(OverlayTexture.getU(1.0f), 10) : OverlayTexture.NO_OVERLAY;
        if (Config.isShaders() && doFullBright) {
            Shaders.setEntityColor(1.0f, 1.0f, 1.0f, 0.5f);
        }
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(blockStateIn, matrixStackIn, renderTypeBuffer, combinedLight, i);
        if (Config.isShaders()) {
            Shaders.setEntityColor(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
}
