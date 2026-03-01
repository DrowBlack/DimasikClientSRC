package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SleepAtHomeTask
extends Task<LivingEntity> {
    private long field_220552_a;

    public SleepAtHomeTask() {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleStatus.REGISTERED));
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
        long i;
        if (owner.isPassenger()) {
            return false;
        }
        Brain<?> brain = owner.getBrain();
        GlobalPos globalpos = brain.getMemory(MemoryModuleType.HOME).get();
        if (worldIn.getDimensionKey() != globalpos.getDimension()) {
            return false;
        }
        Optional<Long> optional = brain.getMemory(MemoryModuleType.LAST_WOKEN);
        if (optional.isPresent() && (i = worldIn.getGameTime() - optional.get()) > 0L && i < 100L) {
            return false;
        }
        BlockState blockstate = worldIn.getBlockState(globalpos.getPos());
        return globalpos.getPos().withinDistance(owner.getPositionVec(), 2.0) && blockstate.getBlock().isIn(BlockTags.BEDS) && blockstate.get(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        Optional<GlobalPos> optional = entityIn.getBrain().getMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockpos = optional.get().getPos();
        return entityIn.getBrain().hasActivity(Activity.REST) && entityIn.getPosY() > (double)blockpos.getY() + 0.4 && blockpos.withinDistance(entityIn.getPositionVec(), 1.14);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (gameTimeIn > this.field_220552_a) {
            InteractWithDoorTask.func_242294_a(worldIn, entityIn, null, null);
            entityIn.startSleeping(entityIn.getBrain().getMemory(MemoryModuleType.HOME).get().getPos());
        }
    }

    @Override
    protected boolean isTimedOut(long gameTime) {
        return false;
    }

    @Override
    protected void resetTask(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (entityIn.isSleeping()) {
            entityIn.wakeUp();
            this.field_220552_a = gameTimeIn + 40L;
        }
    }
}
