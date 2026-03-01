package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;

public class Timer
extends Module {
    public SliderOption timerAmount = new SliderOption("Timer Speed", 2.0f, 1.0f, 10.0f).increment(0.01f);
    private final EventListener<EventUpdate> updates = this::update;

    public Timer() {
        super("Timer", Category.MOVEMENT);
        this.settings(this.timerAmount);
    }

    public void update(EventUpdate e) {
        Timer.mc.timer.speed = ((Float)this.timerAmount.getValue()).floatValue();
    }

    @Override
    public void onDisabled() {
        Timer.mc.timer.speed = 1.0f;
        super.onDisabled();
    }

    @Override
    public void onEnabled() {
        Timer.mc.timer.speed = 1.0f;
        super.onEnabled();
    }
}
