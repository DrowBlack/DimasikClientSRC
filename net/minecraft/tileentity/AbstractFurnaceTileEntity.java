package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class AbstractFurnaceTileEntity
extends LockableTileEntity
implements ISidedInventory,
IRecipeHolder,
IRecipeHelperPopulator,
ITickableTileEntity {
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_HORIZONTAL = new int[]{1};
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int burnTime;
    private int recipesUsed;
    private int cookTime;
    private int cookTimeTotal;
    protected final IIntArray furnaceData = new IIntArray(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return AbstractFurnaceTileEntity.this.burnTime;
                }
                case 1: {
                    return AbstractFurnaceTileEntity.this.recipesUsed;
                }
                case 2: {
                    return AbstractFurnaceTileEntity.this.cookTime;
                }
                case 3: {
                    return AbstractFurnaceTileEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AbstractFurnaceTileEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    AbstractFurnaceTileEntity.this.recipesUsed = value;
                    break;
                }
                case 2: {
                    AbstractFurnaceTileEntity.this.cookTime = value;
                    break;
                }
                case 3: {
                    AbstractFurnaceTileEntity.this.cookTimeTotal = value;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap();
    protected final IRecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceTileEntity(TileEntityType<?> tileTypeIn, IRecipeType<? extends AbstractCookingRecipe> recipeTypeIn) {
        super(tileTypeIn);
        this.recipeType = recipeTypeIn;
    }

    public static Map<Item, Integer> getBurnTimes() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.LAVA_BUCKET, 20000);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.COAL_BLOCK, 16000);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.BLAZE_ROD, 2400);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.COAL, 1600);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.CHARCOAL, 1600);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.LOGS, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.PLANKS, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_STAIRS, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_SLABS, 150);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_TRAPDOORS, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.OAK_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.BIRCH_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.SPRUCE_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.JUNGLE_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.DARK_OAK_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.ACACIA_FENCE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.OAK_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.BIRCH_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.SPRUCE_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.JUNGLE_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.DARK_OAK_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.ACACIA_FENCE_GATE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.NOTE_BLOCK, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.BOOKSHELF, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.LECTERN, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.JUKEBOX, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.CHEST, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.TRAPPED_CHEST, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.CRAFTING_TABLE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.DAYLIGHT_DETECTOR, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.BANNERS, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.BOW, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.FISHING_ROD, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.LADDER, 300);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.SIGNS, 200);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.WOODEN_SHOVEL, 200);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.WOODEN_SWORD, 200);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.WOODEN_HOE, 200);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.WOODEN_AXE, 200);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.WOODEN_PICKAXE, 200);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_DOORS, 200);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.BOATS, 1200);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOOL, 100);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.WOODEN_BUTTONS, 100);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.STICK, 100);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.SAPLINGS, 100);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.BOWL, 100);
        AbstractFurnaceTileEntity.addItemTagBurnTime(map, ItemTags.CARPETS, 67);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.DRIED_KELP_BLOCK, 4001);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Items.CROSSBOW, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.BAMBOO, 50);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.DEAD_BUSH, 100);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.SCAFFOLDING, 400);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.LOOM, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.BARREL, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceTileEntity.addItemBurnTime(map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static boolean isNonFlammable(Item item) {
        return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void addItemTagBurnTime(Map<Item, Integer> map, ITag<Item> itemTag, int burnTimeIn) {
        for (Item item : itemTag.getAllElements()) {
            if (AbstractFurnaceTileEntity.isNonFlammable(item)) continue;
            map.put(item, burnTimeIn);
        }
    }

    private static void addItemBurnTime(Map<Item, Integer> map, IItemProvider itemProvider, int burnTimeIn) {
        Item item = itemProvider.asItem();
        if (AbstractFurnaceTileEntity.isNonFlammable(item)) {
            if (SharedConstants.developmentMode) {
                throw Util.pauseDevMode(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getDisplayName(null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            map.put(item, burnTimeIn);
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, this.items);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.recipesUsed = this.getBurnTime(this.items.get(1));
        CompoundNBT compoundnbt = nbt.getCompound("RecipesUsed");
        for (String s : compoundnbt.keySet()) {
            this.recipes.put(new ResourceLocation(s), compoundnbt.getInt(s));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putShort("BurnTime", (short)this.burnTime);
        compound.putShort("CookTime", (short)this.cookTime);
        compound.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        ItemStackHelper.saveAllItems(compound, this.items);
        CompoundNBT compoundnbt = new CompoundNBT();
        this.recipes.forEach((recipeId, craftedAmount) -> compoundnbt.putInt(recipeId.toString(), (int)craftedAmount));
        compound.put("RecipesUsed", compoundnbt);
        return compound;
    }

    @Override
    public void tick() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        if (!this.world.isRemote) {
            ItemStack itemstack = this.items.get(1);
            if (this.isBurning() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
                IRecipe irecipe = this.world.getRecipeManager().getRecipe(this.recipeType, this, this.world).orElse(null);
                if (!this.isBurning() && this.canSmelt(irecipe)) {
                    this.recipesUsed = this.burnTime = this.getBurnTime(itemstack);
                    if (this.isBurning()) {
                        flag1 = true;
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                Item item1 = item.getContainerItem();
                                this.items.set(1, item1 == null ? ItemStack.EMPTY : new ItemStack(item1));
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canSmelt(irecipe)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.smelt(irecipe);
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            if (flag != this.isBurning()) {
                flag1 = true;
                this.world.setBlockState(this.pos, (BlockState)this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }
        if (flag1) {
            this.markDirty();
        }
    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn) {
        if (!this.items.get(0).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            }
            ItemStack itemstack1 = this.items.get(2);
            if (itemstack1.isEmpty()) {
                return true;
            }
            if (!itemstack1.isItemEqual(itemstack)) {
                return false;
            }
            if (itemstack1.getCount() < this.getInventoryStackLimit() && itemstack1.getCount() < itemstack1.getMaxStackSize()) {
                return true;
            }
            return itemstack1.getCount() < itemstack.getMaxStackSize();
        }
        return false;
    }

    private void smelt(@Nullable IRecipe<?> recipe) {
        if (recipe != null && this.canSmelt(recipe)) {
            ItemStack itemstack = this.items.get(0);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = this.items.get(2);
            if (itemstack2.isEmpty()) {
                this.items.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(1);
            }
            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }
            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }
            itemstack.shrink(1);
        }
    }

    protected int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AbstractFurnaceTileEntity.getBurnTimes().getOrDefault(item, 0);
    }

    protected int getCookTime() {
        return this.world.getRecipeManager().getRecipe(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean isFuel(ItemStack stack) {
        return AbstractFurnaceTileEntity.getBurnTimes().containsKey(stack.getItem());
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return SLOTS_DOWN;
        }
        return side == Direction.UP ? SLOTS_UP : SLOTS_HORIZONTAL;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        Item item;
        return direction != Direction.DOWN || index != 1 || (item = stack.getItem()) == Items.WATER_BUCKET || item == Items.BUCKET;
    }

    @Override
    public int getSizeInventory() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.items, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.items, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.items.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        if (index == 0 && !flag) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        }
        return player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        }
        if (index != 1) {
            return true;
        }
        ItemStack itemstack = this.items.get(1);
        return AbstractFurnaceTileEntity.isFuel(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.getId();
            this.recipes.addTo(resourcelocation, 1);
        }
    }

    @Override
    @Nullable
    public IRecipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void onCrafting(PlayerEntity player) {
    }

    public void unlockRecipes(PlayerEntity player) {
        List<IRecipe<?>> list = this.grantStoredRecipeExperience(player.world, player.getPositionVec());
        player.unlockRecipes(list);
        this.recipes.clear();
    }

    public List<IRecipe<?>> grantStoredRecipeExperience(World world, Vector3d pos) {
        ArrayList<IRecipe<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipes.object2IntEntrySet()) {
            world.getRecipeManager().getRecipe((ResourceLocation)entry.getKey()).ifPresent(recipe -> {
                list.add((IRecipe<?>)recipe);
                AbstractFurnaceTileEntity.splitAndSpawnExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }
        return list;
    }

    private static void splitAndSpawnExperience(World world, Vector3d pos, int craftedAmount, float experience) {
        int i = MathHelper.floor((float)craftedAmount * experience);
        float f = MathHelper.frac((float)craftedAmount * experience);
        if (f != 0.0f && Math.random() < (double)f) {
            ++i;
        }
        while (i > 0) {
            int j = ExperienceOrbEntity.getXPSplit(i);
            i -= j;
            world.addEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, j));
        }
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {
        for (ItemStack itemstack : this.items) {
            helper.accountStack(itemstack);
        }
    }
}
