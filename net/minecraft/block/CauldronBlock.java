package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CauldronBlock
extends Block {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_3;
    private static final VoxelShape INSIDE = CauldronBlock.makeCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(CauldronBlock.makeCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), CauldronBlock.makeCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), CauldronBlock.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE), IBooleanFunction.ONLY_FIRST);

    public CauldronBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LEVEL, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        int i = state.get(LEVEL);
        float f = (float)pos.getY() + (6.0f + (float)(3 * i)) / 16.0f;
        if (!worldIn.isRemote && entityIn.isBurning() && i > 0 && entityIn.getPosY() <= (double)f) {
            entityIn.extinguish();
            this.setWaterLevel(worldIn, pos, state, i - 1);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        IDyeableArmorItem idyeablearmoritem;
        ItemStack itemstack = player.getHeldItem(handIn);
        if (itemstack.isEmpty()) {
            return ActionResultType.PASS;
        }
        int i = state.get(LEVEL);
        Item item = itemstack.getItem();
        if (item == Items.WATER_BUCKET) {
            if (i < 3 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    player.setHeldItem(handIn, new ItemStack(Items.BUCKET));
                }
                player.addStat(Stats.FILL_CAULDRON);
                this.setWaterLevel(worldIn, pos, state, 3);
                worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (item == Items.BUCKET) {
            if (i == 3 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.setHeldItem(handIn, new ItemStack(Items.WATER_BUCKET));
                    } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.WATER_BUCKET))) {
                        player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
                    }
                }
                player.addStat(Stats.USE_CAULDRON);
                this.setWaterLevel(worldIn, pos, state, 0);
                worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (item == Items.GLASS_BOTTLE) {
            if (i > 0 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    ItemStack itemstack4 = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
                    player.addStat(Stats.USE_CAULDRON);
                    itemstack.shrink(1);
                    if (itemstack.isEmpty()) {
                        player.setHeldItem(handIn, itemstack4);
                    } else if (!player.inventory.addItemStackToInventory(itemstack4)) {
                        player.dropItem(itemstack4, false);
                    } else if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                    }
                }
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(worldIn, pos, state, i - 1);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (item == Items.POTION && PotionUtils.getPotionFromItem(itemstack) == Potions.WATER) {
            if (i < 3 && !worldIn.isRemote) {
                if (!player.abilities.isCreativeMode) {
                    ItemStack itemstack3 = new ItemStack(Items.GLASS_BOTTLE);
                    player.addStat(Stats.USE_CAULDRON);
                    player.setHeldItem(handIn, itemstack3);
                    if (player instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                    }
                }
                worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(worldIn, pos, state, i + 1);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (i > 0 && item instanceof IDyeableArmorItem && (idyeablearmoritem = (IDyeableArmorItem)((Object)item)).hasColor(itemstack) && !worldIn.isRemote) {
            idyeablearmoritem.removeColor(itemstack);
            this.setWaterLevel(worldIn, pos, state, i - 1);
            player.addStat(Stats.CLEAN_ARMOR);
            return ActionResultType.SUCCESS;
        }
        if (i > 0 && item instanceof BannerItem) {
            if (BannerTileEntity.getPatterns(itemstack) > 0 && !worldIn.isRemote) {
                ItemStack itemstack2 = itemstack.copy();
                itemstack2.setCount(1);
                BannerTileEntity.removeBannerData(itemstack2);
                player.addStat(Stats.CLEAN_BANNER);
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                    this.setWaterLevel(worldIn, pos, state, i - 1);
                }
                if (itemstack.isEmpty()) {
                    player.setHeldItem(handIn, itemstack2);
                } else if (!player.inventory.addItemStackToInventory(itemstack2)) {
                    player.dropItem(itemstack2, false);
                } else if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
                }
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (i > 0 && item instanceof BlockItem) {
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof ShulkerBoxBlock && !worldIn.isRemote()) {
                ItemStack itemstack1 = new ItemStack(Blocks.SHULKER_BOX, 1);
                if (itemstack.hasTag()) {
                    itemstack1.setTag(itemstack.getTag().copy());
                }
                player.setHeldItem(handIn, itemstack1);
                this.setWaterLevel(worldIn, pos, state, i - 1);
                player.addStat(Stats.CLEAN_SHULKER_BOX);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    public void setWaterLevel(World worldIn, BlockPos pos, BlockState state, int level) {
        worldIn.setBlockState(pos, (BlockState)state.with(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
        worldIn.updateComparatorOutputLevel(pos, this);
    }

    @Override
    public void fillWithRain(World worldIn, BlockPos pos) {
        BlockState blockstate;
        float f;
        if (worldIn.rand.nextInt(20) == 1 && !((f = worldIn.getBiome(pos).getTemperature(pos)) < 0.15f) && (blockstate = worldIn.getBlockState(pos)).get(LEVEL) < 3) {
            worldIn.setBlockState(pos, (BlockState)blockstate.func_235896_a_(LEVEL), 2);
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
}
