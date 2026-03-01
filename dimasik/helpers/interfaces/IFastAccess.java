package dimasik.helpers.interfaces;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.IRenderCall;
import dimasik.helpers.interfaces.IMouse;
import dimasik.utils.shader.interfaces.ShaderList;
import dimasik.utils.shader.main.ProfilerShader;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public interface IFastAccess
extends IMouse {
    public static final Minecraft mc = Minecraft.getInstance();
    public static final ConcurrentLinkedQueue<IRenderCall> BLUR_RUNNABLES = Queues.newConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue<IRenderCall> WHITE_RECT_RUNNABLES = Queues.newConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue<IRenderCall> BLACK_RECT_RUNNABLES = Queues.newConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue<IRenderCall> BLOOM_RUNNABLES = Queues.newConcurrentLinkedQueue();
    public static final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
    public static final ProfilerShader blurProfiler = new ProfilerShader();
    public static final ProfilerShader bloomProfiler = new ProfilerShader();
    public static final ProfilerShader whiteRectProfiler = new ProfilerShader();
    public static final ProfilerShader blackRectProfiler = new ProfilerShader();

    default public void blurSetting(float partialTicks, float radius, float compression) {
        if (!BLUR_RUNNABLES.isEmpty()) {
            blurProfiler.start();
            ShaderList.BLUR_SHADER.setRadius(radius);
            ShaderList.BLUR_SHADER.setCompression(compression);
            ShaderList.BLUR_SHADER.run(partialTicks, BLUR_RUNNABLES);
            blurProfiler.stop();
            blurProfiler.reset();
        }
        this.clearShadersRunnables();
    }

    default public void bloomSetting(float partialTicks, float glow) {
        if (!BLOOM_RUNNABLES.isEmpty()) {
            bloomProfiler.start();
            ShaderList.BLOOM_SHADER.setGlow(glow);
            ShaderList.BLOOM_SHADER.run(partialTicks, BLOOM_RUNNABLES);
            bloomProfiler.stop();
            bloomProfiler.reset();
        }
        this.clearShadersRunnables();
    }

    default public void renderRects(float partialTicks) {
        if (!BLACK_RECT_RUNNABLES.isEmpty()) {
            blackRectProfiler.start();
            ShaderList.BLACK_RECT_SHADER.run(partialTicks, BLACK_RECT_RUNNABLES);
            blackRectProfiler.stop();
            blackRectProfiler.reset();
        }
        if (!WHITE_RECT_RUNNABLES.isEmpty()) {
            whiteRectProfiler.start();
            ShaderList.WHITE_RECT_SHADER.run(partialTicks, WHITE_RECT_RUNNABLES);
            whiteRectProfiler.stop();
            whiteRectProfiler.reset();
        }
        this.clearRectsRunnables();
    }

    private void clearShadersRunnables() {
        BLUR_RUNNABLES.clear();
        BLOOM_RUNNABLES.clear();
    }

    default public void clearRectsRunnables() {
        BLACK_RECT_RUNNABLES.clear();
        WHITE_RECT_RUNNABLES.clear();
    }

    default public float getRandom(float randomVal) {
        return (new Random().nextFloat() * 2.0f - 1.0f) * randomVal;
    }
}
