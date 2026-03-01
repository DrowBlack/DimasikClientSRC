package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.world.server.ServerWorld;

public class GolemLastSeenSensor
extends Sensor<LivingEntity> {
    public GolemLastSeenSensor() {
        this(200);
    }

    public GolemLastSeenSensor(int interval) {
        super(interval);
    }

    @Override
    protected void update(ServerWorld worldIn, LivingEntity entityIn) {
        GolemLastSeenSensor.update(entityIn);
    }

    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.MOBS);
    }

    public static void update(LivingEntity livingEntity) {
        boolean flag;
        Optional<List<LivingEntity>> optional = livingEntity.getBrain().getMemory(MemoryModuleType.MOBS);
        if (optional.isPresent() && (flag = optional.get().stream().anyMatch(entity -> entity.getType().equals(EntityType.IRON_GOLEM)))) {
            GolemLastSeenSensor.reset(livingEntity);
        }
    }

    public static void reset(LivingEntity livingEntity) {
        livingEntity.getBrain().replaceMemory(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}
