package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PathfindingDebugRenderer
implements DebugRenderer.IDebugRenderer {
    private final Map<Integer, Path> pathMap = Maps.newHashMap();
    private final Map<Integer, Float> pathMaxDistance = Maps.newHashMap();
    private final Map<Integer, Long> creationMap = Maps.newHashMap();

    public void addPath(int eid, Path pathIn, float distance) {
        this.pathMap.put(eid, pathIn);
        this.creationMap.put(eid, Util.milliTime());
        this.pathMaxDistance.put(eid, Float.valueOf(distance));
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ) {
        if (!this.pathMap.isEmpty()) {
            long i = Util.milliTime();
            for (Integer integer : this.pathMap.keySet()) {
                Path path = this.pathMap.get(integer);
                float f = this.pathMaxDistance.get(integer).floatValue();
                PathfindingDebugRenderer.func_229032_a_(path, f, true, true, camX, camY, camZ);
            }
            for (Integer integer1 : this.creationMap.keySet().toArray(new Integer[0])) {
                if (i - this.creationMap.get(integer1) <= 5000L) continue;
                this.pathMap.remove(integer1);
                this.creationMap.remove(integer1);
            }
        }
    }

    public static void func_229032_a_(Path p_229032_0_, float p_229032_1_, boolean p_229032_2_, boolean p_229032_3_, double p_229032_4_, double p_229032_6_, double p_229032_8_) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.func_229034_b_(p_229032_0_, p_229032_1_, p_229032_2_, p_229032_3_, p_229032_4_, p_229032_6_, p_229032_8_);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void func_229034_b_(Path p_229034_0_, float p_229034_1_, boolean p_229034_2_, boolean p_229034_3_, double p_229034_4_, double p_229034_6_, double p_229034_8_) {
        PathfindingDebugRenderer.func_229031_a_(p_229034_0_, p_229034_4_, p_229034_6_, p_229034_8_);
        BlockPos blockpos = p_229034_0_.getTarget();
        if (PathfindingDebugRenderer.func_229033_a_(blockpos, p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0f) {
            DebugRenderer.renderBox(new AxisAlignedBB((float)blockpos.getX() + 0.25f, (float)blockpos.getY() + 0.25f, (double)blockpos.getZ() + 0.25, (float)blockpos.getX() + 0.75f, (float)blockpos.getY() + 0.75f, (float)blockpos.getZ() + 0.75f).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int i = 0; i < p_229034_0_.getCurrentPathLength(); ++i) {
                PathPoint pathpoint = p_229034_0_.getPathPointFromIndex(i);
                if (!(PathfindingDebugRenderer.func_229033_a_(pathpoint.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0f)) continue;
                float f = i == p_229034_0_.getCurrentPathIndex() ? 1.0f : 0.0f;
                float f1 = i == p_229034_0_.getCurrentPathIndex() ? 0.0f : 1.0f;
                DebugRenderer.renderBox(new AxisAlignedBB((float)pathpoint.x + 0.5f - p_229034_1_, (float)pathpoint.y + 0.01f * (float)i, (float)pathpoint.z + 0.5f - p_229034_1_, (float)pathpoint.x + 0.5f + p_229034_1_, (float)pathpoint.y + 0.25f + 0.01f * (float)i, (float)pathpoint.z + 0.5f + p_229034_1_).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), f, 0.0f, f1, 0.5f);
            }
        }
        if (p_229034_2_) {
            for (PathPoint pathpoint2 : p_229034_0_.getClosedSet()) {
                if (!(PathfindingDebugRenderer.func_229033_a_(pathpoint2.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0f)) continue;
                DebugRenderer.renderBox(new AxisAlignedBB((float)pathpoint2.x + 0.5f - p_229034_1_ / 2.0f, (float)pathpoint2.y + 0.01f, (float)pathpoint2.z + 0.5f - p_229034_1_ / 2.0f, (float)pathpoint2.x + 0.5f + p_229034_1_ / 2.0f, (double)pathpoint2.y + 0.1, (float)pathpoint2.z + 0.5f + p_229034_1_ / 2.0f).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (PathPoint pathpoint3 : p_229034_0_.getOpenSet()) {
                if (!(PathfindingDebugRenderer.func_229033_a_(pathpoint3.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0f)) continue;
                DebugRenderer.renderBox(new AxisAlignedBB((float)pathpoint3.x + 0.5f - p_229034_1_ / 2.0f, (float)pathpoint3.y + 0.01f, (float)pathpoint3.z + 0.5f - p_229034_1_ / 2.0f, (float)pathpoint3.x + 0.5f + p_229034_1_ / 2.0f, (double)pathpoint3.y + 0.1, (float)pathpoint3.z + 0.5f + p_229034_1_ / 2.0f).offset(-p_229034_4_, -p_229034_6_, -p_229034_8_), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (p_229034_3_) {
            for (int j = 0; j < p_229034_0_.getCurrentPathLength(); ++j) {
                PathPoint pathpoint1 = p_229034_0_.getPathPointFromIndex(j);
                if (!(PathfindingDebugRenderer.func_229033_a_(pathpoint1.func_224759_a(), p_229034_4_, p_229034_6_, p_229034_8_) <= 80.0f)) continue;
                DebugRenderer.renderText(String.format("%s", new Object[]{pathpoint1.nodeType}), (double)pathpoint1.x + 0.5, (double)pathpoint1.y + 0.75, (double)pathpoint1.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.renderText(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathpoint1.costMalus)), (double)pathpoint1.x + 0.5, (double)pathpoint1.y + 0.25, (double)pathpoint1.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void func_229031_a_(Path p_229031_0_, double p_229031_1_, double p_229031_3_, double p_229031_5_) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < p_229031_0_.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = p_229031_0_.getPathPointFromIndex(i);
            if (PathfindingDebugRenderer.func_229033_a_(pathpoint.func_224759_a(), p_229031_1_, p_229031_3_, p_229031_5_) > 80.0f) continue;
            float f = (float)i / (float)p_229031_0_.getCurrentPathLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRGB(f, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int i1 = j & 0xFF;
            bufferbuilder.pos((double)pathpoint.x - p_229031_1_ + 0.5, (double)pathpoint.y - p_229031_3_ + 0.5, (double)pathpoint.z - p_229031_5_ + 0.5).color(k, l, i1, 255).endVertex();
        }
        tessellator.draw();
    }

    private static float func_229033_a_(BlockPos p_229033_0_, double p_229033_1_, double p_229033_3_, double p_229033_5_) {
        return (float)(Math.abs((double)p_229033_0_.getX() - p_229033_1_) + Math.abs((double)p_229033_0_.getY() - p_229033_3_) + Math.abs((double)p_229033_0_.getZ() - p_229033_5_));
    }
}
