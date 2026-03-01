package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.player.MoveUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.AxisAlignedBB;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class Speed
extends Module {
    private boolean boost;
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Colission"), new SelectOptionValue("Vanilla"));
    private final SliderOption sliderOption = new SliderOption("Speed", 1.0f, 1.0f, 5.0f).increment(0.1f).visible(() -> this.mode.getSelected("Vanilla"));
    private final TimerUtils timer = new TimerUtils();
    private final EventListener<EventUpdate> update = this::update;

    public Speed() {
        super("Speed", Category.MOVEMENT);
        this.settings(this.mode, this.sliderOption);
    }

    public void update(EventUpdate event) {
        if (((SelectOptionValue)this.mode.getValue()).getName().equals("ReallyWorld")) {
            this.reallyWorld();
        }
        if (((SelectOptionValue)this.mode.getValue()).getName().equals("Colission")) {
            this.colision();
        }
        if (((SelectOptionValue)this.mode.getValue()).getName().equals("Vanilla")) {
            MoveUtils.setMotion(((Float)this.sliderOption.getValue()).floatValue());
        }
    }

    private void reallyWorld() {
        if (this.timer.hasTimeElapsed(1150L)) {
            this.boost = true;
        }
        if (this.timer.hasTimeElapsed(7000L)) {
            this.boost = false;
            this.timer.reset();
        }
        if (this.boost) {
            if (Speed.mc.player.isOnGround() && !Speed.mc.gameSettings.keyBindJump.isPressed()) {
                Speed.mc.player.jump();
            }
            mc.getTimer().setSpeed(Speed.mc.player.ticksExisted % 2 == 0 ? 1.5f : 1.2f);
        } else {
            mc.getTimer().setSpeed(0.05f);
        }
    }

    private void colision() {
        boolean canBoost;
        AxisAlignedBB aabb = Speed.mc.player.getBoundingBox().grow(0.1);
        int armorstans = Speed.mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
        boolean bl = canBoost = armorstans > 1 || Speed.mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
        if (canBoost && !Speed.mc.player.isOnGround()) {
            Speed.mc.player.jumpMovementFactor = armorstans > 1 ? 1.0f / (float)armorstans : 0.11f;
        }
    }

    @Override
    public void onEnabled() {
        this.boost = false;
    }

    @Override
    public void onDisabled() {
        mc.getTimer().setSpeed(1.0f);
    }
}
