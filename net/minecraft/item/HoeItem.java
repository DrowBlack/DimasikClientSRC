package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoeItem
extends ToolItem {
    private static final Set<Block> EFFECTIVE_ON_BLOCKS = ImmutableSet.of(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, new Block[]{Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES});
    protected static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

    protected HoeItem(IItemTier p_i231595_1_, int p_i231595_2_, float p_i231595_3_, Item.Properties p_i231595_4_) {
        super(p_i231595_2_, p_i231595_3_, p_i231595_1_, EFFECTIVE_ON_BLOCKS, p_i231595_4_);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockState blockstate;
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        if (context.getFace() != Direction.DOWN && world.getBlockState(blockpos.up()).isAir() && (blockstate = HOE_LOOKUP.get(world.getBlockState(blockpos).getBlock())) != null) {
            PlayerEntity playerentity = context.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isRemote) {
                world.setBlockState(blockpos, blockstate, 11);
                if (playerentity != null) {
                    context.getItem().damageItem(1, playerentity, p_220043_1_ -> p_220043_1_.sendBreakAnimation(context.getHand()));
                }
            }
            return ActionResultType.func_233537_a_(world.isRemote);
        }
        return ActionResultType.PASS;
    }
}
