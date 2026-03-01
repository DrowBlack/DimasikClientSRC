package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class StopRidingEntityTask<E extends LivingEntity, T extends Entity>
extends Task<E> {
    private final int field_233890_b_;
    private final BiPredicate<E, Entity> field_233891_c_;

    public StopRidingEntityTask(int p_i231515_1_, BiPredicate<E, Entity> p_i231515_2_) {
        super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleStatus.REGISTERED));
        this.field_233890_b_ = p_i231515_1_;
        this.field_233891_c_ = p_i231515_2_;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, E owner) {
        Entity entity = ((Entity)owner).getRidingEntity();
        Entity entity1 = ((LivingEntity)owner).getBrain().getMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
        if (entity == null && entity1 == null) {
            return false;
        }
        Entity entity2 = entity == null ? entity1 : entity;
        return !this.func_233892_a_(owner, entity2) || this.field_233891_c_.test(owner, entity2);
    }

    private boolean func_233892_a_(E p_233892_1_, Entity p_233892_2_) {
        return p_233892_2_.isAlive() && p_233892_2_.isEntityInRange((Entity)p_233892_1_, this.field_233890_b_) && p_233892_2_.world == ((LivingEntity)p_233892_1_).world;
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
        ((LivingEntity)entityIn).stopRiding();
        ((LivingEntity)entityIn).getBrain().removeMemory(MemoryModuleType.RIDE_TARGET);
    }
}
