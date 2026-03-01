package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.SwordItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooBlock
extends Block
implements IGrowable {
    protected static final VoxelShape SHAPE_NORMAL = Block.makeCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape SHAPE_LARGE_LEAVES = Block.makeCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape SHAPE_COLLISION = Block.makeCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntegerProperty PROPERTY_AGE = BlockStateProperties.AGE_0_1;
    public static final EnumProperty<BambooLeaves> PROPERTY_BAMBOO_LEAVES = BlockStateProperties.BAMBOO_LEAVES;
    public static final IntegerProperty PROPERTY_STAGE = BlockStateProperties.STAGE_0_1;

    public BambooBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PROPERTY_AGE, 0)).with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE)).with(PROPERTY_STAGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_AGE, PROPERTY_BAMBOO_LEAVES, PROPERTY_STAGE);
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = state.get(PROPERTY_BAMBOO_LEAVES) == BambooLeaves.LARGE ? SHAPE_LARGE_LEAVES : SHAPE_NORMAL;
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return voxelshape.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return SHAPE_COLLISION.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        if (!fluidstate.isEmpty()) {
            return null;
        }
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().down());
        if (blockstate.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockstate.isIn(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.getDefaultState().with(PROPERTY_AGE, 0);
            }
            if (blockstate.isIn(Blocks.BAMBOO)) {
                int i = blockstate.get(PROPERTY_AGE) > 0 ? 1 : 0;
                return (BlockState)this.getDefaultState().with(PROPERTY_AGE, i);
            }
            BlockState blockstate1 = context.getWorld().getBlockState(context.getPos().up());
            return !blockstate1.isIn(Blocks.BAMBOO) && !blockstate1.isIn(Blocks.BAMBOO_SAPLING) ? Blocks.BAMBOO_SAPLING.getDefaultState() : (BlockState)this.getDefaultState().with(PROPERTY_AGE, blockstate1.get(PROPERTY_AGE));
        }
        return null;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!state.isValidPosition(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(PROPERTY_STAGE) == 0;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        int i;
        if (state.get(PROPERTY_STAGE) == 0 && random.nextInt(3) == 0 && worldIn.isAirBlock(pos.up()) && worldIn.getLightSubtracted(pos.up(), 0) >= 9 && (i = this.getNumBambooBlocksBelow(worldIn, pos) + 1) < 16) {
            this.grow(state, worldIn, pos, random, i);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }
        if (facing == Direction.UP && facingState.isIn(Blocks.BAMBOO) && facingState.get(PROPERTY_AGE) > stateIn.get(PROPERTY_AGE)) {
            worldIn.setBlockState(currentPos, (BlockState)stateIn.func_235896_a_(PROPERTY_AGE), 2);
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        int j;
        int i = this.getNumBambooBlocksAbove(worldIn, pos);
        return i + (j = this.getNumBambooBlocksBelow(worldIn, pos)) + 1 < 16 && worldIn.getBlockState(pos.up(i)).get(PROPERTY_STAGE) != 1;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int i = this.getNumBambooBlocksAbove(worldIn, pos);
        int j = this.getNumBambooBlocksBelow(worldIn, pos);
        int k = i + j + 1;
        int l = 1 + rand.nextInt(2);
        for (int i1 = 0; i1 < l; ++i1) {
            BlockPos blockpos = pos.up(i);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (k >= 16 || blockstate.get(PROPERTY_STAGE) == 1 || !worldIn.isAirBlock(blockpos.up())) {
                return;
            }
            this.grow(blockstate, worldIn, blockpos, rand, k);
            ++i;
            ++k;
        }
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
        return player.getHeldItemMainhand().getItem() instanceof SwordItem ? 1.0f : super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    protected void grow(BlockState blockStateIn, World worldIn, BlockPos posIn, Random rand, int maxTotalSize) {
        BlockState blockstate = worldIn.getBlockState(posIn.down());
        BlockPos blockpos = posIn.down(2);
        BlockState blockstate1 = worldIn.getBlockState(blockpos);
        BambooLeaves bambooleaves = BambooLeaves.NONE;
        if (maxTotalSize >= 1) {
            if (blockstate.isIn(Blocks.BAMBOO) && blockstate.get(PROPERTY_BAMBOO_LEAVES) != BambooLeaves.NONE) {
                if (blockstate.isIn(Blocks.BAMBOO) && blockstate.get(PROPERTY_BAMBOO_LEAVES) != BambooLeaves.NONE) {
                    bambooleaves = BambooLeaves.LARGE;
                    if (blockstate1.isIn(Blocks.BAMBOO)) {
                        worldIn.setBlockState(posIn.down(), (BlockState)blockstate.with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL), 3);
                        worldIn.setBlockState(blockpos, (BlockState)blockstate1.with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE), 3);
                    }
                }
            } else {
                bambooleaves = BambooLeaves.SMALL;
            }
        }
        int i = blockStateIn.get(PROPERTY_AGE) != 1 && !blockstate1.isIn(Blocks.BAMBOO) ? 0 : 1;
        int j = !(maxTotalSize >= 11 && rand.nextFloat() < 0.25f || maxTotalSize == 15) ? 0 : 1;
        worldIn.setBlockState(posIn.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(PROPERTY_AGE, i)).with(PROPERTY_BAMBOO_LEAVES, bambooleaves)).with(PROPERTY_STAGE, j), 3);
    }

    protected int getNumBambooBlocksAbove(IBlockReader worldIn, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && worldIn.getBlockState(pos.up(i + 1)).isIn(Blocks.BAMBOO); ++i) {
        }
        return i;
    }

    protected int getNumBambooBlocksBelow(IBlockReader worldIn, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && worldIn.getBlockState(pos.down(i + 1)).isIn(Blocks.BAMBOO); ++i) {
        }
        return i;
    }
}
