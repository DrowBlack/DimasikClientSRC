package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.movement.EventJump;
import dimasik.events.main.movement.EventStrafe;
import dimasik.events.main.player.EventElytra;
import dimasik.events.main.player.EventSwimming;
import dimasik.events.main.player.EventSync;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.EnchantedGoldenAppleItem;
import net.minecraft.item.SkullItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class ItemFinder
extends Module {
    private Vector2f rotation;
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Silent Aim"), new SelectOptionValue("Focus Aim"));
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Skull", true), new MultiOptionValue("Elytra", true), new MultiOptionValue("Enchanted Golden Apple", true));
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSync> sync = this::sync;
    private final EventListener<EventJump> jump = this::jump;
    private final EventListener<EventSwimming> swim = this::swimming;
    private final EventListener<EventElytra> elytra = this::elytra;
    private final EventListener<EventStrafe> strafe = this::strafe;

    public ItemFinder() {
        super("ItemFinder", Category.PLAYER);
        this.settings(this.mode, this.elements);
    }

    public void update(EventUpdate event) {
        this.rotation = new Vector2f(ItemFinder.mc.player.rotationYaw, ItemFinder.mc.player.rotationPitch);
        for (Entity entity : ItemFinder.mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity)) continue;
            ItemEntity item = (ItemEntity)entity;
            if (item.getItem().getItem() instanceof SkullItem && this.elements.getSelected("Skull")) {
                this.rotation = this.itemPosition(entity);
            }
            if (item.getItem().getItem() instanceof ElytraItem && this.elements.getSelected("Elytra")) {
                this.rotation = this.itemPosition(entity);
            }
            if (!(item.getItem().getItem() instanceof EnchantedGoldenAppleItem) || !this.elements.getSelected("Enchanted Golden Apple")) continue;
            this.rotation = this.itemPosition(entity);
        }
    }

    public void sync(EventSync event) {
        if (this.mode.getSelected("Silent Aim")) {
            ItemFinder.mc.player.rotationYawHead = this.rotation.x;
            ItemFinder.mc.player.renderYawOffset = this.rotation.x;
            ItemFinder.mc.player.rotationPitchHead = this.rotation.y;
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        } else {
            ItemFinder.mc.player.rotationYaw = this.rotation.x;
            ItemFinder.mc.player.rotationPitch = this.rotation.y;
        }
    }

    public void jump(EventJump event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
        }
    }

    public void swimming(EventSwimming event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        }
    }

    public void elytra(EventElytra event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
            event.setPitch(this.rotation.y);
        }
    }

    public void strafe(EventStrafe event) {
        if (this.mode.getSelected("Silent Aim")) {
            event.setYaw(this.rotation.x);
        }
    }

    private Vector2f itemPosition(Entity entity) {
        double x = entity.getPosX() - ItemFinder.mc.player.getPosX();
        double y = entity.getPosY() - ItemFinder.mc.player.getPosY();
        double z = entity.getPosZ() - ItemFinder.mc.player.getPosZ();
        double sqrt = MathHelper.sqrt(x * x + z * z);
        double mainX = MathHelper.atan2(z, x) * 57.29577951308232 - 90.0;
        double mainY = MathHelper.atan2(y, sqrt) * 57.29577951308232;
        return new Vector2f((float)mainX, (float)(-mainY));
    }
}
