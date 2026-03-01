package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class CreateBabyVillagerTask
extends Task<VillagerEntity> {
    private long duration;

    public CreateBabyVillagerTask() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT), 350, 350);
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
        return this.canBreed(owner);
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        return gameTimeIn <= this.duration && this.canBreed(entityIn);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        AgeableEntity ageableentity = entityIn.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        BrainUtil.lookApproachEachOther(entityIn, ageableentity, 0.5f);
        worldIn.setEntityState(ageableentity, (byte)18);
        worldIn.setEntityState(entityIn, (byte)18);
        int i = 275 + entityIn.getRNG().nextInt(50);
        this.duration = gameTimeIn + (long)i;
    }

    @Override
    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
        VillagerEntity villagerentity = (VillagerEntity)owner.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        if (!(owner.getDistanceSq(villagerentity) > 5.0)) {
            BrainUtil.lookApproachEachOther(owner, villagerentity, 0.5f);
            if (gameTime >= this.duration) {
                owner.func_223346_ep();
                villagerentity.func_223346_ep();
                this.breed(worldIn, owner, villagerentity);
            } else if (owner.getRNG().nextInt(35) == 0) {
                worldIn.setEntityState(villagerentity, (byte)12);
                worldIn.setEntityState(owner, (byte)12);
            }
        }
    }

    private void breed(ServerWorld world, VillagerEntity parent, VillagerEntity partner) {
        Optional<BlockPos> optional = this.findHomePosition(world, parent);
        if (!optional.isPresent()) {
            world.setEntityState(partner, (byte)13);
            world.setEntityState(parent, (byte)13);
        } else {
            Optional<VillagerEntity> optional1 = this.createChild(world, parent, partner);
            if (optional1.isPresent()) {
                this.setHomePosition(world, optional1.get(), optional.get());
            } else {
                world.getPointOfInterestManager().release(optional.get());
                DebugPacketSender.func_218801_c(world, optional.get());
            }
        }
    }

    @Override
    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        entityIn.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
    }

    private boolean canBreed(VillagerEntity villager) {
        Brain<VillagerEntity> brain = villager.getBrain();
        Optional<AgeableEntity> optional = brain.getMemory(MemoryModuleType.BREED_TARGET).filter(breedTarget -> breedTarget.getType() == EntityType.VILLAGER);
        if (!optional.isPresent()) {
            return false;
        }
        return BrainUtil.isCorrectVisibleType(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && villager.canBreed() && optional.get().canBreed();
    }

    private Optional<BlockPos> findHomePosition(ServerWorld world, VillagerEntity villager) {
        return world.getPointOfInterestManager().take(PointOfInterestType.HOME.getPredicate(), pos -> this.canReachHomePosition(villager, (BlockPos)pos), villager.getPosition(), 48);
    }

    private boolean canReachHomePosition(VillagerEntity villager, BlockPos pos) {
        Path path = villager.getNavigator().getPathToPos(pos, PointOfInterestType.HOME.getValidRange());
        return path != null && path.reachesTarget();
    }

    private Optional<VillagerEntity> createChild(ServerWorld world, VillagerEntity parent, VillagerEntity partner) {
        VillagerEntity villagerentity = parent.func_241840_a(world, partner);
        if (villagerentity == null) {
            return Optional.empty();
        }
        parent.setGrowingAge(6000);
        partner.setGrowingAge(6000);
        villagerentity.setGrowingAge(-24000);
        villagerentity.setLocationAndAngles(parent.getPosX(), parent.getPosY(), parent.getPosZ(), 0.0f, 0.0f);
        world.func_242417_l(villagerentity);
        world.setEntityState(villagerentity, (byte)12);
        return Optional.of(villagerentity);
    }

    private void setHomePosition(ServerWorld world, VillagerEntity villager, BlockPos pos) {
        GlobalPos globalpos = GlobalPos.getPosition(world.getDimensionKey(), pos);
        villager.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
    }
}
