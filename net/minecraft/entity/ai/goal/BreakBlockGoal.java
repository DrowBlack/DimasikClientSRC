package net.minecraft.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;

public class BreakBlockGoal
extends MoveToBlockGoal {
    private final Block block;
    private final MobEntity entity;
    private int breakingTime;

    public BreakBlockGoal(Block blockIn, CreatureEntity creature, double speed, int yMax) {
        super(creature, speed, 24, yMax);
        this.block = blockIn;
        this.entity = creature;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            return false;
        }
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        }
        if (this.func_220729_m()) {
            this.runDelay = 20;
            return true;
        }
        this.runDelay = this.getRunDelay(this.creature);
        return false;
    }

    private boolean func_220729_m() {
        return this.destinationBlock != null && this.shouldMoveTo(this.creature.world, this.destinationBlock) ? true : this.searchForDestination();
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.entity.fallDistance = 1.0f;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.breakingTime = 0;
    }

    public void playBreakingSound(IWorld worldIn, BlockPos pos) {
    }

    public void playBrokenSound(World worldIn, BlockPos pos) {
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.entity.world;
        BlockPos blockpos = this.entity.getPosition();
        BlockPos blockpos1 = this.findTarget(blockpos, world);
        Random random = this.entity.getRNG();
        if (this.getIsAboveDestination() && blockpos1 != null) {
            if (this.breakingTime > 0) {
                Vector3d vector3d = this.entity.getMotion();
                this.entity.setMotion(vector3d.x, 0.3, vector3d.z);
                if (!world.isRemote) {
                    double d0 = 0.08;
                    ((ServerWorld)world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.7, (double)blockpos1.getZ() + 0.5, 3, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, 0.15f);
                }
            }
            if (this.breakingTime % 2 == 0) {
                Vector3d vector3d1 = this.entity.getMotion();
                this.entity.setMotion(vector3d1.x, -0.3, vector3d1.z);
                if (this.breakingTime % 6 == 0) {
                    this.playBreakingSound(world, this.destinationBlock);
                }
            }
            if (this.breakingTime > 60) {
                world.removeBlock(blockpos1, false);
                if (!world.isRemote) {
                    for (int i = 0; i < 20; ++i) {
                        double d3 = random.nextGaussian() * 0.02;
                        double d1 = random.nextGaussian() * 0.02;
                        double d2 = random.nextGaussian() * 0.02;
                        ((ServerWorld)world).spawnParticle(ParticleTypes.POOF, (double)blockpos1.getX() + 0.5, blockpos1.getY(), (double)blockpos1.getZ() + 0.5, 1, d3, d1, d2, 0.15f);
                    }
                    this.playBrokenSound(world, blockpos1);
                }
            }
            ++this.breakingTime;
        }
    }

    @Nullable
    private BlockPos findTarget(BlockPos pos, IBlockReader worldIn) {
        BlockPos[] ablockpos;
        if (worldIn.getBlockState(pos).isIn(this.block)) {
            return pos;
        }
        for (BlockPos blockpos : ablockpos = new BlockPos[]{pos.down(), pos.west(), pos.east(), pos.north(), pos.south(), pos.down().down()}) {
            if (!worldIn.getBlockState(blockpos).isIn(this.block)) continue;
            return blockpos;
        }
        return null;
    }

    @Override
    protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
        IChunk ichunk = worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (ichunk == null) {
            return false;
        }
        return ichunk.getBlockState(pos).isIn(this.block) && ichunk.getBlockState(pos.up()).isAir() && ichunk.getBlockState(pos.up(2)).isAir();
    }
}
