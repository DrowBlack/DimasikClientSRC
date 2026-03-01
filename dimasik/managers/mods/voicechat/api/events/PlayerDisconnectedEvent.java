package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ServerEvent;
import java.util.UUID;

public interface PlayerDisconnectedEvent
extends ServerEvent {
    public UUID getPlayerUuid();
}
