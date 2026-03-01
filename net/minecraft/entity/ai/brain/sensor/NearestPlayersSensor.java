package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;

public class NearestPlayersSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
    }

    @Override
    protected void update(ServerWorld worldIn, LivingEntity entityIn) {
        List list = worldIn.getPlayers().stream().filter(EntityPredicates.NOT_SPECTATING).filter(player -> entityIn.isEntityInRange((Entity)player, 16.0)).sorted(Comparator.comparingDouble(entityIn::getDistanceSq)).collect(Collectors.toList());
        Brain<?> brain = entityIn.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, list);
        List list1 = list.stream().filter(player -> NearestPlayersSensor.canAttackTarget(entityIn, player)).collect(Collectors.toList());
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list1.isEmpty() ? null : (PlayerEntity)list1.get(0));
        Optional<Entity> optional = list1.stream().filter(EntityPredicates.CAN_HOSTILE_AI_TARGET).findFirst();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, optional);
    }
}
