package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class LookAtEntityTask
extends Task<LivingEntity> {
    private final Predicate<LivingEntity> targetPredicate;
    private final float field_220520_b;

    public LookAtEntityTask(EntityClassification classification, float distance) {
        this((LivingEntity p_220514_1_) -> classification.equals(p_220514_1_.getType().getClassification()), distance);
    }

    public LookAtEntityTask(EntityType<?> type, float distance) {
        this((LivingEntity p_220518_1_) -> type.equals(p_220518_1_.getType()), distance);
    }

    public LookAtEntityTask(float distance) {
        this((LivingEntity p_233953_0_) -> true, distance);
    }

    public LookAtEntityTask(Predicate<LivingEntity> targetPredicate, float distance) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.targetPredicate = targetPredicate;
        this.field_220520_b = distance * distance;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
        return owner.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).get().stream().anyMatch(this.targetPredicate);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        Brain<?> brain = entityIn.getBrain();
        brain.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(p_220515_3_ -> p_220515_3_.stream().filter(this.targetPredicate).filter(target -> target.getDistanceSq(entityIn) <= (double)this.field_220520_b).findFirst().ifPresent(target -> brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper((Entity)target, true))));
    }
}
