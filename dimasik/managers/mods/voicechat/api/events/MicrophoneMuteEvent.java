package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface MicrophoneMuteEvent
extends ClientEvent {
    public boolean isDisabled();
}
