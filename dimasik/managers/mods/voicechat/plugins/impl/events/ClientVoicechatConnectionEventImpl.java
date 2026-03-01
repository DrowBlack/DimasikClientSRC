package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.ClientVoicechatConnectionEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;

public class ClientVoicechatConnectionEventImpl
extends ClientEventImpl
implements ClientVoicechatConnectionEvent {
    private final boolean connected;

    public ClientVoicechatConnectionEventImpl(boolean connected) {
        this.connected = connected;
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }
}
