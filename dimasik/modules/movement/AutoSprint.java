package dimasik.modules.movement;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.helpers.module.aura.AuraHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.modules.combat.Aura;
import dimasik.modules.combat.TriggerBot;
import dimasik.utils.player.MoveUtils;
import net.minecraft.potion.Effects;

public class AutoSprint
extends Module {
    private final AuraHelpers auraHelpers = new AuraHelpers();
    public final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Rage"));
    private final EventListener<EventUpdate> update = this::update;

    public AutoSprint() {
        super("AutoSprint", Category.MOVEMENT);
        this.settings(this.mode);
    }

    public void update(EventUpdate eventUpdate) {
        Aura aura = Load.getInstance().getHooks().getModuleManagers().getAura();
        TriggerBot triggerBot = Load.getInstance().getHooks().getModuleManagers().getTriggerBot();
        if (this.mode.getSelected("ReallyWorld")) {
            boolean reset;
            boolean bl = reset = aura.isToggled() && aura.options.getSelected("Only Crits") || triggerBot.isToggled();
            if (reset) {
                if (this.auraHelpers.sprint()) {
                    AutoSprint.mc.gameSettings.keyBindSprint.setPressed(true);
                } else {
                    AutoSprint.mc.gameSettings.keyBindSprint.setPressed(false);
                    AutoSprint.mc.player.setSprinting(false);
                }
            } else {
                AutoSprint.mc.gameSettings.keyBindSprint.setPressed(true);
            }
        }
        if (this.mode.getSelected("Rage") && this.canSprint()) {
            AutoSprint.mc.player.setSprinting(MoveUtils.isMoving());
        }
    }

    public boolean canSprint() {
        return !AutoSprint.mc.player.isSneaking() && !AutoSprint.mc.player.collidedHorizontally && AutoSprint.mc.player.movementInput.moveForward > 0.0f && !AutoSprint.mc.player.isCrouching() && !AutoSprint.mc.player.isPotionActive(Effects.SLOWNESS) && !AutoSprint.mc.player.isPotionActive(Effects.BLINDNESS) && !AutoSprint.mc.player.isVisuallySwimming() && !AutoSprint.mc.player.isHandActive();
    }

    @Override
    public void onDisabled() {
        AutoSprint.mc.gameSettings.keyBindSprint.setPressed(false);
    }
}
