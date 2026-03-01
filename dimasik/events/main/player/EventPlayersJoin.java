package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import net.minecraft.entity.player.ServerPlayerEntity;

public class EventPlayersJoin
implements Event {
    public ServerPlayerEntity player;

    public EventPlayersJoin(ServerPlayerEntity player) {
        this.player = player;
    }
}
