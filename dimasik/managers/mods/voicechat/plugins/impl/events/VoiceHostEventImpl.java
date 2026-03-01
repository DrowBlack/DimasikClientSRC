package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.VoiceHostEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;

public class VoiceHostEventImpl
extends ServerEventImpl
implements VoiceHostEvent {
    private String voiceHost;

    public VoiceHostEventImpl(String voiceHost) {
        this.voiceHost = voiceHost;
    }

    @Override
    public String getVoiceHost() {
        return this.voiceHost;
    }

    @Override
    public void setVoiceHost(String voiceHost) {
        this.voiceHost = voiceHost;
    }
}
