package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface DestroyOpenALContextEvent
extends ClientEvent {
    public long getContext();

    public long getDevice();
}
