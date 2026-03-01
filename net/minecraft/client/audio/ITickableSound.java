package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound;

public interface ITickableSound
extends ISound {
    public boolean isDonePlaying();

    public void tick();
}
