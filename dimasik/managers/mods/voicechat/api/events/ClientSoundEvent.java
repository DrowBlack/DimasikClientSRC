package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface ClientSoundEvent
extends ClientEvent {
    public short[] getRawAudio();

    public void setRawAudio(short[] var1);

    public boolean isWhispering();
}
