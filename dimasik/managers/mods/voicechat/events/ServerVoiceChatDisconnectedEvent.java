package dimasik.managers.mods.voicechat.events;

import dimasik.events.api.main.Event;
import java.util.UUID;

public class ServerVoiceChatDisconnectedEvent
implements Event {
    private final UUID playerID;

    public ServerVoiceChatDisconnectedEvent(UUID playerID) {
        this.playerID = playerID;
    }

    public UUID getPlayerID() {
        return this.playerID;
    }

    public boolean isCancel() {
        return false;
    }
}
