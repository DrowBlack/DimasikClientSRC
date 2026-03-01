package dimasik.utils.shader.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.IRenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IShaderAccess;
import dimasik.helpers.visual.ShaderHelpers;
import dimasik.utils.shader.AbstractShader;
import java.nio.FloatBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class BloomShader
extends AbstractShader
implements IShaderAccess {
    private final Framebuffer inputFramebuffer = new Framebuffer(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
    private final Framebuffer outputFramebuffer = new Framebuffer(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), true, Minecraft.IS_RUNNING_ON_MAC);

    @Override
    public void run(float partialTicks, ConcurrentLinkedQueue<IRenderCall> runnable) {
        if (mc.getMainWindow().isClosed()) {
            return;
        }
        this.update();
        this.setActive(this.isActive() || !runnable.isEmpty());
        if (this.isActive()) {
            float exposure = 1.5f;
            this.setupBuffer(this.inputFramebuffer);
            this.setupBuffer(this.outputFramebuffer);
            this.inputFramebuffer.bindFramebuffer(true);
            runnable.forEach(IRenderCall::execute);
            this.inputFramebuffer.unbindFramebuffer();
            this.outputFramebuffer.bindFramebuffer(true);
            BLOOM.useProgram();
            BLOOM.setupUniform1f("radius", this.getGlow());
            BLOOM.setupUniform1f("exposure", exposure);
            BLOOM.setupUniform1i("textureIn", 0);
            BLOOM.setupUniform1i("textureToCheck", 20);
            BLOOM.setupUniform1i("avoidTexture", 1);
            FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
            int i = 0;
            while ((float)i <= this.getGlow()) {
                weightBuffer.put(this.calculateGaussianValue(i, this.getGlow() / 2.0f));
                ++i;
            }
            weightBuffer.rewind();
            RenderSystem.glUniform1(BLOOM.getUniform("weights"), weightBuffer);
            BLOOM.setupUniform2f("texelSize", 1.0f / (float)Minecraft.getInstance().getMainWindow().getWidth(), 1.0f / (float)Minecraft.getInstance().getMainWindow().getHeight());
            BLOOM.setupUniform2f("direction", 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 770);
            GL30.glAlphaFunc(516, 1.0E-4f);
            this.inputFramebuffer.bindFramebufferTexture();
            ShaderHelpers.drawQuads();
            mc.getFramebuffer().bindFramebuffer(false);
            GlStateManager.blendFunc(770, 771);
            BLOOM.setupUniform2f("direction", 0.0f, 1.0f);
            this.outputFramebuffer.bindFramebufferTexture();
            GL30.glActiveTexture(34004);
            this.inputFramebuffer.bindFramebufferTexture();
            GL30.glActiveTexture(33984);
            ShaderHelpers.drawQuads();
            BLOOM.unloadProgram();
            this.outputFramebuffer.unbindFramebuffer();
            GlStateManager.bindTexture(0);
            GlStateManager.disableBlend();
            mc.getFramebuffer().bindFramebuffer(false);
        }
    }

    private float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (double)(sigma * sigma));
        return (float)(output * Math.exp((double)(-(x * x)) / (2.0 * (double)(sigma * sigma))));
    }

    private Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth() || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
            frameBuffer.resize(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), Minecraft.IS_RUNNING_ON_MAC);
        } else {
            frameBuffer.framebufferClear(false);
        }
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        return frameBuffer;
    }

    @Override
    public void update() {
        this.setActive(false);
    }
}
