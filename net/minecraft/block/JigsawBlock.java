package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawBlock
extends Block
implements ITileEntityProvider {
    public static final EnumProperty<JigsawOrientation> ORIENTATION = BlockStateProperties.ORIENTATION;

    protected JigsawBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(ORIENTATION, JigsawOrientation.NORTH_UP));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.with(ORIENTATION, rot.getOrientation().func_235531_a_(state.get(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return (BlockState)state.with(ORIENTATION, mirrorIn.getOrientation().func_235531_a_(state.get(ORIENTATION)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        Direction direction1 = direction.getAxis() == Direction.Axis.Y ? context.getPlacementHorizontalFacing().getOpposite() : Direction.UP;
        return (BlockState)this.getDefaultState().with(ORIENTATION, JigsawOrientation.func_239641_a_(direction, direction1));
    }

    @Override
    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new JigsawTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof JigsawTileEntity && player.canUseCommandBlock()) {
            player.openJigsaw((JigsawTileEntity)tileentity);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.PASS;
    }

    public static boolean hasJigsawMatch(Template.BlockInfo info, Template.BlockInfo info2) {
        Direction direction = JigsawBlock.getConnectingDirection(info.state);
        Direction direction1 = JigsawBlock.getConnectingDirection(info2.state);
        Direction direction2 = JigsawBlock.getJigsawAlignmentDirection(info.state);
        Direction direction3 = JigsawBlock.getJigsawAlignmentDirection(info2.state);
        JigsawTileEntity.OrientationType jigsawtileentity$orientationtype = JigsawTileEntity.OrientationType.func_235673_a_(info.nbt.getString("joint")).orElseGet(() -> direction.getAxis().isHorizontal() ? JigsawTileEntity.OrientationType.ALIGNED : JigsawTileEntity.OrientationType.ROLLABLE);
        boolean flag = jigsawtileentity$orientationtype == JigsawTileEntity.OrientationType.ROLLABLE;
        return direction == direction1.getOpposite() && (flag || direction2 == direction3) && info.nbt.getString("target").equals(info2.nbt.getString("name"));
    }

    public static Direction getConnectingDirection(BlockState state) {
        return state.get(ORIENTATION).func_239642_b_();
    }

    public static Direction getJigsawAlignmentDirection(BlockState state) {
        return state.get(ORIENTATION).func_239644_c_();
    }
}
