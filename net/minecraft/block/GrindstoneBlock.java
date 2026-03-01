package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GrindstoneBlock
extends HorizontalFaceBlock {
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_1 = Block.makeCuboidShape(2.0, 0.0, 6.0, 4.0, 7.0, 10.0);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_2 = Block.makeCuboidShape(12.0, 0.0, 6.0, 14.0, 7.0, 10.0);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0, 7.0, 5.0, 4.0, 13.0, 11.0);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0, 7.0, 5.0, 14.0, 13.0, 11.0);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_1, SHAPE_FLOOR_NORTH_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_2, SHAPE_FLOOR_NORTH_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH_COMBINED = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_FLOOR_NORTH_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_FLOOR_NORTH_SOUTH = VoxelShapes.or(SHAPE_FLOOR_NORTH_SOUTH_COMBINED, Block.makeCuboidShape(4.0, 4.0, 2.0, 12.0, 16.0, 14.0));
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_1 = Block.makeCuboidShape(6.0, 0.0, 2.0, 10.0, 7.0, 4.0);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_2 = Block.makeCuboidShape(6.0, 0.0, 12.0, 10.0, 7.0, 14.0);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_HOLDER_1 = Block.makeCuboidShape(5.0, 7.0, 2.0, 11.0, 13.0, 4.0);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_HOLDER_2 = Block.makeCuboidShape(5.0, 7.0, 12.0, 11.0, 13.0, 14.0);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_1, SHAPE_FLOOR_EAST_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_2, SHAPE_FLOOR_EAST_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST_COMBINED = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_1, SHAPE_FLOOR_EAST_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_FLOOR_EAST_WEST = VoxelShapes.or(SHAPE_FLOOR_EAST_WEST_COMBINED, Block.makeCuboidShape(2.0, 4.0, 4.0, 14.0, 16.0, 12.0));
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_1 = Block.makeCuboidShape(2.0, 6.0, 0.0, 4.0, 10.0, 7.0);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_2 = Block.makeCuboidShape(12.0, 6.0, 0.0, 14.0, 10.0, 7.0);
    public static final VoxelShape SHAPE_WALL_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0, 5.0, 7.0, 4.0, 11.0, 13.0);
    public static final VoxelShape SHAPE_WALL_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0, 5.0, 7.0, 14.0, 11.0, 13.0);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_1, SHAPE_WALL_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_2, SHAPE_WALL_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_SOUTH_COMBINED = VoxelShapes.or(SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_WALL_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_SOUTH = VoxelShapes.or(SHAPE_WALL_SOUTH_COMBINED, Block.makeCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 16.0));
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_1 = Block.makeCuboidShape(2.0, 6.0, 7.0, 4.0, 10.0, 16.0);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_2 = Block.makeCuboidShape(12.0, 6.0, 7.0, 14.0, 10.0, 16.0);
    public static final VoxelShape SHAPE_WALL_NORTH_HOLDER_1 = Block.makeCuboidShape(2.0, 5.0, 3.0, 4.0, 11.0, 9.0);
    public static final VoxelShape SHAPE_WALL_NORTH_HOLDER_2 = Block.makeCuboidShape(12.0, 5.0, 3.0, 14.0, 11.0, 9.0);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_1, SHAPE_WALL_NORTH_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_2, SHAPE_WALL_NORTH_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_NORTH_COMBINED = VoxelShapes.or(SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_1, SHAPE_WALL_NORTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_NORTH = VoxelShapes.or(SHAPE_WALL_NORTH_COMBINED, Block.makeCuboidShape(4.0, 2.0, 0.0, 12.0, 14.0, 12.0));
    public static final VoxelShape SHAPE_WALL_WEST_LEG_1 = Block.makeCuboidShape(7.0, 6.0, 2.0, 16.0, 10.0, 4.0);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_2 = Block.makeCuboidShape(7.0, 6.0, 12.0, 16.0, 10.0, 14.0);
    public static final VoxelShape SHAPE_WALL_WEST_HOLDER_1 = Block.makeCuboidShape(3.0, 5.0, 2.0, 9.0, 11.0, 4.0);
    public static final VoxelShape SHAPE_WALL_WEST_HOLDER_2 = Block.makeCuboidShape(3.0, 5.0, 12.0, 9.0, 11.0, 14.0);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_WEST_LEG_1, SHAPE_WALL_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_WEST_LEG_2, SHAPE_WALL_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_WEST_COMBINED = VoxelShapes.or(SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_1, SHAPE_WALL_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_WEST = VoxelShapes.or(SHAPE_WALL_WEST_COMBINED, Block.makeCuboidShape(0.0, 2.0, 4.0, 12.0, 14.0, 12.0));
    public static final VoxelShape SHAPE_WALL_EAST_LEG_1 = Block.makeCuboidShape(0.0, 6.0, 2.0, 9.0, 10.0, 4.0);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_2 = Block.makeCuboidShape(0.0, 6.0, 12.0, 9.0, 10.0, 14.0);
    public static final VoxelShape SHAPE_WALL_EAST_HOLDER_1 = Block.makeCuboidShape(7.0, 5.0, 2.0, 13.0, 11.0, 4.0);
    public static final VoxelShape SHAPE_WALL_EAST_HOLDER_2 = Block.makeCuboidShape(7.0, 5.0, 12.0, 13.0, 11.0, 14.0);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_WALL_EAST_LEG_1, SHAPE_WALL_EAST_HOLDER_1);
    public static final VoxelShape SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_WALL_EAST_LEG_2, SHAPE_WALL_EAST_HOLDER_2);
    public static final VoxelShape SHAPE_WALL_EAST_COMBINED = VoxelShapes.or(SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_1, SHAPE_WALL_EAST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_WALL_EAST = VoxelShapes.or(SHAPE_WALL_EAST_COMBINED, Block.makeCuboidShape(4.0, 2.0, 4.0, 16.0, 14.0, 12.0));
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_1 = Block.makeCuboidShape(2.0, 9.0, 6.0, 4.0, 16.0, 10.0);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_2 = Block.makeCuboidShape(12.0, 9.0, 6.0, 14.0, 16.0, 10.0);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_1 = Block.makeCuboidShape(2.0, 3.0, 5.0, 4.0, 9.0, 11.0);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_2 = Block.makeCuboidShape(12.0, 3.0, 5.0, 14.0, 9.0, 11.0);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_1, SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_1);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_2, SHAPE_CEILING_NORTH_OR_SOUTH_HOLDER_2);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH_COMBINED = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_1, SHAPE_CEILING_NORTH_OR_SOUTH_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH = VoxelShapes.or(SHAPE_CEILING_NORTH_OR_SOUTH_COMBINED, Block.makeCuboidShape(4.0, 0.0, 2.0, 12.0, 12.0, 14.0));
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_1 = Block.makeCuboidShape(6.0, 9.0, 2.0, 10.0, 16.0, 4.0);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_2 = Block.makeCuboidShape(6.0, 9.0, 12.0, 10.0, 16.0, 14.0);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_HOLDER_1 = Block.makeCuboidShape(5.0, 3.0, 2.0, 11.0, 9.0, 4.0);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_HOLDER_2 = Block.makeCuboidShape(5.0, 3.0, 12.0, 11.0, 9.0, 14.0);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_1 = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_1, SHAPE_CEILING_EAST_OR_WEST_HOLDER_1);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_2 = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_2, SHAPE_CEILING_EAST_OR_WEST_HOLDER_2);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST_COMBINED = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_1, SHAPE_CEILING_EAST_OR_WEST_LEG_HOLDER_COMBINED_2);
    public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST = VoxelShapes.or(SHAPE_CEILING_EAST_OR_WEST_COMBINED, Block.makeCuboidShape(2.0, 0.0, 4.0, 14.0, 12.0, 12.0));
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.grindstone_title");

    protected GrindstoneBlock(AbstractBlock.Properties propertiesIn) {
        super(propertiesIn);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(FACE, AttachFace.WALL));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private VoxelShape getShapeFromState(BlockState state) {
        Direction direction = state.get(HORIZONTAL_FACING);
        switch ((AttachFace)state.get(FACE)) {
            case FLOOR: {
                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return SHAPE_FLOOR_EAST_WEST;
                }
                return SHAPE_FLOOR_NORTH_SOUTH;
            }
            case WALL: {
                if (direction == Direction.NORTH) {
                    return SHAPE_WALL_NORTH;
                }
                if (direction == Direction.SOUTH) {
                    return SHAPE_WALL_SOUTH;
                }
                if (direction == Direction.EAST) {
                    return SHAPE_WALL_EAST;
                }
                return SHAPE_WALL_WEST;
            }
            case CEILING: {
                if (direction != Direction.NORTH && direction != Direction.SOUTH) {
                    return SHAPE_CEILING_EAST_OR_WEST;
                }
                return SHAPE_CEILING_NORTH_OR_SOUTH;
            }
        }
        return SHAPE_FLOOR_EAST_WEST;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.getShapeFromState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.getShapeFromState(state);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }
        player.openContainer(state.getContainer(worldIn, pos));
        player.addStat(Stats.INTERACT_WITH_GRINDSTONE);
        return ActionResultType.CONSUME;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, inventory, player) -> new GrindstoneContainer(id, inventory, IWorldPosCallable.of(worldIn, pos)), CONTAINER_NAME);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, FACE);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
