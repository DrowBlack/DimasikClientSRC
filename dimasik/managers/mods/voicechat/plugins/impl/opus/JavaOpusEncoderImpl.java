package dimasik.managers.mods.voicechat.plugins.impl.opus;

import de.maxhenkel.opus4j.OpusEncoder;
import dimasik.managers.mods.voicechat.decoder.OpusApplication;
import dimasik.managers.mods.voicechat.decoder.OpusEncoder;

public class JavaOpusEncoderImpl
implements dimasik.managers.mods.voicechat.api.opus.OpusEncoder {
    protected OpusEncoder opusEncoder;
    protected byte[] buffer;
    protected int sampleRate;
    protected int frameSize;
    protected OpusEncoder.Application application;

    public JavaOpusEncoderImpl(int sampleRate, int frameSize, int maxPayloadSize, OpusEncoder.Application application) {
        this.sampleRate = sampleRate;
        this.frameSize = frameSize;
        this.application = application;
        this.buffer = new byte[maxPayloadSize];
        this.open();
    }

    private void open() {
        if (this.opusEncoder != null) {
            return;
        }
        try {
            this.opusEncoder = new OpusEncoder(this.sampleRate, 1, JavaOpusEncoderImpl.getApplication(this.application));
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to create Opus encoder", e);
        }
    }

    @Override
    public byte[] encode(short[] rawAudio) {
        int result;
        if (this.isClosed()) {
            throw new IllegalStateException("Encoder is closed");
        }
        try {
            result = this.opusEncoder.encode(rawAudio, 0, this.frameSize, this.buffer, 0, this.buffer.length);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to encode audio", e);
        }
        if (result < 0) {
            throw new RuntimeException("Failed to encode audio data");
        }
        byte[] audio = new byte[result];
        System.arraycopy(this.buffer, 0, audio, 0, result);
        return audio;
    }

    @Override
    public void resetState() {
        if (this.isClosed()) {
            throw new IllegalStateException("Encoder is closed");
        }
        this.opusEncoder.resetState();
    }

    @Override
    public boolean isClosed() {
        return this.opusEncoder == null;
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            return;
        }
        this.opusEncoder = null;
    }

    public static OpusApplication getApplication(OpusEncoder.Application application) {
        switch (application) {
            default: {
                return OpusApplication.OPUS_APPLICATION_VOIP;
            }
            case AUDIO: {
                return OpusApplication.OPUS_APPLICATION_AUDIO;
            }
            case LOW_DELAY: 
        }
        return OpusApplication.OPUS_APPLICATION_RESTRICTED_LOWDELAY;
    }
}
