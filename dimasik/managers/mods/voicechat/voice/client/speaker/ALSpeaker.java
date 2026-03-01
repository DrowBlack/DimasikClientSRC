package dimasik.managers.mods.voicechat.voice.client.speaker;

import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import dimasik.managers.mods.voicechat.voice.client.speaker.ALSpeakerBase;
import java.util.UUID;
import javax.annotation.Nullable;

public class ALSpeaker
extends ALSpeakerBase {
    public ALSpeaker(SoundManager soundManager, int sampleRate, int bufferSize, @Nullable UUID audioChannelId) {
        super(soundManager, sampleRate, bufferSize, audioChannelId);
    }

    @Override
    protected int getFormat() {
        return 4353;
    }
}
