package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.ClientSoundEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;

public class ClientSoundEventImpl
extends ClientEventImpl
implements ClientSoundEvent {
    private short[] rawAudio;
    private boolean whispering;

    public ClientSoundEventImpl(short[] rawAudio, boolean whispering) {
        this.rawAudio = rawAudio;
        this.whispering = whispering;
    }

    @Override
    public short[] getRawAudio() {
        return this.rawAudio;
    }

    @Override
    public void setRawAudio(short[] rawAudio) {
        this.rawAudio = rawAudio;
    }

    @Override
    public boolean isWhispering() {
        return this.whispering;
    }
}
