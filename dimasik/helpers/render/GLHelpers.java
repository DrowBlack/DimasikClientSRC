package dimasik.helpers.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IFastAccess;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL11;

public class GLHelpers
implements IFastAccess {
    public static final GLHelpers INSTANCE = new GLHelpers();

    public void rescale(double factor) {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            factor *= 2.0;
        }
        mc.getMainWindow().setGuiScale(factor);
        this.rescale((double)mc.getMainWindow().getFramebufferWidth() / factor, (double)mc.getMainWindow().getFramebufferHeight() / factor);
    }

    public void rescaleMC() {
        MainWindow mainWindow = mc.getMainWindow();
        int i = mainWindow.calcGuiScale(Minecraft.getInstance().gameSettings.guiScale, Minecraft.getInstance().getForceUnicodeFont());
        mainWindow.setGuiScale(i);
        this.rescale((double)mainWindow.getFramebufferWidth() / mainWindow.getGuiScaleFactor(), (double)mainWindow.getFramebufferHeight() / mainWindow.getGuiScaleFactor());
    }

    public void scaleElements(float xCenter, float yCenter, float scale, Runnable runnable) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(xCenter, yCenter, 0.0f);
        RenderSystem.scalef(scale, scale, 1.0f);
        RenderSystem.translatef(-xCenter, -yCenter, 0.0f);
        runnable.run();
        RenderSystem.popMatrix();
    }

    public void drawItemStack(MatrixStack matrixStack, ItemStack stack, float x, float y, boolean withoutOverlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        if (scale) {
            GL11.glScaled(scaleValue, scaleValue, scaleValue);
        }
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (withoutOverlay) {
            mc.getItemRenderer().renderItemOverlays(GLHelpers.mc.fontRenderer, stack, 0, 0);
        }
        RenderSystem.popMatrix();
    }

    public void rescale(double width, double height) {
        RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0f, 0.0f, -2000.0f);
    }

    public void scaleAnimation(MatrixStack matrixStack, float x, float y, float width, float height, float scaleValue) {
        matrixStack.translate(x + width / 2.0f, y + height / 2.0f, 0.0);
        matrixStack.scale(scaleValue, scaleValue, scaleValue);
        matrixStack.translate(-(x + width / 2.0f), -(y + height / 2.0f), 0.0);
    }

    public void scaleAnimationHeight(MatrixStack matrixStack, float x, float y, float width, float height, float scaleValue) {
        matrixStack.translate(x + width / 2.0f, y + height / 2.0f, 0.0);
        matrixStack.scale(1.0f, scaleValue, 1.0f);
        matrixStack.translate(-(x + width / 2.0f), -(y + height / 2.0f), 0.0);
    }

    public void scaleAnimation(float x, float y, float width, float height, float scaleValue) {
        RenderSystem.translatef(x + width / 2.0f, y + height / 2.0f, 0.0f);
        RenderSystem.scalef(scaleValue, scaleValue, scaleValue);
        RenderSystem.translatef(-(x + width / 2.0f), -(y + height / 2.0f), 0.0f);
    }

    public Vector2f normalizeCords(double mouseX, double mouseY, double factor) {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            factor *= 2.0;
        }
        return new Vector2f((float)(mouseX * mc.getMainWindow().getGuiScaleFactor() / factor), (float)(mouseY * mc.getMainWindow().getGuiScaleFactor() / factor));
    }

    public void setupRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    public void resetRenderState() {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.clearCurrentColor();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
    }
}
