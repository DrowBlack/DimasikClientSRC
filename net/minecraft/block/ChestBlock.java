package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock
extends AbstractChestBlock<ChestTileEntity>
implements IWaterLoggable {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_SINGLE = Block.makeCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>> INVENTORY_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>>(){

        @Override
        public Optional<IInventory> func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
            return Optional.of(new DoubleSidedInventory(p_225539_1_, p_225539_2_));
        }

        @Override
        public Optional<IInventory> func_225538_a_(ChestTileEntity p_225538_1_) {
            return Optional.of(p_225538_1_);
        }

        @Override
        public Optional<IInventory> func_225537_b_() {
            return Optional.empty();
        }
    };
    private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> CONTAINER_MERGER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>(){

        @Override
        public Optional<INamedContainerProvider> func_225539_a_(final ChestTileEntity p_225539_1_, final ChestTileEntity p_225539_2_) {
            final DoubleSidedInventory iinventory = new DoubleSidedInventory(p_225539_1_, p_225539_2_);
            return Optional.of(new INamedContainerProvider(){

                @Override
                @Nullable
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                    if (p_225539_1_.canOpen(p_createMenu_3_) && p_225539_2_.canOpen(p_createMenu_3_)) {
                        p_225539_1_.fillWithLoot(p_createMenu_2_.player);
                        p_225539_2_.fillWithLoot(p_createMenu_2_.player);
                        return ChestContainer.createGeneric9X6(p_createMenu_1_, p_createMenu_2_, iinventory);
                    }
                    return null;
                }

                @Override
                public ITextComponent getDisplayName() {
                    if (p_225539_1_.hasCustomName()) {
                        return p_225539_1_.getDisplayName();
                    }
                    return p_225539_2_.hasCustomName() ? p_225539_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble");
                }
            });
        }

        @Override
        public Optional<INamedContainerProvider> func_225538_a_(ChestTileEntity p_225538_1_) {
            return Optional.of(p_225538_1_);
        }

        @Override
        public Optional<INamedContainerProvider> func_225537_b_() {
            return Optional.empty();
        }
    };

    protected ChestBlock(AbstractBlock.Properties builder, Supplier<TileEntityType<? extends ChestTileEntity>> tileEntityTypeIn) {
        super(builder, tileEntityTypeIn);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TYPE, ChestType.SINGLE)).with(WATERLOGGED, false));
    }

    public static TileEntityMerger.Type getChestMergerType(BlockState state) {
        ChestType chesttype = state.get(TYPE);
        if (chesttype == ChestType.SINGLE) {
            return TileEntityMerger.Type.SINGLE;
        }
        return chesttype == ChestType.RIGHT ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED).booleanValue()) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (facingState.isIn(this) && facing.getAxis().isHorizontal()) {
            ChestType chesttype = facingState.get(TYPE);
            if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && ChestBlock.getDirectionToAttached(facingState) == facing.getOpposite()) {
                return (BlockState)stateIn.with(TYPE, chesttype.opposite());
            }
        } else if (ChestBlock.getDirectionToAttached(stateIn) == facing) {
            return (BlockState)stateIn.with(TYPE, ChestType.SINGLE);
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(TYPE) == ChestType.SINGLE) {
            return SHAPE_SINGLE;
        }
        switch (ChestBlock.getDirectionToAttached(state)) {
            default: {
                return SHAPE_NORTH;
            }
            case SOUTH: {
                return SHAPE_SOUTH;
            }
            case WEST: {
                return SHAPE_WEST;
            }
            case EAST: 
        }
        return SHAPE_EAST;
    }

    public static Direction getDirectionToAttached(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(TYPE) == ChestType.LEFT ? direction.rotateY() : direction.rotateYCCW();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction2;
        ChestType chesttype = ChestType.SINGLE;
        Direction direction = context.getPlacementHorizontalFacing().getOpposite();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        boolean flag = context.hasSecondaryUseForPlayer();
        Direction direction1 = context.getFace();
        if (direction1.getAxis().isHorizontal() && flag && (direction2 = this.getDirectionToAttach(context, direction1.getOpposite())) != null && direction2.getAxis() != direction1.getAxis()) {
            direction = direction2;
            ChestType chestType = chesttype = direction2.rotateYCCW() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if (chesttype == ChestType.SINGLE && !flag) {
            if (direction == this.getDirectionToAttach(context, direction.rotateY())) {
                chesttype = ChestType.LEFT;
            } else if (direction == this.getDirectionToAttach(context, direction.rotateYCCW())) {
                chesttype = ChestType.RIGHT;
            }
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, direction)).with(TYPE, chesttype)).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) != false ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Nullable
    private Direction getDirectionToAttach(BlockItemUseContext context, Direction direction) {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction));
        return blockstate.isIn(this) && blockstate.get(TYPE) == ChestType.SINGLE ? blockstate.get(FACING) : null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tileentity;
        if (stack.hasDisplayName() && (tileentity = worldIn.getTileEntity(pos)) instanceof ChestTileEntity) {
            ((ChestTileEntity)tileentity).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)((Object)tileentity));
                worldIn.updateComparatorOutputLevel(pos, this);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }
        INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
        if (inamedcontainerprovider != null) {
            player.openContainer(inamedcontainerprovider);
            player.addStat(this.getOpenStat());
            PiglinTasks.func_234478_a_(player, true);
        }
        return ActionResultType.CONSUME;
    }

    protected Stat<ResourceLocation> getOpenStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    @Nullable
    public static IInventory getChestInventory(ChestBlock chest, BlockState state, World world, BlockPos pos, boolean override) {
        return chest.combine(state, world, pos, override).apply(INVENTORY_MERGER).orElse(null);
    }

    @Override
    public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> combine(BlockState state, World world, BlockPos pos, boolean override) {
        BiPredicate<IWorld, BlockPos> bipredicate = override ? (worldIn, posIn) -> false : ChestBlock::isBlocked;
        return TileEntityMerger.func_226924_a_((TileEntityType)this.tileEntityType.get(), ChestBlock::getChestMergerType, ChestBlock::getDirectionToAttached, FACING, state, world, pos, bipredicate);
    }

    @Override
    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return this.combine(state, worldIn, pos, false).apply(CONTAINER_MERGER).orElse(null);
    }

    public static TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction> getLidRotationCallback(final IChestLid lid) {
        return new TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction>(){

            @Override
            public Float2FloatFunction func_225539_a_(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
                return angle -> Math.max(p_225539_1_.getLidAngle(angle), p_225539_2_.getLidAngle(angle));
            }

            @Override
            public Float2FloatFunction func_225538_a_(ChestTileEntity p_225538_1_) {
                return p_225538_1_::getLidAngle;
            }

            @Override
            public Float2FloatFunction func_225537_b_() {
                return lid::getLidAngle;
            }
        };
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ChestTileEntity();
    }

    public static boolean isBlocked(IWorld world, BlockPos pos) {
        return ChestBlock.isBelowSolidBlock(world, pos) || ChestBlock.isCatSittingOn(world, pos);
    }

    private static boolean isBelowSolidBlock(IBlockReader reader, BlockPos worldIn) {
        BlockPos blockpos = worldIn.up();
        return reader.getBlockState(blockpos).isNormalCube(reader, blockpos);
    }

    private static boolean isCatSittingOn(IWorld world, BlockPos pos) {
        List<CatEntity> list = world.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        if (!list.isEmpty()) {
            for (CatEntity catentity : list) {
                if (!catentity.isSleeping()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory(ChestBlock.getChestInventory(this, blockState, worldIn, pos, false));
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
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
