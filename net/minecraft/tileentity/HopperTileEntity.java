package net.minecraft.tileentity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class HopperTileEntity
extends LockableLootTileEntity
implements IHopper,
ITickableTileEntity {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long tickedGameTime;

    public HopperTileEntity() {
        super(TileEntityType.HOPPER);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.inventory);
        }
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.inventory);
        }
        compound.putInt("TransferCooldown", this.transferCooldown);
        return compound;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        this.fillWithLoot(null);
        return ItemStackHelper.getAndSplit(this.getItems(), index, count);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.fillWithLoot(null);
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.hopper");
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            --this.transferCooldown;
            this.tickedGameTime = this.world.getGameTime();
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.updateHopper(() -> HopperTileEntity.pullItems(this));
            }
        }
    }

    private boolean updateHopper(Supplier<Boolean> p_200109_1_) {
        if (this.world != null && !this.world.isRemote) {
            if (!this.isOnTransferCooldown() && this.getBlockState().get(HopperBlock.ENABLED).booleanValue()) {
                boolean flag = false;
                if (!this.isEmpty()) {
                    flag = this.transferItemsOut();
                }
                if (!this.isFull()) {
                    flag |= p_200109_1_.get().booleanValue();
                }
                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isFull() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty() && itemstack.getCount() == itemstack.getMaxStackSize()) continue;
            return false;
        }
        return true;
    }

    private boolean transferItemsOut() {
        IInventory iinventory = this.getInventoryForHopperTransfer();
        if (iinventory == null) {
            return false;
        }
        Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
        if (this.isInventoryFull(iinventory, direction)) {
            return false;
        }
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i).isEmpty()) continue;
            ItemStack itemstack = this.getStackInSlot(i).copy();
            ItemStack itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), direction);
            if (itemstack1.isEmpty()) {
                iinventory.markDirty();
                return true;
            }
            this.setInventorySlotContents(i, itemstack);
        }
        return false;
    }

    private static IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
        return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory)p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
    }

    private boolean isInventoryFull(IInventory inventoryIn, Direction side) {
        return HopperTileEntity.func_213972_a(inventoryIn, side).allMatch(p_213970_1_ -> {
            ItemStack itemstack = inventoryIn.getStackInSlot(p_213970_1_);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
        return HopperTileEntity.func_213972_a(inventoryIn, side).allMatch(p_213973_1_ -> inventoryIn.getStackInSlot(p_213973_1_).isEmpty());
    }

    public static boolean pullItems(IHopper hopper) {
        IInventory iinventory = HopperTileEntity.getSourceInventory(hopper);
        if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return HopperTileEntity.isInventoryEmpty(iinventory, direction) ? false : HopperTileEntity.func_213972_a(iinventory, direction).anyMatch(p_213971_3_ -> HopperTileEntity.pullItemFromSlot(hopper, iinventory, p_213971_3_, direction));
        }
        for (ItemEntity itementity : HopperTileEntity.getCaptureItems(hopper)) {
            if (!HopperTileEntity.captureItem(hopper, itementity)) continue;
            return true;
        }
        return false;
    }

    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);
        if (!itemstack.isEmpty() && HopperTileEntity.canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = HopperTileEntity.putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1), null);
            if (itemstack2.isEmpty()) {
                inventoryIn.markDirty();
                return true;
            }
            inventoryIn.setInventorySlotContents(index, itemstack1);
        }
        return false;
    }

    public static boolean captureItem(IInventory p_200114_0_, ItemEntity p_200114_1_) {
        boolean flag = false;
        ItemStack itemstack = p_200114_1_.getItem().copy();
        ItemStack itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(null, p_200114_0_, itemstack, null);
        if (itemstack1.isEmpty()) {
            flag = true;
            p_200114_1_.remove();
        } else {
            p_200114_1_.setItem(itemstack1);
        }
        return flag;
    }

    public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination, ItemStack stack, @Nullable Direction direction) {
        if (destination instanceof ISidedInventory && direction != null) {
            ISidedInventory isidedinventory = (ISidedInventory)destination;
            int[] aint = isidedinventory.getSlotsForFace(direction);
            for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
                stack = HopperTileEntity.insertStack(source, destination, stack, aint[k], direction);
            }
        } else {
            int i = destination.getSizeInventory();
            for (int j = 0; j < i && !stack.isEmpty(); ++j) {
                stack = HopperTileEntity.insertStack(source, destination, stack, j, direction);
            }
        }
        return stack;
    }

    private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, @Nullable Direction side) {
        if (!inventoryIn.isItemValidForSlot(index, stack)) {
            return false;
        }
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
    }

    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, Direction side) {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
    }

    private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack, int index, @Nullable Direction direction) {
        ItemStack itemstack = destination.getStackInSlot(index);
        if (HopperTileEntity.canInsertItemInSlot(destination, stack, index, direction)) {
            boolean flag = false;
            boolean flag1 = destination.isEmpty();
            if (itemstack.isEmpty()) {
                destination.setInventorySlotContents(index, stack);
                stack = ItemStack.EMPTY;
                flag = true;
            } else if (HopperTileEntity.canCombine(itemstack, stack)) {
                int i = stack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemstack.grow(j);
                boolean bl = flag = j > 0;
            }
            if (flag) {
                HopperTileEntity hoppertileentity1;
                if (flag1 && destination instanceof HopperTileEntity && !(hoppertileentity1 = (HopperTileEntity)destination).mayTransfer()) {
                    int k = 0;
                    if (source instanceof HopperTileEntity) {
                        HopperTileEntity hoppertileentity = (HopperTileEntity)source;
                        if (hoppertileentity1.tickedGameTime >= hoppertileentity.tickedGameTime) {
                            k = 1;
                        }
                    }
                    hoppertileentity1.setTransferCooldown(8 - k);
                }
                destination.markDirty();
            }
        }
        return stack;
    }

    @Nullable
    private IInventory getInventoryForHopperTransfer() {
        Direction direction = this.getBlockState().get(HopperBlock.FACING);
        return HopperTileEntity.getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
    }

    @Nullable
    public static IInventory getSourceInventory(IHopper hopper) {
        return HopperTileEntity.getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
    }

    public static List<ItemEntity> getCaptureItems(IHopper p_200115_0_) {
        return p_200115_0_.getCollectionArea().toBoundingBoxList().stream().flatMap(p_200110_1_ -> p_200115_0_.getWorld().getEntitiesWithinAABB(ItemEntity.class, p_200110_1_.offset(p_200115_0_.getXPos() - 0.5, p_200115_0_.getYPos() - 0.5, p_200115_0_.getZPos() - 0.5), EntityPredicates.IS_ALIVE).stream()).collect(Collectors.toList());
    }

    @Nullable
    public static IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
        return HopperTileEntity.getInventoryAtPosition(p_195484_0_, (double)p_195484_1_.getX() + 0.5, (double)p_195484_1_.getY() + 0.5, (double)p_195484_1_.getZ() + 0.5);
    }

    @Nullable
    public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
        List<Entity> list;
        TileEntity tileentity;
        IInventory iinventory = null;
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (block instanceof ISidedInventoryProvider) {
            iinventory = ((ISidedInventoryProvider)((Object)block)).createInventory(blockstate, worldIn, blockpos);
        } else if (block.isTileEntityProvider() && (tileentity = worldIn.getTileEntity(blockpos)) instanceof IInventory && (iinventory = (IInventory)((Object)tileentity)) instanceof ChestTileEntity && block instanceof ChestBlock) {
            iinventory = ChestBlock.getChestInventory((ChestBlock)block, blockstate, worldIn, blockpos, true);
        }
        if (iinventory == null && !(list = worldIn.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.HAS_INVENTORY)).isEmpty()) {
            iinventory = (IInventory)((Object)list.get(worldIn.rand.nextInt(list.size())));
        }
        return iinventory;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }
        if (stack1.getDamage() != stack2.getDamage()) {
            return false;
        }
        if (stack1.getCount() > stack1.getMaxStackSize()) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    @Override
    public double getXPos() {
        return (double)this.pos.getX() + 0.5;
    }

    @Override
    public double getYPos() {
        return (double)this.pos.getY() + 0.5;
    }

    @Override
    public double getZPos() {
        return (double)this.pos.getZ() + 0.5;
    }

    private void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    private boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean mayTransfer() {
        return this.transferCooldown > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.inventory = itemsIn;
    }

    public void onEntityCollision(Entity p_200113_1_) {
        if (p_200113_1_ instanceof ItemEntity) {
            BlockPos blockpos = this.getPos();
            if (VoxelShapes.compare(VoxelShapes.create(p_200113_1_.getBoundingBox().offset(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), this.getCollectionArea(), IBooleanFunction.AND)) {
                this.updateHopper(() -> HopperTileEntity.captureItem(this, (ItemEntity)p_200113_1_));
            }
        }
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new HopperContainer(id, player, this);
    }
}
