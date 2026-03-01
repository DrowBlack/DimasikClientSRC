package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MinecartItem
extends Item {
    private static final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            double d3;
            RailShape railshape;
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            ServerWorld world = source.getWorld();
            double d0 = source.getX() + (double)direction.getXOffset() * 1.125;
            double d1 = Math.floor(source.getY()) + (double)direction.getYOffset();
            double d2 = source.getZ() + (double)direction.getZOffset() * 1.125;
            BlockPos blockpos = source.getBlockPos().offset(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railShape = railshape = blockstate.getBlock() instanceof AbstractRailBlock ? blockstate.get(((AbstractRailBlock)blockstate.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (blockstate.isIn(BlockTags.RAILS)) {
                d3 = railshape.isAscending() ? 0.6 : 0.1;
            } else {
                if (!blockstate.isAir() || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                BlockState blockstate1 = world.getBlockState(blockpos.down());
                RailShape railshape1 = blockstate1.getBlock() instanceof AbstractRailBlock ? blockstate1.get(((AbstractRailBlock)blockstate1.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                d3 = direction != Direction.DOWN && railshape1.isAscending() ? -0.4 : -0.9;
            }
            AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, d0, d1 + d3, d2, ((MinecartItem)stack.getItem()).minecartType);
            if (stack.hasDisplayName()) {
                abstractminecartentity.setCustomName(stack.getDisplayName());
            }
            world.addEntity(abstractminecartentity);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };
    private final AbstractMinecartEntity.Type minecartType;

    public MinecartItem(AbstractMinecartEntity.Type minecartTypeIn, Item.Properties builder) {
        super(builder);
        this.minecartType = minecartTypeIn;
        DispenserBlock.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos blockpos;
        World world = context.getWorld();
        BlockState blockstate = world.getBlockState(blockpos = context.getPos());
        if (!blockstate.isIn(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        }
        ItemStack itemstack = context.getItem();
        if (!world.isRemote) {
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? blockstate.get(((AbstractRailBlock)blockstate.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double d0 = 0.0;
            if (railshape.isAscending()) {
                d0 = 0.5;
            }
            AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, (double)blockpos.getX() + 0.5, (double)blockpos.getY() + 0.0625 + d0, (double)blockpos.getZ() + 0.5, this.minecartType);
            if (itemstack.hasDisplayName()) {
                abstractminecartentity.setCustomName(itemstack.getDisplayName());
            }
            world.addEntity(abstractminecartentity);
        }
        itemstack.shrink(1);
        return ActionResultType.func_233537_a_(world.isRemote);
    }
}
