package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatSocket;
import dimasik.managers.mods.voicechat.api.events.VoicechatServerStartingEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;
import javax.annotation.Nullable;

public class VoicechatServerStartingEventImpl
extends ServerEventImpl
implements VoicechatServerStartingEvent {
    @Nullable
    private VoicechatSocket socketImplementation;

    @Override
    public void setSocketImplementation(VoicechatSocket socket) {
        this.socketImplementation = socket;
    }

    @Override
    @Nullable
    public VoicechatSocket getSocketImplementation() {
        return this.socketImplementation;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
