package de.maxhenkel.opus4j;

import de.maxhenkel.opus4j.Opus;
import de.maxhenkel.opus4j.UnknownPlatformException;
import java.io.IOException;

public class OpusEncoder
implements AutoCloseable {
    private long encoder;

    public OpusEncoder(int sampleRate, int channels, Application application) throws IOException, UnknownPlatformException {
        Opus.load();
        this.encoder = OpusEncoder.createEncoder0(sampleRate, channels, application);
    }

    private static native long createEncoder0(int var0, int var1, Application var2) throws IOException;

    private native void setMaxPayloadSize0(int var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxPayloadSize(int maxPayloadSize) {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            this.setMaxPayloadSize0(maxPayloadSize);
        }
    }

    private native int getMaxPayloadSize0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getMaxPayloadSize() {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            return this.getMaxPayloadSize0();
        }
    }

    private native byte[] encode0(short[] var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] encode(short[] input) {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            return this.encode0(input);
        }
    }

    private native void resetState0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetState() {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            this.resetState0();
        }
    }

    private native void destroyEncoder0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            this.destroyEncoder0();
            this.encoder = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClosed() {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            return this.encoder == 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        OpusEncoder opusEncoder = this;
        synchronized (opusEncoder) {
            return String.format("OpusEncoder[%d]", this.encoder);
        }
    }

    public static enum Application {
        VOIP(0),
        AUDIO(1),
        LOW_DELAY(2);

        private final int value;

        private Application(int value) {
            this.value = value;
        }
    }
}
