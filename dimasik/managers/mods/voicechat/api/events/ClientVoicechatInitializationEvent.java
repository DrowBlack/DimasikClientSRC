package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.ClientVoicechatSocket;
import dimasik.managers.mods.voicechat.api.events.ClientEvent;
import javax.annotation.Nullable;

public interface ClientVoicechatInitializationEvent
extends ClientEvent {
    public void setSocketImplementation(ClientVoicechatSocket var1);

    @Nullable
    public ClientVoicechatSocket getSocketImplementation();
}
