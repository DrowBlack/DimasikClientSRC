package dimasik.managers.notification.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.notification.api.Pattern;
import dimasik.modules.render.Interface;
import lombok.Generated;
import net.minecraft.util.math.vector.Vector4f;

public class Notification
implements IFastAccess {
    private final String text;
    private final String context;
    private final long time;
    private final Animation animation = new Animation();
    private Module module;
    private Pattern pattern = Pattern.NONE;
    private float x;
    private float y;
    private float width;
    private float height;
    private float alpha;
    private final long oldTime;

    public Notification(String text, String context, long time) {
        this.text = text;
        this.time = time;
        this.context = context;
        this.oldTime = System.currentTimeMillis();
    }

    public Notification(String text, String context, long time, Module module) {
        this.text = text;
        this.time = time;
        this.module = module;
        this.context = context;
        this.oldTime = System.currentTimeMillis();
    }

    public Notification setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public void render(EventRender2D.Pre event) {
        int selfcode;
        int text;
        int glow;
        int indicator;
        MatrixStack matrixStack = event.getMatrixStack();
        float imageWidth = this.module != null ? 20.0f : 0.0f;
        this.width = suisse_intl.getWidth(this.getText(), 12.0f) + 24.0f + imageWidth;
        this.height = 38.0f;
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.alpha);
        int outline = ColorHelpers.rgba(190, 190, 190, 10.0 * (double)this.alpha);
        if (this.pattern != Pattern.DISABLE && this.pattern != Pattern.ERROR) {
            indicator = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), this.alpha * 255.0f);
            glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.0 * (double)this.alpha);
            text = ColorHelpers.rgba(255, 255, 255, 255.0f * this.alpha);
            int image = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), this.alpha * 255.0f);
            selfcode = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.0 * (double)this.alpha);
        } else {
            indicator = ColorHelpers.rgba(255, 255, 255, 122.0 * (double)this.alpha);
            glow = ColorHelpers.rgba(255, 255, 255, 61.0 * (double)this.alpha);
            text = ColorHelpers.rgba(255, 255, 255, 183.0 * (double)this.alpha);
            int image = ColorHelpers.rgba(255, 255, 255, 122.0 * (double)this.alpha);
            selfcode = ColorHelpers.getColorWithAlpha(ColorHelpers.rgba(255, 255, 255, 255), 40.0 * (double)this.alpha);
        }
        if ((double)this.alpha > 0.1) {
            Interface interfaces = (Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class);
            if (interfaces.getNotifDesign().getSelected("Transparent")) {
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, back));
                this.blurSetting(Notification.mc.getTimer().renderPartialTicks, 10.0f, ((Float)interfaces.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, selfcode);
            } else if (interfaces.getNotifDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, this.x, this.y, this.width, this.height, 12.0f, 1.0f, outline);
            VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width - 32.0f, this.y, 26.0f, 2.0f, new Vector4f(0.0f, 2.0f, 2.0f, 0.0f), indicator);
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, -1);
            StencilHelpers.read(1);
            if (interfaces.getNotifDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, this.x, this.y, this.width, 48.0f, 100.0f, glow);
            }
            StencilHelpers.uninit();
            if (this.module != null) {
                dimasIcon.drawText(matrixStack, this.module.getCategory().getPath(), this.x + 12.0f, this.y + this.height / 2.0f - 6.0f, ColorHelpers.getThemeColor(1), 12.0f);
                suisse_intl.drawText(matrixStack, this.getText(), this.x + 30.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, text, 12.0f);
            } else {
                suisse_intl.drawText(matrixStack, this.getText(), this.x + 12.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, text, 12.0f);
            }
        }
    }

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    public String getContext() {
        return this.context;
    }

    @Generated
    public long getTime() {
        return this.time;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public Pattern getPattern() {
        return this.pattern;
    }

    @Generated
    public float getX() {
        return this.x;
    }

    @Generated
    public float getY() {
        return this.y;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public float getAlpha() {
        return this.alpha;
    }

    @Generated
    public long getOldTime() {
        return this.oldTime;
    }

    @Generated
    public void setModule(Module module) {
        this.module = module;
    }

    @Generated
    public void setX(float x) {
        this.x = x;
    }

    @Generated
    public void setY(float y) {
        this.y = y;
    }

    @Generated
    public void setWidth(float width) {
        this.width = width;
    }

    @Generated
    public void setHeight(float height) {
        this.height = height;
    }

    @Generated
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
