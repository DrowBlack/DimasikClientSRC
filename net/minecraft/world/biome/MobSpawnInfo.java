package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobSpawnInfo {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MobSpawnInfo EMPTY = new MobSpawnInfo(0.1f, (Map<EntityClassification, List<Spawners>>)Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap(classification -> classification, classification -> ImmutableList.of())), ImmutableMap.of(), false);
    public static final MapCodec<MobSpawnInfo> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(Codec.FLOAT.optionalFieldOf("creature_spawn_probability", Float.valueOf(0.1f)).forGetter(spawnInfo -> Float.valueOf(spawnInfo.creatureSpawnProbability)), Codec.simpleMap(EntityClassification.CODEC, Spawners.CODEC.listOf().promotePartial((Consumer)Util.func_240982_a_("Spawn data: ", LOGGER::error)), IStringSerializable.createKeyable(EntityClassification.values())).fieldOf("spawners").forGetter(spawnInfo -> spawnInfo.spawners), Codec.simpleMap(Registry.ENTITY_TYPE, SpawnCosts.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(spawnInfo -> spawnInfo.spawnCosts), ((MapCodec)Codec.BOOL.fieldOf("player_spawn_friendly")).orElse(false).forGetter(MobSpawnInfo::isValidSpawnBiomeForPlayer)).apply((Applicative<MobSpawnInfo, ?>)builder, MobSpawnInfo::new));
    private final float creatureSpawnProbability;
    private final Map<EntityClassification, List<Spawners>> spawners;
    private final Map<EntityType<?>, SpawnCosts> spawnCosts;
    private final boolean validSpawnBiomeForPlayer;

    private MobSpawnInfo(float creatureSpawnProbability, Map<EntityClassification, List<Spawners>> spawners, Map<EntityType<?>, SpawnCosts> spawnCosts, boolean isValidSpawnBiomeForPlayer) {
        this.creatureSpawnProbability = creatureSpawnProbability;
        this.spawners = spawners;
        this.spawnCosts = spawnCosts;
        this.validSpawnBiomeForPlayer = isValidSpawnBiomeForPlayer;
    }

    public List<Spawners> getSpawners(EntityClassification classification) {
        return this.spawners.getOrDefault(classification, ImmutableList.of());
    }

    @Nullable
    public SpawnCosts getSpawnCost(EntityType<?> entityType) {
        return this.spawnCosts.get(entityType);
    }

    public float getCreatureSpawnProbability() {
        return this.creatureSpawnProbability;
    }

    public boolean isValidSpawnBiomeForPlayer() {
        return this.validSpawnBiomeForPlayer;
    }

    public static class SpawnCosts {
        public static final Codec<SpawnCosts> CODEC = RecordCodecBuilder.create(builder -> builder.group(((MapCodec)Codec.DOUBLE.fieldOf("energy_budget")).forGetter(spawnCosts -> spawnCosts.maxSpawnCost), ((MapCodec)Codec.DOUBLE.fieldOf("charge")).forGetter(spawnCosts -> spawnCosts.entitySpawnCost)).apply((Applicative<SpawnCosts, ?>)builder, SpawnCosts::new));
        private final double maxSpawnCost;
        private final double entitySpawnCost;

        private SpawnCosts(double maxSpawnCost, double entitySpawnCost) {
            this.maxSpawnCost = maxSpawnCost;
            this.entitySpawnCost = entitySpawnCost;
        }

        public double getMaxSpawnCost() {
            return this.maxSpawnCost;
        }

        public double getEntitySpawnCost() {
            return this.entitySpawnCost;
        }
    }

    public static class Spawners
    extends WeightedRandom.Item {
        public static final Codec<Spawners> CODEC = RecordCodecBuilder.create(builder -> builder.group(((MapCodec)Registry.ENTITY_TYPE.fieldOf("type")).forGetter(spawner -> spawner.type), ((MapCodec)Codec.INT.fieldOf("weight")).forGetter(spawner -> spawner.itemWeight), ((MapCodec)Codec.INT.fieldOf("minCount")).forGetter(spawner -> spawner.minCount), ((MapCodec)Codec.INT.fieldOf("maxCount")).forGetter(spawner -> spawner.maxCount)).apply((Applicative<Spawners, ?>)builder, Spawners::new));
        public final EntityType<?> type;
        public final int minCount;
        public final int maxCount;

        public Spawners(EntityType<?> type, int weight, int minCount, int maxCount) {
            super(weight);
            this.type = type.getClassification() == EntityClassification.MISC ? EntityType.PIG : type;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }

        public String toString() {
            return String.valueOf(EntityType.getKey(this.type)) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.itemWeight;
        }
    }

    public static class Builder {
        private final Map<EntityClassification, List<Spawners>> spawners = Stream.of(EntityClassification.values()).collect(ImmutableMap.toImmutableMap(classification -> classification, classification -> Lists.newArrayList()));
        private final Map<EntityType<?>, SpawnCosts> spawnCosts = Maps.newLinkedHashMap();
        private float creatureSpawnProbability = 0.1f;
        private boolean validSpawnBiomeForPlayer;

        public Builder withSpawner(EntityClassification classification, Spawners spawner) {
            this.spawners.get(classification).add(spawner);
            return this;
        }

        public Builder withSpawnCost(EntityType<?> entityType, double spawnCostPerEntity, double maxSpawnCost) {
            this.spawnCosts.put(entityType, new SpawnCosts(maxSpawnCost, spawnCostPerEntity));
            return this;
        }

        public Builder withCreatureSpawnProbability(float probability) {
            this.creatureSpawnProbability = probability;
            return this;
        }

        public Builder isValidSpawnBiomeForPlayer() {
            this.validSpawnBiomeForPlayer = true;
            return this;
        }

        public MobSpawnInfo copy() {
            return new MobSpawnInfo(this.creatureSpawnProbability, (Map<EntityClassification, List<Spawners>>)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))), ImmutableMap.copyOf(this.spawnCosts), this.validSpawnBiomeForPlayer);
        }
    }
}
