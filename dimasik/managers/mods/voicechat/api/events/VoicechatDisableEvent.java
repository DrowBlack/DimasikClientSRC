package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface VoicechatDisableEvent
extends ClientEvent {
    public boolean isDisabled();
}
