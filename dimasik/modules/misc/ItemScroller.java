package dimasik.modules.misc;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ItemScroller
extends Module {
    private final SliderOption delay = new SliderOption("Delay", 50.0f, 0.0f, 100.0f).increment(5.0f);

    public ItemScroller() {
        super("ItemScroller", Category.MISC);
        this.settings(this.delay);
    }

    @Generated
    public SliderOption getDelay() {
        return this.delay;
    }
}
