package de.maxhenkel.lame4j;

import de.maxhenkel.lame4j.Lame;
import de.maxhenkel.lame4j.UnknownPlatformException;
import java.io.IOException;
import java.io.OutputStream;

public class Mp3Encoder
implements AutoCloseable {
    private long lame;
    private final OutputStream outputStream;

    public Mp3Encoder(int channels, int sampleRate, int bitRate, int quality, OutputStream outputStream) throws IOException, UnknownPlatformException {
        Lame.load();
        this.lame = Mp3Encoder.createEncoder0(channels, sampleRate, bitRate, quality);
        this.outputStream = outputStream;
    }

    private static native long createEncoder0(int var0, int var1, int var2, int var3) throws IOException;

    private native byte[] writeInternal0(short[] var1) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(short[] input) throws IOException {
        Mp3Encoder mp3Encoder = this;
        synchronized (mp3Encoder) {
            byte[] buffer = this.writeInternal0(input);
            this.outputStream.write(buffer, 0, buffer.length);
        }
    }

    private native byte[] flush0() throws IOException;

    private native void destroyEncoder0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        Mp3Encoder mp3Encoder = this;
        synchronized (mp3Encoder) {
            byte[] flushBuffer = this.flush0();
            this.outputStream.write(flushBuffer, 0, flushBuffer.length);
            this.destroyEncoder0();
            this.lame = 0L;
            this.outputStream.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClosed() {
        Mp3Encoder mp3Encoder = this;
        synchronized (mp3Encoder) {
            return this.lame == 0L;
        }
    }
}
