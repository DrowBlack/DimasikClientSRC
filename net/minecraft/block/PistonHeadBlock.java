package net.minecraft.block;

import java.util.Arrays;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PistonHeadBlock
extends DirectionalBlock {
    public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
    public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
    protected static final VoxelShape PISTON_EXTENSION_EAST_AABB = Block.makeCuboidShape(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape PISTON_EXTENSION_WEST_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
    protected static final VoxelShape PISTON_EXTENSION_SOUTH_AABB = Block.makeCuboidShape(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape PISTON_EXTENSION_NORTH_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
    protected static final VoxelShape PISTON_EXTENSION_UP_AABB = Block.makeCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape PISTON_EXTENSION_DOWN_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    protected static final VoxelShape UP_ARM_AABB = Block.makeCuboidShape(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape DOWN_ARM_AABB = Block.makeCuboidShape(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
    protected static final VoxelShape SOUTH_ARM_AABB = Block.makeCuboidShape(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape NORTH_ARM_AABB = Block.makeCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
    protected static final VoxelShape EAST_ARM_AABB = Block.makeCuboidShape(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape WEST_ARM_AABB = Block.makeCuboidShape(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_UP_ARM_AABB = Block.makeCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
    protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.makeCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
    protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.makeCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
    protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.makeCuboidShape(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
    protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.makeCuboidShape(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
    protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.makeCuboidShape(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape[] EXTENDED_SHAPES = PistonHeadBlock.getShapesForExtension(true);
    private static final VoxelShape[] UNEXTENDED_SHAPES = PistonHeadBlock.getShapesForExtension(false);

    private static VoxelShape[] getShapesForExtension(boolean extended) {
        return (VoxelShape[])Arrays.stream(Direction.values()).map(direction -> PistonHeadBlock.getShapeForDirection(direction, extended)).toArray(VoxelShape[]::new);
    }

    private static VoxelShape getShapeForDirection(Direction direction, boolean shortArm) {
        switch (direction) {
            default: {
                return VoxelShapes.or(PISTON_EXTENSION_DOWN_AABB, shortArm ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);
            }
            case UP: {
                return VoxelShapes.or(PISTON_EXTENSION_UP_AABB, shortArm ? SHORT_UP_ARM_AABB : UP_ARM_AABB);
            }
            case NORTH: {
                return VoxelShapes.or(PISTON_EXTENSION_NORTH_AABB, shortArm ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);
            }
            case SOUTH: {
                return VoxelShapes.or(PISTON_EXTENSION_SOUTH_AABB, shortArm ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);
            }
            case WEST: {
                return VoxelShapes.or(PISTON_EXTENSION_WEST_AABB, shortArm ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);
            }
            case EAST: 
        }
        return VoxelShapes.or(PISTON_EXTENSION_EAST_AABB, shortArm ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
    }

    public PistonHeadBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT)).with(SHORT, false));
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return (state.get(SHORT) != false ? EXTENDED_SHAPES : UNEXTENDED_SHAPES)[state.get(FACING).ordinal()];
    }

    private boolean isExtended(BlockState baseState, BlockState extendedState) {
        Block block = baseState.get(TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return extendedState.isIn(block) && extendedState.get(PistonBlock.EXTENDED) != false && extendedState.get(FACING) == baseState.get(FACING);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos blockpos;
        if (!worldIn.isRemote && player.abilities.isCreativeMode && this.isExtended(state, worldIn.getBlockState(blockpos = pos.offset(state.get(FACING).getOpposite())))) {
            worldIn.destroyBlock(blockpos, false);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
            if (this.isExtended(state, worldIn.getBlockState(blockpos))) {
                worldIn.destroyBlock(blockpos, true);
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return this.isExtended(state, blockstate) || blockstate.isIn(Blocks.MOVING_PISTON) && blockstate.get(FACING) == state.get(FACING);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (state.isValidPosition(worldIn, pos)) {
            BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
            worldIn.getBlockState(blockpos).neighborChanged(worldIn, blockpos, blockIn, fromPos, false);
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(state.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, SHORT);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
