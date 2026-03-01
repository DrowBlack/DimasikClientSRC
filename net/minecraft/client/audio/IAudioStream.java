package net.minecraft.client.audio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;

public interface IAudioStream
extends Closeable {
    public AudioFormat getAudioFormat();

    public ByteBuffer readOggSoundWithCapacity(int var1) throws IOException;
}
