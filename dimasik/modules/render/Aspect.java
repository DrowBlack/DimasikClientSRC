package dimasik.modules.render;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;

public class Aspect
extends Module {
    public SliderOption aspectvalue = new SliderOption("Aspect value", 0.3f, 0.0f, 0.8f).increment(0.05f);

    public Aspect() {
        super("Aspect", Category.RENDER);
        this.settings(this.aspectvalue);
    }
}
