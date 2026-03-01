package dimasik.managers.mods.voicechat.events;

import dimasik.events.api.main.Event;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;

public class ClientVoiceChatConnectedEvent
implements Event {
    private final ClientVoicechatConnection client;

    public ClientVoiceChatConnectedEvent(ClientVoicechatConnection client) {
        this.client = client;
    }

    public ClientVoicechatConnection getClient() {
        return this.client;
    }

    public boolean isCancel() {
        return false;
    }
}
