package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedstoneOreBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneOreBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        RedstoneOreBlock.activate(state, worldIn, pos);
        super.onBlockClicked(state, worldIn, pos, player);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        RedstoneOreBlock.activate(worldIn.getBlockState(pos), worldIn, pos);
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            RedstoneOreBlock.spawnParticles(worldIn, pos);
        } else {
            RedstoneOreBlock.activate(state, worldIn, pos);
        }
        ItemStack itemstack = player.getHeldItem(handIn);
        return itemstack.getItem() instanceof BlockItem && new BlockItemUseContext(player, handIn, itemstack, hit).canPlace() ? ActionResultType.PASS : ActionResultType.SUCCESS;
    }

    private static void activate(BlockState state, World world, BlockPos pos) {
        RedstoneOreBlock.spawnParticles(world, pos);
        if (!state.get(LIT).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(LIT, true), 3);
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            worldIn.setBlockState(pos, (BlockState)state.with(LIT, false), 3);
        }
    }

    @Override
    public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack) {
        super.spawnAdditionalDrops(state, worldIn, pos, stack);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int i = 1 + worldIn.rand.nextInt(5);
            this.dropXpOnBlockBreak(worldIn, pos, i);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(LIT).booleanValue()) {
            RedstoneOreBlock.spawnParticles(worldIn, pos);
        }
    }

    private static void spawnParticles(World world, BlockPos worldIn) {
        double d0 = 0.5625;
        Random random = world.rand;
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = worldIn.offset(direction);
            if (world.getBlockState(blockpos).isOpaqueCube(world, blockpos)) continue;
            Direction.Axis direction$axis = direction.getAxis();
            double d1 = direction$axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getXOffset() : (double)random.nextFloat();
            double d2 = direction$axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getYOffset() : (double)random.nextFloat();
            double d3 = direction$axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getZOffset() : (double)random.nextFloat();
            world.addParticle(RedstoneParticleData.REDSTONE_DUST, (double)worldIn.getX() + d1, (double)worldIn.getY() + d2, (double)worldIn.getZ() + d3, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}
