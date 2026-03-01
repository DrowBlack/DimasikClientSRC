package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class TradeTask
extends Task<VillagerEntity> {
    private final float speed;

    public TradeTask(float speedIn) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), Integer.MAX_VALUE);
        this.speed = speedIn;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
        PlayerEntity playerentity = owner.getCustomer();
        return owner.isAlive() && playerentity != null && !owner.isInWater() && !owner.velocityChanged && owner.getDistanceSq(playerentity) <= 16.0 && playerentity.openContainer != null;
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        return this.shouldExecute(worldIn, entityIn);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        this.walkAndLookCustomer(entityIn);
    }

    @Override
    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        Brain<VillagerEntity> brain = entityIn.getBrain();
        brain.removeMemory(MemoryModuleType.WALK_TARGET);
        brain.removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
        this.walkAndLookCustomer(owner);
    }

    @Override
    protected boolean isTimedOut(long gameTime) {
        return false;
    }

    private void walkAndLookCustomer(VillagerEntity owner) {
        Brain<VillagerEntity> brain = owner.getBrain();
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(owner.getCustomer(), false), this.speed, 2));
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(owner.getCustomer(), true));
    }
}
