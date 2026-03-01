package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.interfaces.ITranslate;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.client.ClientManagers;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainMenuScreen
extends Screen {
    private static final Logger field_238656_b_ = LogManager.getLogger();
    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
    private final boolean showTitleWronglySpelled;
    @Nullable
    private String splashText;
    private Button buttonResetDemo;
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private boolean hasCheckedForRealmsNotification;
    private Screen realmsNotification;
    private int widthCopyright;
    private int widthCopyrightRest;
    private final RenderSkybox panorama = new RenderSkybox(PANORAMA_RESOURCES);
    private final boolean showFadeInAnimation;
    private long firstRenderTime;
    private Screen modUpdateNotification;

    public MainMenuScreen() {
        this(false);
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
        this.showFadeInAnimation = fadeIn;
        this.showTitleWronglySpelled = (double)new Random().nextFloat() < 1.0E-4;
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
    }

    @Override
    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotification.tick();
        }
        for (Widget widget : this.buttons) {
            if (!(widget instanceof CustomButton)) continue;
            CustomButton customButton = (CustomButton)widget;
            customButton.tick();
        }
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (ClientManagers.isUnHook()) {
            if (this.splashText == null) {
                this.splashText = this.minecraft.getSplashes().getSplashText();
            }
            this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
            this.widthCopyrightRest = this.width - this.widthCopyright - 2;
            int i = 24;
            int j = this.height / 4 + 48;
            Button button = null;
            if (this.minecraft.isDemo()) {
                this.addDemoButtons(j, 24);
            } else {
                this.addSingleplayerMultiplayerButtons(j, 24);
                if (Reflector.ModListScreen_Constructor.exists()) {
                    button = ReflectorForge.makeButtonMods(this, j, 24);
                    this.addButton(button);
                }
            }
            this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, p_lambda$init$0_1_ -> this.minecraft.displayGuiScreen(new LanguageScreen((Screen)this, this.minecraft.gameSettings, this.minecraft.getLanguageManager())), new TranslationTextComponent("narrator.button.language")));
            this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.options"), p_lambda$init$1_1_ -> this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings))));
            this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslationTextComponent("menu.quit"), p_lambda$init$2_1_ -> this.minecraft.shutdown()));
            this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, p_lambda$init$3_1_ -> this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings)), new TranslationTextComponent("narrator.button.accessibility")));
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.gameSettings.realmsNotifications && !this.hasCheckedForRealmsNotification) {
                RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
                this.realmsNotification = realmsbridgescreen.func_239555_b_(this);
                this.hasCheckedForRealmsNotification = true;
            }
            if (this.areRealmsNotificationsEnabled()) {
                this.realmsNotification.init(this.minecraft, this.width, this.height);
            }
            if (Reflector.NotificationModUpdateScreen_init.exists()) {
                this.modUpdateNotification = (Screen)Reflector.call(Reflector.NotificationModUpdateScreen_init, this, button);
            }
            return;
        }
        ClientManagers.setChanged(true);
        int buttonWidth = 258;
        int buttonHeight = 48;
        int x = (int)((double)this.minecraft.getMainWindow().getScaledWidth() * this.minecraft.getMainWindow().getGuiScaleFactor() / 2.0 - (double)((float)buttonWidth / 2.0f));
        int y = (int)((double)this.minecraft.getMainWindow().getScaledHeight() * this.minecraft.getMainWindow().getGuiScaleFactor() / 2.0);
        int offset = (buttonHeight + 8) * -2;
        this.addButton(new CustomButton(x, y + offset, buttonWidth, buttonHeight, new StringTextComponent("Singleplayer"), p_onPress_1_ -> this.minecraft.displayGuiScreen(new WorldSelectionScreen(this)), false));
        this.addButton(new CustomButton(x, y + (offset += buttonHeight + 8), buttonWidth, buttonHeight, new StringTextComponent("Multiplayer"), p_onPress_1_ -> this.minecraft.displayGuiScreen(new MultiplayerScreen(this)), false));
        this.addButton(new CustomButton(x, y + (offset += buttonHeight + 8), buttonWidth, buttonHeight, new StringTextComponent("AltManager"), p_onPress_1_ -> this.minecraft.displayGuiScreen(Load.getInstance().getAltScreen()), false));
        this.addButton(new CustomButton(x, y + (offset += buttonHeight + 8), 202, buttonHeight, new StringTextComponent("Options"), p_onPress_1_ -> this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings)), false));
        this.addButton(new CustomButton(x + 210, y + offset, buttonHeight, buttonHeight, new StringTextComponent("Quit"), p_onPress_1_ -> this.minecraft.shutdownMinecraftApplet(), true));
        this.addButton(new CustomButton((int)((double)this.minecraft.getMainWindow().getScaledWidth() * this.minecraft.getMainWindow().getGuiScaleFactor() - 60.0), (int)((double)this.minecraft.getMainWindow().getScaledHeight() * this.minecraft.getMainWindow().getGuiScaleFactor() - 60.0), 50, 50, new StringTextComponent("Language"), p_onPress_1_ -> {
            if (ClientManagers.getLanguage().equals("ru")) {
                ClientManagers.changeLanguage("eng");
            } else if (ClientManagers.getLanguage().equals("eng")) {
                ClientManagers.changeLanguage("ru");
            }
            ClientManagers.setChanged(true);
        }, false));
    }

    private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.singleplayer"), p_lambda$addSingleplayerMultiplayerButtons$4_1_ -> this.minecraft.displayGuiScreen(new WorldSelectionScreen(this))));
        boolean flag = this.minecraft.isMultiplayerEnabled();
        Button.ITooltip button$itooltip = flag ? Button.field_238486_s_ : (p_lambda$addSingleplayerMultiplayerButtons$5_1_, p_lambda$addSingleplayerMultiplayerButtons$5_2_, p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_) -> {
            if (!p_lambda$addSingleplayerMultiplayerButtons$5_1_.active) {
                this.renderTooltip(p_lambda$addSingleplayerMultiplayerButtons$5_2_, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), p_lambda$addSingleplayerMultiplayerButtons$5_3_, p_lambda$addSingleplayerMultiplayerButtons$5_4_);
            }
        };
        this.addButton(new Button((int)(this.width / 2 - 100), (int)(yIn + rowHeightIn * 1), (int)200, (int)20, (ITextComponent)new TranslationTextComponent((String)"menu.multiplayer"), (Button.IPressable)(Button.IPressable)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/button/AbstractButton;)V, lambda$addSingleplayerMultiplayerButtons$12(net.minecraft.client.gui.widget.button.AbstractButton ), (Lnet/minecraft/client/gui/widget/button/AbstractButton;)V)((MainMenuScreen)this), (Button.ITooltip)button$itooltip)).active = flag;
        this.addButton(new Button((int)(this.width / 2 - 100), (int)(yIn + rowHeightIn * 2), (int)200, (int)20, (ITextComponent)new TranslationTextComponent((String)"menu.online"), (Button.IPressable)(Button.IPressable)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/button/AbstractButton;)V, lambda$addSingleplayerMultiplayerButtons$13(net.minecraft.client.gui.widget.button.AbstractButton ), (Lnet/minecraft/client/gui/widget/button/AbstractButton;)V)((MainMenuScreen)this), (Button.ITooltip)button$itooltip)).active = flag;
        if (Reflector.ModListScreen_Constructor.exists() && this.buttons.size() > 0) {
            Widget widget = (Widget)this.buttons.get(this.buttons.size() - 1);
            widget.x = this.width / 2 + 2;
            widget.setWidth(98);
        }
    }

    private void addDemoButtons(int yIn, int rowHeightIn) {
        boolean flag = this.func_243319_k();
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, new TranslationTextComponent("menu.playdemo"), p_lambda$addDemoButtons$8_2_ -> {
            if (flag) {
                this.minecraft.loadWorld("Demo_World");
            } else {
                DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.func_239770_b_();
                this.minecraft.createWorld("Demo_World", MinecraftServer.DEMO_WORLD_SETTINGS, dynamicregistries$impl, DimensionGeneratorSettings.func_242752_a(dynamicregistries$impl));
            }
        }));
        this.buttonResetDemo = this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, new TranslationTextComponent("menu.resetdemo"), p_lambda$addDemoButtons$9_1_ -> {
            SaveFormat saveformat = this.minecraft.getSaveLoader();
            try (SaveFormat.LevelSave saveformat$levelsave = saveformat.getLevelSave("Demo_World");){
                WorldSummary worldsummary = saveformat$levelsave.readWorldSummary();
                if (worldsummary != null) {
                    this.minecraft.displayGuiScreen(new ConfirmScreen(this::deleteDemoWorld, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", worldsummary.getDisplayName()), new TranslationTextComponent("selectWorld.deleteButton"), DialogTexts.GUI_CANCEL));
                }
            }
            catch (IOException ioexception1) {
                SystemToast.func_238535_a_(this.minecraft, "Demo_World");
                field_238656_b_.warn("Failed to access demo world", (Throwable)ioexception1);
            }
        }));
        this.buttonResetDemo.active = flag;
    }

    private boolean func_243319_k() {
        boolean bl;
        block8: {
            SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getSaveLoader().getLevelSave("Demo_World");
            try {
                boolean bl2 = bl = saveformat$levelsave.readWorldSummary() != null;
                if (saveformat$levelsave == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (saveformat$levelsave != null) {
                        try {
                            saveformat$levelsave.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ioexception1) {
                    SystemToast.func_238535_a_(this.minecraft, "Demo_World");
                    field_238656_b_.warn("Failed to read demo world data", (Throwable)ioexception1);
                    return false;
                }
            }
            saveformat$levelsave.close();
        }
        return bl;
    }

    private void switchToRealms() {
        RealmsBridgeScreen realmsbridgescreen = new RealmsBridgeScreen();
        realmsbridgescreen.func_231394_a_(this);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (ClientManagers.isUnHook()) {
            if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
                this.firstRenderTime = Util.milliTime();
            }
            float f = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0f : 1.0f;
            GlStateManager.disableDepthTest();
            MainMenuScreen.fill(matrixStack, 0, 0, this.width, this.height, -1);
            this.panorama.render(partialTicks, MathHelper.clamp(f, 0.0f, 1.0f));
            int i = 274;
            int j = this.width / 2 - 137;
            int k = 30;
            this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0f, 1.0f)) : 1.0f);
            MainMenuScreen.blit(matrixStack, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
            float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
            int l = MathHelper.ceil(f1 * 255.0f) << 24;
            if ((l & 0xFC000000) != 0) {
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, f1);
                if (this.showTitleWronglySpelled) {
                    this.blitBlackOutline(j, 30, (p_lambda$render$10_2_, p_lambda$render$10_3_) -> {
                        this.blit(matrixStack, p_lambda$render$10_2_ + 0, (int)p_lambda$render$10_3_, 0, 0, 99, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99, (int)p_lambda$render$10_3_, 129, 0, 27, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26, (int)p_lambda$render$10_3_, 126, 0, 3, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 99 + 26 + 3, (int)p_lambda$render$10_3_, 99, 0, 26, 44);
                        this.blit(matrixStack, p_lambda$render$10_2_ + 155, (int)p_lambda$render$10_3_, 0, 45, 155, 44);
                    });
                } else {
                    this.blitBlackOutline(j, 30, (p_lambda$render$11_2_, p_lambda$render$11_3_) -> {
                        this.blit(matrixStack, p_lambda$render$11_2_ + 0, (int)p_lambda$render$11_3_, 0, 0, 155, 44);
                        this.blit(matrixStack, p_lambda$render$11_2_ + 155, (int)p_lambda$render$11_3_, 0, 45, 155, 44);
                    });
                }
                this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
                MainMenuScreen.blit(matrixStack, j + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
                if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_renderMainMenu, this, matrixStack, this.font, this.width, this.height, l);
                }
                if (this.splashText != null) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.width / 2 + 90, 70.0f, 0.0f);
                    RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
                    float f2 = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
                    f2 = f2 * 100.0f / (float)(this.font.getStringWidth(this.splashText) + 32);
                    RenderSystem.scalef(f2, f2, f2);
                    MainMenuScreen.drawCenteredString(matrixStack, this.font, this.splashText, 0, -8, 0xFFFF00 | l);
                    RenderSystem.popMatrix();
                }
                String s = "Minecraft " + SharedConstants.getVersion().getName();
                s = this.minecraft.isDemo() ? s + " Demo" : s + (String)("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
                if (this.minecraft.isModdedClient()) {
                    s = s + I18n.format("menu.modded", new Object[0]);
                }
                if (Reflector.BrandingControl.exists()) {
                    if (Reflector.BrandingControl_forEachLine.exists()) {
                        BiConsumer<Integer, String> biconsumer = (p_lambda$render$12_3_, p_lambda$render$12_4_) -> MainMenuScreen.drawString(matrixStack, this.font, p_lambda$render$12_4_, 2, this.height - (10 + p_lambda$render$12_3_ * 10), 0xFFFFFF | l);
                        Reflector.call(Reflector.BrandingControl_forEachLine, true, true, biconsumer);
                    }
                    if (Reflector.BrandingControl_forEachAboveCopyrightLine.exists()) {
                        BiConsumer<Integer, String> biconsumer1 = (p_lambda$render$13_3_, p_lambda$render$13_4_) -> MainMenuScreen.drawString(matrixStack, this.font, p_lambda$render$13_4_, this.width - this.font.getStringWidth((String)p_lambda$render$13_4_), this.height - (10 + (p_lambda$render$13_3_ + 1) * 10), 0xFFFFFF | l);
                        Reflector.call(Reflector.BrandingControl_forEachAboveCopyrightLine, biconsumer1);
                    }
                } else {
                    MainMenuScreen.drawString(matrixStack, this.font, s, 2, this.height - 10, 0xFFFFFF | l);
                }
                MainMenuScreen.drawString(matrixStack, this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 0xFFFFFF | l);
                if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
                    MainMenuScreen.fill(matrixStack, this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 0xFFFFFF | l);
                }
                for (Widget widget : this.buttons) {
                    widget.setAlpha(f1);
                }
                super.render(matrixStack, mouseX, mouseY, partialTicks);
                if (this.areRealmsNotificationsEnabled() && f1 >= 1.0f) {
                    this.realmsNotification.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            }
            if (this.modUpdateNotification != null) {
                this.modUpdateNotification.render(matrixStack, mouseX, mouseY, partialTicks);
            }
            return;
        }
        Vector2f fixedCoords = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        GLHelpers.INSTANCE.rescale(1.0);
        VisualHelpers.drawRoundedTexture(matrixStack, new ResourceLocation("main/textures/images/mainmenu.png"), 0.0f, 0.0f, (float)this.minecraft.getMainWindow().getWidth(), (float)this.minecraft.getMainWindow().getHeight(), 0.0f, -1);
        super.render(matrixStack, (int)fixedCoords.x, (int)fixedCoords.y, partialTicks);
        GLHelpers.INSTANCE.rescaleMC();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!ClientManagers.isUnHook()) {
            Vector2f fixedCoords = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
            mouseX = fixedCoords.x;
            mouseY = fixedCoords.y;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.areRealmsNotificationsEnabled() && this.realmsNotification.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (mouseX > (double)this.widthCopyrightRest && mouseX < (double)(this.widthCopyrightRest + this.widthCopyright) && mouseY > (double)(this.height - 10) && mouseY < (double)this.height) {
            this.minecraft.displayGuiScreen(new WinGameScreen(false, Runnables.doNothing()));
        }
        return false;
    }

    @Override
    public void onClose() {
        if (this.realmsNotification != null) {
            this.realmsNotification.onClose();
        }
    }

    private void deleteDemoWorld(boolean p_213087_1_) {
        if (p_213087_1_) {
            try (SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getSaveLoader().getLevelSave("Demo_World");){
                saveformat$levelsave.deleteSave();
            }
            catch (IOException ioexception1) {
                SystemToast.func_238538_b_(this.minecraft, "Demo_World");
                field_238656_b_.warn("Failed to delete demo world", (Throwable)ioexception1);
            }
        }
        this.minecraft.displayGuiScreen(this);
    }

    private /* synthetic */ void lambda$addSingleplayerMultiplayerButtons$13(AbstractButton p_lambda$addSingleplayerMultiplayerButtons$7_1_) {
        this.switchToRealms();
    }

    private /* synthetic */ void lambda$addSingleplayerMultiplayerButtons$12(AbstractButton p_lambda$addSingleplayerMultiplayerButtons$6_1_) {
        Screen screen = this.minecraft.gameSettings.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
        this.minecraft.displayGuiScreen(screen);
    }

    public static class CustomButton
    extends AbstractButton
    implements IFastAccess,
    ITranslate {
        public static final Button.ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) -> {};
        protected final Button.IPressable onPress;
        protected final Button.ITooltip onTooltip;
        private final boolean alternative;
        private String name;
        private final Animation animation = new Animation();

        public CustomButton(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction, boolean alternative) {
            this(x, y, width, height, title, pressedAction, field_238486_s_, alternative);
        }

        public CustomButton(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction, Button.ITooltip onTooltip, boolean alternative) {
            super(x, y, width, height, title);
            this.onPress = pressedAction;
            this.onTooltip = onTooltip;
            this.alternative = alternative;
            this.name = title.getString();
        }

        public void tick() {
            this.animation.update(this.isHovered());
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, partialTicks);
            int back = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 10.200000000000001), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.animation.getAnimationValue());
            int outline = ColorHelpers.interpolateColor(ColorHelpers.rgba(190, 190, 190, 15.299999999999999), ColorHelpers.rgba(48, 207, 151, 30.599999999999998), this.animation.getAnimationValue());
            int text = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 + 132.6 * (double)this.animation.getAnimationValue());
            int image = ColorHelpers.interpolateColor(ColorHelpers.rgba(255, 255, 255, 61.199999999999996), ColorHelpers.rgba(48, 207, 151, 255), this.animation.getAnimationValue());
            BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, (float)this.x, (float)this.y, (float)this.width, (float)this.height, 12.0f, -1));
            this.blurSetting(partialTicks, 12.0f, 1.0f);
            VisualHelpers.drawRoundedRect(matrixStack, (float)this.x, (float)this.y, (float)this.width, (float)this.height, 12.0f, back);
            VisualHelpers.drawRoundedOutline(matrixStack, this.x, this.y, this.width, this.height, 12.0f, 1.0f, outline);
            if (!this.alternative) {
                if (this.width > 50) {
                    VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/menu/" + this.getMessage().getString().toLowerCase() + ".png"), (float)this.x + (float)this.width / 2.0f - suisse_intl.getWidth(this.name, 14.0f) / 2.0f - 11.0f, (float)this.y + (float)this.height / 2.0f - 7.0f, 14.0f, 14.0f, image);
                    suisse_intl.drawCenteredText(matrixStack, this.name, (float)this.x + (float)this.width / 2.0f + 11.0f, (float)this.y + (float)this.height / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, text, 14.0f);
                } else {
                    VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/menu/" + this.getMessage().getString().toLowerCase() + ".png"), (float)this.x + (float)this.width / 2.0f - 7.0f, (float)this.y + (float)this.height / 2.0f - 7.0f, 14.0f, 14.0f, image);
                }
            } else {
                VisualHelpers.drawImage(matrixStack, new ResourceLocation("main/textures/images/menu/" + this.getMessage().getString().toLowerCase() + ".png"), (float)this.x + (float)this.width / 2.0f - 7.0f, (float)this.y + (float)this.height / 2.0f - 7.0f, 14.0f, 14.0f, image);
            }
            if (ClientManagers.isChanged()) {
                this.name = this.getTranslation(this.getMessage().getString());
                if (this.getMessage().getString().equals("Options")) {
                    ClientManagers.setChanged(false);
                }
            }
        }

        @Override
        public void onPress() {
            this.onPress.onPress(this);
        }
    }
}
