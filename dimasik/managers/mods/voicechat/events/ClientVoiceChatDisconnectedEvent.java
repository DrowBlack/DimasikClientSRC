package dimasik.managers.mods.voicechat.events;

import dimasik.events.api.main.Event;

public class ClientVoiceChatDisconnectedEvent
implements Event {
    public boolean isCancel() {
        return false;
    }
}
