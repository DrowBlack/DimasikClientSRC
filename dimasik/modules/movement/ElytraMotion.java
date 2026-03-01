package dimasik.modules.movement;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.movement.MovingEvent;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.modules.combat.Aura;
import dimasik.modules.combat.ElytraTarget;
import net.minecraft.entity.LivingEntity;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class ElytraMotion
extends Module {
    public final SliderOption attackDistance = new SliderOption("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 3.0f, 0.1f, 5.0f).increment(0.01f);
    private final CheckboxOption auto = new CheckboxOption("\u0410\u0432\u0442\u043e \u0424\u0435\u0439\u0440", false);
    public boolean freeze;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<MovingEvent> move = this::onMotion;

    public ElytraMotion() {
        super("ElytraMotion", Category.MOVEMENT);
        this.settings(this.attackDistance);
    }

    public void update(EventUpdate eventUpdate) {
        ElytraTarget elytraTarget;
        if (!ElytraMotion.mc.player.isElytraFlying()) {
            this.freeze = false;
            return;
        }
        Aura killAura = Load.getInstance().getHooks().getModuleManagers().getAura();
        if (this.check(killAura, elytraTarget = Load.getInstance().getHooks().getModuleManagers().getElytraTarget())) {
            ElytraMotion.mc.gameSettings.keyBindForward.setPressed(false);
            this.freeze = true;
        } else {
            ElytraMotion.mc.gameSettings.keyBindForward.setPressed(true);
            this.freeze = false;
        }
    }

    private void onMotion(MovingEvent eventMotion) {
        if (this.freeze) {
            eventMotion.getMotion().x = 0.0;
            eventMotion.getMotion().y = 0.0;
            eventMotion.getMotion().z = 0.0;
        }
    }

    public boolean check(Aura killAura, ElytraTarget elytraTarget) {
        LivingEntity target = killAura.getTarget();
        if (target == null) {
            return false;
        }
        boolean canTarget = ElytraMotion.mc.player.isElytraFlying();
        return target.getDistance(ElytraMotion.mc.player) < ((Float)this.attackDistance.getValue()).floatValue() && ElytraMotion.mc.player.isElytraFlying() && !Load.getInstance().getHooks().getModuleManagers().getAura().targetAir;
    }

    @Override
    public void onDisabled() {
        this.freeze = false;
    }
}
