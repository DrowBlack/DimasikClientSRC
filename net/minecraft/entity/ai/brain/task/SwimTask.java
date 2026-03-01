package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.server.ServerWorld;

public class SwimTask
extends Task<MobEntity> {
    private final float field_220590_b;

    public SwimTask(float p_i231540_1_) {
        super(ImmutableMap.of());
        this.field_220590_b = p_i231540_1_;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, MobEntity owner) {
        return owner.isInWater() && owner.func_233571_b_(FluidTags.WATER) > owner.func_233579_cu_() || owner.isInLava();
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, MobEntity entityIn, long gameTimeIn) {
        return this.shouldExecute(worldIn, entityIn);
    }

    @Override
    protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime) {
        if (owner.getRNG().nextFloat() < this.field_220590_b) {
            owner.getJumpController().setJumping();
        }
    }
}
