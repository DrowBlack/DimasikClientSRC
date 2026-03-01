package de.maxhenkel.lame4j;

import de.maxhenkel.lame4j.Audio;
import de.maxhenkel.lame4j.DecodedAudio;
import de.maxhenkel.lame4j.Lame;
import de.maxhenkel.lame4j.ShortArrayBuffer;
import de.maxhenkel.lame4j.UnknownPlatformException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public class Mp3Decoder
implements Audio,
AutoCloseable {
    private long decoder;
    private final InputStream inputStream;

    public Mp3Decoder(InputStream inputStream) throws IOException, UnknownPlatformException {
        Lame.load();
        this.inputStream = inputStream;
        this.decoder = Mp3Decoder.createDecoder0();
    }

    private static native long createDecoder0();

    private native short[] decodeNextFrame0(InputStream var1) throws IOException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public short[] decodeNextFrame() throws IOException {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.decodeNextFrame0(this.inputStream);
        }
    }

    private native boolean headerParsed0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean headerParsed() {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.headerParsed0();
        }
    }

    private native int getChannelCount0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getChannelCount() {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.getChannelCount0();
        }
    }

    private native int getBitRate0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getBitRate() {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.getBitRate0();
        }
    }

    private native int getSampleRate0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSampleRate() {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.getSampleRate0();
        }
    }

    @Override
    @Nullable
    public AudioFormat createAudioFormat() {
        if (!this.headerParsed()) {
            return null;
        }
        return Audio.super.createAudioFormat();
    }

    private native void destroyDecoder0();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            this.destroyDecoder0();
            this.decoder = 0L;
            this.inputStream.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isClosed() {
        Mp3Decoder mp3Decoder = this;
        synchronized (mp3Decoder) {
            return this.decoder == 0L;
        }
    }

    public static DecodedAudio decode(InputStream inputStream) throws IOException, UnknownPlatformException {
        try (Mp3Decoder decoder = new Mp3Decoder(inputStream);){
            ShortArrayBuffer sampleBuffer = new ShortArrayBuffer(2048);
            while (true) {
                short[] samples;
                if ((samples = decoder.decodeNextFrame()) == null) {
                    if (sampleBuffer.size() > 0) break;
                    throw new IOException("No audio data found");
                }
                sampleBuffer.writeShorts(samples);
            }
            if (!decoder.headerParsed()) {
                throw new IOException("No header found");
            }
            DecodedAudio decodedAudio = new DecodedAudio(decoder.getChannelCount(), decoder.getSampleRate(), decoder.getBitRate(), sampleBuffer.toShortArray());
            return decodedAudio;
        }
    }
}
