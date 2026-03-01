package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.input.EventInput;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;

public class AutoMessage
extends Module {
    public BindOption skull = new BindOption("\u043a\u0438\u0434\u0430\u0439 \u0448\u0430\u0440", -1);
    public BindOption ez = new BindOption("-ez", -1);
    private final EventListener<EventInput> input = this::input;

    public AutoMessage() {
        super("AutoMessage", Category.MISC);
        this.settings(this.skull, this.ez);
    }

    public void input(EventInput eventInput) {
        if (eventInput.getKey() == this.skull.getKey()) {
            AutoMessage.mc.player.sendChatMessage("\u043a\u0438\u0434\u0430\u0439 \u0448\u0430\u0440");
        } else if (eventInput.getKey() == this.ez.getKey()) {
            AutoMessage.mc.player.sendChatMessage("!ez");
        }
    }
}
