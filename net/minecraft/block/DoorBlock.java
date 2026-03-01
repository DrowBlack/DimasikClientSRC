package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class DoorBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

    protected DoorBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HINGE, DoorHingeSide.LEFT)).with(POWERED, false)).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(FACING);
        boolean flag = state.get(OPEN) == false;
        boolean flag1 = state.get(HINGE) == DoorHingeSide.RIGHT;
        switch (direction) {
            default: {
                return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
            }
            case SOUTH: {
                return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
            }
            case WEST: {
                return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
            }
            case NORTH: 
        }
        return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
            return facingState.isIn(this) && facingState.get(HALF) != doubleblockhalf ? (BlockState)((BlockState)((BlockState)((BlockState)stateIn.with(FACING, facingState.get(FACING))).with(OPEN, facingState.get(OPEN))).with(HINGE, facingState.get(HINGE))).with(POWERED, facingState.get(POWERED)) : Blocks.AIR.getDefaultState();
        }
        return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote && player.isCreative()) {
            DoublePlantBlock.removeBottomHalf(worldIn, pos, state, player);
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch (type) {
            case LAND: {
                return state.get(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return state.get(OPEN);
            }
        }
        return false;
    }

    private int getCloseSound() {
        return this.material == Material.IRON ? 1011 : 1012;
    }

    private int getOpenSound() {
        return this.material == Material.IRON ? 1005 : 1006;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos blockpos = context.getPos();
        if (blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)) {
            World world = context.getWorld();
            boolean flag = world.isBlockPowered(blockpos) || world.isBlockPowered(blockpos.up());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing())).with(HINGE, this.getHingeSide(context))).with(POWERED, flag)).with(OPEN, flag)).with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), (BlockState)state.with(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHingeSide getHingeSide(BlockItemUseContext context) {
        boolean flag1;
        World iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        Direction direction = context.getPlacementHorizontalFacing();
        BlockPos blockpos1 = blockpos.up();
        Direction direction1 = direction.rotateYCCW();
        BlockPos blockpos2 = blockpos.offset(direction1);
        BlockState blockstate = iblockreader.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.offset(direction1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
        Direction direction2 = direction.rotateY();
        BlockPos blockpos4 = blockpos.offset(direction2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.offset(direction2);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
        int i = (blockstate.hasOpaqueCollisionShape(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.hasOpaqueCollisionShape(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.hasOpaqueCollisionShape(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.hasOpaqueCollisionShape(iblockreader, blockpos5) ? 1 : 0);
        boolean flag = blockstate.isIn(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
        boolean bl = flag1 = blockstate2.isIn(this) && blockstate2.get(HALF) == DoubleBlockHalf.LOWER;
        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = direction.getXOffset();
                int k = direction.getZOffset();
                Vector3d vector3d = context.getHitVec();
                double d0 = vector3d.x - (double)blockpos.getX();
                double d1 = vector3d.z - (double)blockpos.getZ();
                return !(j < 0 && d1 < 0.5 || j > 0 && d1 > 0.5 || k < 0 && d0 > 0.5 || k > 0 && d0 < 0.5) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            }
            return DoorHingeSide.LEFT;
        }
        return DoorHingeSide.RIGHT;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (this.material == Material.IRON) {
            return ActionResultType.PASS;
        }
        state = (BlockState)state.func_235896_a_(OPEN);
        worldIn.setBlockState(pos, state, 10);
        worldIn.playEvent(player, state.get(OPEN) != false ? this.getOpenSound() : this.getCloseSound(), pos, 0);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    public boolean isOpen(BlockState state) {
        return state.get(OPEN);
    }

    public void openDoor(World worldIn, BlockState state, BlockPos pos, boolean open) {
        if (state.isIn(this) && state.get(OPEN) != open) {
            worldIn.setBlockState(pos, (BlockState)state.with(OPEN, open), 10);
            this.playSound(worldIn, pos, open);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag;
        boolean bl = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)) ? true : (flag = false);
        if (blockIn != this && flag != state.get(POWERED)) {
            if (flag != state.get(OPEN)) {
                this.playSound(worldIn, pos, flag);
            }
            worldIn.setBlockState(pos, (BlockState)((BlockState)state.with(POWERED, flag)).with(OPEN, flag), 2);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return state.get(HALF) == DoubleBlockHalf.LOWER ? blockstate.isSolidSide(worldIn, blockpos, Direction.UP) : blockstate.isIn(this);
    }

    private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
        worldIn.playEvent(null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return mirrorIn == Mirror.NONE ? state : (BlockState)state.rotate(mirrorIn.toRotation(state.get(FACING))).func_235896_a_(HINGE);
    }

    @Override
    public long getPositionRandom(BlockState state, BlockPos pos) {
        return MathHelper.getCoordinateRandom(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static boolean isWooden(World world, BlockPos pos) {
        return DoorBlock.isWooden(world.getBlockState(pos));
    }

    public static boolean isWooden(BlockState state) {
        return state.getBlock() instanceof DoorBlock && (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.NETHER_WOOD);
    }
}
