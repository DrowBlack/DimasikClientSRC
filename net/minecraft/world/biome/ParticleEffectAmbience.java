package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;

public class ParticleEffectAmbience {
    public static final Codec<ParticleEffectAmbience> CODEC = RecordCodecBuilder.create(particleAmbienceCodecInstance -> particleAmbienceCodecInstance.group(((MapCodec)ParticleTypes.CODEC.fieldOf("options")).forGetter(particleAmbience -> particleAmbience.particleOptions), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(particleAmbience -> Float.valueOf(particleAmbience.probability))).apply((Applicative<ParticleEffectAmbience, ?>)particleAmbienceCodecInstance, ParticleEffectAmbience::new));
    private final IParticleData particleOptions;
    private final float probability;

    public ParticleEffectAmbience(IParticleData particleOptions, float probability) {
        this.particleOptions = particleOptions;
        this.probability = probability;
    }

    public IParticleData getParticleOptions() {
        return this.particleOptions;
    }

    public boolean shouldParticleSpawn(Random rand) {
        return rand.nextFloat() <= this.probability;
    }
}
