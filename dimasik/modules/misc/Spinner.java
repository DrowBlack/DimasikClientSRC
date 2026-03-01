package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.player.EventSync;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;

public class Spinner
extends Module {
    private final SliderOption speed = new SliderOption("Speed", 30.0f, 1.0f, 100.0f).increment(1.0f);
    private final SliderOption angle = new SliderOption("Angle", 0.0f, -90.0f, 90.0f).increment(1.0f);
    private long rotationTime = 0L;
    private float rotation = 0.0f;
    private final EventListener<EventSync> sync = this::sync;

    public Spinner() {
        super("Spinner", Category.MISC);
        this.settings(this.speed, this.angle);
    }

    public void sync(EventSync event) {
        if (System.currentTimeMillis() - this.rotationTime >= 0L) {
            this.rotation -= ((Float)this.speed.getValue()).floatValue();
            Spinner.mc.player.rotationYawHead = this.rotation;
            Spinner.mc.player.renderYawOffset = this.rotation;
            Spinner.mc.player.rotationPitchHead = ((Float)this.angle.getValue()).floatValue();
            this.rotationTime = System.currentTimeMillis();
        }
    }
}
