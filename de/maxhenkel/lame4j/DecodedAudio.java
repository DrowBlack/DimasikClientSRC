package de.maxhenkel.lame4j;

import de.maxhenkel.lame4j.Audio;
import javax.sound.sampled.AudioFormat;

public class DecodedAudio
implements Audio {
    private final int channelCount;
    private final int sampleRate;
    private final int bitRate;
    private final short[] samples;

    public DecodedAudio(int channelCount, int sampleRate, int bitRate, short[] samples) {
        this.channelCount = channelCount;
        this.sampleRate = sampleRate;
        this.bitRate = bitRate;
        this.samples = samples;
    }

    @Override
    public int getChannelCount() {
        return this.channelCount;
    }

    @Override
    public int getSampleRate() {
        return this.sampleRate;
    }

    @Override
    public int getBitRate() {
        return this.bitRate;
    }

    public short[] getSamples() {
        return this.samples;
    }

    @Override
    public AudioFormat createAudioFormat() {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, this.sampleRate, this.getSampleSizeInBits(), this.channelCount, this.getSampleSizeInBytes() * this.channelCount, this.sampleRate, false);
    }
}
