package dimasik.utils.shader.list;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.IRenderCall;
import dimasik.helpers.interfaces.IShaderAccess;
import dimasik.utils.shader.AbstractShader;
import dimasik.utils.shader.main.KernelBlur;
import java.nio.FloatBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

public class BlurShader
extends AbstractShader
implements IShaderAccess {
    private Framebuffer inputFramebuffer = new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
    private Framebuffer outputFramebuffer = new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
    private KernelBlur gaussianKernel = new KernelBlur(0);

    @Override
    public void run(float partialTicks, ConcurrentLinkedQueue<IRenderCall> runnable) {
        if (mc.getMainWindow().isClosed()) {
            return;
        }
        this.update();
        this.setActive(this.isActive() || !runnable.isEmpty());
        if (this.isActive()) {
            this.inputFramebuffer.bindFramebuffer(true);
            runnable.forEach(IRenderCall::execute);
            float radius = this.getRadius();
            float compression = this.getCompression();
            this.outputFramebuffer.bindFramebuffer(true);
            BLUR.useProgram();
            if ((float)this.gaussianKernel.getSize() != radius) {
                this.gaussianKernel = new KernelBlur((int)radius);
                this.gaussianKernel.compute();
                FloatBuffer buffer = BufferUtils.createFloatBuffer((int)radius);
                buffer.put(this.gaussianKernel.getKernel());
                buffer.flip();
                BLUR.setupUniform1f("u_radius", radius);
                BLUR.setupUniformBF("u_kernel", buffer);
                BLUR.setupUniform1i("u_diffuse_sampler", 0);
                BLUR.setupUniform1i("u_other_sampler", 20);
            }
            BLUR.setupUniform2f("u_texel_size", 1.0f / (float)mc.getMainWindow().getWidth(), 1.0f / (float)mc.getMainWindow().getHeight());
            BLUR.setupUniform2f("u_direction", compression, 0.0f);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.0f);
            mc.getFramebuffer().bindFramebufferTexture();
            BLUR.drawQuads();
            mc.getFramebuffer().bindFramebuffer(true);
            BLUR.setupUniform2f("u_direction", 0.0f, compression);
            this.outputFramebuffer.bindFramebufferTexture();
            GL13.glActiveTexture(34004);
            this.inputFramebuffer.bindFramebufferTexture();
            GL13.glActiveTexture(33984);
            BLUR.drawQuads();
            GlStateManager.disableBlend();
            BLUR.unloadProgram();
        }
    }

    @Override
    public void update() {
        this.setActive(false);
        if (mc.getMainWindow().getWidth() != this.inputFramebuffer.framebufferWidth || mc.getMainWindow().getFramebufferHeight() != this.inputFramebuffer.framebufferHeight) {
            this.inputFramebuffer.deleteFramebuffer();
            this.inputFramebuffer = new Framebuffer(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
            this.outputFramebuffer.deleteFramebuffer();
            this.outputFramebuffer = new Framebuffer(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getScaledHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
        } else {
            this.inputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            this.outputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        }
    }
}
