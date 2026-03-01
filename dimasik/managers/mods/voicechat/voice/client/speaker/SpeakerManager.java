package dimasik.managers.mods.voicechat.voice.client.speaker;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.voice.client.SoundManager;
import dimasik.managers.mods.voicechat.voice.client.speaker.ALSpeaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.ALSpeakerBase;
import dimasik.managers.mods.voicechat.voice.client.speaker.AudioType;
import dimasik.managers.mods.voicechat.voice.client.speaker.FakeALSpeaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.MonoALSpeaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.Speaker;
import dimasik.managers.mods.voicechat.voice.client.speaker.SpeakerException;
import java.util.UUID;
import javax.annotation.Nullable;

public class SpeakerManager {
    public static Speaker createSpeaker(SoundManager soundManager, @Nullable UUID audioChannel) throws SpeakerException {
        ALSpeakerBase speaker = switch (VoicechatClient.CLIENT_CONFIG.audioType.get()) {
            default -> new ALSpeaker(soundManager, 48000, 960, audioChannel);
            case AudioType.REDUCED -> new FakeALSpeaker(soundManager, 48000, 960, audioChannel);
            case AudioType.OFF -> new MonoALSpeaker(soundManager, 48000, 960, audioChannel);
        };
        speaker.open();
        return speaker;
    }
}
