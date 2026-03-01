package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DebugStickItem
extends Item {
    public DebugStickItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if (!worldIn.isRemote) {
            this.handleClick(player, state, worldIn, pos, false, player.getHeldItem(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity playerentity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isRemote && playerentity != null) {
            BlockPos blockpos = context.getPos();
            this.handleClick(playerentity, world.getBlockState(blockpos), world, blockpos, true, context.getItem());
        }
        return ActionResultType.func_233537_a_(world.isRemote);
    }

    private void handleClick(PlayerEntity player, BlockState state, IWorld worldIn, BlockPos pos, boolean rightClick, ItemStack stack) {
        if (player.canUseCommandBlock()) {
            Block block = state.getBlock();
            StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
            Collection<Property<?>> collection = statecontainer.getProperties();
            String s = Registry.BLOCK.getKey(block).toString();
            if (collection.isEmpty()) {
                DebugStickItem.sendMessage(player, new TranslationTextComponent(this.getTranslationKey() + ".empty", s));
            } else {
                CompoundNBT compoundnbt = stack.getOrCreateChildTag("DebugProperty");
                String s1 = compoundnbt.getString(s);
                Property<?> property = statecontainer.getProperty(s1);
                if (rightClick) {
                    if (property == null) {
                        property = collection.iterator().next();
                    }
                    BlockState blockstate = DebugStickItem.cycleProperty(state, property, player.isSecondaryUseActive());
                    worldIn.setBlockState(pos, blockstate, 18);
                    DebugStickItem.sendMessage(player, new TranslationTextComponent(this.getTranslationKey() + ".update", property.getName(), DebugStickItem.func_195957_a(blockstate, property)));
                } else {
                    property = DebugStickItem.getAdjacentValue(collection, property, player.isSecondaryUseActive());
                    String s2 = property.getName();
                    compoundnbt.putString(s, s2);
                    DebugStickItem.sendMessage(player, new TranslationTextComponent(this.getTranslationKey() + ".select", s2, DebugStickItem.func_195957_a(state, property)));
                }
            }
        }
    }

    private static <T extends Comparable<T>> BlockState cycleProperty(BlockState state, Property<T> propertyIn, boolean backwards) {
        return (BlockState)state.with(propertyIn, (Comparable)DebugStickItem.getAdjacentValue(propertyIn.getAllowedValues(), state.get(propertyIn), backwards));
    }

    private static <T> T getAdjacentValue(Iterable<T> allowedValues, @Nullable T currentValue, boolean backwards) {
        return backwards ? Util.getElementBefore(allowedValues, currentValue) : Util.getElementAfter(allowedValues, currentValue);
    }

    private static void sendMessage(PlayerEntity player, ITextComponent text) {
        ((ServerPlayerEntity)player).func_241151_a_(text, ChatType.GAME_INFO, Util.DUMMY_UUID);
    }

    private static <T extends Comparable<T>> String func_195957_a(BlockState state, Property<T> propertyIn) {
        return propertyIn.getName(state.get(propertyIn));
    }
}
