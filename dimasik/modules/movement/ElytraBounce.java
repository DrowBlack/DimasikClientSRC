package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.player.EventElytra;
import dimasik.events.main.player.EventSync;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;

public class ElytraBounce
extends Module {
    private final SliderOption pitch = new SliderOption("Pitch", -45.0f, -90.0f, 90.0f).increment(1.0f);
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventSync> sync = this::sync;
    private final EventListener<EventElytra> elytra = this::elytra;

    public ElytraBounce() {
        super("ElytraBounce", Category.MOVEMENT);
        this.settings(this.pitch);
    }

    public void update(EventUpdate event) {
        if (ElytraBounce.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
            if (ElytraBounce.mc.player.isOnGround()) {
                ElytraBounce.mc.player.jump();
            }
            if (!ElytraBounce.mc.player.isOnGround() && !ElytraBounce.mc.player.isElytraFlying()) {
                ElytraBounce.mc.player.startFallFlying();
                ElytraBounce.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraBounce.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
    }

    public void sync(EventSync event) {
        if (ElytraBounce.mc.player.isElytraFlying()) {
            ElytraBounce.mc.player.rotationPitchHead = ((Float)this.pitch.getValue()).floatValue();
            ElytraBounce.mc.player.prevRotationPitchHead = ((Float)this.pitch.getValue()).floatValue();
            event.setPitch(((Float)this.pitch.getValue()).floatValue());
        }
    }

    public void elytra(EventElytra event) {
        if (ElytraBounce.mc.player.isElytraFlying()) {
            event.setPitch(((Float)this.pitch.getValue()).floatValue());
        }
    }
}
