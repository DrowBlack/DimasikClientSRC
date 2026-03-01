package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatClientApi;
import dimasik.managers.mods.voicechat.api.events.ClientEvent;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatClientApiImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.EventImpl;

public class ClientEventImpl
extends EventImpl
implements ClientEvent {
    @Override
    public VoicechatClientApi getVoicechat() {
        return VoicechatClientApiImpl.instance();
    }
}
