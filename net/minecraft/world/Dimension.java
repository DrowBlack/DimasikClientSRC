package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

public final class Dimension {
    public static final Codec<Dimension> CODEC = RecordCodecBuilder.create(builder -> builder.group(((MapCodec)DimensionType.DIMENSION_TYPE_CODEC.fieldOf("type")).forGetter(Dimension::getDimensionTypeSupplier), ((MapCodec)ChunkGenerator.field_235948_a_.fieldOf("generator")).forGetter(Dimension::getChunkGenerator)).apply((Applicative<Dimension, ?>)builder, builder.stable(Dimension::new)));
    public static final RegistryKey<Dimension> OVERWORLD = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, new ResourceLocation("overworld"));
    public static final RegistryKey<Dimension> THE_NETHER = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, new ResourceLocation("the_nether"));
    public static final RegistryKey<Dimension> THE_END = RegistryKey.getOrCreateKey(Registry.DIMENSION_KEY, new ResourceLocation("the_end"));
    private static final LinkedHashSet<RegistryKey<Dimension>> DIMENSION_KEYS = Sets.newLinkedHashSet(ImmutableList.of(OVERWORLD, THE_NETHER, THE_END));
    private final Supplier<DimensionType> dimensionTypeSupplier;
    private final ChunkGenerator chunkGenerator;

    public Dimension(Supplier<DimensionType> dimensionTypeSupplier, ChunkGenerator chunkGenerator) {
        this.dimensionTypeSupplier = dimensionTypeSupplier;
        this.chunkGenerator = chunkGenerator;
    }

    public Supplier<DimensionType> getDimensionTypeSupplier() {
        return this.dimensionTypeSupplier;
    }

    public DimensionType getDimensionType() {
        return this.dimensionTypeSupplier.get();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    public static SimpleRegistry<Dimension> func_236062_a_(SimpleRegistry<Dimension> registry) {
        SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<Dimension>(Registry.DIMENSION_KEY, Lifecycle.experimental());
        for (RegistryKey registryKey : DIMENSION_KEYS) {
            Dimension dimension = registry.getValueForKey(registryKey);
            if (dimension == null) continue;
            simpleregistry.register(registryKey, dimension, registry.getLifecycleByRegistry(dimension));
        }
        for (Map.Entry entry : registry.getEntries()) {
            RegistryKey registrykey1 = (RegistryKey)entry.getKey();
            if (DIMENSION_KEYS.contains(registrykey1)) continue;
            simpleregistry.register(registrykey1, (Dimension)entry.getValue(), registry.getLifecycleByRegistry((Dimension)entry.getValue()));
        }
        return simpleregistry;
    }

    public static boolean func_236060_a_(long seed, SimpleRegistry<Dimension> registry) {
        ArrayList<Map.Entry<RegistryKey<Dimension>, Dimension>> list = Lists.newArrayList(registry.getEntries());
        if (list.size() != DIMENSION_KEYS.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry1 = (Map.Entry)list.get(1);
        Map.Entry entry2 = (Map.Entry)list.get(2);
        if (entry.getKey() == OVERWORLD && entry1.getKey() == THE_NETHER && entry2.getKey() == THE_END) {
            if (!((Dimension)entry.getValue()).getDimensionType().isSame(DimensionType.OVERWORLD_TYPE) && ((Dimension)entry.getValue()).getDimensionType() != DimensionType.OVERWORLD_CAVES_TYPE) {
                return false;
            }
            if (!((Dimension)entry1.getValue()).getDimensionType().isSame(DimensionType.NETHER_TYPE)) {
                return false;
            }
            if (!((Dimension)entry2.getValue()).getDimensionType().isSame(DimensionType.END_TYPE)) {
                return false;
            }
            if (((Dimension)entry1.getValue()).getChunkGenerator() instanceof NoiseChunkGenerator && ((Dimension)entry2.getValue()).getChunkGenerator() instanceof NoiseChunkGenerator) {
                NoiseChunkGenerator noisechunkgenerator = (NoiseChunkGenerator)((Dimension)entry1.getValue()).getChunkGenerator();
                NoiseChunkGenerator noisechunkgenerator1 = (NoiseChunkGenerator)((Dimension)entry2.getValue()).getChunkGenerator();
                if (!noisechunkgenerator.func_236088_a_(seed, DimensionSettings.field_242736_e)) {
                    return false;
                }
                if (!noisechunkgenerator1.func_236088_a_(seed, DimensionSettings.field_242737_f)) {
                    return false;
                }
                if (!(noisechunkgenerator.getBiomeProvider() instanceof NetherBiomeProvider)) {
                    return false;
                }
                NetherBiomeProvider netherbiomeprovider = (NetherBiomeProvider)noisechunkgenerator.getBiomeProvider();
                if (!netherbiomeprovider.isDefaultPreset(seed)) {
                    return false;
                }
                if (!(noisechunkgenerator1.getBiomeProvider() instanceof EndBiomeProvider)) {
                    return false;
                }
                EndBiomeProvider endbiomeprovider = (EndBiomeProvider)noisechunkgenerator1.getBiomeProvider();
                return endbiomeprovider.areProvidersEqual(seed);
            }
            return false;
        }
        return false;
    }
}
