package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.helpers.module.aura.RayTrace;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.Hand;

public class AutoCrystal
extends Module {
    private final SliderOption distance = new SliderOption("Distance", 3.0f, 1.0f, 4.0f).increment(0.1f);
    private final CheckboxOption dontExplosion = new CheckboxOption("Dont Explosion Self", true);
    private final EventListener<EventUpdate> update = this::update;

    public AutoCrystal() {
        super("CrystalOptimizer", Category.COMBAT);
        this.settings(this.distance, this.dontExplosion);
    }

    public void update(EventUpdate event) {
        for (Entity entity : AutoCrystal.mc.world.getAllEntities()) {
            if (!(entity instanceof EnderCrystalEntity) || !(AutoCrystal.mc.player.getDistance(entity) <= ((Float)this.distance.getValue()).floatValue()) || RayTrace.getMouseOver(entity, AutoCrystal.mc.player.rotationYaw, AutoCrystal.mc.player.rotationPitch, 6.0) != entity) continue;
            double y = entity.getPosY();
            double yPlayer = AutoCrystal.mc.player.getPosY() + (double)0.6f;
            if (((Boolean)this.dontExplosion.getValue()).booleanValue() && !(y >= yPlayer)) continue;
            AutoCrystal.mc.playerController.attackEntity(AutoCrystal.mc.player, entity);
            AutoCrystal.mc.player.swingArm(Hand.MAIN_HAND);
        }
    }
}
