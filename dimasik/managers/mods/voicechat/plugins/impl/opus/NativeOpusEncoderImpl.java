package dimasik.managers.mods.voicechat.plugins.impl.opus;

import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import java.io.IOException;

public class NativeOpusEncoderImpl
extends de.maxhenkel.opus4j.OpusEncoder
implements OpusEncoder {
    public NativeOpusEncoderImpl(int sampleRate, int channels, OpusEncoder.Application application) throws IOException, UnknownPlatformException {
        super(sampleRate, channels, application);
    }
}
