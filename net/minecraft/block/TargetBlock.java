package net.minecraft.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TargetBlock
extends Block {
    private static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

    public TargetBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWER, 0));
    }

    @Override
    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
        int i = TargetBlock.getPowerFromHitVec(worldIn, state, hit, projectile);
        Entity entity = projectile.func_234616_v_();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            serverplayerentity.addStat(Stats.field_232863_aD_);
            CriteriaTriggers.TARGET_HIT.test(serverplayerentity, projectile, hit.getHitVec(), i);
        }
    }

    private static int getPowerFromHitVec(IWorld world, BlockState state, BlockRayTraceResult result, Entity entity) {
        int j;
        int i = TargetBlock.getPowerFromHitVec(result, result.getHitVec());
        int n = j = entity instanceof AbstractArrowEntity ? 20 : 8;
        if (!world.getPendingBlockTicks().isTickScheduled(result.getPos(), state.getBlock())) {
            TargetBlock.powerTarget(world, state, i, result.getPos(), j);
        }
        return i;
    }

    private static int getPowerFromHitVec(BlockRayTraceResult result, Vector3d vector) {
        Direction direction = result.getFace();
        double d0 = Math.abs(MathHelper.frac(vector.x) - 0.5);
        double d1 = Math.abs(MathHelper.frac(vector.y) - 0.5);
        double d2 = Math.abs(MathHelper.frac(vector.z) - 0.5);
        Direction.Axis direction$axis = direction.getAxis();
        double d3 = direction$axis == Direction.Axis.Y ? Math.max(d0, d2) : (direction$axis == Direction.Axis.Z ? Math.max(d0, d1) : Math.max(d1, d2));
        return Math.max(1, MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - d3) / 0.5, 0.0, 1.0)));
    }

    private static void powerTarget(IWorld world, BlockState state, int power, BlockPos pos, int waitTime) {
        world.setBlockState(pos, (BlockState)state.with(POWER, power), 3);
        world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), waitTime);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (state.get(POWER) != 0) {
            worldIn.setBlockState(pos, (BlockState)state.with(POWER, 0), 3);
        }
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWER);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!(worldIn.isRemote() || state.isIn(oldState.getBlock()) || state.get(POWER) <= 0 || worldIn.getPendingBlockTicks().isTickScheduled(pos, this))) {
            worldIn.setBlockState(pos, (BlockState)state.with(POWER, 0), 18);
        }
    }
}
