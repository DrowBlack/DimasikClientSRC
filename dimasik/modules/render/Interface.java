package dimasik.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.draggable.api.Component;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import lombok.Generated;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

public class Interface
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("KeyBinds", true), new MultiOptionValue("StaffList", true), new MultiOptionValue("TargetHud", true), new MultiOptionValue("PotionList", true), new MultiOptionValue("Information", true), new MultiOptionValue("WaterMark", true), new MultiOptionValue("ArmorHud", true), new MultiOptionValue("Notifications", true), new MultiOptionValue("Hotbar", true), new MultiOptionValue("Totem Counter", true), new MultiOptionValue("Cooldowns", true), new MultiOptionValue("MediaTracker", true));
    private final SelectOption notifDesign = new SelectOption("Notification Design", 0, new SelectOptionValue("Standard"), new SelectOptionValue("Transparent")).visible(() -> this.elements.getSelected("Notifications"));
    private final SliderOption compression = new SliderOption("Compression", 1.0f, 1.0f, 8.0f).visible(() -> this.elements.getSelected("Notifications") && this.notifDesign.getSelected("Transparent")).increment(1.0f);
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventRender2D.Pre> render = this::render;
    public float animationToggle;

    public Interface() {
        super("Interface", Category.RENDER);
        this.settings(this.elements, this.notifDesign, this.compression);
    }

    public void update(EventUpdate event) {
        if (this.elements.getSelected("Notifications")) {
            Load.getInstance().getHooks().getNotificationManagers().update();
        }
        for (Component drag : Load.getInstance().getHooks().getDraggableManagers()) {
            drag.getDraggableOption().getClickAnimation().update(drag.getDraggableOption().isClick());
            drag.update(event);
        }
    }

    public void render(EventRender2D.Pre event) {
        if (this.elements.getSelected("Notifications")) {
            Load.getInstance().getHooks().getNotificationManagers().render(event);
        }
        for (Object drag : Load.getInstance().getHooks().getDraggableManagers()) {
            ((Component)drag).getDraggableOption().getClickAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.BACK_OUT, event.getPartialTicks());
            ((Component)drag).render(event);
        }
        if (this.elements.getSelected("Totem Counter")) {
            int count = 0;
            for (ItemStack itemStack : Interface.mc.player.inventory.mainInventory) {
                if (itemStack.getItem() != Items.TOTEM_OF_UNDYING) continue;
                count += itemStack.getCount();
            }
            int backColor = ColorHelpers.rgba(31, 31, 34, 255);
            int bindColor = ColorHelpers.rgba(26, 27, 31, 255);
            boolean totemInInv = Interface.mc.player.inventory.mainInventory.stream().map(ItemStack::getItem).toList().contains(Items.TOTEM_OF_UNDYING);
            if (totemInInv) {
                VisualHelpers.drawRoundedRect(event.getMatrixStack(), (float)mc.getMainWindow().getScaledWidth() / 2.0f - 30.5f, (float)mc.getMainWindow().getScaledHeight() / 2.0f + 128.0f, 32.0f, 32.0f, new Vector4f(0.0f, 0.0f, 6.0f, 6.0f), bindColor);
                VisualHelpers.drawRoundedRect(event.getMatrixStack(), (float)mc.getMainWindow().getScaledWidth() / 2.0f, (float)mc.getMainWindow().getScaledHeight() / 2.0f + 128.0f, 32.0f, 32.0f, new Vector4f(6.0f, 6.0f, 0.0f, 0.0f), backColor);
                this.drawItemStack(new ItemStack(Items.TOTEM_OF_UNDYING), (float)mc.getMainWindow().getScaledWidth() / 2.0f - 26.5f, (float)mc.getMainWindow().getScaledHeight() / 2.0f + 132.0f, 1.5f);
                suisse_medium.drawText(event.getMatrixStack(), "" + count, (float)mc.getMainWindow().getScaledWidth() / 2.0f + 11.0f, (float)mc.getMainWindow().getScaledHeight() / 2.0f + 136.0f, -1, 14.0f);
            }
        }
    }

    public static float calculateBPS() {
        double distance = Math.sqrt(Math.pow(Interface.mc.player.getPosX() - Interface.mc.player.prevPosX, 2.0) + Math.pow(Interface.mc.player.getPosY() - Interface.mc.player.prevPosY, 2.0) + Math.pow(Interface.mc.player.getPosZ() - Interface.mc.player.prevPosZ, 2.0));
        float bps = (float)(distance * (double)Interface.mc.getTimer().speed * 20.0);
        return (float)Math.round(bps * 10.0f) / 10.0f;
    }

    private void drawItemStack(ItemStack stack, float x, float y, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        GL11.glScaled(scaleValue, scaleValue, scaleValue);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getItemRenderer().renderItemOverlays(Interface.mc.fontRenderer, stack, 0, 0);
        RenderSystem.popMatrix();
    }

    @Generated
    public MultiOption getElements() {
        return this.elements;
    }

    @Generated
    public SelectOption getNotifDesign() {
        return this.notifDesign;
    }

    @Generated
    public SliderOption getCompression() {
        return this.compression;
    }

    @Generated
    public EventListener<EventUpdate> getUpdate() {
        return this.update;
    }

    @Generated
    public EventListener<EventRender2D.Pre> getRender() {
        return this.render;
    }

    @Generated
    public float getAnimationToggle() {
        return this.animationToggle;
    }
}
