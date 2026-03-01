package dimasik.modules.render;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;

public class FullBright
extends Module {
    public FullBright() {
        super("FullBright", Category.RENDER);
    }

    @Override
    public void onEnabled() {
        assert (FullBright.mc.gameSettings != null);
        FullBright.mc.gameSettings.gamma = 1000000.0;
    }

    @Override
    public void onDisabled() {
        assert (FullBright.mc.gameSettings != null);
        FullBright.mc.gameSettings.gamma = 1.0;
    }
}
