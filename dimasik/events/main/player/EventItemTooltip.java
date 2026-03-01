package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.item.ItemStack;

public class EventItemTooltip
implements Event {
    private final ItemStack itemStack;

    @Generated
    public EventItemTooltip(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Generated
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
