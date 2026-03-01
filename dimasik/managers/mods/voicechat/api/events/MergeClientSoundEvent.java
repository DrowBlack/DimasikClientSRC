package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.ClientEvent;

public interface MergeClientSoundEvent
extends ClientEvent {
    public void mergeAudio(short[] var1);
}
