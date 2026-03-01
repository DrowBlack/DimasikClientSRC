package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class SimpleSound
extends LocatableSound {
    public SimpleSound(SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn, BlockPos pos) {
        this(soundIn, categoryIn, volumeIn, pitchIn, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    public static SimpleSound master(SoundEvent soundIn, float pitchIn) {
        return SimpleSound.master(soundIn, pitchIn, 0.25f);
    }

    public static SimpleSound master(SoundEvent soundIn, float pitchIn, float volumeIn) {
        return new SimpleSound(soundIn.getName(), SoundCategory.MASTER, volumeIn, pitchIn, false, 0, ISound.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSound music(SoundEvent soundIn) {
        return new SimpleSound(soundIn.getName(), SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSound record(SoundEvent soundIn, double xIn, double zIn, double zTrueIn) {
        return new SimpleSound(soundIn, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, xIn, zIn, zTrueIn);
    }

    public static SimpleSound ambientWithoutAttenuation(SoundEvent sound, float volume, float pitch) {
        return new SimpleSound(sound.getName(), SoundCategory.AMBIENT, pitch, volume, false, 0, ISound.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSound ambient(SoundEvent sound) {
        return SimpleSound.ambientWithoutAttenuation(sound, 1.0f, 1.0f);
    }

    public static SimpleSound ambientWithAttenuation(SoundEvent sound, double x, double y, double z) {
        return new SimpleSound(sound, SoundCategory.AMBIENT, 1.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, x, y, z);
    }

    public SimpleSound(SoundEvent sound, SoundCategory category, float volume, float pitch, double x, double y, double z) {
        this(sound, category, volume, pitch, false, 0, ISound.AttenuationType.LINEAR, x, y, z);
    }

    private SimpleSound(SoundEvent sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, ISound.AttenuationType attenuationType, double x, double y, double z) {
        this(sound.getName(), category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, false);
    }

    public SimpleSound(ResourceLocation sound, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, ISound.AttenuationType attenuationType, double x, double y, double z, boolean global) {
        super(sound, category);
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
        this.global = global;
    }
}
