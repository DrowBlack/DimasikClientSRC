package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BubbleColumnBlock
extends Block
implements IBucketPickupHandler {
    public static final BooleanProperty DRAG = BlockStateProperties.DRAG;

    public BubbleColumnBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(DRAG, true));
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        BlockState blockstate = worldIn.getBlockState(pos.up());
        if (blockstate.isAir()) {
            entityIn.onEnterBubbleColumnWithAirAbove(state.get(DRAG));
            if (!worldIn.isRemote) {
                ServerWorld serverworld = (ServerWorld)worldIn;
                for (int i = 0; i < 2; ++i) {
                    serverworld.spawnParticle(ParticleTypes.SPLASH, (double)pos.getX() + worldIn.rand.nextDouble(), pos.getY() + 1, (double)pos.getZ() + worldIn.rand.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                    serverworld.spawnParticle(ParticleTypes.BUBBLE, (double)pos.getX() + worldIn.rand.nextDouble(), pos.getY() + 1, (double)pos.getZ() + worldIn.rand.nextDouble(), 1, 0.0, 0.01, 0.0, 0.2);
                }
            }
        } else {
            entityIn.onEnterBubbleColumn(state.get(DRAG));
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        BubbleColumnBlock.placeBubbleColumn(worldIn, pos.up(), BubbleColumnBlock.getDrag(worldIn, pos.down()));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        BubbleColumnBlock.placeBubbleColumn(worldIn, pos.up(), BubbleColumnBlock.getDrag(worldIn, pos));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStillFluidState(false);
    }

    public static void placeBubbleColumn(IWorld world, BlockPos pos, boolean drag) {
        if (BubbleColumnBlock.canHoldBubbleColumn(world, pos)) {
            world.setBlockState(pos, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, drag), 2);
        }
    }

    public static boolean canHoldBubbleColumn(IWorld world, BlockPos pos) {
        FluidState fluidstate = world.getFluidState(pos);
        return world.getBlockState(pos).isIn(Blocks.WATER) && fluidstate.getLevel() >= 8 && fluidstate.isSource();
    }

    private static boolean getDrag(IBlockReader reader, BlockPos pos) {
        BlockState blockstate = reader.getBlockState(pos);
        if (blockstate.isIn(Blocks.BUBBLE_COLUMN)) {
            return blockstate.get(DRAG);
        }
        return !blockstate.isIn(Blocks.SOUL_SAND);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();
        if (stateIn.get(DRAG).booleanValue()) {
            worldIn.addOptionalParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5, d1 + 0.8, d2, 0.0, 0.0, 0.0);
            if (rand.nextInt(200) == 0) {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2f + rand.nextFloat() * 0.2f, 0.9f + rand.nextFloat() * 0.15f, false);
            }
        } else {
            worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5, d1, d2 + 0.5, 0.0, 0.04, 0.0);
            worldIn.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0, 0.04, 0.0);
            if (rand.nextInt(200) == 0) {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2f + rand.nextFloat() * 0.2f, 0.9f + rand.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            return Blocks.WATER.getDefaultState();
        }
        if (facing == Direction.DOWN) {
            worldIn.setBlockState(currentPos, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, BubbleColumnBlock.getDrag(worldIn, facingPos)), 2);
        } else if (facing == Direction.UP && !facingState.isIn(Blocks.BUBBLE_COLUMN) && BubbleColumnBlock.canHoldBubbleColumn(worldIn, facingPos)) {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 5);
        }
        worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos.down());
        return blockstate.isIn(Blocks.BUBBLE_COLUMN) || blockstate.isIn(Blocks.MAGMA_BLOCK) || blockstate.isIn(Blocks.SOUL_SAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DRAG);
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return Fluids.WATER;
    }
}
