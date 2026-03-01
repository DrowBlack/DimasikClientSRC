package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.misc.EventPush;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;

public class NoPush
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Blocks", true), new MultiOptionValue("Entities", true), new MultiOptionValue("Water", false));
    private final EventListener<EventPush> push = this::push;

    public NoPush() {
        super("NoPush", Category.PLAYER);
        this.settings(this.elements);
    }

    public void push(EventPush eventPush) {
        EventPush.PushType pushType = eventPush.getPushType();
        boolean cancelPush = switch (pushType) {
            default -> throw new IncompatibleClassChangeError();
            case EventPush.PushType.Entities -> this.elements.getSelected("Entities");
            case EventPush.PushType.Blocks -> this.elements.getSelected("Blocks");
            case EventPush.PushType.Water -> this.elements.getSelected("Water");
        };
        eventPush.setCancelled(cancelPush);
    }
}
