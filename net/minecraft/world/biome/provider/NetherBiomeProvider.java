package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.MaxMinNoiseMixer;

public class NetherBiomeProvider
extends BiomeProvider {
    private static final Noise DEFAULT_NOISE = new Noise(-7, ImmutableList.of(Double.valueOf(1.0), Double.valueOf(1.0)));
    public static final MapCodec<NetherBiomeProvider> PACKET_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(((MapCodec)Codec.LONG.fieldOf("seed")).forGetter(netherProvider -> netherProvider.seed), ((MapCodec)RecordCodecBuilder.create(biomeAttributes -> biomeAttributes.group(((MapCodec)Biome.Attributes.CODEC.fieldOf("parameters")).forGetter(Pair::getFirst), ((MapCodec)Biome.BIOME_CODEC.fieldOf("biome")).forGetter(Pair::getSecond)).apply((Applicative<Pair, ?>)biomeAttributes, Pair::of)).listOf().fieldOf("biomes")).forGetter(netherProvider -> netherProvider.biomeAttributes), ((MapCodec)Noise.CODEC.fieldOf("temperature_noise")).forGetter(netherProvider -> netherProvider.temperatureNoise), ((MapCodec)Noise.CODEC.fieldOf("humidity_noise")).forGetter(netherProvider -> netherProvider.humidityNoise), ((MapCodec)Noise.CODEC.fieldOf("altitude_noise")).forGetter(netherProvider -> netherProvider.altitudeNoise), ((MapCodec)Noise.CODEC.fieldOf("weirdness_noise")).forGetter(netherProvider -> netherProvider.weirdnessNoise)).apply((Applicative<NetherBiomeProvider, ?>)builder, NetherBiomeProvider::new));
    public static final Codec<NetherBiomeProvider> CODEC = Codec.mapEither(DefaultBuilder.CODEC, PACKET_CODEC).xmap(either -> either.map(DefaultBuilder::build, Function.identity()), netherProvider -> netherProvider.getDefaultBuilder().map(Either::left).orElseGet(() -> Either.right(netherProvider))).codec();
    private final Noise temperatureNoise;
    private final Noise humidityNoise;
    private final Noise altitudeNoise;
    private final Noise weirdnessNoise;
    private final MaxMinNoiseMixer temperatureNoiseMixer;
    private final MaxMinNoiseMixer humidityNoiseMixer;
    private final MaxMinNoiseMixer altitudeNoiseMixer;
    private final MaxMinNoiseMixer weirdnessNoiseMixer;
    private final List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes;
    private final boolean useHeightForNoise;
    private final long seed;
    private final Optional<Pair<Registry<Biome>, Preset>> netherProviderPreset;

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, Optional<Pair<Registry<Biome>, Preset>> netherProviderPreset) {
        this(seed, biomeAttributes, DEFAULT_NOISE, DEFAULT_NOISE, DEFAULT_NOISE, DEFAULT_NOISE, netherProviderPreset);
    }

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, Noise temperatureNoise, Noise humidityNoise, Noise altitudeNoise, Noise weirdnessNoise) {
        this(seed, biomeAttributes, temperatureNoise, humidityNoise, altitudeNoise, weirdnessNoise, Optional.empty());
    }

    private NetherBiomeProvider(long seed, List<Pair<Biome.Attributes, Supplier<Biome>>> biomeAttributes, Noise temperatureNoise, Noise humidityNoise, Noise altitudeNoise, Noise weirdnessNoise, Optional<Pair<Registry<Biome>, Preset>> netherProviderPreset) {
        super(biomeAttributes.stream().map(Pair::getSecond));
        this.seed = seed;
        this.netherProviderPreset = netherProviderPreset;
        this.temperatureNoise = temperatureNoise;
        this.humidityNoise = humidityNoise;
        this.altitudeNoise = altitudeNoise;
        this.weirdnessNoise = weirdnessNoise;
        this.temperatureNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed), temperatureNoise.getNumberOfOctaves(), temperatureNoise.getAmplitudes());
        this.humidityNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 1L), humidityNoise.getNumberOfOctaves(), humidityNoise.getAmplitudes());
        this.altitudeNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 2L), altitudeNoise.getNumberOfOctaves(), altitudeNoise.getAmplitudes());
        this.weirdnessNoiseMixer = MaxMinNoiseMixer.func_242930_a(new SharedSeedRandom(seed + 3L), weirdnessNoise.getNumberOfOctaves(), weirdnessNoise.getAmplitudes());
        this.biomeAttributes = biomeAttributes;
        this.useHeightForNoise = false;
    }

    @Override
    protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
        return CODEC;
    }

    @Override
    public BiomeProvider getBiomeProvider(long seed) {
        return new NetherBiomeProvider(seed, this.biomeAttributes, this.temperatureNoise, this.humidityNoise, this.altitudeNoise, this.weirdnessNoise, this.netherProviderPreset);
    }

    private Optional<DefaultBuilder> getDefaultBuilder() {
        return this.netherProviderPreset.map(registryPresetPair -> new DefaultBuilder((Preset)registryPresetPair.getSecond(), (Registry)registryPresetPair.getFirst(), this.seed));
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        int i = this.useHeightForNoise ? y : 0;
        Biome.Attributes biome$attributes = new Biome.Attributes((float)this.temperatureNoiseMixer.func_237211_a_(x, i, z), (float)this.humidityNoiseMixer.func_237211_a_(x, i, z), (float)this.altitudeNoiseMixer.func_237211_a_(x, i, z), (float)this.weirdnessNoiseMixer.func_237211_a_(x, i, z), 0.0f);
        return this.biomeAttributes.stream().min(Comparator.comparing(attributeBiomePair -> Float.valueOf(((Biome.Attributes)attributeBiomePair.getFirst()).getAttributeDifference(biome$attributes)))).map(Pair::getSecond).map(Supplier::get).orElse(BiomeRegistry.THE_VOID);
    }

    public boolean isDefaultPreset(long seed) {
        return this.seed == seed && this.netherProviderPreset.isPresent() && Objects.equals(this.netherProviderPreset.get().getSecond(), Preset.DEFAULT_NETHER_PROVIDER_PRESET);
    }

    static class Noise {
        private final int numOctaves;
        private final DoubleList amplitudes;
        public static final Codec<Noise> CODEC = RecordCodecBuilder.create(builder -> builder.group(((MapCodec)Codec.INT.fieldOf("firstOctave")).forGetter(Noise::getNumberOfOctaves), ((MapCodec)Codec.DOUBLE.listOf().fieldOf("amplitudes")).forGetter(Noise::getAmplitudes)).apply((Applicative<Noise, ?>)builder, Noise::new));

        public Noise(int numOctaves, List<Double> amplitudes) {
            this.numOctaves = numOctaves;
            this.amplitudes = new DoubleArrayList(amplitudes);
        }

        public int getNumberOfOctaves() {
            return this.numOctaves;
        }

        public DoubleList getAmplitudes() {
            return this.amplitudes;
        }
    }

    public static class Preset {
        private static final Map<ResourceLocation, Preset> PRESETS = Maps.newHashMap();
        public static final Preset DEFAULT_NETHER_PROVIDER_PRESET = new Preset(new ResourceLocation("nether"), (preset, lookupRegistry, seed) -> new NetherBiomeProvider((long)seed, (List<Pair<Biome.Attributes, Supplier<Biome>>>)ImmutableList.of(Pair.of(new Biome.Attributes(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), () -> lookupRegistry.getOrThrow(Biomes.NETHER_WASTES)), Pair.of(new Biome.Attributes(0.0f, -0.5f, 0.0f, 0.0f, 0.0f), () -> lookupRegistry.getOrThrow(Biomes.SOUL_SAND_VALLEY)), Pair.of(new Biome.Attributes(0.4f, 0.0f, 0.0f, 0.0f, 0.0f), () -> lookupRegistry.getOrThrow(Biomes.CRIMSON_FOREST)), Pair.of(new Biome.Attributes(0.0f, 0.5f, 0.0f, 0.0f, 0.375f), () -> lookupRegistry.getOrThrow(Biomes.WARPED_FOREST)), Pair.of(new Biome.Attributes(-0.5f, 0.0f, 0.0f, 0.0f, 0.175f), () -> lookupRegistry.getOrThrow(Biomes.BASALT_DELTAS))), Optional.of(Pair.of(lookupRegistry, preset))));
        private final ResourceLocation id;
        private final Function3<Preset, Registry<Biome>, Long, NetherBiomeProvider> netherProviderFunction;

        public Preset(ResourceLocation id, Function3<Preset, Registry<Biome>, Long, NetherBiomeProvider> netherProviderFunction) {
            this.id = id;
            this.netherProviderFunction = netherProviderFunction;
            PRESETS.put(id, this);
        }

        public NetherBiomeProvider build(Registry<Biome> lookupRegistry, long seed) {
            return this.netherProviderFunction.apply(this, lookupRegistry, seed);
        }
    }

    static final class DefaultBuilder {
        public static final MapCodec<DefaultBuilder> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(((MapCodec)ResourceLocation.CODEC.flatXmap(id -> Optional.ofNullable(Preset.PRESETS.get(id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown preset: " + String.valueOf(id))), preset -> DataResult.success(preset.id)).fieldOf("preset")).stable().forGetter(DefaultBuilder::getPreset), RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(DefaultBuilder::getLookupRegistry), ((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(DefaultBuilder::getSeed)).apply((Applicative<DefaultBuilder, ?>)builder, builder.stable(DefaultBuilder::new)));
        private final Preset preset;
        private final Registry<Biome> lookupRegistry;
        private final long seed;

        private DefaultBuilder(Preset preset, Registry<Biome> lookupRegistry, long seed) {
            this.preset = preset;
            this.lookupRegistry = lookupRegistry;
            this.seed = seed;
        }

        public Preset getPreset() {
            return this.preset;
        }

        public Registry<Biome> getLookupRegistry() {
            return this.lookupRegistry;
        }

        public long getSeed() {
            return this.seed;
        }

        public NetherBiomeProvider build() {
            return this.preset.build(this.lookupRegistry, this.seed);
        }
    }
}
