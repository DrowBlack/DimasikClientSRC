package dimasik.modules.render;

import dimasik.events.api.EventListener;
import dimasik.events.main.misc.EventCrosshair;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import java.awt.Color;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class Crosshair
extends Module {
    private final Animation animkk = new Animation();
    private static final int RED_COLOR = new Color(255, 80, 80).getRGB();
    private final EventListener<EventCrosshair> crosshair = this::crosshair;
    private final EventListener<EventRender2D.Pre> render = this::render;

    public Crosshair() {
        super("Crosshair", Category.RENDER);
        this.animkk.setValue(0.0f);
    }

    public void crosshair(EventCrosshair event) {
        event.setCancelled(true);
    }

    public void render(EventRender2D.Pre event) {
        EntityRayTraceResult entityResult;
        float x = (float)mc.getMainWindow().getScaledWidth() / 2.0f;
        float y = (float)mc.getMainWindow().getScaledHeight() / 2.0f;
        boolean istrigger = false;
        if (Crosshair.mc.objectMouseOver != null && Crosshair.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY && (entityResult = (EntityRayTraceResult)Crosshair.mc.objectMouseOver).getEntity() instanceof LivingEntity && entityResult.getEntity().isAlive()) {
            istrigger = true;
        }
        this.animkk.update(istrigger);
        this.animkk.animate(0.0f, 1.0f, 0.08f, EasingList.CIRC_OUT, mc.getRenderPartialTicks());
        float progress = this.animkk.getProgress();
        int themeColor = ColorHelpers.getThemeColor(1);
        int color = ColorHelpers.interpolateColor(themeColor, RED_COLOR, progress);
        VisualHelpers.drawCircle(x, y, 0.0f, 360.0f, 10.0f, color, 1.5f);
    }
}
