package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.Module;
import dimasik.utils.time.TimerUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.PropertiesOrdered;
import org.apache.commons.lang3.SystemUtils;

public class ResourceLoadProgressGui
extends LoadingGui
implements IFastAccess {
    private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int field_238627_b_ = ColorHelper.PackedColor.packColor(255, 239, 50, 61);
    private static final int field_238628_c_ = field_238627_b_ & 0xFFFFFF;
    private final Minecraft mc;
    private final IAsyncReloader asyncReloader;
    private final Consumer<Optional<Throwable>> completedCallback;
    private final boolean reloading;
    private float progress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;
    private int colorBackground = field_238628_c_;
    private int colorBar = field_238628_c_;
    private int colorOutline = 0xFFFFFF;
    private int colorProgress = 0xFFFFFF;
    private GlBlendState blendState = null;
    private boolean fadeOut = false;
    private String visualName = "";
    private final TimerUtils timerUtils = new TimerUtils();
    private final String[] filePathList = ResourceLoadProgressGui.generatePathFiles();

    public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
        this.mc = p_i225928_1_;
        this.asyncReloader = p_i225928_2_;
        this.completedCallback = p_i225928_3_;
        this.reloading = false;
    }

    public static void loadLogoTexture(Minecraft mc) {
        mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new MojangLogoTexture());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        float f2;
        float f1;
        int i = this.mc.getMainWindow().getScaledWidth();
        int j = this.mc.getMainWindow().getScaledHeight();
        long k = Util.milliTime();
        if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }
        float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0f : -1.0f;
        float f3 = f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            this.fadeOut = true;
            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
            }
            int l = MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f);
            if (ClientManagers.isUnHook()) {
                ResourceLoadProgressGui.fill(matrixStack, 0, 0, i, j, this.colorBackground | l << 24);
            } else {
                VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/loading.png"), 0.0f, 0.0f, (float)((double)this.mc.getMainWindow().getWidth() / this.mc.getMainWindow().getGuiScaleFactor()), (float)((double)this.mc.getMainWindow().getHeight() / this.mc.getMainWindow().getGuiScaleFactor()), ColorHelpers.getColorWithAlpha(ColorHelpers.rgba(255, 255, 255, 255), l));
            }
            f2 = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else if (this.reloading) {
            if (this.mc.currentScreen != null && f1 < 1.0f) {
                this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            int i2 = MathHelper.ceil(MathHelper.clamp((double)f1, 0.15, 1.0) * 255.0);
            ResourceLoadProgressGui.fill(matrixStack, 0, 0, i, j, this.colorBackground | i2 << 24);
            f2 = MathHelper.clamp(f1, 0.0f, 1.0f);
        } else {
            if (ClientManagers.isUnHook()) {
                ResourceLoadProgressGui.fill(matrixStack, 0, 0, i, j, this.colorBackground | 0xFF000000);
            } else {
                VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/loading.png"), 0.0f, 0.0f, (float)((double)this.mc.getMainWindow().getWidth() / this.mc.getMainWindow().getGuiScaleFactor()), (float)((double)this.mc.getMainWindow().getHeight() / this.mc.getMainWindow().getGuiScaleFactor()));
            }
            f2 = 1.0f;
        }
        int j2 = (int)((double)this.mc.getMainWindow().getScaledWidth() * 0.5);
        int i1 = (int)((double)this.mc.getMainWindow().getScaledHeight() * 0.5);
        double d0 = Math.min((double)this.mc.getMainWindow().getScaledWidth() * 0.75, (double)this.mc.getMainWindow().getScaledHeight()) * 0.25;
        int j1 = (int)(d0 * 0.5);
        double d1 = d0 * 4.0;
        int k1 = (int)(d1 * 0.5);
        this.mc.getTextureManager().bindTexture(MOJANG_LOGO_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.alphaFunc(516, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f2);
        boolean flag = true;
        if (this.blendState != null) {
            this.blendState.apply();
            if (!this.blendState.isEnabled() && this.fadeOut) {
                flag = false;
            }
        }
        if (flag && ClientManagers.isUnHook()) {
            ResourceLoadProgressGui.blit(matrixStack, j2 - k1, i1 - j1, k1, (int)d0, -0.0625f, 0.0f, 120, 60, 120, 120);
            ResourceLoadProgressGui.blit(matrixStack, j2, i1 - j1, k1, (int)d0, 0.0625f, 60.0f, 120, 60, 120, 120);
        }
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableBlend();
        int l1 = (int)((double)this.mc.getMainWindow().getScaledHeight() * 0.8325);
        float f32 = this.asyncReloader.estimateExecutionSpeed();
        this.progress = MathHelper.clamp(this.progress * 0.95f + f32 * 0.050000012f, 0.0f, 1.0f);
        Reflector.ClientModLoader_renderProgressText.call(new Object[0]);
        if (f < 1.0f) {
            this.func_238629_a_(matrixStack, i / 2 - k1, l1 - 5, i / 2 + k1, l1 + 5, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
        }
        if (f >= 2.0f) {
            this.mc.setLoadingGui(null);
        }
        if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0f)) {
            this.fadeOutStart = Util.milliTime();
            try {
                this.asyncReloader.join();
                this.completedCallback.accept(Optional.empty());
            }
            catch (Throwable throwable) {
                this.completedCallback.accept(Optional.of(throwable));
            }
            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
            }
        }
    }

    private void func_238629_a_(MatrixStack p_238629_1_, int p_238629_2_, int p_238629_3_, int p_238629_4_, int p_238629_5_, float p_238629_6_) {
        int i = MathHelper.ceil((float)(p_238629_4_ - p_238629_2_ - 2) * this.progress);
        int j = Math.round(p_238629_6_ * 255.0f);
        if (this.colorBar != this.colorBackground) {
            int k = this.colorBar >> 16 & 0xFF;
            int l = this.colorBar >> 8 & 0xFF;
            int i1 = this.colorBar & 0xFF;
            int j1 = ColorHelper.PackedColor.packColor(j, k, l, i1);
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_4_, p_238629_5_, j1);
        }
        int j2 = this.colorOutline >> 16 & 0xFF;
        int k2 = this.colorOutline >> 8 & 0xFF;
        int l2 = this.colorOutline & 0xFF;
        int i3 = ColorHelper.PackedColor.packColor(j, j2, k2, l2);
        if (ClientManagers.isUnHook()) {
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_2_ + 1, p_238629_3_, p_238629_4_ - 1, p_238629_3_ + 1, i3);
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_2_ + 1, p_238629_5_, p_238629_4_ - 1, p_238629_5_ - 1, i3);
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_2_ + 1, p_238629_5_, i3);
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_4_, p_238629_3_, p_238629_4_ - 1, p_238629_5_, i3);
        }
        int k1 = this.colorProgress >> 16 & 0xFF;
        int l1 = this.colorProgress >> 8 & 0xFF;
        int i2 = this.colorProgress & 0xFF;
        i3 = ColorHelper.PackedColor.packColor(j, k1, l1, i2);
        if (ClientManagers.isUnHook()) {
            ResourceLoadProgressGui.fill(p_238629_1_, p_238629_2_ + 2, p_238629_3_ + 2, p_238629_2_ + i, p_238629_5_ - 2, i3);
        } else {
            GLHelpers.INSTANCE.rescale(1.0);
            VisualHelpers.drawRoundedRect(p_238629_1_, (float)this.mc.getMainWindow().getWidth() / 2.0f - 384.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f + 37.5f, 768.0f, 18.0f, 9.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999));
            VisualHelpers.drawRoundedRect(p_238629_1_, (float)this.mc.getMainWindow().getWidth() / 2.0f - 384.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f + 37.5f, 768.0f * this.progress, 18.0f, 9.0f, ColorHelpers.rgba(48, 207, 151, 255));
            suisse_intl.drawText(p_238629_1_, Math.round(this.progress * 100.0f) + "%", (float)this.mc.getMainWindow().getWidth() / 2.0f - 384.0f + 768.0f * this.progress - suisse_intl.getWidth(Math.round(this.progress * 100.0f) + "%", 12.0f) - 6.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f + 39.5f, ColorHelpers.rgba(255, 255, 255, 255), 12.0f);
            suisse_intl.drawCenteredText(p_238629_1_, "Dimasik Client", (float)this.mc.getMainWindow().getWidth() / 2.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f - 9.5f, ColorHelpers.rgba(255, 255, 255, 255), 16.0f);
            Random random = new Random();
            if (this.timerUtils.hasTimeElapsed(100L)) {
                this.visualName = this.filePathList[random.nextInt(this.filePathList.length)];
                this.timerUtils.reset();
            }
            suisse_intl.drawCenteredText(p_238629_1_, this.visualName, (float)this.mc.getMainWindow().getWidth() / 2.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f + 69.5f, ColorHelpers.rgba(255, 255, 255, 122.39999999999999), 14.0f);
            suisse_intl.drawCenteredText(p_238629_1_, Math.round(this.progress * 2048.0f) + " / 2048", (float)this.mc.getMainWindow().getWidth() / 2.0f, (float)this.mc.getMainWindow().getHeight() / 2.0f + 89.5f, ColorHelpers.rgba(255, 255, 255, 61.199999999999996), 12.0f);
            GLHelpers.INSTANCE.rescaleMC();
        }
    }

    public static String[] generatePathFiles() {
        List<String> moduleNames = Load.getInstance().getHooks().getModuleManagers().stream().map(Module::getName).toList();
        String path = String.format("C:\\Users\\%s\\Dimasik\\assets\\modules\\", SystemUtils.USER_NAME);
        String[] fileNames = new String[moduleNames.size()];
        for (int i = 0; i < moduleNames.size(); ++i) {
            fileNames[i] = path + moduleNames.get(i) + ".java";
        }
        return fileNames;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    public void update() {
        this.colorBackground = field_238628_c_;
        this.colorBar = field_238628_c_;
        this.colorOutline = 0xFFFFFF;
        this.colorProgress = 0xFFFFFF;
        if (Config.isCustomColors()) {
            try {
                String s = "optifine/color.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);
                if (!Config.hasResource(resourcelocation)) {
                    return;
                }
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                Config.dbg("Loading " + s);
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                this.colorBackground = ResourceLoadProgressGui.readColor(properties, "screen.loading", this.colorBackground);
                this.colorOutline = ResourceLoadProgressGui.readColor(properties, "screen.loading.outline", this.colorOutline);
                this.colorBar = ResourceLoadProgressGui.readColor(properties, "screen.loading.bar", this.colorBar);
                this.colorProgress = ResourceLoadProgressGui.readColor(properties, "screen.loading.progress", this.colorProgress);
                this.blendState = ShaderPackParser.parseBlendState(properties.getProperty("screen.loading.blend"));
            }
            catch (Exception exception) {
                Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_, int p_readColor_2_) {
        String s = p_readColor_0_.getProperty(p_readColor_1_);
        if (s == null) {
            return p_readColor_2_;
        }
        int i = ResourceLoadProgressGui.parseColor(s = s.trim(), p_readColor_2_);
        if (i < 0) {
            Config.warn("Invalid color: " + p_readColor_1_ + " = " + s);
            return i;
        }
        Config.dbg(p_readColor_1_ + " = " + s);
        return i;
    }

    private static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        }
        p_parseColor_0_ = p_parseColor_0_.trim();
        try {
            return Integer.parseInt(p_parseColor_0_, 16) & 0xFFFFFF;
        }
        catch (NumberFormatException numberformatexception) {
            return p_parseColor_1_;
        }
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    static class MojangLogoTexture
    extends SimpleTexture {
        public MojangLogoTexture() {
            super(MOJANG_LOGO_TEXTURE);
        }

        @Override
        protected SimpleTexture.TextureData getTextureData(IResourceManager resourceManager) {
            SimpleTexture.TextureData textureData;
            block8: {
                Minecraft minecraft = Minecraft.getInstance();
                VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();
                InputStream inputstream = MojangLogoTexture.getLogoInputStream(resourceManager, vanillapack);
                try {
                    textureData = new SimpleTexture.TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
                    if (inputstream == null) break block8;
                }
                catch (Throwable throwable) {
                    try {
                        if (inputstream != null) {
                            try {
                                inputstream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException ioexception1) {
                        return new SimpleTexture.TextureData(ioexception1);
                    }
                }
                inputstream.close();
            }
            return textureData;
        }

        private static InputStream getLogoInputStream(IResourceManager p_getLogoInputStream_0_, VanillaPack p_getLogoInputStream_1_) throws IOException {
            return p_getLogoInputStream_0_.hasResource(MOJANG_LOGO_TEXTURE) ? p_getLogoInputStream_0_.getResource(MOJANG_LOGO_TEXTURE).getInputStream() : p_getLogoInputStream_1_.getResourceStream(ResourcePackType.CLIENT_RESOURCES, MOJANG_LOGO_TEXTURE);
        }
    }
}
