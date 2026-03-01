package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PiglinEntity
extends AbstractPiglinEntity
implements ICrossbowUser {
    private static final DataParameter<Boolean> field_234415_d_ = EntityDataManager.createKey(PiglinEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> field_234409_bv_ = EntityDataManager.createKey(PiglinEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DANCING = EntityDataManager.createKey(PiglinEntity.class, DataSerializers.BOOLEAN);
    private static final UUID BABY_SPEED_MODIFIER_IDENTIFIER = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
    private static final AttributeModifier BABY_SPEED_MODIFIER = new AttributeModifier(BABY_SPEED_MODIFIER_IDENTIFIER, "Baby speed boost", (double)0.2f, AttributeModifier.Operation.MULTIPLY_BASE);
    private final Inventory inventory = new Inventory(8);
    private boolean field_234407_bB_ = false;
    protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinEntity>>> field_234405_b_ = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> field_234414_c_ = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEAREST_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, new MemoryModuleType[]{MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT});

    public PiglinEntity(EntityType<? extends AbstractPiglinEntity> p_i231570_1_, World p_i231570_2_) {
        super(p_i231570_1_, p_i231570_2_);
        this.experienceValue = 5;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.isChild()) {
            compound.putBoolean("IsBaby", true);
        }
        if (this.field_234407_bB_) {
            compound.putBoolean("CannotHunt", true);
        }
        compound.put("Inventory", this.inventory.write());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setChild(compound.getBoolean("IsBaby"));
        this.setCannotHunt(compound.getBoolean("CannotHunt"));
        this.inventory.read(compound.getList("Inventory", 10));
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        this.inventory.func_233543_f_().forEach(this::entityDropItem);
    }

    protected ItemStack func_234436_k_(ItemStack p_234436_1_) {
        return this.inventory.addItem(p_234436_1_);
    }

    protected boolean func_234437_l_(ItemStack p_234437_1_) {
        return this.inventory.func_233541_b_(p_234437_1_);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(field_234415_d_, false);
        this.dataManager.register(field_234409_bv_, false);
        this.dataManager.register(DANCING, false);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (field_234415_d_.equals(key)) {
            this.recalculateSize();
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234420_eI_() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 16.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35f).createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0);
    }

    public static boolean func_234418_b_(EntityType<PiglinEntity> p_234418_0_, IWorld p_234418_1_, SpawnReason p_234418_2_, BlockPos p_234418_3_, Random p_234418_4_) {
        return !p_234418_1_.getBlockState(p_234418_3_.down()).isIn(Blocks.NETHER_WART_BLOCK);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (reason != SpawnReason.STRUCTURE) {
            if (worldIn.getRandom().nextFloat() < 0.2f) {
                this.setChild(true);
            } else if (this.func_242337_eM()) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, this.func_234432_eW_());
            }
        }
        PiglinTasks.func_234466_a_(this);
        this.setEquipmentBasedOnDifficulty(difficultyIn);
        this.setEnchantmentBasedOnDifficulty(difficultyIn);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected boolean isDespawnPeaceful() {
        return false;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isNoDespawnRequired();
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if (this.func_242337_eM()) {
            this.func_234419_d_(EquipmentSlotType.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.func_234419_d_(EquipmentSlotType.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.func_234419_d_(EquipmentSlotType.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.func_234419_d_(EquipmentSlotType.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }
    }

    private void func_234419_d_(EquipmentSlotType p_234419_1_, ItemStack p_234419_2_) {
        if (this.world.rand.nextFloat() < 0.1f) {
            this.setItemStackToSlot(p_234419_1_, p_234419_2_);
        }
    }

    protected Brain.BrainCodec<PiglinEntity> getBrainCodec() {
        return Brain.createCodec(field_234414_c_, field_234405_b_);
    }

    @Override
    protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
        return PiglinTasks.func_234469_a_(this, this.getBrainCodec().deserialize(dynamicIn));
    }

    public Brain<PiglinEntity> getBrain() {
        return super.getBrain();
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
        if (actionresulttype.isSuccessOrConsume()) {
            return actionresulttype;
        }
        if (!this.world.isRemote) {
            return PiglinTasks.func_234471_a_(this, p_230254_1_, p_230254_2_);
        }
        boolean flag = PiglinTasks.func_234489_b_(this, p_230254_1_.getHeldItem(p_230254_2_)) && this.func_234424_eM_() != PiglinAction.ADMIRING_ITEM;
        return flag ? ActionResultType.SUCCESS : ActionResultType.PASS;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? 0.93f : 1.74f;
    }

    @Override
    public double getMountedYOffset() {
        return (double)this.getHeight() * 0.92;
    }

    @Override
    public void setChild(boolean childZombie) {
        this.getDataManager().set(field_234415_d_, childZombie);
        if (!this.world.isRemote) {
            ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            modifiableattributeinstance.removeModifier(BABY_SPEED_MODIFIER);
            if (childZombie) {
                modifiableattributeinstance.applyNonPersistentModifier(BABY_SPEED_MODIFIER);
            }
        }
    }

    @Override
    public boolean isChild() {
        return this.getDataManager().get(field_234415_d_);
    }

    private void setCannotHunt(boolean p_234443_1_) {
        this.field_234407_bB_ = p_234443_1_;
    }

    @Override
    protected boolean func_234422_eK_() {
        return !this.field_234407_bB_;
    }

    @Override
    protected void updateAITasks() {
        this.world.getProfiler().startSection("piglinBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().endSection();
        PiglinTasks.func_234486_b_(this);
        super.updateAITasks();
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return this.experienceValue;
    }

    @Override
    protected void func_234416_a_(ServerWorld p_234416_1_) {
        PiglinTasks.func_234496_c_(this);
        this.inventory.func_233543_f_().forEach(this::entityDropItem);
        super.func_234416_a_(p_234416_1_);
    }

    private ItemStack func_234432_eW_() {
        return (double)this.rand.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
    }

    private boolean func_234433_eX_() {
        return this.dataManager.get(field_234409_bv_);
    }

    @Override
    public void setCharging(boolean isCharging) {
        this.dataManager.set(field_234409_bv_, isCharging);
    }

    @Override
    public void func_230283_U__() {
        this.idleTime = 0;
    }

    @Override
    public PiglinAction func_234424_eM_() {
        if (this.func_234425_eN_()) {
            return PiglinAction.DANCING;
        }
        if (PiglinTasks.func_234480_a_(this.getHeldItemOffhand().getItem())) {
            return PiglinAction.ADMIRING_ITEM;
        }
        if (this.isAggressive() && this.func_242338_eO()) {
            return PiglinAction.ATTACKING_WITH_MELEE_WEAPON;
        }
        if (this.func_234433_eX_()) {
            return PiglinAction.CROSSBOW_CHARGE;
        }
        return this.isAggressive() && this.canEquip(Items.CROSSBOW) ? PiglinAction.CROSSBOW_HOLD : PiglinAction.DEFAULT;
    }

    public boolean func_234425_eN_() {
        return this.dataManager.get(DANCING);
    }

    public void func_234442_u_(boolean p_234442_1_) {
        this.dataManager.set(DANCING, p_234442_1_);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (this.world.isRemote) {
            return false;
        }
        if (flag && source.getTrueSource() instanceof LivingEntity) {
            PiglinTasks.func_234468_a_(this, (LivingEntity)source.getTrueSource());
        }
        return flag;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        this.func_234281_b_(this, 1.6f);
    }

    @Override
    public void func_230284_a_(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_) {
        this.func_234279_a_(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6f);
    }

    @Override
    public boolean func_230280_a_(ShootableItem p_230280_1_) {
        return p_230280_1_ == Items.CROSSBOW;
    }

    protected void func_234438_m_(ItemStack p_234438_1_) {
        this.func_233657_b_(EquipmentSlotType.MAINHAND, p_234438_1_);
    }

    protected void func_234439_n_(ItemStack p_234439_1_) {
        if (p_234439_1_.getItem() == PiglinTasks.field_234444_a_) {
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, p_234439_1_);
            this.func_233663_d_(EquipmentSlotType.OFFHAND);
        } else {
            this.func_233657_b_(EquipmentSlotType.OFFHAND, p_234439_1_);
        }
    }

    @Override
    public boolean func_230293_i_(ItemStack p_230293_1_) {
        return this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.canPickUpLoot() && PiglinTasks.func_234474_a_(this, p_230293_1_);
    }

    protected boolean func_234440_o_(ItemStack p_234440_1_) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_234440_1_);
        ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
        return this.shouldExchangeEquipment(p_234440_1_, itemstack);
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        boolean flag1;
        if (EnchantmentHelper.hasBindingCurse(existing)) {
            return false;
        }
        boolean flag = PiglinTasks.func_234480_a_(candidate.getItem()) || candidate.getItem() == Items.CROSSBOW;
        boolean bl = flag1 = PiglinTasks.func_234480_a_(existing.getItem()) || existing.getItem() == Items.CROSSBOW;
        if (flag && !flag1) {
            return true;
        }
        if (!flag && flag1) {
            return false;
        }
        return this.func_242337_eM() && candidate.getItem() != Items.CROSSBOW && existing.getItem() == Items.CROSSBOW ? false : super.shouldExchangeEquipment(candidate, existing);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        this.triggerItemPickupTrigger(itemEntity);
        PiglinTasks.func_234470_a_(this, itemEntity);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        if (this.isChild() && entityIn.getType() == EntityType.HOGLIN) {
            entityIn = this.func_234417_b_(entityIn, 3);
        }
        return super.startRiding(entityIn, force);
    }

    private Entity func_234417_b_(Entity p_234417_1_, int p_234417_2_) {
        List<Entity> list = p_234417_1_.getPassengers();
        return p_234417_2_ != 1 && !list.isEmpty() ? this.func_234417_b_(list.get(0), p_234417_2_ - 1) : p_234417_1_;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.world.isRemote ? null : PiglinTasks.func_241429_d_(this).orElse(null);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_PIGLIN_STEP, 0.15f, 1.0f);
    }

    protected void func_241417_a_(SoundEvent p_241417_1_) {
        this.playSound(p_241417_1_, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    protected void func_241848_eP() {
        this.func_241417_a_(SoundEvents.ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED);
    }
}
