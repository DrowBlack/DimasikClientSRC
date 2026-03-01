package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EventItemPickup
implements Event {
    private final PlayerEntity player;
    private final ItemEntity item;

    @Generated
    public EventItemPickup(PlayerEntity player, ItemEntity item) {
        this.player = player;
        this.item = item;
    }

    @Generated
    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Generated
    public ItemEntity getItem() {
        return this.item;
    }
}
