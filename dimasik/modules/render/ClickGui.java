package dimasik.modules.render;

import dimasik.Load;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ClickGui
extends Module {
    private final SelectOption design = new SelectOption("Design", 0, new SelectOptionValue("Standard"), new SelectOptionValue("Transparent"));
    public final SelectOption size = new SelectOption("Size", 1, new SelectOptionValue("Big"), new SelectOptionValue("Small"));
    public final MultiOption better = new MultiOption("Betters", new MultiOptionValue("Darkening Background", true), new MultiOptionValue("Colorful Background", true));
    private final SliderOption compression = new SliderOption("Compression", 1.0f, 1.0f, 8.0f).increment(1.0f).visible(() -> this.design.getSelected("Transparent"));

    public ClickGui() {
        super("ClickGui", Category.RENDER);
        this.setCurrentKey(344);
        this.settings(this.design, this.better, this.compression, this.size);
    }

    @Override
    public void toggle() {
        if (!this.isToggled() && ClickGui.mc.currentScreen != Load.getInstance().getUiScreen() && !ClientManagers.isUnHook()) {
            mc.displayGuiScreen(Load.getInstance().getUiScreen());
        }
    }

    @Generated
    public SelectOption getDesign() {
        return this.design;
    }

    @Generated
    public SliderOption getCompression() {
        return this.compression;
    }
}
