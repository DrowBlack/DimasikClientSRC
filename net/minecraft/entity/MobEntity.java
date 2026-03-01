package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;

public abstract class MobEntity
extends LivingEntity {
    private static final DataParameter<Byte> AI_FLAGS = EntityDataManager.createKey(MobEntity.class, DataSerializers.BYTE);
    public int livingSoundTime;
    protected int experienceValue;
    protected LookController lookController;
    protected MovementController moveController;
    protected JumpController jumpController;
    private final BodyController bodyController;
    protected PathNavigator navigator;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity attackTarget;
    private final EntitySenses senses;
    private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final float[] inventoryHandsDropChances = new float[2];
    private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
    protected final float[] inventoryArmorDropChances = new float[4];
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<PathNodeType, Float> mapPathPriority = Maps.newEnumMap(PathNodeType.class);
    private ResourceLocation deathLootTable;
    private long deathLootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int leashHolderID;
    @Nullable
    private CompoundNBT leashNBTTag;
    private BlockPos homePosition = BlockPos.ZERO;
    private float maximumHomeDistance = -1.0f;

    protected MobEntity(EntityType<? extends MobEntity> type, World worldIn) {
        super((EntityType<? extends LivingEntity>)type, worldIn);
        this.goalSelector = new GoalSelector(worldIn.getWorldProfiler());
        this.targetSelector = new GoalSelector(worldIn.getWorldProfiler());
        this.lookController = new LookController(this);
        this.moveController = new MovementController(this);
        this.jumpController = new JumpController(this);
        this.bodyController = this.createBodyController();
        this.navigator = this.createNavigator(worldIn);
        this.senses = new EntitySenses(this);
        Arrays.fill(this.inventoryArmorDropChances, 0.085f);
        Arrays.fill(this.inventoryHandsDropChances, 0.085f);
        if (worldIn != null && !worldIn.isRemote) {
            this.registerGoals();
        }
    }

    protected void registerGoals() {
    }

    public static AttributeModifierMap.MutableAttribute func_233666_p_() {
        return LivingEntity.registerAttributes().createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0).createMutableAttribute(Attributes.ATTACK_KNOCKBACK);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new GroundPathNavigator(this, worldIn);
    }

    protected boolean func_230286_q_() {
        return false;
    }

    public float getPathPriority(PathNodeType nodeType) {
        MobEntity mobentity = this.getRidingEntity() instanceof MobEntity && ((MobEntity)this.getRidingEntity()).func_230286_q_() ? (MobEntity)this.getRidingEntity() : this;
        Float f = mobentity.mapPathPriority.get((Object)nodeType);
        return f == null ? nodeType.getPriority() : f.floatValue();
    }

    public void setPathPriority(PathNodeType nodeType, float priority) {
        this.mapPathPriority.put(nodeType, Float.valueOf(priority));
    }

    public boolean func_233660_b_(PathNodeType p_233660_1_) {
        return p_233660_1_ != PathNodeType.DANGER_FIRE && p_233660_1_ != PathNodeType.DANGER_CACTUS && p_233660_1_ != PathNodeType.DANGER_OTHER && p_233660_1_ != PathNodeType.WALKABLE_DOOR;
    }

    protected BodyController createBodyController() {
        return new BodyController(this);
    }

    public LookController getLookController() {
        return this.lookController;
    }

    public MovementController getMoveHelper() {
        if (this.isPassenger() && this.getRidingEntity() instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)this.getRidingEntity();
            return mobentity.getMoveHelper();
        }
        return this.moveController;
    }

    public JumpController getJumpController() {
        return this.jumpController;
    }

    public PathNavigator getNavigator() {
        if (this.isPassenger() && this.getRidingEntity() instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)this.getRidingEntity();
            return mobentity.getNavigator();
        }
        return this.navigator;
    }

    public EntitySenses getEntitySenses() {
        return this.senses;
    }

    @Nullable
    public LivingEntity getAttackTarget() {
        return this.attackTarget;
    }

    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        this.attackTarget = entitylivingbaseIn;
        Reflector.callVoid(Reflector.ForgeHooks_onLivingSetAttackTarget, this, entitylivingbaseIn);
    }

    @Override
    public boolean canAttack(EntityType<?> typeIn) {
        return typeIn != EntityType.GHAST;
    }

    public boolean func_230280_a_(ShootableItem p_230280_1_) {
        return false;
    }

    public void eatGrassBonus() {
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(AI_FLAGS, (byte)0);
    }

    public int getTalkInterval() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.world.getProfiler().startSection("mobBaseTick");
        if (this.isAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
            this.func_241821_eG();
            this.playAmbientSound();
        }
        this.world.getProfiler().endSection();
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.func_241821_eG();
        super.playHurtSound(source);
    }

    private void func_241821_eG() {
        this.livingSoundTime = -this.getTalkInterval();
    }

    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        if (this.experienceValue > 0) {
            int i = this.experienceValue;
            for (int j = 0; j < this.inventoryArmor.size(); ++j) {
                if (this.inventoryArmor.get(j).isEmpty() || !(this.inventoryArmorDropChances[j] <= 1.0f)) continue;
                i += 1 + this.rand.nextInt(3);
            }
            for (int k = 0; k < this.inventoryHands.size(); ++k) {
                if (this.inventoryHands.get(k).isEmpty() || !(this.inventoryHandsDropChances[k] <= 1.0f)) continue;
                i += 1 + this.rand.nextInt(3);
            }
            return i;
        }
        return this.experienceValue;
    }

    public void spawnExplosionParticle() {
        if (this.world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02;
                double d1 = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = 10.0;
                this.world.addParticle(ParticleTypes.POOF, this.getPosXWidth(1.0) - d0 * 10.0, this.getPosYRandom() - d1 * 10.0, this.getPosZRandom(1.0) - d2 * 10.0, d0, d1, d2);
            }
        } else {
            this.world.setEntityState(this, (byte)20);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 20) {
            this.spawnExplosionParticle();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void tick() {
        if (Config.isSmoothWorld() && this.canSkipUpdate()) {
            this.onUpdateMinimal();
        } else {
            super.tick();
            if (!this.world.isRemote) {
                this.updateLeashedState();
                if (this.ticksExisted % 5 == 0) {
                    this.updateMovementGoalFlags();
                }
            }
        }
    }

    protected void updateMovementGoalFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof MobEntity);
        boolean flag1 = !(this.getRidingEntity() instanceof BoatEntity);
        this.goalSelector.setFlag(Goal.Flag.MOVE, flag);
        this.goalSelector.setFlag(Goal.Flag.JUMP, flag && flag1);
        this.goalSelector.setFlag(Goal.Flag.LOOK, flag);
    }

    @Override
    protected float updateDistance(float p_110146_1_, float p_110146_2_) {
        this.bodyController.updateRenderAngles();
        return p_110146_2_;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        compound.putBoolean("PersistenceRequired", this.persistenceRequired);
        ListNBT listnbt = new ListNBT();
        for (ItemStack itemStack : this.inventoryArmor) {
            CompoundNBT compoundNBT = new CompoundNBT();
            if (!itemStack.isEmpty()) {
                itemStack.write(compoundNBT);
            }
            listnbt.add(compoundNBT);
        }
        compound.put("ArmorItems", listnbt);
        ListNBT listnbt1 = new ListNBT();
        for (ItemStack itemStack : this.inventoryHands) {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            if (!itemStack.isEmpty()) {
                itemStack.write(compoundnbt1);
            }
            listnbt1.add(compoundnbt1);
        }
        compound.put("HandItems", listnbt1);
        ListNBT listNBT = new ListNBT();
        for (float f : this.inventoryArmorDropChances) {
            listNBT.add(FloatNBT.valueOf(f));
        }
        compound.put("ArmorDropChances", listNBT);
        ListNBT listNBT2 = new ListNBT();
        for (float f1 : this.inventoryHandsDropChances) {
            listNBT2.add(FloatNBT.valueOf(f1));
        }
        compound.put("HandDropChances", listNBT2);
        if (this.leashHolder != null) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            if (this.leashHolder instanceof LivingEntity) {
                UUID uuid = this.leashHolder.getUniqueID();
                compoundnbt2.putUniqueId("UUID", uuid);
            } else if (this.leashHolder instanceof HangingEntity) {
                BlockPos blockpos = ((HangingEntity)this.leashHolder).getHangingPosition();
                compoundnbt2.putInt("X", blockpos.getX());
                compoundnbt2.putInt("Y", blockpos.getY());
                compoundnbt2.putInt("Z", blockpos.getZ());
            }
            compound.put("Leash", compoundnbt2);
        } else if (this.leashNBTTag != null) {
            compound.put("Leash", this.leashNBTTag.copy());
        }
        compound.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.deathLootTable != null) {
            compound.putString("DeathLootTable", this.deathLootTable.toString());
            if (this.deathLootTableSeed != 0L) {
                compound.putLong("DeathLootTableSeed", this.deathLootTableSeed);
            }
        }
        if (this.isAIDisabled()) {
            compound.putBoolean("NoAI", this.isAIDisabled());
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(compound.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = compound.getBoolean("PersistenceRequired");
        if (compound.contains("ArmorItems", 9)) {
            ListNBT listnbt = compound.getList("ArmorItems", 10);
            for (int i = 0; i < this.inventoryArmor.size(); ++i) {
                this.inventoryArmor.set(i, ItemStack.read(listnbt.getCompound(i)));
            }
        }
        if (compound.contains("HandItems", 9)) {
            ListNBT listnbt1 = compound.getList("HandItems", 10);
            for (int j = 0; j < this.inventoryHands.size(); ++j) {
                this.inventoryHands.set(j, ItemStack.read(listnbt1.getCompound(j)));
            }
        }
        if (compound.contains("ArmorDropChances", 9)) {
            ListNBT listnbt2 = compound.getList("ArmorDropChances", 5);
            for (int k = 0; k < listnbt2.size(); ++k) {
                this.inventoryArmorDropChances[k] = listnbt2.getFloat(k);
            }
        }
        if (compound.contains("HandDropChances", 9)) {
            ListNBT listnbt3 = compound.getList("HandDropChances", 5);
            for (int l = 0; l < listnbt3.size(); ++l) {
                this.inventoryHandsDropChances[l] = listnbt3.getFloat(l);
            }
        }
        if (compound.contains("Leash", 10)) {
            this.leashNBTTag = compound.getCompound("Leash");
        }
        this.setLeftHanded(compound.getBoolean("LeftHanded"));
        if (compound.contains("DeathLootTable", 8)) {
            this.deathLootTable = new ResourceLocation(compound.getString("DeathLootTable"));
            this.deathLootTableSeed = compound.getLong("DeathLootTableSeed");
        }
        this.setNoAI(compound.getBoolean("NoAI"));
    }

    @Override
    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently) {
        super.dropLoot(damageSourceIn, attackedRecently);
        this.deathLootTable = null;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean attackedRecently, DamageSource damageSourceIn) {
        return super.getLootContextBuilder(attackedRecently, damageSourceIn).withSeededRandom(this.deathLootTableSeed, this.rand);
    }

    @Override
    public final ResourceLocation getLootTableResourceLocation() {
        return this.deathLootTable == null ? this.getLootTable() : this.deathLootTable;
    }

    protected ResourceLocation getLootTable() {
        return super.getLootTableResourceLocation();
    }

    public void setMoveForward(float amount) {
        this.moveForward = amount;
    }

    public void setMoveVertical(float amount) {
        this.moveVertical = amount;
    }

    public void setMoveStrafing(float amount) {
        this.moveStrafing = amount;
    }

    @Override
    public void setAIMoveSpeed(float speedIn) {
        super.setAIMoveSpeed(speedIn);
        this.setMoveForward(speedIn);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.world.getProfiler().startSection("looting");
        boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
        if (Reflector.ForgeEventFactory_getMobGriefingEvent.exists()) {
            flag = Reflector.callBoolean(Reflector.ForgeEventFactory_getMobGriefingEvent, this.world, this);
        }
        if (!this.world.isRemote && this.canPickUpLoot() && this.isAlive() && !this.dead && flag) {
            for (ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0, 0.0, 1.0))) {
                if (itementity.removed || itementity.getItem().isEmpty() || itementity.cannotPickup() || !this.func_230293_i_(itementity.getItem())) continue;
                this.updateEquipmentIfNeeded(itementity);
            }
        }
        this.world.getProfiler().endSection();
    }

    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack = itemEntity.getItem();
        if (this.func_233665_g_(itemstack)) {
            this.triggerItemPickupTrigger(itemEntity);
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
        }
    }

    public boolean func_233665_g_(ItemStack p_233665_1_) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_233665_1_);
        ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
        boolean flag = this.shouldExchangeEquipment(p_233665_1_, itemstack);
        if (flag && this.canEquipItem(p_233665_1_)) {
            double d0 = this.getDropChance(equipmentslottype);
            if (!itemstack.isEmpty() && (double)Math.max(this.rand.nextFloat() - 0.1f, 0.0f) < d0) {
                this.entityDropItem(itemstack);
            }
            this.func_233657_b_(equipmentslottype, p_233665_1_);
            this.playEquipSound(p_233665_1_);
            return true;
        }
        return false;
    }

    protected void func_233657_b_(EquipmentSlotType p_233657_1_, ItemStack p_233657_2_) {
        this.setItemStackToSlot(p_233657_1_, p_233657_2_);
        this.func_233663_d_(p_233657_1_);
        this.persistenceRequired = true;
    }

    public void func_233663_d_(EquipmentSlotType p_233663_1_) {
        switch (p_233663_1_.getSlotType()) {
            case HAND: {
                this.inventoryHandsDropChances[p_233663_1_.getIndex()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.inventoryArmorDropChances[p_233663_1_.getIndex()] = 2.0f;
            }
        }
    }

    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        if (existing.isEmpty()) {
            return true;
        }
        if (candidate.getItem() instanceof SwordItem) {
            if (!(existing.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem sworditem = (SwordItem)candidate.getItem();
            SwordItem sworditem1 = (SwordItem)existing.getItem();
            if (sworditem.getAttackDamage() != sworditem1.getAttackDamage()) {
                return sworditem.getAttackDamage() > sworditem1.getAttackDamage();
            }
            return this.func_233659_b_(candidate, existing);
        }
        if (candidate.getItem() instanceof BowItem && existing.getItem() instanceof BowItem) {
            return this.func_233659_b_(candidate, existing);
        }
        if (candidate.getItem() instanceof CrossbowItem && existing.getItem() instanceof CrossbowItem) {
            return this.func_233659_b_(candidate, existing);
        }
        if (candidate.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(existing)) {
                return false;
            }
            if (!(existing.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem armoritem = (ArmorItem)candidate.getItem();
            ArmorItem armoritem1 = (ArmorItem)existing.getItem();
            if (armoritem.getDamageReduceAmount() != armoritem1.getDamageReduceAmount()) {
                return armoritem.getDamageReduceAmount() > armoritem1.getDamageReduceAmount();
            }
            if (armoritem.func_234657_f_() != armoritem1.func_234657_f_()) {
                return armoritem.func_234657_f_() > armoritem1.func_234657_f_();
            }
            return this.func_233659_b_(candidate, existing);
        }
        if (candidate.getItem() instanceof ToolItem) {
            if (existing.getItem() instanceof BlockItem) {
                return true;
            }
            if (existing.getItem() instanceof ToolItem) {
                ToolItem toolitem = (ToolItem)candidate.getItem();
                ToolItem toolitem1 = (ToolItem)existing.getItem();
                if (toolitem.getAttackDamage() != toolitem1.getAttackDamage()) {
                    return toolitem.getAttackDamage() > toolitem1.getAttackDamage();
                }
                return this.func_233659_b_(candidate, existing);
            }
        }
        return false;
    }

    public boolean func_233659_b_(ItemStack p_233659_1_, ItemStack p_233659_2_) {
        if (p_233659_1_.getDamage() >= p_233659_2_.getDamage() && (!p_233659_1_.hasTag() || p_233659_2_.hasTag())) {
            if (p_233659_1_.hasTag() && p_233659_2_.hasTag()) {
                return p_233659_1_.getTag().keySet().stream().anyMatch(p_lambda$func_233659_b_$0_0_ -> !p_lambda$func_233659_b_$0_0_.equals("Damage")) && !p_233659_2_.getTag().keySet().stream().anyMatch(p_lambda$func_233659_b_$1_0_ -> !p_lambda$func_233659_b_$1_0_.equals("Damage"));
            }
            return false;
        }
        return true;
    }

    public boolean canEquipItem(ItemStack stack) {
        return true;
    }

    public boolean func_230293_i_(ItemStack p_230293_1_) {
        return this.canEquipItem(p_230293_1_);
    }

    public boolean canDespawn(double distanceToClosestPlayer) {
        return true;
    }

    public boolean preventDespawn() {
        return this.isPassenger();
    }

    protected boolean isDespawnPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDespawnPeaceful()) {
            this.remove();
        } else if (!this.isNoDespawnRequired() && !this.preventDespawn()) {
            PlayerEntity entity = this.world.getClosestPlayer(this, -1.0);
            if (Reflector.ForgeEventFactory_canEntityDespawn.exists()) {
                Object object = Reflector.ForgeEventFactory_canEntityDespawn.call((Object)this);
                if (object == ReflectorForge.EVENT_RESULT_DENY) {
                    this.idleTime = 0;
                    entity = null;
                } else if (object == ReflectorForge.EVENT_RESULT_ALLOW) {
                    this.remove();
                    entity = null;
                }
            }
            if (entity != null) {
                int i;
                int j;
                double d0 = entity.getDistanceSq(this);
                if (d0 > (double)(j = (i = this.getType().getClassification().getInstantDespawnDistance()) * i) && this.canDespawn(d0)) {
                    this.remove();
                }
                int k = this.getType().getClassification().getRandomDespawnDistance();
                int l = k * k;
                if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d0 > (double)l && this.canDespawn(d0)) {
                    this.remove();
                } else if (d0 < (double)l) {
                    this.idleTime = 0;
                }
            }
        } else {
            this.idleTime = 0;
        }
    }

    @Override
    protected final void updateEntityActionState() {
        ++this.idleTime;
        this.world.getProfiler().startSection("sensing");
        this.senses.tick();
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("targetSelector");
        this.targetSelector.tick();
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("navigation");
        this.navigator.tick();
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("mob tick");
        this.updateAITasks();
        this.world.getProfiler().endSection();
        this.world.getProfiler().startSection("controls");
        this.world.getProfiler().startSection("move");
        this.moveController.tick();
        this.world.getProfiler().endStartSection("look");
        this.lookController.tick();
        this.world.getProfiler().endStartSection("jump");
        this.jumpController.tick();
        this.world.getProfiler().endSection();
        this.world.getProfiler().endSection();
        this.sendDebugPackets();
    }

    protected void sendDebugPackets() {
        DebugPacketSender.sendGoal(this.world, this, this.goalSelector);
    }

    protected void updateAITasks() {
    }

    public int getVerticalFaceSpeed() {
        return 40;
    }

    public int getHorizontalFaceSpeed() {
        return 75;
    }

    public int getFaceRotSpeed() {
        return 10;
    }

    public void faceEntity(Entity entityIn, float maxYawIncrease, float maxPitchIncrease) {
        double d2;
        double d0 = entityIn.getPosX() - this.getPosX();
        double d1 = entityIn.getPosZ() - this.getPosZ();
        if (entityIn instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn;
            d2 = livingentity.getPosYEye() - this.getPosYEye();
        } else {
            d2 = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0 - this.getPosYEye();
        }
        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f = (float)(MathHelper.atan2(d1, d0) * 57.2957763671875) - 90.0f;
        float f1 = (float)(-(MathHelper.atan2(d2, d3) * 57.2957763671875));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f1, maxPitchIncrease);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f, maxYawIncrease);
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        if (f > maxIncrease) {
            f = maxIncrease;
        }
        if (f < -maxIncrease) {
            f = -maxIncrease;
        }
        return angle + f;
    }

    public static boolean canSpawnOn(EntityType<? extends MobEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.down();
        return reason == SpawnReason.SPAWNER || worldIn.getBlockState(blockpos).canEntitySpawn(worldIn, blockpos, typeIn);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return true;
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return !worldIn.containsAnyLiquid(this.getBoundingBox()) && worldIn.checkNoEntityCollision(this);
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    public boolean isMaxGroupSize(int sizeIn) {
        return false;
    }

    @Override
    public int getMaxFallHeight() {
        if (this.getAttackTarget() == null) {
            return 3;
        }
        int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if ((i -= (3 - this.world.getDifficulty().getId()) * 4) < 0) {
            i = 0;
        }
        return i + 3;
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return this.inventoryHands;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return this.inventoryArmor;
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        switch (slotIn.getSlotType()) {
            case HAND: {
                return this.inventoryHands.get(slotIn.getIndex());
            }
            case ARMOR: {
                return this.inventoryArmor.get(slotIn.getIndex());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        switch (slotIn.getSlotType()) {
            case HAND: {
                this.inventoryHands.set(slotIn.getIndex(), stack);
                break;
            }
            case ARMOR: {
                this.inventoryArmor.set(slotIn.getIndex(), stack);
            }
        }
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropSpecialItems(source, looting, recentlyHitIn);
        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            boolean flag;
            ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
            float f = this.getDropChance(equipmentslottype);
            boolean bl = flag = f > 1.0f;
            if (itemstack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemstack) || !recentlyHitIn && !flag || !(Math.max(this.rand.nextFloat() - (float)looting * 0.01f, 0.0f) < f)) continue;
            if (!flag && itemstack.isDamageable()) {
                itemstack.setDamage(itemstack.getMaxDamage() - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }
            this.entityDropItem(itemstack);
            this.setItemStackToSlot(equipmentslottype, ItemStack.EMPTY);
        }
    }

    protected float getDropChance(EquipmentSlotType slotIn) {
        return switch (slotIn.getSlotType()) {
            case EquipmentSlotType.Group.HAND -> this.inventoryHandsDropChances[slotIn.getIndex()];
            case EquipmentSlotType.Group.ARMOR -> this.inventoryArmorDropChances[slotIn.getIndex()];
            default -> 0.0f;
        };
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if (this.rand.nextFloat() < 0.15f * difficulty.getClampedAdditionalDifficulty()) {
            float f;
            int i = this.rand.nextInt(2);
            float f2 = f = this.world.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++i;
            }
            boolean flag = true;
            for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
                Item item;
                if (equipmentslottype.getSlotType() != EquipmentSlotType.Group.ARMOR) continue;
                ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
                if (!flag && this.rand.nextFloat() < f) break;
                flag = false;
                if (!itemstack.isEmpty() || (item = MobEntity.getArmorByChance(equipmentslottype, i)) == null) continue;
                this.setItemStackToSlot(equipmentslottype, new ItemStack(item));
            }
        }
    }

    public static EquipmentSlotType getSlotForItemStack(ItemStack stack) {
        EquipmentSlotType equipmentslottype;
        if (Reflector.IForgeItemStack_getEquipmentSlot.exists() && (equipmentslottype = (EquipmentSlotType)((Object)Reflector.call(stack, Reflector.IForgeItemStack_getEquipmentSlot, new Object[0]))) != null) {
            return equipmentslottype;
        }
        Item item = stack.getItem();
        if (!(item == Blocks.CARVED_PUMPKIN.asItem() || item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock)) {
            if (item instanceof ArmorItem) {
                return ((ArmorItem)item).getEquipmentSlot();
            }
            if (item == Items.ELYTRA) {
                return EquipmentSlotType.CHEST;
            }
            return ReflectorForge.isShield(stack, null) ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND;
        }
        return EquipmentSlotType.HEAD;
    }

    @Nullable
    public static Item getArmorByChance(EquipmentSlotType slotIn, int chance) {
        switch (slotIn) {
            case HEAD: {
                if (chance == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (chance == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (chance == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (chance == 3) {
                    return Items.IRON_HELMET;
                }
                if (chance == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (chance == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (chance == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (chance == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (chance == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (chance == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (chance == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (chance == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (chance == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (chance == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (chance == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (chance == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (chance == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (chance == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (chance == 3) {
                    return Items.IRON_BOOTS;
                }
                if (chance != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty) {
        float f = difficulty.getClampedAdditionalDifficulty();
        this.func_241844_w(f);
        for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            if (equipmentslottype.getSlotType() != EquipmentSlotType.Group.ARMOR) continue;
            this.func_242289_a(f, equipmentslottype);
        }
    }

    protected void func_241844_w(float p_241844_1_) {
        if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25f * p_241844_1_) {
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0f + p_241844_1_ * (float)this.rand.nextInt(18)), false));
        }
    }

    protected void func_242289_a(float p_242289_1_, EquipmentSlotType p_242289_2_) {
        ItemStack itemstack = this.getItemStackFromSlot(p_242289_2_);
        if (!itemstack.isEmpty() && this.rand.nextFloat() < 0.5f * p_242289_1_) {
            this.setItemStackToSlot(p_242289_2_, EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0f + p_242289_1_ * (float)this.rand.nextInt(18)), false));
        }
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.getAttribute(Attributes.FOLLOW_RANGE).applyPersistentModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.rand.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return spawnDataIn;
    }

    public boolean canBeSteered() {
        return false;
    }

    public void enablePersistence() {
        this.persistenceRequired = true;
    }

    public void setDropChance(EquipmentSlotType slotIn, float chance) {
        switch (slotIn.getSlotType()) {
            case HAND: {
                this.inventoryHandsDropChances[slotIn.getIndex()] = chance;
                break;
            }
            case ARMOR: {
                this.inventoryArmorDropChances[slotIn.getIndex()] = chance;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean canPickup) {
        this.canPickUpLoot = canPickup;
    }

    @Override
    public boolean canPickUpItem(ItemStack itemstackIn) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
        return this.getItemStackFromSlot(equipmentslottype).isEmpty() && this.canPickUpLoot();
    }

    public boolean isNoDespawnRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (!this.isAlive()) {
            return ActionResultType.PASS;
        }
        if (this.getLeashHolder() == player) {
            this.clearLeashed(true, !player.abilities.isCreativeMode);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        ActionResultType actionresulttype = this.func_233661_c_(player, hand);
        if (actionresulttype.isSuccessOrConsume()) {
            return actionresulttype;
        }
        actionresulttype = this.func_230254_b_(player, hand);
        return actionresulttype.isSuccessOrConsume() ? actionresulttype : super.processInitialInteract(player, hand);
    }

    private ActionResultType func_233661_c_(PlayerEntity p_233661_1_, Hand p_233661_2_) {
        ActionResultType actionresulttype;
        ItemStack itemstack = p_233661_1_.getHeldItem(p_233661_2_);
        if (itemstack.getItem() == Items.LEAD && this.canBeLeashedTo(p_233661_1_)) {
            this.setLeashHolder(p_233661_1_, true);
            itemstack.shrink(1);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        if (itemstack.getItem() == Items.NAME_TAG && (actionresulttype = itemstack.interactWithEntity(p_233661_1_, this, p_233661_2_)).isSuccessOrConsume()) {
            return actionresulttype;
        }
        if (itemstack.getItem() instanceof SpawnEggItem) {
            if (this.world instanceof ServerWorld) {
                SpawnEggItem spawneggitem = (SpawnEggItem)itemstack.getItem();
                Optional<MobEntity> optional = spawneggitem.getChildToSpawn(p_233661_1_, this, this.getType(), (ServerWorld)this.world, this.getPositionVec(), itemstack);
                optional.ifPresent(p_lambda$func_233661_c_$2_2_ -> this.onChildSpawnFromEgg(p_233661_1_, (MobEntity)p_lambda$func_233661_c_$2_2_));
                return optional.isPresent() ? ActionResultType.SUCCESS : ActionResultType.PASS;
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    protected void onChildSpawnFromEgg(PlayerEntity playerIn, MobEntity child) {
    }

    protected ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        return ActionResultType.PASS;
    }

    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistanceFromPosition(this.getPosition());
    }

    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        if (this.maximumHomeDistance == -1.0f) {
            return true;
        }
        return this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
    }

    public void setHomePosAndDistance(BlockPos pos, int distance) {
        this.homePosition = pos;
        this.maximumHomeDistance = distance;
    }

    public BlockPos getHomePosition() {
        return this.homePosition;
    }

    public float getMaximumHomeDistance() {
        return this.maximumHomeDistance;
    }

    public boolean detachHome() {
        return this.maximumHomeDistance != -1.0f;
    }

    @Nullable
    public <T extends MobEntity> T func_233656_b_(EntityType<T> p_233656_1_, boolean p_233656_2_) {
        if (this.removed) {
            return (T)((MobEntity)null);
        }
        MobEntity t = (MobEntity)p_233656_1_.create(this.world);
        t.copyLocationAndAnglesFrom(this);
        t.setChild(this.isChild());
        t.setNoAI(this.isAIDisabled());
        if (this.hasCustomName()) {
            t.setCustomName(this.getCustomName());
            t.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isNoDespawnRequired()) {
            t.enablePersistence();
        }
        t.setInvulnerable(this.isInvulnerable());
        if (p_233656_2_) {
            t.setCanPickUpLoot(this.canPickUpLoot());
            for (EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
                ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
                if (itemstack.isEmpty()) continue;
                t.setItemStackToSlot(equipmentslottype, itemstack.copy());
                t.setDropChance(equipmentslottype, this.getDropChance(equipmentslottype));
                itemstack.setCount(0);
            }
        }
        this.world.addEntity(t);
        if (this.isPassenger()) {
            Entity entity = this.getRidingEntity();
            this.stopRiding();
            t.startRiding(entity, true);
        }
        this.remove();
        return (T)t;
    }

    protected void updateLeashedState() {
        if (this.leashNBTTag != null) {
            this.recreateLeash();
        }
        if (!(this.leashHolder == null || this.isAlive() && this.leashHolder.isAlive())) {
            this.clearLeashed(true, true);
        }
    }

    public void clearLeashed(boolean sendPacket, boolean dropLead) {
        if (this.leashHolder != null) {
            this.forceSpawn = false;
            if (!(this.leashHolder instanceof PlayerEntity)) {
                this.leashHolder.forceSpawn = false;
            }
            this.leashHolder = null;
            this.leashNBTTag = null;
            if (!this.world.isRemote && dropLead) {
                this.entityDropItem(Items.LEAD);
            }
            if (!this.world.isRemote && sendPacket && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SMountEntityPacket(this, null));
            }
        }
    }

    public boolean canBeLeashedTo(PlayerEntity player) {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed() {
        return this.leashHolder != null;
    }

    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.leashHolderID != 0 && this.world.isRemote) {
            this.leashHolder = this.world.getEntityByID(this.leashHolderID);
        }
        return this.leashHolder;
    }

    public void setLeashHolder(Entity entityIn, boolean sendAttachNotification) {
        this.leashHolder = entityIn;
        this.leashNBTTag = null;
        this.forceSpawn = true;
        if (!(this.leashHolder instanceof PlayerEntity)) {
            this.leashHolder.forceSpawn = true;
        }
        if (!this.world.isRemote && sendAttachNotification && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SMountEntityPacket(this, this.leashHolder));
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
    }

    public void setVehicleEntityId(int leashHolderIDIn) {
        this.leashHolderID = leashHolderIDIn;
        this.clearLeashed(false, false);
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        boolean flag = super.startRiding(entityIn, force);
        if (flag && this.getLeashed()) {
            this.clearLeashed(true, true);
        }
        return flag;
    }

    private void recreateLeash() {
        if (this.leashNBTTag != null && this.world instanceof ServerWorld) {
            if (this.leashNBTTag.hasUniqueId("UUID")) {
                UUID uuid = this.leashNBTTag.getUniqueId("UUID");
                Entity entity = ((ServerWorld)this.world).getEntityByUuid(uuid);
                if (entity != null) {
                    this.setLeashHolder(entity, true);
                    return;
                }
            } else if (this.leashNBTTag.contains("X", 99) && this.leashNBTTag.contains("Y", 99) && this.leashNBTTag.contains("Z", 99)) {
                BlockPos blockpos = new BlockPos(this.leashNBTTag.getInt("X"), this.leashNBTTag.getInt("Y"), this.leashNBTTag.getInt("Z"));
                this.setLeashHolder(LeashKnotEntity.create(this.world, blockpos), true);
                return;
            }
            if (this.ticksExisted > 100) {
                this.entityDropItem(Items.LEAD);
                this.leashNBTTag = null;
            }
        }
    }

    @Override
    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
        EquipmentSlotType equipmentslottype;
        if (inventorySlot == 98) {
            equipmentslottype = EquipmentSlotType.MAINHAND;
        } else if (inventorySlot == 99) {
            equipmentslottype = EquipmentSlotType.OFFHAND;
        } else if (inventorySlot == 100 + EquipmentSlotType.HEAD.getIndex()) {
            equipmentslottype = EquipmentSlotType.HEAD;
        } else if (inventorySlot == 100 + EquipmentSlotType.CHEST.getIndex()) {
            equipmentslottype = EquipmentSlotType.CHEST;
        } else if (inventorySlot == 100 + EquipmentSlotType.LEGS.getIndex()) {
            equipmentslottype = EquipmentSlotType.LEGS;
        } else {
            if (inventorySlot != 100 + EquipmentSlotType.FEET.getIndex()) {
                return false;
            }
            equipmentslottype = EquipmentSlotType.FEET;
        }
        if (!itemStackIn.isEmpty() && !MobEntity.isItemStackInSlot(equipmentslottype, itemStackIn) && equipmentslottype != EquipmentSlotType.HEAD) {
            return false;
        }
        this.setItemStackToSlot(equipmentslottype, itemStackIn);
        return true;
    }

    @Override
    public boolean canPassengerSteer() {
        return this.canBeSteered() && super.canPassengerSteer();
    }

    public static boolean isItemStackInSlot(EquipmentSlotType slotIn, ItemStack stack) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(stack);
        return equipmentslottype == slotIn || equipmentslottype == EquipmentSlotType.MAINHAND && slotIn == EquipmentSlotType.OFFHAND || equipmentslottype == EquipmentSlotType.OFFHAND && slotIn == EquipmentSlotType.MAINHAND;
    }

    @Override
    public boolean isServerWorld() {
        return super.isServerWorld() && !this.isAIDisabled();
    }

    public void setNoAI(boolean disable) {
        byte b0 = this.dataManager.get(AI_FLAGS);
        this.dataManager.set(AI_FLAGS, disable ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean leftHanded) {
        byte b0 = this.dataManager.get(AI_FLAGS);
        this.dataManager.set(AI_FLAGS, leftHanded ? (byte)(b0 | 2) : (byte)(b0 & 0xFFFFFFFD));
    }

    public void setAggroed(boolean hasAggro) {
        byte b0 = this.dataManager.get(AI_FLAGS);
        this.dataManager.set(AI_FLAGS, hasAggro ? (byte)(b0 | 4) : (byte)(b0 & 0xFFFFFFFB));
    }

    public boolean isAIDisabled() {
        return (this.dataManager.get(AI_FLAGS) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.dataManager.get(AI_FLAGS) & 2) != 0;
    }

    public boolean isAggressive() {
        return (this.dataManager.get(AI_FLAGS) & 4) != 0;
    }

    public void setChild(boolean childZombie) {
    }

    @Override
    public HandSide getPrimaryHand() {
        return this.isLeftHanded() ? HandSide.LEFT : HandSide.RIGHT;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target.getType() == EntityType.PLAYER && ((PlayerEntity)target).abilities.disableDamage ? false : super.canAttack(target);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag;
        int i;
        float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entityIn instanceof LivingEntity) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity)entityIn).getCreatureAttribute());
            f1 += (float)EnchantmentHelper.getKnockbackModifier(this);
        }
        if ((i = EnchantmentHelper.getFireAspectModifier(this)) > 0) {
            entityIn.setFire(i * 4);
        }
        if (flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f)) {
            if (f1 > 0.0f && entityIn instanceof LivingEntity) {
                ((LivingEntity)entityIn).applyKnockback(f1 * 0.5f, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)), -MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)));
                this.setMotion(this.getMotion().mul(0.6, 1.0, 0.6));
            }
            if (entityIn instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)entityIn;
                this.func_233655_a_(playerentity, this.getHeldItemMainhand(), playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY);
            }
            this.applyEnchantments(this, entityIn);
            this.setLastAttackedEntity(entityIn);
        }
        return flag;
    }

    private void func_233655_a_(PlayerEntity p_233655_1_, ItemStack p_233655_2_, ItemStack p_233655_3_) {
        if (!p_233655_2_.isEmpty() && !p_233655_3_.isEmpty() && p_233655_2_.getItem() instanceof AxeItem && p_233655_3_.getItem() == Items.SHIELD) {
            float f = 0.25f + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05f;
            if (this.rand.nextFloat() < f) {
                p_233655_1_.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                this.world.setEntityState(p_233655_1_, (byte)30);
            }
        }
    }

    protected boolean isInDaylight() {
        if (this.world.isDaytime() && !this.world.isRemote) {
            BlockPos blockpos;
            float f = this.getBrightness();
            BlockPos blockPos = blockpos = this.getRidingEntity() instanceof BoatEntity ? new BlockPos(this.getPosX(), Math.round(this.getPosY()), this.getPosZ()).up() : new BlockPos(this.getPosX(), Math.round(this.getPosY()), this.getPosZ());
            if (f > 0.5f && this.rand.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.world.canSeeSky(blockpos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void handleFluidJump(ITag<Fluid> fluidTag) {
        if (this.getNavigator().getCanSwim()) {
            super.handleFluidJump(fluidTag);
        } else {
            this.setMotion(this.getMotion().add(0.0, 0.3, 0.0));
        }
    }

    @Override
    protected void setDead() {
        super.setDead();
        this.clearLeashed(true, false);
    }

    private boolean canSkipUpdate() {
        double d1;
        if (this.isChild()) {
            return false;
        }
        if (this.hurtTime > 0) {
            return false;
        }
        if (this.ticksExisted < 20) {
            return false;
        }
        List list = this.getListPlayers(this.getEntityWorld());
        if (list == null) {
            return false;
        }
        if (list.size() != 1) {
            return false;
        }
        Entity entity = (Entity)list.get(0);
        double d0 = Math.max(Math.abs(this.getPosX() - entity.getPosX()) - 16.0, 0.0);
        double d2 = d0 * d0 + (d1 = Math.max(Math.abs(this.getPosZ() - entity.getPosZ()) - 16.0, 0.0)) * d1;
        return !this.isInRangeToRenderDist(d2);
    }

    private List getListPlayers(World p_getListPlayers_1_) {
        World world = this.getEntityWorld();
        if (world instanceof ClientWorld) {
            ClientWorld clientworld = (ClientWorld)world;
            return clientworld.getPlayers();
        }
        if (world instanceof ServerWorld) {
            ServerWorld serverworld = (ServerWorld)world;
            return serverworld.getPlayers();
        }
        return null;
    }

    private void onUpdateMinimal() {
        ++this.idleTime;
        if (this instanceof MonsterEntity) {
            float f = this.getBrightness();
            boolean flag = this instanceof AbstractRaiderEntity;
            if (f > 0.5f || flag) {
                this.idleTime += 2;
            }
        }
    }
}
