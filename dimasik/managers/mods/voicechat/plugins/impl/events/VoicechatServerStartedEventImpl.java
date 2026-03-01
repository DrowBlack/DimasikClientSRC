package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.VoicechatServerStartedEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;

public class VoicechatServerStartedEventImpl
extends ServerEventImpl
implements VoicechatServerStartedEvent {
    @Override
    public boolean isCancellable() {
        return false;
    }
}
