package net.minecraft.entity.item.minecart;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class ContainerMinecartEntity
extends AbstractMinecartEntity
implements IInventory,
INamedContainerProvider {
    private NonNullList<ItemStack> minecartContainerItems = NonNullList.withSize(36, ItemStack.EMPTY);
    private boolean dropContentsWhenDead = true;
    @Nullable
    private ResourceLocation lootTable;
    private long lootTableSeed;

    protected ContainerMinecartEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    protected ContainerMinecartEntity(EntityType<?> type, double x, double y, double z, World world) {
        super(type, world, x, y, z);
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            Entity entity;
            InventoryHelper.dropInventoryItems(this.world, this, (IInventory)this);
            if (!this.world.isRemote && (entity = source.getImmediateSource()) != null && entity.getType() == EntityType.PLAYER) {
                PiglinTasks.func_234478_a_((PlayerEntity)entity, true);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.minecartContainerItems) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        this.addLoot(null);
        return this.minecartContainerItems.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        this.addLoot(null);
        return ItemStackHelper.getAndSplit(this.minecartContainerItems, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        this.addLoot(null);
        ItemStack itemstack = this.minecartContainerItems.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.minecartContainerItems.set(index, ItemStack.EMPTY);
        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.addLoot(null);
        this.minecartContainerItems.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (inventorySlot >= 0 && inventorySlot < this.getSizeInventory()) {
            this.setInventorySlotContents(inventorySlot, itemStackIn);
            return true;
        }
        return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.removed) {
            return false;
        }
        return !(player.getDistanceSq(this) > 64.0);
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerWorld server) {
        this.dropContentsWhenDead = false;
        return super.changeDimension(server);
    }

    @Override
    public void remove() {
        if (!this.world.isRemote && this.dropContentsWhenDead) {
            InventoryHelper.dropInventoryItems(this.world, this, (IInventory)this);
        }
        super.remove();
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.lootTable != null) {
            compound.putString("LootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                compound.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            ItemStackHelper.saveAllItems(compound, this.minecartContainerItems);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (compound.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compound.getString("LootTable"));
            this.lootTableSeed = compound.getLong("LootTableSeed");
        } else {
            ItemStackHelper.loadAllItems(compound, this.minecartContainerItems);
        }
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        player.openContainer(this);
        if (!player.world.isRemote) {
            PiglinTasks.func_234478_a_(player, true);
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void applyDrag() {
        float f = 0.98f;
        if (this.lootTable == null) {
            int i = 15 - Container.calcRedstoneFromInventory(this);
            f += (float)i * 0.001f;
        }
        this.setMotion(this.getMotion().mul(f, 0.0, f));
    }

    public void addLoot(@Nullable PlayerEntity player) {
        if (this.lootTable != null && this.world.getServer() != null) {
            LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
            if (player instanceof ServerPlayerEntity) {
                CriteriaTriggers.PLAYER_GENERATES_CONTAINER_LOOT.test((ServerPlayerEntity)player, this.lootTable);
            }
            this.lootTable = null;
            LootContext.Builder lootcontext$builder = new LootContext.Builder((ServerWorld)this.world).withParameter(LootParameters.field_237457_g_, this.getPositionVec()).withSeed(this.lootTableSeed);
            if (player != null) {
                lootcontext$builder.withLuck(player.getLuck()).withParameter(LootParameters.THIS_ENTITY, player);
            }
            loottable.fillInventory(this, lootcontext$builder.build(LootParameterSets.CHEST));
        }
    }

    @Override
    public void clear() {
        this.addLoot(null);
        this.minecartContainerItems.clear();
    }

    public void setLootTable(ResourceLocation lootTableIn, long lootTableSeedIn) {
        this.lootTable = lootTableIn;
        this.lootTableSeed = lootTableSeedIn;
    }

    @Override
    @Nullable
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        if (this.lootTable != null && p_createMenu_3_.isSpectator()) {
            return null;
        }
        this.addLoot(p_createMenu_2_.player);
        return this.createContainer(p_createMenu_1_, p_createMenu_2_);
    }

    protected abstract Container createContainer(int var1, PlayerInventory var2);
}
