package dimasik.modules.movement;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import net.minecraft.item.Items;

public class NoDelay
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Jump", true), new MultiOptionValue("Right Click", true), new MultiOptionValue("Experience Bottle", true));
    private final EventListener<EventUpdate> update = this::update;

    public NoDelay() {
        super("NoDelay", Category.MOVEMENT);
        this.settings(this.elements);
    }

    public void update(EventUpdate eventUpdate) {
        if (this.elements.getSelected("Jump")) {
            NoDelay.mc.player.setJumpTicks(0);
        }
        if (this.elements.getSelected("Right Click")) {
            mc.setRightClickDelayTimer(1);
        }
        if (this.elements.getSelected("Experience Bottle") && (NoDelay.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || NoDelay.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) && !NoDelay.mc.player.isHandActive()) {
            mc.setRightClickDelayTimer(1);
        }
    }
}
