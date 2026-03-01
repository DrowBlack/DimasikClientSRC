package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ComposterBlock
extends Block
implements ISidedInventoryProvider {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_8;
    public static final Object2FloatMap<IItemProvider> CHANCES = new Object2FloatOpenHashMap<IItemProvider>();
    private static final VoxelShape OUT_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] SHAPE = Util.make(new VoxelShape[9], shapes -> {
        for (int i = 0; i < 8; ++i) {
            shapes[i] = VoxelShapes.combineAndSimplify(OUT_SHAPE, Block.makeCuboidShape(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), IBooleanFunction.ONLY_FIRST);
        }
        shapes[8] = shapes[7];
    });

    public static void init() {
        CHANCES.defaultReturnValue(-1.0f);
        float f = 0.3f;
        float f1 = 0.5f;
        float f2 = 0.65f;
        float f3 = 0.85f;
        float f4 = 1.0f;
        ComposterBlock.registerCompostable(0.3f, Items.JUNGLE_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.OAK_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.SPRUCE_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.DARK_OAK_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.ACACIA_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.BIRCH_LEAVES);
        ComposterBlock.registerCompostable(0.3f, Items.OAK_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.SPRUCE_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.BIRCH_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.JUNGLE_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.ACACIA_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.DARK_OAK_SAPLING);
        ComposterBlock.registerCompostable(0.3f, Items.BEETROOT_SEEDS);
        ComposterBlock.registerCompostable(0.3f, Items.DRIED_KELP);
        ComposterBlock.registerCompostable(0.3f, Items.GRASS);
        ComposterBlock.registerCompostable(0.3f, Items.KELP);
        ComposterBlock.registerCompostable(0.3f, Items.MELON_SEEDS);
        ComposterBlock.registerCompostable(0.3f, Items.PUMPKIN_SEEDS);
        ComposterBlock.registerCompostable(0.3f, Items.SEAGRASS);
        ComposterBlock.registerCompostable(0.3f, Items.SWEET_BERRIES);
        ComposterBlock.registerCompostable(0.3f, Items.WHEAT_SEEDS);
        ComposterBlock.registerCompostable(0.5f, Items.DRIED_KELP_BLOCK);
        ComposterBlock.registerCompostable(0.5f, Items.TALL_GRASS);
        ComposterBlock.registerCompostable(0.5f, Items.CACTUS);
        ComposterBlock.registerCompostable(0.5f, Items.SUGAR_CANE);
        ComposterBlock.registerCompostable(0.5f, Items.VINE);
        ComposterBlock.registerCompostable(0.5f, Items.NETHER_SPROUTS);
        ComposterBlock.registerCompostable(0.5f, Items.WEEPING_VINES);
        ComposterBlock.registerCompostable(0.5f, Items.TWISTING_VINES);
        ComposterBlock.registerCompostable(0.5f, Items.MELON_SLICE);
        ComposterBlock.registerCompostable(0.65f, Items.SEA_PICKLE);
        ComposterBlock.registerCompostable(0.65f, Items.LILY_PAD);
        ComposterBlock.registerCompostable(0.65f, Items.PUMPKIN);
        ComposterBlock.registerCompostable(0.65f, Items.CARVED_PUMPKIN);
        ComposterBlock.registerCompostable(0.65f, Items.MELON);
        ComposterBlock.registerCompostable(0.65f, Items.APPLE);
        ComposterBlock.registerCompostable(0.65f, Items.BEETROOT);
        ComposterBlock.registerCompostable(0.65f, Items.CARROT);
        ComposterBlock.registerCompostable(0.65f, Items.COCOA_BEANS);
        ComposterBlock.registerCompostable(0.65f, Items.POTATO);
        ComposterBlock.registerCompostable(0.65f, Items.WHEAT);
        ComposterBlock.registerCompostable(0.65f, Items.BROWN_MUSHROOM);
        ComposterBlock.registerCompostable(0.65f, Items.RED_MUSHROOM);
        ComposterBlock.registerCompostable(0.65f, Items.MUSHROOM_STEM);
        ComposterBlock.registerCompostable(0.65f, Items.CRIMSON_FUNGUS);
        ComposterBlock.registerCompostable(0.65f, Items.WARPED_FUNGUS);
        ComposterBlock.registerCompostable(0.65f, Items.NETHER_WART);
        ComposterBlock.registerCompostable(0.65f, Items.CRIMSON_ROOTS);
        ComposterBlock.registerCompostable(0.65f, Items.WARPED_ROOTS);
        ComposterBlock.registerCompostable(0.65f, Items.SHROOMLIGHT);
        ComposterBlock.registerCompostable(0.65f, Items.DANDELION);
        ComposterBlock.registerCompostable(0.65f, Items.POPPY);
        ComposterBlock.registerCompostable(0.65f, Items.BLUE_ORCHID);
        ComposterBlock.registerCompostable(0.65f, Items.ALLIUM);
        ComposterBlock.registerCompostable(0.65f, Items.AZURE_BLUET);
        ComposterBlock.registerCompostable(0.65f, Items.RED_TULIP);
        ComposterBlock.registerCompostable(0.65f, Items.ORANGE_TULIP);
        ComposterBlock.registerCompostable(0.65f, Items.WHITE_TULIP);
        ComposterBlock.registerCompostable(0.65f, Items.PINK_TULIP);
        ComposterBlock.registerCompostable(0.65f, Items.OXEYE_DAISY);
        ComposterBlock.registerCompostable(0.65f, Items.CORNFLOWER);
        ComposterBlock.registerCompostable(0.65f, Items.LILY_OF_THE_VALLEY);
        ComposterBlock.registerCompostable(0.65f, Items.WITHER_ROSE);
        ComposterBlock.registerCompostable(0.65f, Items.FERN);
        ComposterBlock.registerCompostable(0.65f, Items.SUNFLOWER);
        ComposterBlock.registerCompostable(0.65f, Items.LILAC);
        ComposterBlock.registerCompostable(0.65f, Items.ROSE_BUSH);
        ComposterBlock.registerCompostable(0.65f, Items.PEONY);
        ComposterBlock.registerCompostable(0.65f, Items.LARGE_FERN);
        ComposterBlock.registerCompostable(0.85f, Items.HAY_BLOCK);
        ComposterBlock.registerCompostable(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostable(0.85f, Items.RED_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostable(0.85f, Items.NETHER_WART_BLOCK);
        ComposterBlock.registerCompostable(0.85f, Items.WARPED_WART_BLOCK);
        ComposterBlock.registerCompostable(0.85f, Items.BREAD);
        ComposterBlock.registerCompostable(0.85f, Items.BAKED_POTATO);
        ComposterBlock.registerCompostable(0.85f, Items.COOKIE);
        ComposterBlock.registerCompostable(1.0f, Items.CAKE);
        ComposterBlock.registerCompostable(1.0f, Items.PUMPKIN_PIE);
    }

    private static void registerCompostable(float chance, IItemProvider itemIn) {
        CHANCES.put((IItemProvider)itemIn.asItem(), chance);
    }

    public ComposterBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LEVEL, 0));
    }

    public static void playEvent(World world, BlockPos pos, boolean success) {
        BlockState blockstate = world.getBlockState(pos);
        world.playSound(pos.getX(), (double)pos.getY(), (double)pos.getZ(), success ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        double d0 = blockstate.getShape(world, pos).max(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double d1 = 0.13125f;
        double d2 = 0.7375f;
        Random random = world.getRandom();
        for (int i = 0; i < 10; ++i) {
            double d3 = random.nextGaussian() * 0.02;
            double d4 = random.nextGaussian() * 0.02;
            double d5 = random.nextGaussian() * 0.02;
            world.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), (double)pos.getY() + d0 + (double)random.nextFloat() * (1.0 - d0), (double)pos.getZ() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), d3, d4, d5);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE[state.get(LEVEL)];
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return OUT_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE[0];
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.get(LEVEL) == 7) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 20);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        int i = state.get(LEVEL);
        ItemStack itemstack = player.getHeldItem(handIn);
        if (i < 8 && CHANCES.containsKey(itemstack.getItem())) {
            if (i < 7 && !worldIn.isRemote) {
                BlockState blockstate = ComposterBlock.attemptCompost(state, worldIn, pos, itemstack);
                worldIn.playEvent(1500, pos, state != blockstate ? 1 : 0);
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (i == 8) {
            ComposterBlock.empty(state, worldIn, pos);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.PASS;
    }

    public static BlockState attemptFill(BlockState state, ServerWorld world, ItemStack stack, BlockPos pos) {
        int i = state.get(LEVEL);
        if (i < 7 && CHANCES.containsKey(stack.getItem())) {
            BlockState blockstate = ComposterBlock.attemptCompost(state, world, pos, stack);
            stack.shrink(1);
            return blockstate;
        }
        return state;
    }

    public static BlockState empty(BlockState state, World world, BlockPos pos) {
        if (!world.isRemote) {
            float f = 0.7f;
            double d0 = (double)(world.rand.nextFloat() * 0.7f) + (double)0.15f;
            double d1 = (double)(world.rand.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
            double d2 = (double)(world.rand.nextFloat() * 0.7f) + (double)0.15f;
            ItemEntity itementity = new ItemEntity(world, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, new ItemStack(Items.BONE_MEAL));
            itementity.setDefaultPickupDelay();
            world.addEntity(itementity);
        }
        BlockState blockstate = ComposterBlock.resetFillState(state, world, pos);
        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return blockstate;
    }

    private static BlockState resetFillState(BlockState state, IWorld world, BlockPos pos) {
        BlockState blockstate = (BlockState)state.with(LEVEL, 0);
        world.setBlockState(pos, blockstate, 3);
        return blockstate;
    }

    private static BlockState attemptCompost(BlockState state, IWorld world, BlockPos pos, ItemStack stack) {
        int i = state.get(LEVEL);
        float f = CHANCES.getFloat(stack.getItem());
        if (!(i == 0 && f > 0.0f || world.getRandom().nextDouble() < (double)f)) {
            return state;
        }
        int j = i + 1;
        BlockState blockstate = (BlockState)state.with(LEVEL, j);
        world.setBlockState(pos, blockstate, 3);
        if (j == 7) {
            world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 20);
        }
        return blockstate;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (state.get(LEVEL) == 7) {
            worldIn.setBlockState(pos, (BlockState)state.func_235896_a_(LEVEL), 3);
            worldIn.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return blockState.get(LEVEL);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
        int i = state.get(LEVEL);
        if (i == 8) {
            return new FullInventory(state, world, pos, new ItemStack(Items.BONE_MEAL));
        }
        return (ISidedInventory)((Object)(i < 7 ? new PartialInventory(state, world, pos) : new EmptyInventory()));
    }

    static class FullInventory
    extends Inventory
    implements ISidedInventory {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean extracted;

        public FullInventory(BlockState state, IWorld world, BlockPos pos, ItemStack stack) {
            super(stack);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            int[] nArray;
            if (side == Direction.DOWN) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
            return !this.extracted && direction == Direction.DOWN && stack.getItem() == Items.BONE_MEAL;
        }

        @Override
        public void markDirty() {
            ComposterBlock.resetFillState(this.state, this.world, this.pos);
            this.extracted = true;
        }
    }

    static class PartialInventory
    extends Inventory
    implements ISidedInventory {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean inserted;

        public PartialInventory(BlockState state, IWorld world, BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            int[] nArray;
            if (side == Direction.UP) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            } else {
                nArray = new int[]{};
            }
            return nArray;
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
            return !this.inserted && direction == Direction.UP && CHANCES.containsKey(itemStackIn.getItem());
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
            return false;
        }

        @Override
        public void markDirty() {
            ItemStack itemstack = this.getStackInSlot(0);
            if (!itemstack.isEmpty()) {
                this.inserted = true;
                BlockState blockstate = ComposterBlock.attemptCompost(this.state, this.world, this.pos, itemstack);
                this.world.playEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
                this.removeStackFromSlot(0);
            }
        }
    }

    static class EmptyInventory
    extends Inventory
    implements ISidedInventory {
        public EmptyInventory() {
            super(0);
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
            return false;
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }
}
