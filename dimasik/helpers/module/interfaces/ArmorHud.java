package dimasik.helpers.module.interfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.EasingList;
import dimasik.managers.draggable.api.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector2f;
import org.lwjgl.opengl.GL11;

public class ArmorHud
extends Component {
    public ArmorHud() {
        super("ArmorHud", new Vector2f(350.0f, 106.0f), 145.0f, 66.0f);
    }

    @Override
    public void update(EventUpdate event) {
        boolean show = Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("ArmorHud") && Load.getInstance().getHooks().getModuleManagers().getInterfaces().isToggled();
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, ArmorHud.mc.getTimer().renderPartialTicks);
        this.drawItemStack(x + 96.0f, y, -32.0f, 2.0f * this.getShowAnimation().getAnimationValue());
        this.getDraggableOption().setWidth(128.0f);
        this.getDraggableOption().setHeight(32.0f);
    }

    private void drawItemStack(float x, float y, float offset, float scaleValue) {
        ArrayList stackList = new ArrayList((Collection)ArmorHud.mc.player.getArmorInventoryList());
        AtomicReference<Float> posX = new AtomicReference<Float>(Float.valueOf(x));
        stackList.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> this.drawItemStack((ItemStack)stack, posX.getAndAccumulate(Float.valueOf(offset), Float::sum).floatValue(), y, scaleValue));
    }

    private void drawItemStack(ItemStack stack, float x, float y, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        GL11.glScaled(scaleValue, scaleValue, scaleValue);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getItemRenderer().renderItemOverlays(ArmorHud.mc.fontRenderer, stack, 0, 0);
        RenderSystem.popMatrix();
    }
}
