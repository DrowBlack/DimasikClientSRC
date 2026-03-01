package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class MoodSoundAmbience {
    public static final Codec<MoodSoundAmbience> CODEC = RecordCodecBuilder.create(moodSoundCodecInstance -> moodSoundCodecInstance.group(((MapCodec)SoundEvent.CODEC.fieldOf("sound")).forGetter(moodSound -> moodSound.sound), ((MapCodec)Codec.INT.fieldOf("tick_delay")).forGetter(moodSound -> moodSound.tickDelay), ((MapCodec)Codec.INT.fieldOf("block_search_extent")).forGetter(moodSound -> moodSound.searchRadius), ((MapCodec)Codec.DOUBLE.fieldOf("offset")).forGetter(moodSound -> moodSound.offset)).apply((Applicative<MoodSoundAmbience, ?>)moodSoundCodecInstance, MoodSoundAmbience::new));
    public static final MoodSoundAmbience DEFAULT_CAVE = new MoodSoundAmbience(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
    private SoundEvent sound;
    private int tickDelay;
    private int searchRadius;
    private double offset;

    public MoodSoundAmbience(SoundEvent sound, int tickDelay, int searchRadius, double offset) {
        this.sound = sound;
        this.tickDelay = tickDelay;
        this.searchRadius = searchRadius;
        this.offset = offset;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public int getTickDelay() {
        return this.tickDelay;
    }

    public int getSearchRadius() {
        return this.searchRadius;
    }

    public double getOffset() {
        return this.offset;
    }
}
