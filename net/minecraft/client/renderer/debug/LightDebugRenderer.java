package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;

public class LightDebugRenderer
implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;

    public LightDebugRenderer(Minecraft minecraftIn) {
        this.minecraft = minecraftIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        ClientWorld world = this.minecraft.world;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockpos = new BlockPos(camX, camY, camZ);
        LongOpenHashSet longset = new LongOpenHashSet();
        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
            int i = world.getLightFor(LightType.SKY, blockpos1);
            float f = (float)(15 - i) / 15.0f * 0.5f + 0.16f;
            int j = MathHelper.hsvToRGB(f, 0.9f, 0.9f);
            long k = SectionPos.worldToSection(blockpos1.toLong());
            if (longset.add(k)) {
                DebugRenderer.renderText(world.getChunkProvider().getLightManager().getDebugInfo(LightType.SKY, SectionPos.from(k)), SectionPos.extractX(k) * 16 + 8, SectionPos.extractY(k) * 16 + 8, SectionPos.extractZ(k) * 16 + 8, 0xFF0000, 0.3f);
            }
            if (i == 15) continue;
            DebugRenderer.renderText(String.valueOf(i), (double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.25, (double)blockpos1.getZ() + 0.5, j);
        }
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}
