package dimasik.managers.mods.voicechat.plugins.impl.mp3;

import de.maxhenkel.lame4j.UnknownPlatformException;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Encoder;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public class Mp3EncoderImpl
implements Mp3Encoder,
AutoCloseable {
    private final de.maxhenkel.lame4j.Mp3Encoder encoder;

    public Mp3EncoderImpl(AudioFormat audioFormat, int bitrate, int quality, OutputStream outputStream) throws IOException, UnknownPlatformException {
        this.encoder = new de.maxhenkel.lame4j.Mp3Encoder(audioFormat.getChannels(), (int)audioFormat.getSampleRate(), bitrate, quality, outputStream);
    }

    @Override
    public void encode(short[] samples) throws IOException {
        this.encoder.write(samples);
    }

    @Override
    public void close() throws IOException {
        this.encoder.close();
    }

    @Nullable
    public static Mp3Encoder createEncoder(AudioFormat audioFormat, int bitrate, int quality, OutputStream outputStream) {
        if (!CrossSideManager.get().useNatives()) {
            return null;
        }
        return Utils.createSafe(() -> new Mp3EncoderImpl(audioFormat, bitrate, quality, outputStream), e -> Voicechat.LOGGER.error("Failed to load mp3 encoder", e));
    }
}
