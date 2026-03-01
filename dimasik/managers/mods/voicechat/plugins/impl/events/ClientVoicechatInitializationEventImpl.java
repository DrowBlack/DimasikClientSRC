package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.ClientVoicechatSocket;
import dimasik.managers.mods.voicechat.api.events.ClientVoicechatInitializationEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;
import javax.annotation.Nullable;

public class ClientVoicechatInitializationEventImpl
extends ClientEventImpl
implements ClientVoicechatInitializationEvent {
    @Nullable
    private ClientVoicechatSocket socketImplementation;

    @Override
    public void setSocketImplementation(ClientVoicechatSocket socket) {
        this.socketImplementation = socket;
    }

    @Override
    @Nullable
    public ClientVoicechatSocket getSocketImplementation() {
        return this.socketImplementation;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
