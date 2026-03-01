package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MovingPistonBlock
extends ContainerBlock {
    public static final DirectionProperty FACING = PistonHeadBlock.FACING;
    public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

    public MovingPistonBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT));
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return null;
    }

    public static TileEntity createTilePiston(BlockState state, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
        return new PistonTileEntity(state, direction, extending, shouldHeadBeRendered);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileentity;
        if (!state.isIn(newState.getBlock()) && (tileentity = worldIn.getTileEntity(pos)) instanceof PistonTileEntity) {
            ((PistonTileEntity)tileentity).clearPistonTileEntity();
        }
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof PistonBlock && blockstate.get(PistonBlock.EXTENDED).booleanValue()) {
            worldIn.removeBlock(blockpos, false);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            worldIn.removeBlock(pos, false);
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        PistonTileEntity pistontileentity = this.getTileEntity(builder.getWorld(), new BlockPos(builder.assertPresent(LootParameters.field_237457_g_)));
        return pistontileentity == null ? Collections.emptyList() : pistontileentity.getPistonState().getDrops(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        PistonTileEntity pistontileentity = this.getTileEntity(worldIn, pos);
        return pistontileentity != null ? pistontileentity.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
    }

    @Nullable
    private PistonTileEntity getTileEntity(IBlockReader blockReader, BlockPos pos) {
        TileEntity tileentity = blockReader.getTileEntity(pos);
        return tileentity instanceof PistonTileEntity ? (PistonTileEntity)tileentity : null;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
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
        builder.add(FACING, TYPE);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
