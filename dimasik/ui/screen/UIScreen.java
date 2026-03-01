package dimasik.ui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.interfaces.IManager;
import dimasik.helpers.interfaces.ITranslate;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.main.Category;
import dimasik.ui.option.OptionScreen;
import dimasik.ui.screen.component.Component;
import dimasik.ui.screen.panel.Panel;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.NonNull;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.ONLY_GENERATIVE)
public class UIScreen
extends Screen
implements IFastAccess,
ITranslate,
IManager<Component> {
    private final List<Component> components = new ArrayList<Component>();
    private final Animation animation = new Animation();
    private final Animation backgroundAnimation = new Animation();
    float marginY = 5.0f;
    float marginX = 5.0f;
    private float lastX;
    private float lastY;
    final float width = mc.getMainWindow().getWidth();
    final float height = mc.getMainWindow().getHeight();
    final float x = this.width / 2.0f;
    final float y = this.height / 2.0f;
    private boolean isPressed = false;
    private boolean control = false;
    private float scrollingX = 0.0f;
    private float scrollingOutX;
    private String textSearch = "";
    private String ideas;
    private boolean searching = false;
    private final Animation searchAnimation = new Animation();
    private Category category;
    private boolean update = true;
    private final List<Panel> panels = new ArrayList<Panel>();
    private final TimerUtils timer = new TimerUtils();

    @CompileNativeCalls
    @AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
    public UIScreen() {
        super(new StringTextComponent("PoweredByNirficusAndRlezz"));
        for (Category category : Category.values()) {
            this.panels.add(new Panel(this.x, this.y, this.width, this.height, this, category));
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void tick() {
        this.searchAnimation.update(this.searching);
        this.animation.update(this.update);
        this.backgroundAnimation.update(this.update);
        if (UIScreen.mc.player != null) {
            KeyBinding[] pressedKeys = new KeyBinding[]{UIScreen.mc.gameSettings.keyBindForward, UIScreen.mc.gameSettings.keyBindBack, UIScreen.mc.gameSettings.keyBindLeft, UIScreen.mc.gameSettings.keyBindRight, UIScreen.mc.gameSettings.keyBindJump};
            if (UIScreen.mc.currentScreen instanceof ChatScreen || UIScreen.mc.currentScreen instanceof EditSignScreen) {
                return;
            }
            if (!this.searching) {
                for (KeyBinding keyBinding : pressedKeys) {
                    boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                    keyBinding.setPressed(isKeyPressed);
                }
            }
        }
        for (Panel panel : this.panels) {
            panel.tick();
        }
    }

    @Override
    public void render(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        this.backgroundAnimation.animate(0.0f, 1.0f, 0.125f, EasingList.NONE, UIScreen.mc.getTimer().renderPartialTicks);
        if (Load.getInstance().getHooks().getModuleManagers().getClickGui().better.getSelected("Darkening Background")) {
            VisualHelpers.drawRoundedRect(matrixStack, -1000.0f, -1000.0f, (float)(mc.getMainWindow().getWidth() + 2000), (float)(mc.getMainWindow().getHeight() + 2000), 0.0f, ColorHelpers.rgba(0, 0, 0, (int)(150.0f * this.backgroundAnimation.getAnimationValue())));
        }
        if (Load.getInstance().getHooks().getModuleManagers().getClickGui().better.getSelected("Colorful Background")) {
            VisualHelpers.drawRoundedGradientRect(matrixStack, -10.0f, -10.0f, (float)(mc.getMainWindow().getWidth() + 20), (float)(mc.getMainWindow().getHeight() - 100), 0.0f, ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.rgba(0, 0, 0, 0), ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(45), 255.0f * this.backgroundAnimation.getAnimationValue()), ColorHelpers.getColorWithAlpha(ColorHelpers.getTheme(90), 255.0f * this.backgroundAnimation.getAnimationValue()));
        }
        GLHelpers.INSTANCE.rescale(1.0);
        this.animation.animate(0.0f, 1.0f, 0.125f, EasingList.BACK_OUT, UIScreen.mc.getTimer().renderPartialTicks);
        float width = 200.0f;
        float height = Load.getInstance().getHooks().getModuleManagers().getClickGui().size.getSelected("Small") ? 531 : 651;
        float x = (float)mc.getMainWindow().getWidth() / 2.0f - (width + 8.0f) * (float)Category.values().length / 2.0f;
        float y = (float)mc.getMainWindow().getHeight() / 2.0f - height / 2.0f;
        float xPos = (float)mc.getMainWindow().getWidth() / 2.0f - ((float)mc.getMainWindow().getWidth() / (float)Category.values().length + 8.0f) / 2.0f / 1.25f * (float)Category.values().length;
        float yPos = (float)mc.getMainWindow().getHeight() / 2.0f - (float)mc.getMainWindow().getHeight() / 1.5f / 2.0f;
        GLHelpers.INSTANCE.scaleAnimation(matrixStack, 0.0f, 0.0f, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), this.animation.getAnimationValue());
        this.scrollingOutX = Animation.animate(this.scrollingOutX, this.scrollingX);
        float panelOff = 0.0f;
        for (Panel panel : this.panels) {
            panel.setX(x + panelOff + this.scrollingOutX);
            panel.setY(y);
            panel.setWidth(width);
            panel.setHeight(height);
            panel.render(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
            panelOff += width + 8.0f;
        }
        GLHelpers.INSTANCE.rescaleMC();
        this.renderSetting(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
        this.renderSearch(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
        this.renderIdea(matrixStack, (int)fixedMouse.x, (int)fixedMouse.y, partialTicks);
        if (this.animation.getPrevValue() == 0.0f && this.animation.getValue() == 0.0f && !this.update) {
            this.update = true;
            super.closeScreen();
        }
    }

    private void renderSetting(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        GLHelpers.INSTANCE.rescale(1.0);
        this.searchAnimation.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, UIScreen.mc.getTimer().renderPartialTicks);
        float width = 200.0f;
        float height = 70.0f;
        float x = (float)mc.getMainWindow().getWidth() - width + 130.0f;
        float y = 0.0f;
        boolean hovered = ScreenHelpers.isHovered(mouseX, mouseY, x, y, 70.0f, height);
        this.marginX = Animation.animate(this.marginX, hovered ? 130.0f : 0.0f);
        VisualHelpers.drawRoundedRect(matrixStack, x - this.marginX, y, width, height, new Vector4f(0.0f, 0.0f, 20.0f, 20.0f), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255), ColorHelpers.rgba(15, 15, 15, 255), ColorHelpers.rgba(5, 5, 5, 255));
        sf_semibold.drawText(matrixStack, "Theme and Config", x - this.marginX + 10.0f, y + height / 2.0f - sf_semibold.getHeight(18.0f) / 2.0f, ColorHelpers.rgba(255, 255, 255, 255), 18.0f);
        GLHelpers.INSTANCE.rescaleMC();
    }

    private void renderSearch(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        GLHelpers.INSTANCE.rescale(1.0);
        float sWidth = 240.0f;
        float sHeight = 50.0f;
        float xSearch = (float)mc.getMainWindow().getWidth() / 2.0f - sWidth / 2.0f;
        float ySearch = mc.getMainWindow().getHeight();
        float animY = ySearch - (sHeight + 5.0f) * this.searchAnimation.getAnimationValue();
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.searchAnimation.getAnimationValue());
        int outline = ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.searchAnimation.getAnimationValue());
        int indicator = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * this.searchAnimation.getAnimationValue());
        int glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996 * (double)this.searchAnimation.getAnimationValue());
        int text = ColorHelpers.rgba(255, 255, 255, 183.6 * (double)this.searchAnimation.getAnimationValue());
        VisualHelpers.drawRoundedRect(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, back);
        VisualHelpers.drawRoundedOutline(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, 2.0f, outline);
        StencilHelpers.init();
        VisualHelpers.drawRoundedRect(matrixStack, xSearch, animY, sWidth, sHeight, 12.0f, -1);
        StencilHelpers.read(1);
        VisualHelpers.drawGlow(matrixStack, xSearch, animY, sWidth, 48.0f, 40.0f, glow);
        suisse_intl.drawText(matrixStack, this.textSearch, xSearch + 8.0f, animY + sHeight / 2.0f - suisse_intl.getHeight(14.0f) / 2.0f, text, 14.0f);
        StencilHelpers.uninit();
        GLHelpers.INSTANCE.rescaleMC();
    }

    private void renderIdea(@NonNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (matrixStack == null) {
            throw new NullPointerException("matrixStack is marked non-null but is null");
        }
        GLHelpers.INSTANCE.rescale(1.0);
        suisse_intl.drawCenteredText(matrixStack, this.ideas, (float)mc.getMainWindow().getWidth() / 2.0f, 0.0f, ColorHelpers.rgba(190, 190, 190, 255), 14.0f);
        GLHelpers.INSTANCE.rescaleMC();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse) {
        float y;
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        for (Panel panel : this.panels) {
            panel.mouseClicked(fixedMouse.x, fixedMouse.y, mouse);
        }
        float width = 200.0f;
        float height = 70.0f;
        float x = (float)mc.getMainWindow().getWidth() - width + 130.0f;
        boolean onClick = ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, x - this.marginX, y = 0.0f, width, height);
        if (onClick && mouse == 0) {
            mc.displayGuiScreen(new OptionScreen());
        }
        return super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.isPressed) {
            this.scrollingX += (float)(delta * 15.0);
        }
        for (Panel panel : this.panels) {
            Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
            panel.mouseScrolled(fixedMouse.x, fixedMouse.y, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : this.panels) {
            Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
            panel.mouseReleased(fixedMouse.x, fixedMouse.y, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl = this.isPressed = keyCode == 340;
        if (!this.textSearch.isEmpty() && this.searching) {
            if (keyCode == 259) {
                this.textSearch = this.textSearch.substring(0, this.textSearch.length() - 1);
            } else if (keyCode == 261) {
                this.textSearch = "";
            }
        }
        if (keyCode == 341) {
            this.control = true;
        }
        if (this.control && keyCode == 70) {
            boolean bl2 = this.searching = !this.searching;
            if (!this.searching) {
                this.textSearch = "";
            }
        }
        for (Panel panel : this.panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.isPressed = false;
        if (keyCode == 341) {
            this.control = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searching) {
            this.textSearch = this.textSearch + codePoint;
        }
        for (Panel panel : this.panels) {
            panel.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void closeScreen() {
        this.isPressed = false;
        for (Panel panel : this.panels) {
            panel.exit();
        }
        this.update = false;
        this.searching = false;
        this.textSearch = "";
    }

    @Override
    public void register(Component component) {
        this.components.add(component);
    }

    public void translate() {
        for (Panel panel : this.panels) {
            panel.translate();
        }
        this.ideas = this.getTranslation("To open search, press CTRL + F");
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getBackgroundAnimation() {
        return this.backgroundAnimation;
    }

    @Generated
    public String getTextSearch() {
        return this.textSearch;
    }

    @Generated
    public boolean isSearching() {
        return this.searching;
    }

    @Generated
    public void setSearching(boolean searching) {
        this.searching = searching;
    }
}
