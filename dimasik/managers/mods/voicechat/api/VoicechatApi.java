package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.Entity;
import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audio.AudioConverter;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Decoder;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Encoder;
import dimasik.managers.mods.voicechat.api.opus.OpusDecoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoderMode;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public interface VoicechatApi {
    public OpusEncoder createEncoder();

    public OpusEncoder createEncoder(OpusEncoderMode var1);

    public OpusDecoder createDecoder();

    @Nullable
    public Mp3Encoder createMp3Encoder(AudioFormat var1, int var2, int var3, OutputStream var4);

    @Nullable
    public Mp3Decoder createMp3Decoder(InputStream var1);

    public AudioConverter getAudioConverter();

    public Entity fromEntity(Object var1);

    public ServerLevel fromServerLevel(Object var1);

    public ServerPlayer fromServerPlayer(Object var1);

    public Position createPosition(double var1, double var3, double var5);

    public VolumeCategory.Builder volumeCategoryBuilder();

    public double getVoiceChatDistance();
}
