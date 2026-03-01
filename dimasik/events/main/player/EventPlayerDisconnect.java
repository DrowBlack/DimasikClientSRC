package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import net.minecraft.entity.player.ServerPlayerEntity;

public class EventPlayerDisconnect
implements Event {
    public ServerPlayerEntity player;

    public EventPlayerDisconnect(ServerPlayerEntity player) {
        this.player = player;
    }
}
