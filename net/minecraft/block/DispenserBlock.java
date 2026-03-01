package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DispenserBlock
extends ContainerBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final Map<Item, IDispenseItemBehavior> DISPENSE_BEHAVIOR_REGISTRY = Util.make(new Object2ObjectOpenHashMap(), behaviour -> behaviour.defaultReturnValue(new DefaultDispenseItemBehavior()));

    public static void registerDispenseBehavior(IItemProvider itemIn, IDispenseItemBehavior behavior) {
        DISPENSE_BEHAVIOR_REGISTRY.put(itemIn.asItem(), behavior);
    }

    protected DispenserBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof DispenserTileEntity) {
            player.openContainer((DispenserTileEntity)tileentity);
            if (tileentity instanceof DropperTileEntity) {
                player.addStat(Stats.INSPECT_DROPPER);
            } else {
                player.addStat(Stats.INSPECT_DISPENSER);
            }
        }
        return ActionResultType.CONSUME;
    }

    protected void dispense(ServerWorld worldIn, BlockPos pos) {
        ProxyBlockSource proxyblocksource = new ProxyBlockSource(worldIn, pos);
        DispenserTileEntity dispensertileentity = (DispenserTileEntity)proxyblocksource.getBlockTileEntity();
        int i = dispensertileentity.getDispenseSlot();
        if (i < 0) {
            worldIn.playEvent(1001, pos, 0);
        } else {
            ItemStack itemstack = dispensertileentity.getStackInSlot(i);
            IDispenseItemBehavior idispenseitembehavior = this.getBehavior(itemstack);
            if (idispenseitembehavior != IDispenseItemBehavior.NOOP) {
                dispensertileentity.setInventorySlotContents(i, idispenseitembehavior.dispense(proxyblocksource, itemstack));
            }
        }
    }

    protected IDispenseItemBehavior getBehavior(ItemStack stack) {
        return DISPENSE_BEHAVIOR_REGISTRY.get(stack.getItem());
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        boolean flag1 = state.get(TRIGGERED);
        if (flag && !flag1) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 4);
            worldIn.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), 4);
        } else if (!flag && flag1) {
            worldIn.setBlockState(pos, (BlockState)state.with(TRIGGERED, false), 4);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        this.dispense(worldIn, pos);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DispenserTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return (BlockState)this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tileentity;
        if (stack.hasDisplayName() && (tileentity = worldIn.getTileEntity(pos)) instanceof DispenserTileEntity) {
            ((DispenserTileEntity)tileentity).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof DispenserTileEntity) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)((DispenserTileEntity)tileentity));
                worldIn.updateComparatorOutputLevel(pos, this);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    public static IPosition getDispensePosition(IBlockSource coords) {
        Direction direction = coords.getBlockState().get(FACING);
        double d0 = coords.getX() + 0.7 * (double)direction.getXOffset();
        double d1 = coords.getY() + 0.7 * (double)direction.getYOffset();
        double d2 = coords.getZ() + 0.7 * (double)direction.getZOffset();
        return new Position(d0, d1, d2);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        builder.add(FACING, TRIGGERED);
    }
}
