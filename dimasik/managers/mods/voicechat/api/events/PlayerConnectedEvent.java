package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;

public interface PlayerConnectedEvent
extends ServerEvent {
    public VoicechatConnection getConnection();
}
