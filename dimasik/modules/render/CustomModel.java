package dimasik.modules.render;

import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import lombok.Generated;

public class CustomModel
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Rabbit"), new SelectOptionValue("Jeff Killer"), new SelectOptionValue("Demon"), new SelectOptionValue("White Demon"), new SelectOptionValue("Freddy Bear"), new SelectOptionValue("Chinchilla"));
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Self", true), new MultiOptionValue("Friends", true), new MultiOptionValue("Others", true));

    public CustomModel() {
        super("CustomModel", Category.RENDER);
        this.settings(this.mode, this.elements);
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public MultiOption getElements() {
        return this.elements;
    }
}
