package dimasik.modules.render;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;

public class BetterTab
extends Module {
    public static CheckboxOption extend = new CheckboxOption("Extend tab", false);

    public BetterTab() {
        super("BetterTab", Category.RENDER);
        this.settings(extend);
    }
}
