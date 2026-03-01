package net.minecraft.world;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;

public class DimensionType {
    public static final ResourceLocation OVERWORLD_ID = new ResourceLocation("overworld");
    public static final ResourceLocation THE_NETHER_ID = new ResourceLocation("the_nether");
    public static final ResourceLocation THE_END_ID = new ResourceLocation("the_end");
    public static final Codec<DimensionType> CODEC = RecordCodecBuilder.create(builder -> builder.group(Codec.LONG.optionalFieldOf("fixed_time").xmap(fixedTime -> fixedTime.map(OptionalLong::of).orElseGet(OptionalLong::empty), fixedTime -> fixedTime.isPresent() ? Optional.of(fixedTime.getAsLong()) : Optional.empty()).forGetter(type -> type.fixedTime), ((MapCodec)Codec.BOOL.fieldOf("has_skylight")).forGetter(DimensionType::hasSkyLight), ((MapCodec)Codec.BOOL.fieldOf("has_ceiling")).forGetter(DimensionType::getHasCeiling), ((MapCodec)Codec.BOOL.fieldOf("ultrawarm")).forGetter(DimensionType::isUltrawarm), ((MapCodec)Codec.BOOL.fieldOf("natural")).forGetter(DimensionType::isNatural), ((MapCodec)Codec.doubleRange(1.0E-5f, 3.0E7).fieldOf("coordinate_scale")).forGetter(DimensionType::getCoordinateScale), ((MapCodec)Codec.BOOL.fieldOf("piglin_safe")).forGetter(DimensionType::isPiglinSafe), ((MapCodec)Codec.BOOL.fieldOf("bed_works")).forGetter(DimensionType::doesBedWork), ((MapCodec)Codec.BOOL.fieldOf("respawn_anchor_works")).forGetter(DimensionType::doesRespawnAnchorWorks), ((MapCodec)Codec.BOOL.fieldOf("has_raids")).forGetter(DimensionType::isHasRaids), ((MapCodec)Codec.intRange(0, 256).fieldOf("logical_height")).forGetter(DimensionType::getLogicalHeight), ((MapCodec)ResourceLocation.CODEC.fieldOf("infiniburn")).forGetter(type -> type.infiniburn), ((MapCodec)ResourceLocation.CODEC.fieldOf("effects")).orElse(OVERWORLD_ID).forGetter(type -> type.effects), ((MapCodec)Codec.FLOAT.fieldOf("ambient_light")).forGetter(type -> Float.valueOf(type.ambientLight))).apply((Applicative<DimensionType, ?>)builder, DimensionType::new));
    public static final float[] MOON_PHASE_FACTORS = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final RegistryKey<DimensionType> OVERWORLD = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation("overworld"));
    public static final RegistryKey<DimensionType> THE_NETHER = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation("the_nether"));
    public static final RegistryKey<DimensionType> THE_END = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation("the_end"));
    protected static final DimensionType OVERWORLD_TYPE = new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, false, false, true, false, true, 256, ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_ID, 0.0f);
    protected static final DimensionType NETHER_TYPE = new DimensionType(OptionalLong.of(18000L), false, true, true, false, 8.0, false, true, false, true, false, 128, FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_NETHER.getName(), THE_NETHER_ID, 0.1f);
    protected static final DimensionType END_TYPE = new DimensionType(OptionalLong.of(6000L), false, false, false, false, 1.0, true, false, false, false, true, 256, FuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_END.getName(), THE_END_ID, 0.0f);
    public static final RegistryKey<DimensionType> OVERWORLD_CAVES = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation("overworld_caves"));
    protected static final DimensionType OVERWORLD_CAVES_TYPE = new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0, false, false, true, false, true, 256, ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), OVERWORLD_ID, 0.0f);
    public static final Codec<Supplier<DimensionType>> DIMENSION_TYPE_CODEC = RegistryKeyCodec.create(Registry.DIMENSION_TYPE_KEY, CODEC);
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultrawarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean hasDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final IBiomeMagnifier magnifier;
    private final ResourceLocation infiniburn;
    private final ResourceLocation effects;
    private final float ambientLight;
    private final transient float[] ambientWorldLight;

    protected DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, ResourceLocation infiniburn, ResourceLocation effects, float ambientLight) {
        this(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, coordinateScale, false, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, FuzzedBiomeMagnifier.INSTANCE, infiniburn, effects, ambientLight);
    }

    protected DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean hasDragonFight, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, IBiomeMagnifier magnifier, ResourceLocation infiniburn, ResourceLocation effects, float ambientLight) {
        this.fixedTime = fixedTime;
        this.hasSkyLight = hasSkyLight;
        this.hasCeiling = hasCeiling;
        this.ultrawarm = ultrawarm;
        this.natural = natural;
        this.coordinateScale = coordinateScale;
        this.hasDragonFight = hasDragonFight;
        this.piglinSafe = piglinSafe;
        this.bedWorks = bedWorks;
        this.respawnAnchorWorks = respawnAnchorWorks;
        this.hasRaids = hasRaids;
        this.logicalHeight = logicalHeight;
        this.magnifier = magnifier;
        this.infiniburn = infiniburn;
        this.effects = effects;
        this.ambientLight = ambientLight;
        this.ambientWorldLight = DimensionType.defaultAmbientLightWorld(ambientLight);
    }

    private static float[] defaultAmbientLightWorld(float light) {
        float[] afloat = new float[16];
        for (int i = 0; i <= 15; ++i) {
            float f = (float)i / 15.0f;
            float f1 = f / (4.0f - 3.0f * f);
            afloat[i] = MathHelper.lerp(light, f1, 1.0f);
        }
        return afloat;
    }

    @Deprecated
    public static DataResult<RegistryKey<World>> decodeWorldKey(Dynamic<?> dynamic) {
        Optional<Number> optional = dynamic.asNumber().result();
        if (optional.isPresent()) {
            int i = optional.get().intValue();
            if (i == -1) {
                return DataResult.success(World.THE_NETHER);
            }
            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }
            if (i == 1) {
                return DataResult.success(World.THE_END);
            }
        }
        return World.CODEC.parse(dynamic);
    }

    public static DynamicRegistries.Impl registerTypes(DynamicRegistries.Impl impl) {
        MutableRegistry<DimensionType> mutableregistry = impl.getRegistry(Registry.DIMENSION_TYPE_KEY);
        mutableregistry.register(OVERWORLD, OVERWORLD_TYPE, Lifecycle.stable());
        mutableregistry.register(OVERWORLD_CAVES, OVERWORLD_CAVES_TYPE, Lifecycle.stable());
        mutableregistry.register(THE_NETHER, NETHER_TYPE, Lifecycle.stable());
        mutableregistry.register(THE_END, END_TYPE, Lifecycle.stable());
        return impl;
    }

    private static ChunkGenerator getEndChunkGenerator(Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> settingsRegistry, long seed) {
        return new NoiseChunkGenerator(new EndBiomeProvider(lookUpRegistryBiome, seed), seed, () -> settingsRegistry.getOrThrow(DimensionSettings.field_242737_f));
    }

    private static ChunkGenerator getNetherChunkGenerator(Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> lookUpRegistryDimensionType, long seed) {
        return new NoiseChunkGenerator(NetherBiomeProvider.Preset.DEFAULT_NETHER_PROVIDER_PRESET.build(lookUpRegistryBiome, seed), seed, () -> lookUpRegistryDimensionType.getOrThrow(DimensionSettings.field_242736_e));
    }

    public static SimpleRegistry<Dimension> getDefaultSimpleRegistry(Registry<DimensionType> lookUpRegistryDimensionType, Registry<Biome> lookUpRegistryBiome, Registry<DimensionSettings> lookUpRegistryDimensionSettings, long seed) {
        SimpleRegistry<Dimension> simpleregistry = new SimpleRegistry<Dimension>(Registry.DIMENSION_KEY, Lifecycle.experimental());
        simpleregistry.register(Dimension.THE_NETHER, new Dimension(() -> lookUpRegistryDimensionType.getOrThrow(THE_NETHER), DimensionType.getNetherChunkGenerator(lookUpRegistryBiome, lookUpRegistryDimensionSettings, seed)), Lifecycle.stable());
        simpleregistry.register(Dimension.THE_END, new Dimension(() -> lookUpRegistryDimensionType.getOrThrow(THE_END), DimensionType.getEndChunkGenerator(lookUpRegistryBiome, lookUpRegistryDimensionSettings, seed)), Lifecycle.stable());
        return simpleregistry;
    }

    public static double getCoordinateDifference(DimensionType firstType, DimensionType secondType) {
        double d0 = firstType.getCoordinateScale();
        double d1 = secondType.getCoordinateScale();
        return d0 / d1;
    }

    @Deprecated
    public String getSuffix() {
        return this.isSame(END_TYPE) ? "_end" : "";
    }

    public static File getDimensionFolder(RegistryKey<World> dimensionKey, File worldFolder) {
        if (dimensionKey == World.OVERWORLD) {
            return worldFolder;
        }
        if (dimensionKey == World.THE_END) {
            return new File(worldFolder, "DIM1");
        }
        return dimensionKey == World.THE_NETHER ? new File(worldFolder, "DIM-1") : new File(worldFolder, "dimensions/" + dimensionKey.getLocation().getNamespace() + "/" + dimensionKey.getLocation().getPath());
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public boolean getHasCeiling() {
        return this.hasCeiling;
    }

    public boolean isUltrawarm() {
        return this.ultrawarm;
    }

    public boolean isNatural() {
        return this.natural;
    }

    public double getCoordinateScale() {
        return this.coordinateScale;
    }

    public boolean isPiglinSafe() {
        return this.piglinSafe;
    }

    public boolean doesBedWork() {
        return this.bedWorks;
    }

    public boolean doesRespawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public boolean isHasRaids() {
        return this.hasRaids;
    }

    public int getLogicalHeight() {
        return this.logicalHeight;
    }

    public boolean doesHasDragonFight() {
        return this.hasDragonFight;
    }

    public IBiomeMagnifier getMagnifier() {
        return this.magnifier;
    }

    public boolean doesFixedTimeExist() {
        return this.fixedTime.isPresent();
    }

    public float getCelestrialAngleByTime(long dayTime) {
        double d0 = MathHelper.frac((double)this.fixedTime.orElse(dayTime) / 24000.0 - 0.25);
        double d1 = 0.5 - Math.cos(d0 * Math.PI) / 2.0;
        return (float)(d0 * 2.0 + d1) / 3.0f;
    }

    public int getMoonPhase(long dayTime) {
        return (int)(dayTime / 24000L % 8L + 8L) % 8;
    }

    public float getAmbientLight(int lightIn) {
        return this.ambientWorldLight[lightIn];
    }

    public ITag<Block> isInfiniBurn() {
        ITag<Block> itag = BlockTags.getCollection().get(this.infiniburn);
        return itag != null ? itag : BlockTags.INFINIBURN_OVERWORLD;
    }

    public ResourceLocation getEffects() {
        return this.effects;
    }

    public boolean isSame(DimensionType type) {
        if (this == type) {
            return true;
        }
        return this.hasSkyLight == type.hasSkyLight && this.hasCeiling == type.hasCeiling && this.ultrawarm == type.ultrawarm && this.natural == type.natural && this.coordinateScale == type.coordinateScale && this.hasDragonFight == type.hasDragonFight && this.piglinSafe == type.piglinSafe && this.bedWorks == type.bedWorks && this.respawnAnchorWorks == type.respawnAnchorWorks && this.hasRaids == type.hasRaids && this.logicalHeight == type.logicalHeight && Float.compare(type.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(type.fixedTime) && this.magnifier.equals(type.magnifier) && this.infiniburn.equals(type.infiniburn) && this.effects.equals(type.effects);
    }
}
