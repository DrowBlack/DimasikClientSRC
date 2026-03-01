package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock
extends HorizontalBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
    protected static final VoxelShape AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
    protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
    protected static final VoxelShape TRUE_AABB_COLLISION_BOX_ZAXIS = Block.makeCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
    protected static final VoxelShape AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
    protected static final VoxelShape AABB_RENDER_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.makeCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
    protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.makeCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
    protected static final VoxelShape AABB_RENDER_BOX_ZAXIS_INWALL = VoxelShapes.or(Block.makeCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.makeCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
    protected static final VoxelShape AABB_RENDER_BOX_XAXIS_INWALL = VoxelShapes.or(Block.makeCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.makeCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));

    public FenceGateBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(OPEN, false)).with(POWERED, false)).with(IN_WALL, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(IN_WALL).booleanValue()) {
            return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
        }
        return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis direction$axis = facing.getAxis();
        if (stateIn.get(HORIZONTAL_FACING).rotateY().getAxis() != direction$axis) {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        boolean flag = this.isWall(facingState) || this.isWall(worldIn.getBlockState(currentPos.offset(facing.getOpposite())));
        return (BlockState)stateIn.with(IN_WALL, flag);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(OPEN).booleanValue()) {
            return VoxelShapes.empty();
        }
        return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? TRUE_AABB_COLLISION_BOX_ZAXIS : AABB_COLLISION_BOX_XAXIS;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        if (state.get(IN_WALL).booleanValue()) {
            return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_RENDER_BOX_XAXIS_INWALL : AABB_RENDER_BOX_ZAXIS_INWALL;
        }
        return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_COLLISION_BOX_ZAXIS : AABB_RENDER_BOX_ZAXIS;
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

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        boolean flag = world.isBlockPowered(blockpos);
        Direction direction = context.getPlacementHorizontalFacing();
        Direction.Axis direction$axis = direction.getAxis();
        boolean flag1 = direction$axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockpos.west())) || this.isWall(world.getBlockState(blockpos.east()))) || direction$axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockpos.north())) || this.isWall(world.getBlockState(blockpos.south())));
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(HORIZONTAL_FACING, direction)).with(OPEN, flag)).with(POWERED, flag)).with(IN_WALL, flag1);
    }

    private boolean isWall(BlockState state) {
        return state.getBlock().isIn(BlockTags.WALLS);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (state.get(OPEN).booleanValue()) {
            state = (BlockState)state.with(OPEN, false);
            worldIn.setBlockState(pos, state, 10);
        } else {
            Direction direction = player.getHorizontalFacing();
            if (state.get(HORIZONTAL_FACING) == direction.getOpposite()) {
                state = (BlockState)state.with(HORIZONTAL_FACING, direction);
            }
            state = (BlockState)state.with(OPEN, true);
            worldIn.setBlockState(pos, state, 10);
        }
        worldIn.playEvent(player, state.get(OPEN) != false ? 1008 : 1014, pos, 0);
        return ActionResultType.func_233537_a_(worldIn.isRemote);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            boolean flag = worldIn.isBlockPowered(pos);
            if (state.get(POWERED) != flag) {
                worldIn.setBlockState(pos, (BlockState)((BlockState)state.with(POWERED, flag)).with(OPEN, flag), 2);
                if (state.get(OPEN) != flag) {
                    worldIn.playEvent(null, flag ? 1008 : 1014, pos, 0);
                }
            }
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, OPEN, POWERED, IN_WALL);
    }

    public static boolean isParallel(BlockState state, Direction direction) {
        return state.get(HORIZONTAL_FACING).getAxis() == direction.rotateY().getAxis();
    }
}
