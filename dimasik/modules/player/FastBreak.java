package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;

public class FastBreak
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Default"), new SelectOptionValue("Custom"));
    private final SliderOption customSpeed = new SliderOption("Custom Speed", 1.0f, 0.0f, 1.0f).increment(0.01f).visible(() -> this.mode.getSelected("Custom"));
    private final EventListener<EventUpdate> update = this::update;

    public FastBreak() {
        super("FastBreak", Category.PLAYER);
        this.settings(this.mode, this.customSpeed);
    }

    public void update(EventUpdate event) {
        FastBreak.mc.playerController.resetBlockRemoving();
        FastBreak.mc.playerController.setBlockHitDelay(0);
        if (this.mode.getSelected("Custom")) {
            FastBreak.mc.playerController.setCurBlockDamageMP(((Float)this.customSpeed.getValue()).floatValue());
        }
    }
}
