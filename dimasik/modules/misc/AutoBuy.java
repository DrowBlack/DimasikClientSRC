package dimasik.modules.misc;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.input.EventInput;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;

public class AutoBuy
extends Module {
    public final MultiOption better = new MultiOption("Betters", new MultiOptionValue("Darkening Background", true), new MultiOptionValue("Colorful Background", true));
    public BindOption openKey = new BindOption("Open", -1);
    private final EventListener<EventInput> input = this::key;

    public AutoBuy() {
        super("AutoBuyTest", Category.MISC);
        this.settings(this.better, this.openKey);
    }

    public void key(EventInput eventInput) {
        if (this.openKey.getKey() == eventInput.getKey()) {
            mc.displayGuiScreen(Load.getInstance().getBuyScreen());
        }
    }
}
