package net.minecraft.entity.item;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ItemEntity
extends Entity {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemEntity.class, DataSerializers.ITEMSTACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    public final float hoverStart;

    public ItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World world) {
        super(p_i50217_1_, world);
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0);
    }

    public ItemEntity(World worldIn, double x, double y, double z) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, worldIn);
        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0f;
        this.setMotion(this.rand.nextDouble() * 0.2 - 0.1, 0.2, this.rand.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    private ItemEntity(ItemEntity p_i231561_1_) {
        super(p_i231561_1_.getType(), p_i231561_1_.world);
        this.setItem(p_i231561_1_.getItem().copy());
        this.copyLocationAndAnglesFrom(p_i231561_1_);
        this.age = p_i231561_1_.age;
        this.hoverStart = p_i231561_1_.hoverStart;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (this.getItem().isEmpty()) {
            this.remove();
        } else {
            double d0;
            int i;
            super.tick();
            if (this.pickupDelay > 0 && this.pickupDelay != Short.MAX_VALUE) {
                --this.pickupDelay;
            }
            this.prevPosX = this.getPosX();
            this.prevPosY = this.getPosY();
            this.prevPosZ = this.getPosZ();
            Vector3d vector3d = this.getMotion();
            float f = this.getEyeHeight() - 0.11111111f;
            if (this.isInWater() && this.func_233571_b_(FluidTags.WATER) > (double)f) {
                this.applyFloatMotion();
            } else if (this.isInLava() && this.func_233571_b_(FluidTags.LAVA) > (double)f) {
                this.func_234274_v_();
            } else if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0, -0.04, 0.0));
            }
            if (this.world.isRemote) {
                this.noClip = false;
            } else {
                boolean bl = this.noClip = !this.world.hasNoCollisions(this);
                if (this.noClip) {
                    this.pushOutOfBlocks(this.getPosX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getPosZ());
                }
            }
            if (!this.onGround || ItemEntity.horizontalMag(this.getMotion()) > (double)1.0E-5f || (this.ticksExisted + this.getEntityId()) % 4 == 0) {
                this.move(MoverType.SELF, this.getMotion());
                float f1 = 0.98f;
                if (this.onGround) {
                    f1 = this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() - 1.0, this.getPosZ())).getBlock().getSlipperiness() * 0.98f;
                }
                this.setMotion(this.getMotion().mul(f1, 0.98, f1));
                if (this.onGround) {
                    Vector3d vector3d1 = this.getMotion();
                    if (vector3d1.y < 0.0) {
                        this.setMotion(vector3d1.mul(1.0, -0.5, 1.0));
                    }
                }
            }
            boolean flag = MathHelper.floor(this.prevPosX) != MathHelper.floor(this.getPosX()) || MathHelper.floor(this.prevPosY) != MathHelper.floor(this.getPosY()) || MathHelper.floor(this.prevPosZ) != MathHelper.floor(this.getPosZ());
            int n = i = flag ? 2 : 40;
            if (this.ticksExisted % i == 0) {
                if (this.world.getFluidState(this.getPosition()).isTagged(FluidTags.LAVA) && !this.isImmuneToFire()) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.rand.nextFloat() * 0.4f);
                }
                if (!this.world.isRemote && this.func_213857_z()) {
                    this.searchForOtherItemsNearby();
                }
            }
            if (this.age != Short.MIN_VALUE) {
                ++this.age;
            }
            this.isAirBorne |= this.func_233566_aG_();
            if (!this.world.isRemote && (d0 = this.getMotion().subtract(vector3d).lengthSquared()) > 0.01) {
                this.isAirBorne = true;
            }
            if (!this.world.isRemote && this.age >= 6000) {
                this.remove();
            }
        }
    }

    private void applyFloatMotion() {
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x * (double)0.99f, vector3d.y + (double)(vector3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vector3d.z * (double)0.99f);
    }

    private void func_234274_v_() {
        Vector3d vector3d = this.getMotion();
        this.setMotion(vector3d.x * (double)0.95f, vector3d.y + (double)(vector3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vector3d.z * (double)0.95f);
    }

    private void searchForOtherItemsNearby() {
        if (this.func_213857_z()) {
            for (ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.5, 0.0, 0.5), p_213859_1_ -> p_213859_1_ != this && p_213859_1_.func_213857_z())) {
                if (!itementity.func_213857_z()) continue;
                this.func_226530_a_(itementity);
                if (!this.removed) continue;
                break;
            }
        }
    }

    private boolean func_213857_z() {
        ItemStack itemstack = this.getItem();
        return this.isAlive() && this.pickupDelay != Short.MAX_VALUE && this.age != Short.MIN_VALUE && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
    }

    private void func_226530_a_(ItemEntity item) {
        ItemStack itemstack = this.getItem();
        ItemStack itemstack1 = item.getItem();
        if (Objects.equals(this.getOwnerId(), item.getOwnerId()) && ItemEntity.canMergeStacks(itemstack, itemstack1)) {
            if (itemstack1.getCount() < itemstack.getCount()) {
                ItemEntity.func_213858_a(this, itemstack, item, itemstack1);
            } else {
                ItemEntity.func_213858_a(item, itemstack1, this, itemstack);
            }
        }
    }

    public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        if (stack2.getItem() != stack1.getItem()) {
            return false;
        }
        if (stack2.getCount() + stack1.getCount() > stack2.getMaxStackSize()) {
            return false;
        }
        if (stack2.hasTag() ^ stack1.hasTag()) {
            return false;
        }
        return !stack2.hasTag() || stack2.getTag().equals(stack1.getTag());
    }

    public static ItemStack mergeStacks(ItemStack stack1, ItemStack stack2, int p_226533_2_) {
        int i = Math.min(Math.min(stack1.getMaxStackSize(), p_226533_2_) - stack1.getCount(), stack2.getCount());
        ItemStack itemstack = stack1.copy();
        itemstack.grow(i);
        stack2.shrink(i);
        return itemstack;
    }

    private static void func_226531_a_(ItemEntity entity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemstack = ItemEntity.mergeStacks(stack1, stack2, 64);
        entity.setItem(itemstack);
    }

    private static void func_213858_a(ItemEntity entity1, ItemStack stack1, ItemEntity entity2, ItemStack stack2) {
        ItemEntity.func_226531_a_(entity1, stack1, stack2);
        entity1.pickupDelay = Math.max(entity1.pickupDelay, entity2.pickupDelay);
        entity1.age = Math.min(entity1.age, entity2.age);
        if (stack2.isEmpty()) {
            entity2.remove();
        }
    }

    @Override
    public boolean isImmuneToFire() {
        return this.getItem().getItem().isImmuneToFire() || super.isImmuneToFire();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && source.isExplosion()) {
            return false;
        }
        if (!this.getItem().getItem().isDamageable(source)) {
            return false;
        }
        this.markVelocityChanged();
        this.health = (int)((float)this.health - amount);
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putShort("Health", (short)this.health);
        compound.putShort("Age", (short)this.age);
        compound.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrowerId() != null) {
            compound.putUniqueId("Thrower", this.getThrowerId());
        }
        if (this.getOwnerId() != null) {
            compound.putUniqueId("Owner", this.getOwnerId());
        }
        if (!this.getItem().isEmpty()) {
            compound.put("Item", this.getItem().write(new CompoundNBT()));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");
        if (compound.contains("PickupDelay")) {
            this.pickupDelay = compound.getShort("PickupDelay");
        }
        if (compound.hasUniqueId("Owner")) {
            this.owner = compound.getUniqueId("Owner");
        }
        if (compound.hasUniqueId("Thrower")) {
            this.thrower = compound.getUniqueId("Thrower");
        }
        CompoundNBT compoundnbt = compound.getCompound("Item");
        this.setItem(ItemStack.read(compoundnbt));
        if (this.getItem().isEmpty()) {
            this.remove();
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        if (!this.world.isRemote) {
            ItemStack itemstack = this.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();
            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(entityIn.getUniqueID())) && entityIn.inventory.addItemStackToInventory(itemstack)) {
                entityIn.onItemPickup(this, i);
                if (itemstack.isEmpty()) {
                    this.remove();
                    itemstack.setCount(i);
                }
                entityIn.addStat(Stats.ITEM_PICKED_UP.get(item), i);
                entityIn.triggerItemPickupTrigger(this);
            }
        }
    }

    @Override
    public ITextComponent getName() {
        ITextComponent itextcomponent = this.getCustomName();
        return itextcomponent != null ? itextcomponent : new TranslationTextComponent(this.getItem().getTranslationKey());
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    @Nullable
    public Entity changeDimension(ServerWorld server) {
        Entity entity = super.changeDimension(server);
        if (!this.world.isRemote && entity instanceof ItemEntity) {
            ((ItemEntity)entity).searchForOtherItemsNearby();
        }
        return entity;
    }

    public ItemStack getItem() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (ITEM.equals(key)) {
            this.getItem().setAttachedEntity(this);
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner;
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.owner = ownerId;
    }

    @Nullable
    public UUID getThrowerId() {
        return this.thrower;
    }

    public void setThrowerId(@Nullable UUID throwerId) {
        this.thrower = throwerId;
    }

    public int getAge() {
        return this.age;
    }

    public void setDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void setNoPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = Short.MAX_VALUE;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void setNoDespawn() {
        this.age = -6000;
    }

    public void makeFakeItem() {
        this.setInfinitePickupDelay();
        this.age = 5999;
    }

    public float getItemHover(float partialTicks) {
        return ((float)this.getAge() + partialTicks) / 20.0f + this.hoverStart;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    public ItemEntity func_234273_t_() {
        return new ItemEntity(this);
    }
}
