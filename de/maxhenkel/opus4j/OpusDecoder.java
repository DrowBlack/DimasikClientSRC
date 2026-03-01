package de.maxhenkel.opus4j;

import de.maxhenkel.opus4j.Opus;
import de.maxhenkel.opus4j.UnknownPlatformException;
import java.io.IOException;
import javax.annotation.Nullable;

public class OpusDecoder
implements AutoCloseable {
    private long decoder;

    public OpusDecoder(int sampleRate, int channels) throws IOException, UnknownPlatformException {
        Opus.load();
        this.decoder = OpusDecoder.createDecoder0(sampleRate, channels);
    }

    private static native long createDecoder0(int var0, int var1) throws IOException;

    private native void setFrameSize0(int var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFrameSize(int frameSize) {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            this.setFrameSize0(frameSize);
        }
    }

    private native int getFrameSize0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getFrameSize() {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            return this.getFrameSize0();
        }
    }

    private native short[] decode0(@Nullable byte[] var1, boolean var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public short[] decode(@Nullable byte[] input, boolean fec) {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            return this.decode0(input, fec);
        }
    }

    public short[] decode(@Nullable byte[] input) {
        return this.decode(input, false);
    }

    public short[] decodeFec() {
        return this.decode(null, true);
    }

    private native void resetState0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetState() {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            this.resetState0();
        }
    }

    private native void destroyDecoder0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            this.destroyDecoder0();
            this.decoder = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClosed() {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            return this.decoder == 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        OpusDecoder opusDecoder = this;
        synchronized (opusDecoder) {
            return String.format("OpusDecoder[%d]", this.decoder);
        }
    }
}
