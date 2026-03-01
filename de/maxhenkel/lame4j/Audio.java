package de.maxhenkel.lame4j;

import javax.sound.sampled.AudioFormat;

public interface Audio {
    public int getChannelCount();

    public int getSampleRate();

    public int getBitRate();

    default public int getSampleSizeInBytes() {
        return 2;
    }

    default public int getSampleSizeInBits() {
        return this.getSampleSizeInBytes() * 8;
    }

    default public AudioFormat createAudioFormat() {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, this.getSampleRate(), this.getSampleSizeInBits(), this.getChannelCount(), this.getSampleSizeInBytes() * this.getChannelCount(), this.getSampleRate(), false);
    }
}
