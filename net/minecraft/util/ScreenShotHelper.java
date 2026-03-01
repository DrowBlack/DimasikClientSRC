package net.minecraft.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScreenShotHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
        ScreenShotHelper.saveScreenshot(gameDirectory, null, width, height, buffer, messageConsumer);
    }

    public static void saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ScreenShotHelper.saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer));
        } else {
            ScreenShotHelper.saveScreenshotRaw(gameDirectory, screenshotName, width, height, buffer, messageConsumer);
        }
    }

    private static void saveScreenshotRaw(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> messageConsumer) {
        boolean flag;
        Minecraft minecraft = Config.getMinecraft();
        MainWindow mainwindow = minecraft.getMainWindow();
        GameSettings gamesettings = Config.getGameSettings();
        int i = mainwindow.getFramebufferWidth();
        int j = mainwindow.getFramebufferHeight();
        int k = gamesettings.guiScale;
        int l = mainwindow.calcGuiScale(minecraft.gameSettings.guiScale, minecraft.gameSettings.forceUnicodeFont);
        int i1 = Config.getScreenshotSize();
        boolean bl = flag = GLX.isUsingFBOs() && i1 > 1;
        if (flag) {
            gamesettings.guiScale = l * i1;
            mainwindow.resizeFramebuffer(i * i1, j * i1);
            GlStateManager.pushMatrix();
            GlStateManager.clear(16640);
            minecraft.getFramebuffer().bindFramebuffer(true);
            GlStateManager.enableTexture();
            minecraft.gameRenderer.updateCameraAndRender(minecraft.getRenderPartialTicks(), System.nanoTime(), true);
        }
        NativeImage nativeimage = ScreenShotHelper.createScreenshot(width, height, buffer);
        if (flag) {
            minecraft.getFramebuffer().unbindFramebuffer();
            GlStateManager.popMatrix();
            Config.getGameSettings().guiScale = k;
            mainwindow.resizeFramebuffer(i, j);
        }
        File file1 = new File(gameDirectory, "screenshots");
        file1.mkdir();
        File file2 = screenshotName == null ? ScreenShotHelper.getTimestampedPNGFileForDirectory(file1) : new File(file1, screenshotName);
        Object object = null;
        if (Reflector.ForgeHooksClient_onScreenshot.exists()) {
            object = Reflector.call(Reflector.ForgeHooksClient_onScreenshot, nativeimage, file2);
            if (Reflector.callBoolean(object, Reflector.Event_isCanceled, new Object[0])) {
                ITextComponent itextcomponent = (ITextComponent)Reflector.call(object, Reflector.ScreenshotEvent_getCancelMessage, new Object[0]);
                messageConsumer.accept(itextcomponent);
                return;
            }
            file2 = (File)Reflector.call(object, Reflector.ScreenshotEvent_getScreenshotFile, new Object[0]);
        }
        File file3 = file2;
        Object object1 = object;
        Util.getRenderingService().execute(() -> {
            try {
                nativeimage.write(file3);
                IFormattableTextComponent itextcomponent1 = new StringTextComponent(file3.getName()).mergeStyle(TextFormatting.UNDERLINE).modifyStyle(p_lambda$null$1_1_ -> p_lambda$null$1_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath())));
                if (object1 != null && Reflector.call(object1, Reflector.ScreenshotEvent_getResultMessage, new Object[0]) != null) {
                    messageConsumer.accept((ITextComponent)Reflector.call(object1, Reflector.ScreenshotEvent_getResultMessage, new Object[0]));
                } else {
                    messageConsumer.accept(new TranslationTextComponent("screenshot.success", itextcomponent1));
                }
            }
            catch (Exception exception1) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception1);
                messageConsumer.accept(new TranslationTextComponent("screenshot.failure", exception1.getMessage()));
            }
            finally {
                nativeimage.close();
            }
        });
    }

    public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
        if (!GLX.isUsingFBOs()) {
            NativeImage nativeimage1 = new NativeImage(width, height, false);
            nativeimage1.downloadFromFramebuffer(true);
            nativeimage1.flip();
            return nativeimage1;
        }
        width = framebufferIn.framebufferTextureWidth;
        height = framebufferIn.framebufferTextureHeight;
        NativeImage nativeimage = new NativeImage(width, height, false);
        RenderSystem.bindTexture(framebufferIn.func_242996_f());
        nativeimage.downloadFromTexture(0, true);
        nativeimage.flip();
        return nativeimage;
    }

    private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
        String s = DATE_FORMAT.format(new Date());
        int i = 1;
        File file1;
        while ((file1 = new File(gameDirectory, s + (String)(i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file1;
    }
}
