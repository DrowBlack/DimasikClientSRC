package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity
extends HangingEntity {
    private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.VARINT);
    private float itemDropChance = 1.0f;
    private boolean fixed;

    public ItemFrameEntity(EntityType<? extends ItemFrameEntity> p_i50224_1_, World world) {
        super((EntityType<? extends HangingEntity>)p_i50224_1_, world);
    }

    public ItemFrameEntity(World worldIn, BlockPos pos, Direction facing) {
        super(EntityType.ITEM_FRAME, worldIn, pos);
        this.updateFacingWithBoundingBox(facing);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.0f;
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
        this.getDataManager().register(ROTATION, 0);
    }

    @Override
    protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        this.facingDirection = facingDirectionIn;
        if (facingDirectionIn.getAxis().isHorizontal()) {
            this.rotationPitch = 0.0f;
            this.rotationYaw = this.facingDirection.getHorizontalIndex() * 90;
        } else {
            this.rotationPitch = -90 * facingDirectionIn.getAxisDirection().getOffset();
            this.rotationYaw = 0.0f;
        }
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.updateBoundingBox();
    }

    @Override
    protected void updateBoundingBox() {
        if (this.facingDirection != null) {
            double d0 = 0.46875;
            double d1 = (double)this.hangingPosition.getX() + 0.5 - (double)this.facingDirection.getXOffset() * 0.46875;
            double d2 = (double)this.hangingPosition.getY() + 0.5 - (double)this.facingDirection.getYOffset() * 0.46875;
            double d3 = (double)this.hangingPosition.getZ() + 0.5 - (double)this.facingDirection.getZOffset() * 0.46875;
            this.setRawPosition(d1, d2, d3);
            double d4 = this.getWidthPixels();
            double d5 = this.getHeightPixels();
            double d6 = this.getWidthPixels();
            Direction.Axis direction$axis = this.facingDirection.getAxis();
            switch (direction$axis) {
                case X: {
                    d4 = 1.0;
                    break;
                }
                case Y: {
                    d5 = 1.0;
                    break;
                }
                case Z: {
                    d6 = 1.0;
                }
            }
            this.setBoundingBox(new AxisAlignedBB(d1 - (d4 /= 32.0), d2 - (d5 /= 32.0), d3 - (d6 /= 32.0), d1 + d4, d2 + d5, d3 + d6));
        }
    }

    @Override
    public boolean onValidSurface() {
        if (this.fixed) {
            return true;
        }
        if (!this.world.hasNoCollisions(this)) {
            return false;
        }
        BlockState blockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
        return blockstate.getMaterial().isSolid() || this.facingDirection.getAxis().isHorizontal() && RedstoneDiodeBlock.isDiode(blockstate) ? this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty() : false;
    }

    @Override
    public void move(MoverType typeIn, Vector3d pos) {
        if (!this.fixed) {
            super.move(typeIn, pos);
        }
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (!this.fixed) {
            super.addVelocity(x, y, z);
        }
    }

    @Override
    public float getCollisionBorderSize() {
        return 0.0f;
    }

    @Override
    public void onKillCommand() {
        this.removeItem(this.getDisplayedItem());
        super.onKillCommand();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.fixed) {
            return source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer() ? false : super.attackEntityFrom(source, amount);
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!source.isExplosion() && !this.getDisplayedItem().isEmpty()) {
            if (!this.world.isRemote) {
                this.dropItemOrSelf(source.getTrueSource(), false);
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public int getWidthPixels() {
        return 12;
    }

    @Override
    public int getHeightPixels() {
        return 12;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = 16.0;
        return distance < (d0 = d0 * 64.0 * ItemFrameEntity.getRenderDistanceWeight()) * d0;
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.dropItemOrSelf(brokenEntity, true);
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }

    private void dropItemOrSelf(@Nullable Entity entityIn, boolean p_146065_2_) {
        if (!this.fixed) {
            ItemStack itemstack = this.getDisplayedItem();
            this.setDisplayedItem(ItemStack.EMPTY);
            if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                if (entityIn == null) {
                    this.removeItem(itemstack);
                }
            } else {
                if (entityIn instanceof PlayerEntity) {
                    PlayerEntity playerentity = (PlayerEntity)entityIn;
                    if (playerentity.abilities.isCreativeMode) {
                        this.removeItem(itemstack);
                        return;
                    }
                }
                if (p_146065_2_) {
                    this.entityDropItem(Items.ITEM_FRAME);
                }
                if (!itemstack.isEmpty()) {
                    itemstack = itemstack.copy();
                    this.removeItem(itemstack);
                    if (this.rand.nextFloat() < this.itemDropChance) {
                        this.entityDropItem(itemstack);
                    }
                }
            }
        }
    }

    private void removeItem(ItemStack stack) {
        if (stack.getItem() == Items.FILLED_MAP) {
            MapData mapdata = FilledMapItem.getMapData(stack, this.world);
            mapdata.removeItemFrame(this.hangingPosition, this.getEntityId());
            mapdata.setDirty(true);
        }
        stack.setAttachedEntity(null);
    }

    public ItemStack getDisplayedItem() {
        return this.getDataManager().get(ITEM);
    }

    public void setDisplayedItem(ItemStack stack) {
        this.setDisplayedItemWithUpdate(stack, true);
    }

    public void setDisplayedItemWithUpdate(ItemStack stack, boolean p_174864_2_) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);
            stack.setAttachedEntity(this);
        }
        this.getDataManager().set(ITEM, stack);
        if (!stack.isEmpty()) {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (p_174864_2_ && this.hangingPosition != null) {
            this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
        }
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        if (inventorySlot == 0) {
            this.setDisplayedItem(itemStackIn);
            return true;
        }
        return false;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        ItemStack itemstack;
        if (key.equals(ITEM) && !(itemstack = this.getDisplayedItem()).isEmpty() && itemstack.getItemFrame() != this) {
            itemstack.setAttachedEntity(this);
        }
    }

    public int getRotation() {
        return this.getDataManager().get(ROTATION);
    }

    public void setItemRotation(int rotationIn) {
        this.setRotation(rotationIn, true);
    }

    private void setRotation(int rotationIn, boolean p_174865_2_) {
        this.getDataManager().set(ROTATION, rotationIn % 8);
        if (p_174865_2_ && this.hangingPosition != null) {
            this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (!this.getDisplayedItem().isEmpty()) {
            compound.put("Item", this.getDisplayedItem().write(new CompoundNBT()));
            compound.putByte("ItemRotation", (byte)this.getRotation());
            compound.putFloat("ItemDropChance", this.itemDropChance);
        }
        compound.putByte("Facing", (byte)this.facingDirection.getIndex());
        compound.putBoolean("Invisible", this.isInvisible());
        compound.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        CompoundNBT compoundnbt = compound.getCompound("Item");
        if (compoundnbt != null && !compoundnbt.isEmpty()) {
            ItemStack itemstack1;
            ItemStack itemstack = ItemStack.read(compoundnbt);
            if (itemstack.isEmpty()) {
                PRIVATE_LOGGER.warn("Unable to load item from: {}", (Object)compoundnbt);
            }
            if (!(itemstack1 = this.getDisplayedItem()).isEmpty() && !ItemStack.areItemStacksEqual(itemstack, itemstack1)) {
                this.removeItem(itemstack1);
            }
            this.setDisplayedItemWithUpdate(itemstack, false);
            this.setRotation(compound.getByte("ItemRotation"), false);
            if (compound.contains("ItemDropChance", 99)) {
                this.itemDropChance = compound.getFloat("ItemDropChance");
            }
        }
        this.updateFacingWithBoundingBox(Direction.byIndex(compound.getByte("Facing")));
        this.setInvisible(compound.getBoolean("Invisible"));
        this.fixed = compound.getBoolean("Fixed");
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        boolean flag1;
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = !this.getDisplayedItem().isEmpty();
        boolean bl = flag1 = !itemstack.isEmpty();
        if (this.fixed) {
            return ActionResultType.PASS;
        }
        if (!this.world.isRemote) {
            if (!flag) {
                if (flag1 && !this.removed) {
                    this.setDisplayedItem(itemstack);
                    if (!player.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                }
            } else {
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
                this.setItemRotation(this.getRotation() + 1);
            }
            return ActionResultType.CONSUME;
        }
        return !flag && !flag1 ? ActionResultType.PASS : ActionResultType.SUCCESS;
    }

    public int getAnalogOutput() {
        return this.getDisplayedItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this, this.getType(), this.facingDirection.getIndex(), this.getHangingPosition());
    }
}
