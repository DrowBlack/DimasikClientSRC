package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock
extends Block {
    private static final Map<Block, Block> POTTED_CONTENT = Maps.newHashMap();
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    private final Block flower;

    public FlowerPotBlock(Block block, AbstractBlock.Properties properties) {
        super(properties);
        this.flower = block;
        POTTED_CONTENT.put(block, this);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        boolean flag1;
        ItemStack itemstack = player.getHeldItem(handIn);
        Item item = itemstack.getItem();
        Block block = item instanceof BlockItem ? POTTED_CONTENT.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
        boolean flag = block == Blocks.AIR;
        boolean bl = flag1 = this.flower == Blocks.AIR;
        if (flag != flag1) {
            if (flag1) {
                worldIn.setBlockState(pos, block.getDefaultState(), 3);
                player.addStat(Stats.POT_FLOWER);
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            } else {
                ItemStack itemstack1 = new ItemStack(this.flower);
                if (itemstack.isEmpty()) {
                    player.setHeldItem(handIn, itemstack1);
                } else if (!player.addItemStackToInventory(itemstack1)) {
                    player.dropItem(itemstack1, false);
                }
                worldIn.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.CONSUME;
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return this.flower == Blocks.AIR ? super.getItem(worldIn, pos, state) : new ItemStack(this.flower);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public Block getFlower() {
        return this.flower;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
