package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.RangedInteger;
import net.minecraft.world.server.ServerWorld;

public class ChildFollowNearestAdultTask<E extends AgeableEntity>
extends Task<E> {
    private final RangedInteger distance;
    private final float speed;

    public ChildFollowNearestAdultTask(RangedInteger distance, float speed) {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.distance = distance;
        this.speed = speed;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, E owner) {
        if (!((AgeableEntity)owner).isChild()) {
            return false;
        }
        AgeableEntity ageableentity = this.getNearestVisibleAdult(owner);
        return ((Entity)owner).isEntityInRange(ageableentity, this.distance.getMax() + 1) && !((Entity)owner).isEntityInRange(ageableentity, this.distance.getMinInclusive());
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
        BrainUtil.setTargetEntity(entityIn, this.getNearestVisibleAdult(entityIn), this.speed, this.distance.getMinInclusive() - 1);
    }

    private AgeableEntity getNearestVisibleAdult(E ageable) {
        return ((LivingEntity)ageable).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
    }
}
