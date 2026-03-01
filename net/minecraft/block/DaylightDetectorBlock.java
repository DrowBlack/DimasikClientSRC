package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock
extends ContainerBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public DaylightDetectorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWER, 0)).with(INVERTED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWER);
    }

    public static void updatePower(BlockState state, World world, BlockPos pos) {
        if (world.getDimensionType().hasSkyLight()) {
            int i = world.getLightFor(LightType.SKY, pos) - world.getSkylightSubtracted();
            float f = world.getCelestialAngleRadians(1.0f);
            boolean flag = state.get(INVERTED);
            if (flag) {
                i = 15 - i;
            } else if (i > 0) {
                float f1 = f < (float)Math.PI ? 0.0f : (float)Math.PI * 2;
                f += (f1 - f) * 0.2f;
                i = Math.round((float)i * MathHelper.cos(f));
            }
            i = MathHelper.clamp(i, 0, 15);
            if (state.get(POWER) != i) {
                world.setBlockState(pos, (BlockState)state.with(POWER, i), 3);
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.isAllowEdit()) {
            if (worldIn.isRemote) {
                return ActionResultType.SUCCESS;
            }
            BlockState blockstate = (BlockState)state.func_235896_a_(INVERTED);
            worldIn.setBlockState(pos, blockstate, 4);
            DaylightDetectorBlock.updatePower(blockstate, worldIn, pos);
            return ActionResultType.CONSUME;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DaylightDetectorTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER, INVERTED);
    }
}
