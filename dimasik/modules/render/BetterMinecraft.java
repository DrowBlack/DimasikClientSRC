package dimasik.modules.render;

import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.events.main.visual.EventCamera;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.utils.client.AnimationMath;
import net.minecraft.client.gui.screen.ChatScreen;

public class BetterMinecraft
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Extra Hunger", false), new MultiOptionValue("Camera Through Walls", false));
    private final EventListener<EventRender2D.Hunger> render = this::render;
    private final EventListener<EventCamera> camera = this::camera;
    public float animationToggle;

    public BetterMinecraft() {
        super("BetterMinecraft", Category.RENDER);
        this.settings(this.elements);
    }

    public void render(EventRender2D.Hunger event) {
        if (this.elements.getSelected("Extra Hunger")) {
            this.animationToggle = AnimationMath.lerp(this.animationToggle, BetterMinecraft.mc.currentScreen instanceof ChatScreen ? 16.0f : 0.0f, 10.0f);
            int k1 = (int)((float)(BetterMinecraft.mc.ingameGUI.getScaledHeight() - 39) - this.animationToggle);
            int j1 = BetterMinecraft.mc.ingameGUI.getScaledWidth() / 2 + 91;
            for (int k6 = 0; k6 < 10; ++k6) {
                int i7 = k1;
                int k7 = 16;
                boolean i8 = false;
                int k8 = j1 - k6 * 8 - 9;
                BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, 16, 27, 9, 9);
                if (k6 * 2 + 1 < (int)BetterMinecraft.mc.player.getFoodStats().getSaturationLevel()) {
                    BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, k7 + 36, 27, 9, 9);
                }
                if (k6 * 2 + 1 != (int)BetterMinecraft.mc.player.getFoodStats().getSaturationLevel()) continue;
                BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, k7 + 45, 27, 9, 9);
            }
        }
    }

    public void camera(EventCamera event) {
        if (this.elements.getSelected("Camera Through Walls")) {
            event.setCancelled(true);
        }
    }
}
