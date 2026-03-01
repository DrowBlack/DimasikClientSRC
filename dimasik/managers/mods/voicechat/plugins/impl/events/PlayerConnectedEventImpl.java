package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.PlayerConnectedEvent;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatConnectionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;

public class PlayerConnectedEventImpl
extends ServerEventImpl
implements PlayerConnectedEvent {
    protected VoicechatConnectionImpl connection;

    public PlayerConnectedEventImpl(VoicechatConnectionImpl connection) {
        this.connection = connection;
    }

    @Override
    public VoicechatConnection getConnection() {
        return this.connection;
    }
}
