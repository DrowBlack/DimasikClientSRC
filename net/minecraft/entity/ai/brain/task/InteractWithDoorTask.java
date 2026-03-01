package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class InteractWithDoorTask
extends Task<LivingEntity> {
    @Nullable
    private PathPoint field_242292_b;
    private int field_242293_c;

    public InteractWithDoorTask() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.OPENED_DOORS, MemoryModuleStatus.REGISTERED));
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
        Path path = owner.getBrain().getMemory(MemoryModuleType.PATH).get();
        if (!path.func_242945_b() && !path.isFinished()) {
            if (!Objects.equals(this.field_242292_b, path.func_237225_h_())) {
                this.field_242293_c = 20;
                return true;
            }
            if (this.field_242293_c > 0) {
                --this.field_242293_c;
            }
            return this.field_242293_c == 0;
        }
        return false;
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        DoorBlock doorblock1;
        BlockPos blockpos1;
        BlockState blockstate1;
        Path path = entityIn.getBrain().getMemory(MemoryModuleType.PATH).get();
        this.field_242292_b = path.func_237225_h_();
        PathPoint pathpoint = path.func_242950_i();
        PathPoint pathpoint1 = path.func_237225_h_();
        BlockPos blockpos = pathpoint.func_224759_a();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.isIn(BlockTags.WOODEN_DOORS)) {
            DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
            if (!doorblock.isOpen(blockstate)) {
                doorblock.openDoor(worldIn, blockstate, blockpos, true);
            }
            this.func_242301_c(worldIn, entityIn, blockpos);
        }
        if ((blockstate1 = worldIn.getBlockState(blockpos1 = pathpoint1.func_224759_a())).isIn(BlockTags.WOODEN_DOORS) && !(doorblock1 = (DoorBlock)blockstate1.getBlock()).isOpen(blockstate1)) {
            doorblock1.openDoor(worldIn, blockstate1, blockpos1, true);
            this.func_242301_c(worldIn, entityIn, blockpos1);
        }
        InteractWithDoorTask.func_242294_a(worldIn, entityIn, pathpoint, pathpoint1);
    }

    public static void func_242294_a(ServerWorld p_242294_0_, LivingEntity p_242294_1_, @Nullable PathPoint p_242294_2_, @Nullable PathPoint p_242294_3_) {
        Brain<Set<GlobalPos>> brain = p_242294_1_.getBrain();
        if (brain.hasMemory(MemoryModuleType.OPENED_DOORS)) {
            Iterator<GlobalPos> iterator = brain.getMemory(MemoryModuleType.OPENED_DOORS).get().iterator();
            while (iterator.hasNext()) {
                GlobalPos globalpos = iterator.next();
                BlockPos blockpos = globalpos.getPos();
                if (p_242294_2_ != null && p_242294_2_.func_224759_a().equals(blockpos) || p_242294_3_ != null && p_242294_3_.func_224759_a().equals(blockpos)) continue;
                if (InteractWithDoorTask.func_242296_a(p_242294_0_, p_242294_1_, globalpos)) {
                    iterator.remove();
                    continue;
                }
                BlockState blockstate = p_242294_0_.getBlockState(blockpos);
                if (!blockstate.isIn(BlockTags.WOODEN_DOORS)) {
                    iterator.remove();
                    continue;
                }
                DoorBlock doorblock = (DoorBlock)blockstate.getBlock();
                if (!doorblock.isOpen(blockstate)) {
                    iterator.remove();
                    continue;
                }
                if (InteractWithDoorTask.func_242295_a(p_242294_0_, p_242294_1_, blockpos)) {
                    iterator.remove();
                    continue;
                }
                doorblock.openDoor(p_242294_0_, blockstate, blockpos, false);
                iterator.remove();
            }
        }
    }

    private static boolean func_242295_a(ServerWorld p_242295_0_, LivingEntity p_242295_1_, BlockPos p_242295_2_) {
        Brain<List<LivingEntity>> brain = p_242295_1_.getBrain();
        return !brain.hasMemory(MemoryModuleType.MOBS) ? false : brain.getMemory(MemoryModuleType.MOBS).get().stream().filter(p_242298_1_ -> p_242298_1_.getType() == p_242295_1_.getType()).filter(p_242299_1_ -> p_242295_2_.withinDistance(p_242299_1_.getPositionVec(), 2.0)).anyMatch(p_242297_2_ -> InteractWithDoorTask.func_242300_b(p_242295_0_, p_242297_2_, p_242295_2_));
    }

    private static boolean func_242300_b(ServerWorld p_242300_0_, LivingEntity p_242300_1_, BlockPos p_242300_2_) {
        if (!p_242300_1_.getBrain().hasMemory(MemoryModuleType.PATH)) {
            return false;
        }
        Path path = p_242300_1_.getBrain().getMemory(MemoryModuleType.PATH).get();
        if (path.isFinished()) {
            return false;
        }
        PathPoint pathpoint = path.func_242950_i();
        if (pathpoint == null) {
            return false;
        }
        PathPoint pathpoint1 = path.func_237225_h_();
        return p_242300_2_.equals(pathpoint.func_224759_a()) || p_242300_2_.equals(pathpoint1.func_224759_a());
    }

    private static boolean func_242296_a(ServerWorld p_242296_0_, LivingEntity p_242296_1_, GlobalPos p_242296_2_) {
        return p_242296_2_.getDimension() != p_242296_0_.getDimensionKey() || !p_242296_2_.getPos().withinDistance(p_242296_1_.getPositionVec(), 2.0);
    }

    private void func_242301_c(ServerWorld p_242301_1_, LivingEntity p_242301_2_, BlockPos p_242301_3_) {
        Brain<?> brain = p_242301_2_.getBrain();
        GlobalPos globalpos = GlobalPos.getPosition(p_242301_1_.getDimensionKey(), p_242301_3_);
        if (brain.getMemory(MemoryModuleType.OPENED_DOORS).isPresent()) {
            brain.getMemory(MemoryModuleType.OPENED_DOORS).get().add(globalpos);
        } else {
            brain.setMemory(MemoryModuleType.OPENED_DOORS, Sets.newHashSet(globalpos));
        }
    }
}
