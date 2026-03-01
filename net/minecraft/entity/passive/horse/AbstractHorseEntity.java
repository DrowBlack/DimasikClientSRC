package net.minecraft.entity.passive.horse;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractHorseEntity
extends AnimalEntity
implements IInventoryChangedListener,
IJumpingMount,
IEquipable {
    private static final Predicate<LivingEntity> IS_HORSE_BREEDING = p_213617_0_ -> p_213617_0_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_213617_0_).isBreeding();
    private static final EntityPredicate MOMMY_TARGETING = new EntityPredicate().setDistance(16.0).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired().setCustomPredicate(IS_HORSE_BREEDING);
    private static final Ingredient field_234235_bE_ = Ingredient.fromItems(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final DataParameter<Byte> STATUS = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(AbstractHorseEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private int eatingCounter;
    private int openMouthCounter;
    private int jumpRearingCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean horseJumping;
    protected Inventory horseChest;
    protected int temper;
    protected float jumpPower;
    private boolean allowStandSliding;
    private float headLean;
    private float prevHeadLean;
    private float rearingAmount;
    private float prevRearingAmount;
    private float mouthOpenness;
    private float prevMouthOpenness;
    protected boolean canGallop = true;
    protected int gallopTime;

    protected AbstractHorseEntity(EntityType<? extends AbstractHorseEntity> type, World worldIn) {
        super((EntityType<? extends AnimalEntity>)type, worldIn);
        this.stepHeight = 1.0f;
        this.initHorseChest();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, AbstractHorseEntity.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.initExtraAI();
    }

    protected void initExtraAI() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(STATUS, (byte)0);
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    protected boolean getHorseWatchableBoolean(int p_110233_1_) {
        return (this.dataManager.get(STATUS) & p_110233_1_) != 0;
    }

    protected void setHorseWatchableBoolean(int p_110208_1_, boolean p_110208_2_) {
        byte b0 = this.dataManager.get(STATUS);
        if (p_110208_2_) {
            this.dataManager.set(STATUS, (byte)(b0 | p_110208_1_));
        } else {
            this.dataManager.set(STATUS, (byte)(b0 & ~p_110208_1_));
        }
    }

    public boolean isTame() {
        return this.getHorseWatchableBoolean(2);
    }

    @Nullable
    public UUID getOwnerUniqueId() {
        return this.dataManager.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerUniqueId(@Nullable UUID uniqueId) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(uniqueId));
    }

    public boolean isHorseJumping() {
        return this.horseJumping;
    }

    public void setHorseTamed(boolean tamed) {
        this.setHorseWatchableBoolean(2, tamed);
    }

    public void setHorseJumping(boolean jumping) {
        this.horseJumping = jumping;
    }

    @Override
    protected void onLeashDistance(float distance) {
        if (distance > 6.0f && this.isEatingHaystack()) {
            this.setEatingHaystack(false);
        }
    }

    public boolean isEatingHaystack() {
        return this.getHorseWatchableBoolean(16);
    }

    public boolean isRearing() {
        return this.getHorseWatchableBoolean(32);
    }

    public boolean isBreeding() {
        return this.getHorseWatchableBoolean(8);
    }

    public void setBreeding(boolean breeding) {
        this.setHorseWatchableBoolean(8, breeding);
    }

    @Override
    public boolean func_230264_L__() {
        return this.isAlive() && !this.isChild() && this.isTame();
    }

    @Override
    public void func_230266_a_(@Nullable SoundCategory p_230266_1_) {
        this.horseChest.setInventorySlotContents(0, new ItemStack(Items.SADDLE));
        if (p_230266_1_ != null) {
            this.world.playMovingSound(null, this, SoundEvents.ENTITY_HORSE_SADDLE, p_230266_1_, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isHorseSaddled() {
        return this.getHorseWatchableBoolean(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int temperIn) {
        this.temper = temperIn;
    }

    public int increaseTemper(int p_110198_1_) {
        int i = MathHelper.clamp(this.getTemper() + p_110198_1_, 0, this.getMaxTemper());
        this.setTemper(i);
        return i;
    }

    @Override
    public boolean canBePushed() {
        return !this.isBeingRidden();
    }

    private void eatingHorse() {
        SoundEvent soundevent;
        this.openHorseMouth();
        if (!this.isSilent() && (soundevent = this.func_230274_fe_()) != null) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), soundevent, this.getSoundCategory(), 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        int i;
        if (distance > 1.0f) {
            this.playSound(SoundEvents.ENTITY_HORSE_LAND, 0.4f, 1.0f);
        }
        if ((i = this.calculateFallDamage(distance, damageMultiplier)) <= 0) {
            return false;
        }
        this.attackEntityFrom(DamageSource.FALL, i);
        if (this.isBeingRidden()) {
            for (Entity entity : this.getRecursivePassengers()) {
                entity.attackEntityFrom(DamageSource.FALL, i);
            }
        }
        this.playFallSound();
        return true;
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return MathHelper.ceil((distance * 0.5f - 3.0f) * damageMultiplier);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void initHorseChest() {
        Inventory inventory = this.horseChest;
        this.horseChest = new Inventory(this.getInventorySize());
        if (inventory != null) {
            inventory.removeListener(this);
            int i = Math.min(inventory.getSizeInventory(), this.horseChest.getSizeInventory());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventory.getStackInSlot(j);
                if (itemstack.isEmpty()) continue;
                this.horseChest.setInventorySlotContents(j, itemstack.copy());
            }
        }
        this.horseChest.addListener(this);
        this.func_230275_fc_();
    }

    protected void func_230275_fc_() {
        if (!this.world.isRemote) {
            this.setHorseWatchableBoolean(4, !this.horseChest.getStackInSlot(0).isEmpty());
        }
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        boolean flag = this.isHorseSaddled();
        this.func_230275_fc_();
        if (this.ticksExisted > 20 && !flag && this.isHorseSaddled()) {
            this.playSound(SoundEvents.ENTITY_HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getHorseJumpStrength() {
        return this.getAttributeValue(Attributes.HORSE_JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEvent func_230274_fe_() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextInt(3) == 0) {
            this.makeHorseRear();
        }
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.rand.nextInt(10) == 0 && !this.isMovementBlocked()) {
            this.makeHorseRear();
        }
        return null;
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        this.makeHorseRear();
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if (!blockIn.getMaterial().isLiquid()) {
            BlockState blockstate = this.world.getBlockState(pos.up());
            SoundType soundtype = blockIn.getSoundType();
            if (blockstate.isIn(Blocks.SNOW)) {
                soundtype = blockstate.getSoundType();
            }
            if (this.isBeingRidden() && this.canGallop) {
                ++this.gallopTime;
                if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
                    this.playGallopSound(soundtype);
                } else if (this.gallopTime <= 5) {
                    this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15f, soundtype.getPitch());
                }
            } else if (soundtype == SoundType.WOOD) {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP_WOOD, soundtype.getVolume() * 0.15f, soundtype.getPitch());
            } else {
                this.playSound(SoundEvents.ENTITY_HORSE_STEP, soundtype.getVolume() * 0.15f, soundtype.getPitch());
            }
        }
    }

    protected void playGallopSound(SoundType p_190680_1_) {
        this.playSound(SoundEvents.ENTITY_HORSE_GALLOP, p_190680_1_.getVolume() * 0.15f, p_190680_1_.getPitch());
    }

    public static AttributeModifierMap.MutableAttribute func_234237_fg_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.HORSE_JUMP_STRENGTH).createMutableAttribute(Attributes.MAX_HEALTH, 53.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.225f);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getTalkInterval() {
        return 400;
    }

    public void openGUI(PlayerEntity playerEntity) {
        if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity)) && this.isTame()) {
            playerEntity.openHorseInventory(this, this.horseChest);
        }
    }

    public ActionResultType func_241395_b_(PlayerEntity p_241395_1_, ItemStack p_241395_2_) {
        boolean flag = this.handleEating(p_241395_1_, p_241395_2_);
        if (!p_241395_1_.abilities.isCreativeMode) {
            p_241395_2_.shrink(1);
        }
        if (this.world.isRemote) {
            return ActionResultType.CONSUME;
        }
        return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    protected boolean handleEating(PlayerEntity player, ItemStack stack) {
        boolean flag = false;
        float f = 0.0f;
        int i = 0;
        int j = 0;
        Item item = stack.getItem();
        if (item == Items.WHEAT) {
            f = 2.0f;
            i = 20;
            j = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0f;
            i = 30;
            j = 3;
        } else if (item == Blocks.HAY_BLOCK.asItem()) {
            f = 20.0f;
            i = 180;
        } else if (item == Items.APPLE) {
            f = 3.0f;
            i = 60;
            j = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0f;
            i = 60;
            j = 5;
            if (!this.world.isRemote && this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
                flag = true;
                this.setInLove(player);
            }
        } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0f;
            i = 240;
            j = 10;
            if (!this.world.isRemote && this.isTame() && this.getGrowingAge() == 0 && !this.isInLove()) {
                flag = true;
                this.setInLove(player);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && f > 0.0f) {
            this.heal(f);
            flag = true;
        }
        if (this.isChild() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0), this.getPosYRandom() + 0.5, this.getPosZRandom(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isRemote) {
                this.addGrowth(i);
            }
            flag = true;
        }
        if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            flag = true;
            if (!this.world.isRemote) {
                this.increaseTemper(j);
            }
        }
        if (flag) {
            this.eatingHorse();
        }
        return flag;
    }

    protected void mountTo(PlayerEntity player) {
        this.setEatingHaystack(false);
        this.setRearing(false);
        if (!this.world.isRemote) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            player.startRiding(this);
        }
    }

    @Override
    protected boolean isMovementBlocked() {
        return super.isMovementBlocked() && this.isBeingRidden() && this.isHorseSaddled() || this.isEatingHaystack() || this.isRearing();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return field_234235_bE_.test(stack);
    }

    private void moveTail() {
        this.tailCounter = 1;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.horseChest != null) {
            for (int i = 0; i < this.horseChest.getSizeInventory(); ++i) {
                ItemStack itemstack = this.horseChest.getStackInSlot(i);
                if (itemstack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemstack)) continue;
                this.entityDropItem(itemstack);
            }
        }
    }

    @Override
    public void livingTick() {
        if (this.rand.nextInt(200) == 0) {
            this.moveTail();
        }
        super.livingTick();
        if (!this.world.isRemote && this.isAlive()) {
            if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0f);
            }
            if (this.canEatGrass()) {
                if (!this.isEatingHaystack() && !this.isBeingRidden() && this.rand.nextInt(300) == 0 && this.world.getBlockState(this.getPosition().down()).isIn(Blocks.GRASS_BLOCK)) {
                    this.setEatingHaystack(true);
                }
                if (this.isEatingHaystack() && ++this.eatingCounter > 50) {
                    this.eatingCounter = 0;
                    this.setEatingHaystack(false);
                }
            }
            this.followMother();
        }
    }

    protected void followMother() {
        AbstractHorseEntity livingentity;
        if (this.isBreeding() && this.isChild() && !this.isEatingHaystack() && (livingentity = this.world.getClosestEntityWithinAABB(AbstractHorseEntity.class, MOMMY_TARGETING, this, this.getPosX(), this.getPosY(), this.getPosZ(), this.getBoundingBox().grow(16.0))) != null && this.getDistanceSq(livingentity) > 4.0) {
            this.navigator.getPathToEntity(livingentity, 0);
        }
    }

    public boolean canEatGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.openMouthCounter > 0 && ++this.openMouthCounter > 30) {
            this.openMouthCounter = 0;
            this.setHorseWatchableBoolean(64, false);
        }
        if ((this.canPassengerSteer() || this.isServerWorld()) && this.jumpRearingCounter > 0 && ++this.jumpRearingCounter > 20) {
            this.jumpRearingCounter = 0;
            this.setRearing(false);
        }
        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }
        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }
        this.prevHeadLean = this.headLean;
        if (this.isEatingHaystack()) {
            this.headLean += (1.0f - this.headLean) * 0.4f + 0.05f;
            if (this.headLean > 1.0f) {
                this.headLean = 1.0f;
            }
        } else {
            this.headLean += (0.0f - this.headLean) * 0.4f - 0.05f;
            if (this.headLean < 0.0f) {
                this.headLean = 0.0f;
            }
        }
        this.prevRearingAmount = this.rearingAmount;
        if (this.isRearing()) {
            this.prevHeadLean = this.headLean = 0.0f;
            this.rearingAmount += (1.0f - this.rearingAmount) * 0.4f + 0.05f;
            if (this.rearingAmount > 1.0f) {
                this.rearingAmount = 1.0f;
            }
        } else {
            this.allowStandSliding = false;
            this.rearingAmount += (0.8f * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6f - 0.05f;
            if (this.rearingAmount < 0.0f) {
                this.rearingAmount = 0.0f;
            }
        }
        this.prevMouthOpenness = this.mouthOpenness;
        if (this.getHorseWatchableBoolean(64)) {
            this.mouthOpenness += (1.0f - this.mouthOpenness) * 0.7f + 0.05f;
            if (this.mouthOpenness > 1.0f) {
                this.mouthOpenness = 1.0f;
            }
        } else {
            this.mouthOpenness += (0.0f - this.mouthOpenness) * 0.7f - 0.05f;
            if (this.mouthOpenness < 0.0f) {
                this.mouthOpenness = 0.0f;
            }
        }
    }

    private void openHorseMouth() {
        if (!this.world.isRemote) {
            this.openMouthCounter = 1;
            this.setHorseWatchableBoolean(64, true);
        }
    }

    public void setEatingHaystack(boolean p_110227_1_) {
        this.setHorseWatchableBoolean(16, p_110227_1_);
    }

    public void setRearing(boolean rearing) {
        if (rearing) {
            this.setEatingHaystack(false);
        }
        this.setHorseWatchableBoolean(32, rearing);
    }

    private void makeHorseRear() {
        if (this.canPassengerSteer() || this.isServerWorld()) {
            this.jumpRearingCounter = 1;
            this.setRearing(true);
        }
    }

    public void makeMad() {
        if (!this.isRearing()) {
            this.makeHorseRear();
            SoundEvent soundevent = this.getAngrySound();
            if (soundevent != null) {
                this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public boolean setTamedBy(PlayerEntity player) {
        this.setOwnerUniqueId(player.getUniqueID());
        this.setHorseTamed(true);
        if (player instanceof ServerPlayerEntity) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
        }
        this.world.setEntityState(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isAlive()) {
            if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled()) {
                LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
                this.prevRotationYaw = this.rotationYaw = livingentity.rotationYaw;
                this.rotationPitch = livingentity.rotationPitch * 0.5f;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
                float f = livingentity.moveStrafing * 0.5f;
                float f1 = livingentity.moveForward;
                if (f1 <= 0.0f) {
                    f1 *= 0.25f;
                    this.gallopTime = 0;
                }
                if (this.onGround && this.jumpPower == 0.0f && this.isRearing() && !this.allowStandSliding) {
                    f = 0.0f;
                    f1 = 0.0f;
                }
                if (this.jumpPower > 0.0f && !this.isHorseJumping() && this.onGround) {
                    double d0 = this.getHorseJumpStrength() * (double)this.jumpPower * (double)this.getJumpFactor();
                    double d1 = this.isPotionActive(Effects.JUMP_BOOST) ? d0 + (double)((float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1f) : d0;
                    Vector3d vector3d = this.getMotion();
                    this.setMotion(vector3d.x, d1, vector3d.z);
                    this.setHorseJumping(true);
                    this.isAirBorne = true;
                    if (f1 > 0.0f) {
                        float f2 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180));
                        float f3 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180));
                        this.setMotion(this.getMotion().add(-0.4f * f2 * this.jumpPower, 0.0, 0.4f * f3 * this.jumpPower));
                    }
                    this.jumpPower = 0.0f;
                }
                this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1f;
                if (this.canPassengerSteer()) {
                    this.setAIMoveSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    super.travel(new Vector3d(f, travelVector.y, f1));
                } else if (livingentity instanceof PlayerEntity) {
                    this.setMotion(Vector3d.ZERO);
                }
                if (this.onGround) {
                    this.jumpPower = 0.0f;
                    this.setHorseJumping(false);
                }
                this.func_233629_a_(this, false);
            } else {
                this.jumpMovementFactor = 0.02f;
                super.travel(travelVector);
            }
        }
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("EatingHaystack", this.isEatingHaystack());
        compound.putBoolean("Bred", this.isBreeding());
        compound.putInt("Temper", this.getTemper());
        compound.putBoolean("Tame", this.isTame());
        if (this.getOwnerUniqueId() != null) {
            compound.putUniqueId("Owner", this.getOwnerUniqueId());
        }
        if (!this.horseChest.getStackInSlot(0).isEmpty()) {
            compound.put("SaddleItem", this.horseChest.getStackInSlot(0).write(new CompoundNBT()));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        ItemStack itemstack;
        UUID uuid;
        super.readAdditional(compound);
        this.setEatingHaystack(compound.getBoolean("EatingHaystack"));
        this.setBreeding(compound.getBoolean("Bred"));
        this.setTemper(compound.getInt("Temper"));
        this.setHorseTamed(compound.getBoolean("Tame"));
        if (compound.hasUniqueId("Owner")) {
            uuid = compound.getUniqueId("Owner");
        } else {
            String s = compound.getString("Owner");
            uuid = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s);
        }
        if (uuid != null) {
            this.setOwnerUniqueId(uuid);
        }
        if (compound.contains("SaddleItem", 10) && (itemstack = ItemStack.read(compound.getCompound("SaddleItem"))).getItem() == Items.SADDLE) {
            this.horseChest.setInventorySlotContents(0, itemstack);
        }
        this.func_230275_fc_();
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

    protected boolean canMate() {
        return !this.isBeingRidden() && !this.isPassenger() && this.isTame() && !this.isChild() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Override
    @Nullable
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    protected void setOffspringAttributes(AgeableEntity p_190681_1_, AbstractHorseEntity p_190681_2_) {
        double d0 = this.getBaseAttributeValue(Attributes.MAX_HEALTH) + p_190681_1_.getBaseAttributeValue(Attributes.MAX_HEALTH) + (double)this.getModifiedMaxHealth();
        p_190681_2_.getAttribute(Attributes.MAX_HEALTH).setBaseValue(d0 / 3.0);
        double d1 = this.getBaseAttributeValue(Attributes.HORSE_JUMP_STRENGTH) + p_190681_1_.getBaseAttributeValue(Attributes.HORSE_JUMP_STRENGTH) + this.getModifiedJumpStrength();
        p_190681_2_.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(d1 / 3.0);
        double d2 = this.getBaseAttributeValue(Attributes.MOVEMENT_SPEED) + p_190681_1_.getBaseAttributeValue(Attributes.MOVEMENT_SPEED) + this.getModifiedMovementSpeed();
        p_190681_2_.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(d2 / 3.0);
    }

    @Override
    public boolean canBeSteered() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    public float getGrassEatingAmount(float p_110258_1_) {
        return MathHelper.lerp(p_110258_1_, this.prevHeadLean, this.headLean);
    }

    public float getRearingAmount(float p_110223_1_) {
        return MathHelper.lerp(p_110223_1_, this.prevRearingAmount, this.rearingAmount);
    }

    public float getMouthOpennessAngle(float p_110201_1_) {
        return MathHelper.lerp(p_110201_1_, this.prevMouthOpenness, this.mouthOpenness);
    }

    @Override
    public void setJumpPower(int jumpPowerIn) {
        if (this.isHorseSaddled()) {
            if (jumpPowerIn < 0) {
                jumpPowerIn = 0;
            } else {
                this.allowStandSliding = true;
                this.makeHorseRear();
            }
            this.jumpPower = jumpPowerIn >= 90 ? 1.0f : 0.4f + 0.4f * (float)jumpPowerIn / 90.0f;
        }
    }

    @Override
    public boolean canJump() {
        return this.isHorseSaddled();
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.allowStandSliding = true;
        this.makeHorseRear();
        this.playJumpSound();
    }

    @Override
    public void handleStopJump() {
    }

    protected void spawnHorseParticles(boolean p_110216_1_) {
        BasicParticleType iparticledata = p_110216_1_ ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02;
            double d1 = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            this.world.addParticle(iparticledata, this.getPosXRandom(1.0), this.getPosYRandom() + 0.5, this.getPosZRandom(1.0), d0, d1, d2);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 7) {
            this.spawnHorseParticles(true);
        } else if (id == 6) {
            this.spawnHorseParticles(false);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (passenger instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)passenger;
            this.renderYawOffset = mobentity.renderYawOffset;
        }
        if (this.prevRearingAmount > 0.0f) {
            float f3 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180));
            float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180));
            float f1 = 0.7f * this.prevRearingAmount;
            float f2 = 0.15f * this.prevRearingAmount;
            passenger.setPosition(this.getPosX() + (double)(f1 * f3), this.getPosY() + this.getMountedYOffset() + passenger.getYOffset() + (double)f2, this.getPosZ() - (double)(f1 * f));
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).renderYawOffset = this.renderYawOffset;
            }
        }
    }

    protected float getModifiedMaxHealth() {
        return 15.0f + (float)this.rand.nextInt(8) + (float)this.rand.nextInt(9);
    }

    protected double getModifiedJumpStrength() {
        return (double)0.4f + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2 + this.rand.nextDouble() * 0.2;
    }

    protected double getModifiedMovementSpeed() {
        return ((double)0.45f + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3 + this.rand.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.95f;
    }

    public boolean func_230276_fq_() {
        return false;
    }

    public boolean func_230277_fr_() {
        return !this.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty();
    }

    public boolean isArmor(ItemStack stack) {
        return false;
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        int i = inventorySlot - 400;
        if (i >= 0 && i < 2 && i < this.horseChest.getSizeInventory()) {
            if (i == 0 && itemStackIn.getItem() != Items.SADDLE) {
                return false;
            }
            if (i != 1 || this.func_230276_fq_() && this.isArmor(itemStackIn)) {
                this.horseChest.setInventorySlotContents(i, itemStackIn);
                this.func_230275_fc_();
                return true;
            }
            return false;
        }
        int j = inventorySlot - 500 + 2;
        if (j >= 2 && j < this.horseChest.getSizeInventory()) {
            this.horseChest.setInventorySlotContents(j, itemStackIn);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Nullable
    private Vector3d func_234236_a_(Vector3d p_234236_1_, LivingEntity p_234236_2_) {
        double d0 = this.getPosX() + p_234236_1_.x;
        double d1 = this.getBoundingBox().minY;
        double d2 = this.getPosZ() + p_234236_1_.z;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        block0: for (Pose pose : p_234236_2_.getAvailablePoses()) {
            blockpos$mutable.setPos(d0, d1, d2);
            double d3 = this.getBoundingBox().maxY + 0.75;
            do {
                Vector3d vector3d;
                AxisAlignedBB axisalignedbb;
                double d4 = this.world.func_242403_h(blockpos$mutable);
                if ((double)blockpos$mutable.getY() + d4 > d3) continue block0;
                if (TransportationHelper.func_234630_a_(d4) && TransportationHelper.func_234631_a_(this.world, p_234236_2_, (axisalignedbb = p_234236_2_.getPoseAABB(pose)).offset(vector3d = new Vector3d(d0, (double)blockpos$mutable.getY() + d4, d2)))) {
                    p_234236_2_.setPose(pose);
                    return vector3d;
                }
                blockpos$mutable.move(Direction.UP);
            } while ((double)blockpos$mutable.getY() < d3);
        }
        return null;
    }

    @Override
    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        Vector3d vector3d = AbstractHorseEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.RIGHT ? 90.0f : -90.0f));
        Vector3d vector3d1 = this.func_234236_a_(vector3d, livingEntity);
        if (vector3d1 != null) {
            return vector3d1;
        }
        Vector3d vector3d2 = AbstractHorseEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), this.rotationYaw + (livingEntity.getPrimaryHand() == HandSide.LEFT ? 90.0f : -90.0f));
        Vector3d vector3d3 = this.func_234236_a_(vector3d2, livingEntity);
        return vector3d3 != null ? vector3d3 : this.getPositionVec();
    }

    protected void func_230273_eI_() {
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (spawnDataIn == null) {
            spawnDataIn = new AgeableEntity.AgeableData(0.2f);
        }
        this.func_230273_eI_();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
}
