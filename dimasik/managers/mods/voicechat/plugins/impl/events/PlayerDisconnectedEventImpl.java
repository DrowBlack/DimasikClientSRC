package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.PlayerDisconnectedEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;
import java.util.UUID;

public class PlayerDisconnectedEventImpl
extends ServerEventImpl
implements PlayerDisconnectedEvent {
    protected UUID player;

    public PlayerDisconnectedEventImpl(UUID player) {
        this.player = player;
    }

    @Override
    public UUID getPlayerUuid() {
        return this.player;
    }
}
