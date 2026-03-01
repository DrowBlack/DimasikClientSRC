package dimasik.managers.mods.voicechat.plugins.impl.opus;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.opus.OpusDecoder;
import dimasik.managers.mods.voicechat.decoder.OpusException;
import javax.annotation.Nullable;

public class JavaOpusDecoderImpl
implements OpusDecoder {
    protected dimasik.managers.mods.voicechat.decoder.OpusDecoder opusDecoder;
    protected short[] buffer;
    protected int sampleRate;

    public JavaOpusDecoderImpl(int sampleRate, int frameSize) {
        this.sampleRate = sampleRate;
        this.buffer = new short[frameSize];
        this.open();
    }

    private void open() {
        if (this.opusDecoder != null) {
            return;
        }
        try {
            this.opusDecoder = new dimasik.managers.mods.voicechat.decoder.OpusDecoder(this.sampleRate, 1);
        }
        catch (OpusException e) {
            throw new IllegalStateException("Failed to create Opus decoder", e);
        }
        Voicechat.LOGGER.debug("Initializing Java Opus decoder with sample rate {} Hz, frame size {} bytes", this.sampleRate, this.buffer.length);
    }

    @Override
    public short[] decode(@Nullable byte[] data) {
        int result;
        if (this.isClosed()) {
            throw new IllegalStateException("Decoder is closed");
        }
        try {
            result = data == null || data.length == 0 ? this.opusDecoder.decode(null, 0, 0, this.buffer, 0, this.buffer.length, true) : this.opusDecoder.decode(data, 0, data.length, this.buffer, 0, this.buffer.length, false);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to decode audio", e);
        }
        short[] audio = new short[result];
        System.arraycopy(this.buffer, 0, audio, 0, result);
        return audio;
    }

    @Override
    public boolean isClosed() {
        return this.opusDecoder == null;
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            return;
        }
        this.opusDecoder = null;
    }

    @Override
    public void resetState() {
        if (this.isClosed()) {
            throw new IllegalStateException("Decoder is closed");
        }
        this.opusDecoder.resetState();
    }
}
