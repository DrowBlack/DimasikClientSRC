package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.VoicechatServerStoppedEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;

public class VoicechatServerStoppedEventImpl
extends ServerEventImpl
implements VoicechatServerStoppedEvent {
    @Override
    public boolean isCancellable() {
        return false;
    }
}
