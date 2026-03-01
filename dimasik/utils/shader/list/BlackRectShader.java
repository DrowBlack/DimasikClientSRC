package dimasik.utils.shader.list;

import com.mojang.blaze3d.systems.IRenderCall;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.utils.shader.AbstractShader;
import java.awt.Color;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlackRectShader
extends AbstractShader {
    @Override
    public void run(float partialTicks, ConcurrentLinkedQueue<IRenderCall> runnable) {
        if (mc.getMainWindow().isClosed()) {
            return;
        }
        this.setActive(this.isActive() || !runnable.isEmpty());
        if (this.isActive()) {
            StencilHelpers.init();
            runnable.forEach(IRenderCall::execute);
            StencilHelpers.read(1);
            VisualHelpers.drawRect(0.0f, 0.0f, mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), Color.BLACK.getRGB());
            StencilHelpers.uninit();
        }
    }

    @Override
    public void update() {
    }
}
