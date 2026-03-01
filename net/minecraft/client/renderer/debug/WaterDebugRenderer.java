package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterDebugRenderer
implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;

    public WaterDebugRenderer(Minecraft minecraftIn) {
        this.minecraft = minecraftIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        BlockPos blockpos = this.minecraft.player.getPosition();
        World iworldreader = this.minecraft.player.world;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
            FluidState fluidstate = iworldreader.getFluidState(blockpos1);
            if (!fluidstate.isTagged(FluidTags.WATER)) continue;
            double d0 = (float)blockpos1.getY() + fluidstate.getActualHeight(iworldreader, blockpos1);
            DebugRenderer.renderBox(new AxisAlignedBB((float)blockpos1.getX() + 0.01f, (float)blockpos1.getY() + 0.01f, (float)blockpos1.getZ() + 0.01f, (float)blockpos1.getX() + 0.99f, d0, (float)blockpos1.getZ() + 0.99f).offset(-camX, -camY, -camZ), 1.0f, 1.0f, 1.0f, 0.2f);
        }
        for (BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
            FluidState fluidstate1 = iworldreader.getFluidState(blockpos2);
            if (!fluidstate1.isTagged(FluidTags.WATER)) continue;
            DebugRenderer.renderText(String.valueOf(fluidstate1.getLevel()), (double)blockpos2.getX() + 0.5, (float)blockpos2.getY() + fluidstate1.getActualHeight(iworldreader, blockpos2), (double)blockpos2.getZ() + 0.5, -16777216);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
