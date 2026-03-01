package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.VoicechatServerApi;
import dimasik.managers.mods.voicechat.api.events.Event;

public interface ServerEvent
extends Event {
    public VoicechatServerApi getVoicechat();
}
