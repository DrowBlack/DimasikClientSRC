package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VoicechatClientApi;
import dimasik.managers.mods.voicechat.api.events.Event;

public interface ClientEvent
extends Event {
    public VoicechatClientApi getVoicechat();
}
