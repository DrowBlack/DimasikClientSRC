package de.maxhenkel.rnnoise4j;

import de.maxhenkel.rnnoise4j.RNNoise;
import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import java.io.IOException;

public class Denoiser
implements AutoCloseable {
    private long denoiser;

    public Denoiser() throws IOException, UnknownPlatformException {
        RNNoise.load();
        this.denoiser = Denoiser.createDenoiser0();
    }

    private static native long createDenoiser0();

    private native short[] denoise0(short[] var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public short[] denoise(short[] input) {
        Denoiser denoiser = this;
        synchronized (denoiser) {
            return this.denoise0(input);
        }
    }

    private native long destroyDenoiser0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        Denoiser denoiser = this;
        synchronized (denoiser) {
            this.destroyDenoiser0();
            this.denoiser = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClosed() {
        Denoiser denoiser = this;
        synchronized (denoiser) {
            return this.denoiser == 0L;
        }
    }
}
