package dimasik.modules.misc;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ClientSound
extends Module {
    private static final MultiOption settings = new MultiOption("Settings", new MultiOptionValue("Scrolling", false), new MultiOptionValue("Module", false), new MultiOptionValue("Notification", false));
    public SliderOption volume = new SliderOption("Volume", 70.0f, 0.0f, 100.0f).increment(1.0f);

    public ClientSound() {
        super("ClientSound", Category.MISC);
        this.settings(this.volume, settings);
    }

    @Generated
    public static MultiOption getSettings() {
        return settings;
    }
}
