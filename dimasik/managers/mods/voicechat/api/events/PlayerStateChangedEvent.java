package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;
import java.util.UUID;
import javax.annotation.Nullable;

public interface PlayerStateChangedEvent
extends ServerEvent {
    public boolean isDisabled();

    public boolean isDisconnected();

    public UUID getPlayerUuid();

    @Nullable
    public VoicechatConnection getConnection();
}
