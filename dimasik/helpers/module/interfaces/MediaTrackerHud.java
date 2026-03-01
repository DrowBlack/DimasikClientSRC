package dimasik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.StencilHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.draggable.api.Component;
import dimasik.managers.module.Module;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.utils.time.TimerUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.vector.Vector2f;

public class MediaTrackerHud
extends Component {
    private final CheckboxOption hide = new CheckboxOption("Hide", true);
    public TimerUtils timerUtils = new TimerUtils();

    public MediaTrackerHud() {
        super("MediaTrackerHud", new Vector2f((float)mc.getMainWindow().getWidth() / 2.0f, (float)mc.getMainWindow().getHeight() / 2.0f), 114.0f, 90.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        boolean show = (!Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::isToggled).filter(Module::hasBind).toList().isEmpty() || MediaTrackerHud.mc.currentScreen instanceof ChatScreen) && Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("MediaTracker") || (Boolean)this.hide.getValue() == false;
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float width = 174.0f;
        float height = 62.0f;
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 30.599999999999998 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, MediaTrackerHud.mc.getTimer().renderPartialTicks);
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back));
                this.blurSetting(MediaTrackerHud.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
            } else if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, width, height, 12.0f, 2.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.getShowAnimation().getAnimationValue()));
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, -1);
            StencilHelpers.read(1);
            if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, x, y, width, 192.0f, 40.0f, glow);
            }
            StencilHelpers.uninit();
            this.getDraggableOption().setWidth(width);
            this.getDraggableOption().setHeight(height);
        }
    }
}
