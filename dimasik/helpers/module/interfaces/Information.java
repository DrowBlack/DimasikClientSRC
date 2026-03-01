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
import dimasik.modules.render.Interface;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class Information
extends Component {
    public Information() {
        super("Information", new Vector2f(350.0f, 46.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression());
    }

    @Override
    public void update(EventUpdate event) {
        boolean show = ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("Information") && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).isToggled();
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        MatrixStack matrixStack = event.getMatrixStack();
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, Information.mc.getTimer().renderPartialTicks);
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        float height = 38.0f;
        float width = 122.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f) + suisse_intl.getWidth(Interface.calculateBPS() + "bps", 12.0f) + suisse_intl.getWidth("20.0tps", 12.0f);
        this.drawRect(matrixStack, x, y, width, height);
        this.getDraggableOption().setWidth(width);
        this.getDraggableOption().setHeight(height);
    }

    private void drawRect(MatrixStack matrixStack, float x, float y, float width, float height) {
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.getShowAnimation().getAnimationValue());
        int back3 = ColorHelpers.rgba(190, 190, 190, 15.299999f * this.getShowAnimation().getAnimationValue());
        int glow = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(30.599999999999998 * (double)this.getShowAnimation().getAnimationValue()));
        int back2 = ColorHelpers.rgba(15, 15, 15, 30.599999999999998 * (double)this.getShowAnimation().getAnimationValue());
        int indicator = ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            if (this.getDesign().getSelected("Transparent")) {
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back));
                this.blurSetting(Information.mc.getTimer().renderPartialTicks, 10.0f, ((Float)this.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.getShowAnimation().getAnimationValue()));
            } else if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, x, y, width, height, 12.0f, 2.0f, ColorHelpers.rgba(190, 190, 190, 15.299999999999999 * (double)this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(matrixStack, x + width - 30.0f, y, 26.0f, 3.0f, new Vector4f(0.0f, 4.0f, 4.0f, 0.0f), indicator);
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, x, y, width, height, 12.0f, -1);
            StencilHelpers.read(1);
            if (this.getDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, x, y, width, 192.0f, 40.0f, glow);
            }
            dimasIcon.drawText(matrixStack, "C", x + 12.0f, y + 13.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            suisse_intl.drawText(matrixStack, "x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), x + 30.0f, y + 14.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 12.0f);
            VisualHelpers.drawRoundedRect(x + 38.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f), y + 17.0f, 8.0f, 8.0f, 4.0f, back3);
            dimasIcon.drawText(matrixStack, "D", x + 54.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f), y + 13.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            suisse_intl.drawText(matrixStack, Interface.calculateBPS() + "bps", x + 72.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f), y + 14.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 12.0f);
            VisualHelpers.drawRoundedRect(x + 78.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f) + suisse_intl.getWidth(Interface.calculateBPS() + "bps", 12.0f), y + 17.0f, 8.0f, 8.0f, 4.0f, back3);
            dimasIcon.drawText(matrixStack, "E", x + 94.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f) + suisse_intl.getWidth(Interface.calculateBPS() + "bps", 12.0f), y + 13.0f, ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), 14.0f);
            suisse_intl.drawText(matrixStack, "20.0tps", x + 112.0f + suisse_intl.getWidth("x" + (int)Information.mc.player.getPosX() + " y" + (int)Information.mc.player.getPosY() + " z" + (int)Information.mc.player.getPosZ(), 12.0f) + suisse_intl.getWidth(Interface.calculateBPS() + "bps", 12.0f), y + 14.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 12.0f);
            StencilHelpers.uninit();
        }
    }
}
