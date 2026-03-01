package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public interface ISound {
    public ResourceLocation getSoundLocation();

    @Nullable
    public SoundEventAccessor createAccessor(SoundHandler var1);

    public Sound getSound();

    public SoundCategory getCategory();

    public boolean canRepeat();

    public boolean isGlobal();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public double getX();

    public double getY();

    public double getZ();

    public AttenuationType getAttenuationType();

    default public boolean canBeSilent() {
        return false;
    }

    default public boolean shouldPlaySound() {
        return true;
    }

    public static enum AttenuationType {
        NONE,
        LINEAR;

    }
}
