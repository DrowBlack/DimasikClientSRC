package net.optifine;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.IProfiler;
import net.optifine.Config;
import net.optifine.util.MemoryMonitor;
import org.lwjgl.opengl.GL11;

public class Lagometer {
    private static Minecraft mc;
    private static GameSettings gameSettings;
    private static IProfiler profiler;
    public static boolean active;
    public static TimerNano timerTick;
    public static TimerNano timerScheduledExecutables;
    public static TimerNano timerChunkUpload;
    public static TimerNano timerChunkUpdate;
    public static TimerNano timerVisibility;
    public static TimerNano timerTerrain;
    public static TimerNano timerServer;
    private static long[] timesFrame;
    private static long[] timesTick;
    private static long[] timesScheduledExecutables;
    private static long[] timesChunkUpload;
    private static long[] timesChunkUpdate;
    private static long[] timesVisibility;
    private static long[] timesTerrain;
    private static long[] timesServer;
    private static boolean[] gcs;
    private static int numRecordedFrameTimes;
    private static long prevFrameTimeNano;
    private static long renderTimeNano;

    public static void updateLagometer() {
        if (mc == null) {
            mc = Minecraft.getInstance();
            gameSettings = Lagometer.mc.gameSettings;
            profiler = mc.getProfiler();
        }
        if (Lagometer.gameSettings.showDebugInfo && (Lagometer.gameSettings.ofLagometer || Lagometer.gameSettings.showLagometer)) {
            active = true;
            long timeNowNano = System.nanoTime();
            if (prevFrameTimeNano == -1L) {
                prevFrameTimeNano = timeNowNano;
            } else {
                int j = numRecordedFrameTimes & timesFrame.length - 1;
                ++numRecordedFrameTimes;
                boolean flag = MemoryMonitor.isGcEvent();
                Lagometer.timesFrame[j] = timeNowNano - prevFrameTimeNano - renderTimeNano;
                Lagometer.timesTick[j] = Lagometer.timerTick.timeNano;
                Lagometer.timesScheduledExecutables[j] = Lagometer.timerScheduledExecutables.timeNano;
                Lagometer.timesChunkUpload[j] = Lagometer.timerChunkUpload.timeNano;
                Lagometer.timesChunkUpdate[j] = Lagometer.timerChunkUpdate.timeNano;
                Lagometer.timesVisibility[j] = Lagometer.timerVisibility.timeNano;
                Lagometer.timesTerrain[j] = Lagometer.timerTerrain.timeNano;
                Lagometer.timesServer[j] = Lagometer.timerServer.timeNano;
                Lagometer.gcs[j] = flag;
                timerTick.reset();
                timerScheduledExecutables.reset();
                timerVisibility.reset();
                timerChunkUpdate.reset();
                timerChunkUpload.reset();
                timerTerrain.reset();
                timerServer.reset();
                prevFrameTimeNano = System.nanoTime();
            }
        } else {
            active = false;
            prevFrameTimeNano = -1L;
        }
    }

    public static void showLagometer(MatrixStack matrixStackIn, int scaleFactor) {
        if (gameSettings != null && (Lagometer.gameSettings.ofLagometer || Lagometer.gameSettings.showLagometer)) {
            long i = System.nanoTime();
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            int j = mc.getMainWindow().getFramebufferWidth();
            int k = mc.getMainWindow().getFramebufferHeight();
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, j, k, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
            GL11.glLineWidth(1.0f);
            GlStateManager.disableTexture();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
            for (int l = 0; l < timesFrame.length; ++l) {
                int i1 = (l - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
                i1 += 155;
                float f = k;
                long j1 = 0L;
                if (gcs[l]) {
                    Lagometer.renderTime(l, timesFrame[l], i1, i1 / 2, 0, f, bufferbuilder);
                    continue;
                }
                Lagometer.renderTime(l, timesFrame[l], i1, i1, i1, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesServer[l], i1 / 2, i1 / 2, i1 / 2, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesTerrain[l], 0, i1, 0, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesVisibility[l], i1, i1, 0, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesChunkUpdate[l], i1, 0, 0, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesChunkUpload[l], i1, 0, i1, f, bufferbuilder);
                f -= (float)Lagometer.renderTime(l, timesScheduledExecutables[l], 0, 0, i1, f, bufferbuilder);
                float f2 = f - (float)Lagometer.renderTime(l, timesTick[l], 0, i1, i1, f, bufferbuilder);
            }
            Lagometer.renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, k, bufferbuilder);
            Lagometer.renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, k, bufferbuilder);
            tessellator.draw();
            GlStateManager.enableTexture();
            int i3 = k - 80;
            int j3 = k - 160;
            String s = Config.isShowFrameTime() ? "33" : "30";
            String s1 = Config.isShowFrameTime() ? "17" : "60";
            Lagometer.mc.fontRenderer.drawString(matrixStackIn, s, 2.0f, j3 + 1, -8947849);
            Lagometer.mc.fontRenderer.drawString(matrixStackIn, s, 1.0f, j3, -3881788);
            Lagometer.mc.fontRenderer.drawString(matrixStackIn, s1, 2.0f, i3 + 1, -8947849);
            Lagometer.mc.fontRenderer.drawString(matrixStackIn, s1, 1.0f, i3, -3881788);
            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture();
            float f1 = 1.0f - (float)((double)(System.currentTimeMillis() - MemoryMonitor.getStartTimeMs()) / 1000.0);
            f1 = Config.limit(f1, 0.0f, 1.0f);
            int k1 = (int)(170.0f + f1 * 85.0f);
            int l1 = (int)(100.0f + f1 * 55.0f);
            int i2 = (int)(10.0f + f1 * 10.0f);
            int j2 = k1 << 16 | l1 << 8 | i2;
            int k2 = 512 / scaleFactor + 2;
            int l2 = k / scaleFactor - 8;
            IngameGui ingamegui = Lagometer.mc.ingameGUI;
            IngameGui.fill(matrixStackIn, k2 - 1, l2 - 1, k2 + 50, l2 + 10, -1605349296);
            Lagometer.mc.fontRenderer.drawString(matrixStackIn, " " + MemoryMonitor.getGcRateMb() + " MB/s", k2, l2, j2);
            renderTimeNano = System.nanoTime() - i;
        }
    }

    private static long renderTime(int frameNum, long time, int r, int g, int b, float baseHeight, BufferBuilder tessellator) {
        long i = time / 200000L;
        if (i < 3L) {
            return 0L;
        }
        tessellator.pos((float)frameNum + 0.5f, baseHeight - (float)i + 0.5f, 0.0).color(r, g, b, 255).endVertex();
        tessellator.pos((float)frameNum + 0.5f, baseHeight + 0.5f, 0.0).color(r, g, b, 255).endVertex();
        return i;
    }

    private static long renderTimeDivider(int frameStart, int frameEnd, long time, int r, int g, int b, float baseHeight, BufferBuilder tessellator) {
        long i = time / 200000L;
        if (i < 3L) {
            return 0L;
        }
        tessellator.pos((float)frameStart + 0.5f, baseHeight - (float)i + 0.5f, 0.0).color(r, g, b, 255).endVertex();
        tessellator.pos((float)frameEnd + 0.5f, baseHeight - (float)i + 0.5f, 0.0).color(r, g, b, 255).endVertex();
        return i;
    }

    public static boolean isActive() {
        return active;
    }

    static {
        active = false;
        timerTick = new TimerNano();
        timerScheduledExecutables = new TimerNano();
        timerChunkUpload = new TimerNano();
        timerChunkUpdate = new TimerNano();
        timerVisibility = new TimerNano();
        timerTerrain = new TimerNano();
        timerServer = new TimerNano();
        timesFrame = new long[512];
        timesTick = new long[512];
        timesScheduledExecutables = new long[512];
        timesChunkUpload = new long[512];
        timesChunkUpdate = new long[512];
        timesVisibility = new long[512];
        timesTerrain = new long[512];
        timesServer = new long[512];
        gcs = new boolean[512];
        numRecordedFrameTimes = 0;
        prevFrameTimeNano = -1L;
        renderTimeNano = 0L;
    }

    public static class TimerNano {
        public long timeStartNano = 0L;
        public long timeNano = 0L;

        public void start() {
            if (active && this.timeStartNano == 0L) {
                this.timeStartNano = System.nanoTime();
            }
        }

        public void end() {
            if (active && this.timeStartNano != 0L) {
                this.timeNano += System.nanoTime() - this.timeStartNano;
                this.timeStartNano = 0L;
            }
        }

        private void reset() {
            this.timeNano = 0L;
            this.timeStartNano = 0L;
        }
    }
}
