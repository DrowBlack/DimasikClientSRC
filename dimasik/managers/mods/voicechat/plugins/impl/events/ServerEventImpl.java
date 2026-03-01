package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatServerApi;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatServerApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.EventImpl;

public class ServerEventImpl
extends EventImpl
implements ServerEvent {
    @Override
    public VoicechatServerApi getVoicechat() {
        return VoicechatServerApiImpl.instance();
    }
}
