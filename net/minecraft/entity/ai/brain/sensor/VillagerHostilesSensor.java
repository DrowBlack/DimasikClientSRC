package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class VillagerHostilesSensor
extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> enemyPresenceRange = ImmutableMap.builder().put(EntityType.DROWNED, Float.valueOf(8.0f)).put(EntityType.EVOKER, Float.valueOf(12.0f)).put(EntityType.HUSK, Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, Float.valueOf(12.0f)).put(EntityType.PILLAGER, Float.valueOf(15.0f)).put(EntityType.RAVAGER, Float.valueOf(12.0f)).put(EntityType.VEX, Float.valueOf(8.0f)).put(EntityType.VINDICATOR, Float.valueOf(10.0f)).put(EntityType.ZOGLIN, Float.valueOf(10.0f)).put(EntityType.ZOMBIE, Float.valueOf(8.0f)).put(EntityType.ZOMBIE_VILLAGER, Float.valueOf(8.0f)).build();

    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void update(ServerWorld worldIn, LivingEntity entityIn) {
        entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.findNearestHostile(entityIn));
    }

    private Optional<LivingEntity> findNearestHostile(LivingEntity livingEntity) {
        return this.getVisibleEntities(livingEntity).flatMap(entities -> entities.stream().filter(this::hasPresence).filter(enemy -> this.canNoticePresence(livingEntity, (LivingEntity)enemy)).min((enemy1, enemy2) -> this.compareHostileDistances(livingEntity, (LivingEntity)enemy1, (LivingEntity)enemy2)));
    }

    private Optional<List<LivingEntity>> getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
    }

    private int compareHostileDistances(LivingEntity livingEntity, LivingEntity target1, LivingEntity target2) {
        return MathHelper.floor(target1.getDistanceSq(livingEntity) - target2.getDistanceSq(livingEntity));
    }

    private boolean canNoticePresence(LivingEntity livingEntity, LivingEntity target) {
        float f = enemyPresenceRange.get(target.getType()).floatValue();
        return target.getDistanceSq(livingEntity) <= (double)(f * f);
    }

    private boolean hasPresence(LivingEntity livingEntity) {
        return enemyPresenceRange.containsKey(livingEntity.getType());
    }
}
