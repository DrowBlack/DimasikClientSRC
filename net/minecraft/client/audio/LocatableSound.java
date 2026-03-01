package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class LocatableSound
implements ISound {
    protected Sound sound;
    protected final SoundCategory category;
    protected final ResourceLocation positionedSoundLocation;
    protected float volume = 1.0f;
    protected float pitch = 1.0f;
    protected double x;
    protected double y;
    protected double z;
    protected boolean repeat;
    protected int repeatDelay;
    protected ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;
    protected boolean priority;
    protected boolean global;

    protected LocatableSound(SoundEvent soundIn, SoundCategory categoryIn) {
        this(soundIn.getName(), categoryIn);
    }

    protected LocatableSound(ResourceLocation soundId, SoundCategory categoryIn) {
        this.positionedSoundLocation = soundId;
        this.category = categoryIn;
    }

    @Override
    public ResourceLocation getSoundLocation() {
        return this.positionedSoundLocation;
    }

    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler) {
        SoundEventAccessor soundeventaccessor = handler.getAccessor(this.positionedSoundLocation);
        this.sound = soundeventaccessor == null ? SoundHandler.MISSING_SOUND : soundeventaccessor.cloneEntry();
        return soundeventaccessor;
    }

    @Override
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean canRepeat() {
        return this.repeat;
    }

    @Override
    public int getRepeatDelay() {
        return this.repeatDelay;
    }

    @Override
    public float getVolume() {
        return this.volume * this.sound.getVolume();
    }

    @Override
    public float getPitch() {
        return this.pitch * this.sound.getPitch();
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public ISound.AttenuationType getAttenuationType() {
        return this.attenuationType;
    }

    @Override
    public boolean isGlobal() {
        return this.global;
    }

    public String toString() {
        return "SoundInstance[" + String.valueOf(this.positionedSoundLocation) + "]";
    }
}
