package net.minecraft.client.audio;

import net.minecraft.client.audio.SoundEngine;

public interface ISoundEventAccessor<T> {
    public int getWeight();

    public T cloneEntry();

    public void enqueuePreload(SoundEngine var1);
}
