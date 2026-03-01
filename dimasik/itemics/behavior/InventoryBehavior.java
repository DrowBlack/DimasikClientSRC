package dimasik.itemics.behavior;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.event.events.TickEvent;
import dimasik.itemics.behavior.Behavior;
import dimasik.itemics.utils.ToolSet;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public final class InventoryBehavior
extends Behavior {
    public InventoryBehavior(Itemics itemics) {
        super(itemics);
    }

    @Override
    public void onTick(TickEvent event) {
        int pick;
        if (!((Boolean)Itemics.settings().allowInventory.value).booleanValue()) {
            return;
        }
        if (event.getType() == TickEvent.Type.OUT) {
            return;
        }
        if (this.ctx.player().openContainer != this.ctx.player().container) {
            return;
        }
        if (this.firstValidThrowaway() >= 9) {
            this.swapWithHotBar(this.firstValidThrowaway(), 8);
        }
        if ((pick = this.bestToolAgainst(Blocks.STONE, PickaxeItem.class)) >= 9) {
            this.swapWithHotBar(pick, 0);
        }
    }

    public void attemptToPutOnHotbar(int inMainInvy, Predicate<Integer> disallowedHotbar) {
        OptionalInt destination = this.getTempHotbarSlot(disallowedHotbar);
        if (destination.isPresent()) {
            this.swapWithHotBar(inMainInvy, destination.getAsInt());
        }
    }

    public OptionalInt getTempHotbarSlot(Predicate<Integer> disallowedHotbar) {
        int i;
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (i = 1; i < 8; ++i) {
            if (!this.ctx.player().inventory.mainInventory.get(i).isEmpty() || disallowedHotbar.test(i)) continue;
            candidates.add(i);
        }
        if (candidates.isEmpty()) {
            for (i = 1; i < 8; ++i) {
                if (disallowedHotbar.test(i)) continue;
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of((Integer)candidates.get(new Random().nextInt(candidates.size())));
    }

    private void swapWithHotBar(int inInventory, int inHotbar) {
        this.ctx.playerController().windowClick(this.ctx.player().container.windowId, inInventory < 9 ? inInventory + 36 : inInventory, inHotbar, ClickType.SWAP, this.ctx.player());
    }

    private int firstValidThrowaway() {
        NonNullList<ItemStack> invy = this.ctx.player().inventory.mainInventory;
        for (int i = 0; i < invy.size(); ++i) {
            if (!((List)Itemics.settings().acceptableThrowawayItems.value).contains(invy.get(i).getItem())) continue;
            return i;
        }
        return -1;
    }

    private int bestToolAgainst(Block against, Class<? extends ToolItem> cla$$) {
        NonNullList<ItemStack> invy = this.ctx.player().inventory.mainInventory;
        int bestInd = -1;
        double bestSpeed = -1.0;
        for (int i = 0; i < invy.size(); ++i) {
            double speed;
            ItemStack stack = invy.get(i);
            if (stack.isEmpty() || ((Boolean)Itemics.settings().itemSaver.value).booleanValue() && stack.getDamage() + (Integer)Itemics.settings().itemSaverThreshold.value >= stack.getMaxDamage() && stack.getMaxDamage() > 1 || !cla$$.isInstance(stack.getItem()) || !((speed = ToolSet.calculateSpeedVsBlock(stack, against.getDefaultState())) > bestSpeed)) continue;
            bestSpeed = speed;
            bestInd = i;
        }
        return bestInd;
    }

    public boolean hasGenericThrowaway() {
        for (Item item : (List)Itemics.settings().acceptableThrowawayItems.value) {
            if (!this.throwaway(false, stack -> item.equals(stack.getItem()))) continue;
            return true;
        }
        return false;
    }

    public boolean selectThrowawayForLocation(boolean select, int x, int y, int z) {
        BlockState maybe = this.itemics.getBuilderProcess().placeAt(x, y, z, this.itemics.bsi.get0(x, y, z));
        if (maybe != null && this.throwaway(select, stack -> stack.getItem() instanceof BlockItem && maybe.equals(((BlockItem)stack.getItem()).getBlock().getStateForPlacement(new BlockItemUseContext(new ItemUseContext(this.ctx.world(), this.ctx.player(), Hand.MAIN_HAND, (ItemStack)stack, new BlockRayTraceResult(new Vector3d(this.ctx.player().getPositionVec().x, this.ctx.player().getPositionVec().y, this.ctx.player().getPositionVec().z), Direction.UP, this.ctx.playerFeet(), false)){}))))) {
            return true;
        }
        if (maybe != null && this.throwaway(select, stack -> stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock().equals(maybe.getBlock()))) {
            return true;
        }
        for (Item item : (List)Itemics.settings().acceptableThrowawayItems.value) {
            if (!this.throwaway(select, stack -> item.equals(stack.getItem()))) continue;
            return true;
        }
        return false;
    }

    public boolean throwaway(boolean select, Predicate<? super ItemStack> desired) {
        return this.throwaway(select, desired, (Boolean)Itemics.settings().allowInventory.value);
    }

    public boolean throwaway(boolean select, Predicate<? super ItemStack> desired, boolean allowInventory) {
        ItemStack item;
        int i;
        ClientPlayerEntity p = this.ctx.player();
        NonNullList<ItemStack> inv = p.inventory.mainInventory;
        for (i = 0; i < 9; ++i) {
            item = inv.get(i);
            if (!desired.test(item)) continue;
            if (select) {
                p.inventory.currentItem = i;
            }
            return true;
        }
        if (desired.test(p.inventory.offHandInventory.get(0))) {
            for (i = 0; i < 9; ++i) {
                item = inv.get(i);
                if (!item.isEmpty() && !(item.getItem() instanceof PickaxeItem)) continue;
                if (select) {
                    p.inventory.currentItem = i;
                }
                return true;
            }
        }
        if (allowInventory) {
            for (i = 9; i < 36; ++i) {
                if (!desired.test(inv.get(i))) continue;
                this.swapWithHotBar(i, 7);
                if (select) {
                    p.inventory.currentItem = 7;
                }
                return true;
            }
        }
        return false;
    }
}
