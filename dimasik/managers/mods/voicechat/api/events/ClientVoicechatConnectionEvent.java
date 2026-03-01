package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface ClientVoicechatConnectionEvent
extends ClientEvent {
    public boolean isConnected();
}
