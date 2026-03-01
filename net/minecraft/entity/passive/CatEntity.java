package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.CatLieOnBedGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class CatEntity
extends TameableEntity {
    private static final Ingredient BREEDING_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON);
    private static final DataParameter<Integer> CAT_TYPE = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> field_213428_bG = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> field_213429_bH = EntityDataManager.createKey(CatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(CatEntity.class, DataSerializers.VARINT);
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_ID = Util.make(Maps.newHashMap(), p_213410_0_ -> {
        p_213410_0_.put(0, new ResourceLocation("textures/entity/cat/tabby.png"));
        p_213410_0_.put(1, new ResourceLocation("textures/entity/cat/black.png"));
        p_213410_0_.put(2, new ResourceLocation("textures/entity/cat/red.png"));
        p_213410_0_.put(3, new ResourceLocation("textures/entity/cat/siamese.png"));
        p_213410_0_.put(4, new ResourceLocation("textures/entity/cat/british_shorthair.png"));
        p_213410_0_.put(5, new ResourceLocation("textures/entity/cat/calico.png"));
        p_213410_0_.put(6, new ResourceLocation("textures/entity/cat/persian.png"));
        p_213410_0_.put(7, new ResourceLocation("textures/entity/cat/ragdoll.png"));
        p_213410_0_.put(8, new ResourceLocation("textures/entity/cat/white.png"));
        p_213410_0_.put(9, new ResourceLocation("textures/entity/cat/jellie.png"));
        p_213410_0_.put(10, new ResourceLocation("textures/entity/cat/all_black.png"));
    });
    private AvoidPlayerGoal<PlayerEntity> avoidPlayerGoal;
    private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
    private float field_213433_bL;
    private float field_213434_bM;
    private float field_213435_bN;
    private float field_213436_bO;
    private float field_213437_bP;
    private float field_213438_bQ;

    public CatEntity(EntityType<? extends CatEntity> type, World worldIn) {
        super((EntityType<? extends TameableEntity>)type, worldIn);
    }

    public ResourceLocation getCatTypeName() {
        return TEXTURE_BY_ID.getOrDefault(this.getCatType(), TEXTURE_BY_ID.get(0));
    }

    @Override
    protected void registerGoals() {
        this.temptGoal = new TemptGoal(this, 0.6, BREEDING_ITEMS, true);
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(1, new SitGoal(this));
        this.goalSelector.addGoal(2, new MorningGiftGoal(this));
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(5, new CatLieOnBedGoal(this, 1.1, 8));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f, false));
        this.goalSelector.addGoal(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.addGoal(8, new LeapAtTargetGoal(this, 0.3f));
        this.goalSelector.addGoal(9, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(10, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal((CreatureEntity)this, 0.8, 1.0000001E-5f));
        this.goalSelector.addGoal(12, new LookAtGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.addGoal(1, new NonTamedTargetGoal<RabbitEntity>(this, RabbitEntity.class, false, null));
        this.targetSelector.addGoal(1, new NonTamedTargetGoal<TurtleEntity>(this, TurtleEntity.class, false, TurtleEntity.TARGET_DRY_BABY));
    }

    public int getCatType() {
        return this.dataManager.get(CAT_TYPE);
    }

    public void setCatType(int type) {
        if (type < 0 || type >= 11) {
            type = this.rand.nextInt(10);
        }
        this.dataManager.set(CAT_TYPE, type);
    }

    public void func_213419_u(boolean p_213419_1_) {
        this.dataManager.set(field_213428_bG, p_213419_1_);
    }

    public boolean func_213416_eg() {
        return this.dataManager.get(field_213428_bG);
    }

    public void func_213415_v(boolean p_213415_1_) {
        this.dataManager.set(field_213429_bH, p_213415_1_);
    }

    public boolean func_213409_eh() {
        return this.dataManager.get(field_213429_bH);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataManager.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color) {
        this.dataManager.set(COLLAR_COLOR, color.getId());
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CAT_TYPE, 1);
        this.dataManager.register(field_213428_bG, false);
        this.dataManager.register(field_213429_bH, false);
        this.dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("CatType", this.getCatType());
        compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setCatType(compound.getInt("CatType"));
        if (compound.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
        }
    }

    @Override
    public void updateAITasks() {
        if (this.getMoveHelper().isUpdating()) {
            double d0 = this.getMoveHelper().getSpeed();
            if (d0 == 0.6) {
                this.setPose(Pose.CROUCHING);
                this.setSprinting(false);
            } else if (d0 == 1.33) {
                this.setPose(Pose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(Pose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(Pose.STANDING);
            this.setSprinting(false);
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isTamed()) {
            if (this.isInLove()) {
                return SoundEvents.ENTITY_CAT_PURR;
            }
            return this.rand.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
        }
        return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
    }

    @Override
    public int getTalkInterval() {
        return 120;
    }

    public void func_213420_ej() {
        this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAT_DEATH;
    }

    public static AttributeModifierMap.MutableAttribute func_234184_eY_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3f).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void consumeItemFromStack(PlayerEntity player, ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0f, 1.0f);
        }
        super.consumeItemFromStack(player, stack);
    }

    private float func_226510_eF_() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_226510_eF_());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTamed() && this.ticksExisted % 100 == 0) {
            this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
        }
        this.func_213412_ek();
    }

    private void func_213412_ek() {
        if ((this.func_213416_eg() || this.func_213409_eh()) && this.ticksExisted % 5 == 0) {
            this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6f + 0.4f * (this.rand.nextFloat() - this.rand.nextFloat()), 1.0f);
        }
        this.func_213418_el();
        this.func_213411_em();
    }

    private void func_213418_el() {
        this.field_213434_bM = this.field_213433_bL;
        this.field_213436_bO = this.field_213435_bN;
        if (this.func_213416_eg()) {
            this.field_213433_bL = Math.min(1.0f, this.field_213433_bL + 0.15f);
            this.field_213435_bN = Math.min(1.0f, this.field_213435_bN + 0.08f);
        } else {
            this.field_213433_bL = Math.max(0.0f, this.field_213433_bL - 0.22f);
            this.field_213435_bN = Math.max(0.0f, this.field_213435_bN - 0.13f);
        }
    }

    private void func_213411_em() {
        this.field_213438_bQ = this.field_213437_bP;
        this.field_213437_bP = this.func_213409_eh() ? Math.min(1.0f, this.field_213437_bP + 0.1f) : Math.max(0.0f, this.field_213437_bP - 0.13f);
    }

    public float func_213408_v(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.field_213434_bM, this.field_213433_bL);
    }

    public float func_213421_w(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.field_213436_bO, this.field_213435_bN);
    }

    public float func_213424_x(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.field_213438_bQ, this.field_213437_bP);
    }

    @Override
    public CatEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        CatEntity catentity = EntityType.CAT.create(p_241840_1_);
        if (p_241840_2_ instanceof CatEntity) {
            if (this.rand.nextBoolean()) {
                catentity.setCatType(this.getCatType());
            } else {
                catentity.setCatType(((CatEntity)p_241840_2_).getCatType());
            }
            if (this.isTamed()) {
                catentity.setOwnerId(this.getOwnerId());
                catentity.setTamed(true);
                if (this.rand.nextBoolean()) {
                    catentity.setCollarColor(this.getCollarColor());
                } else {
                    catentity.setCollarColor(((CatEntity)p_241840_2_).getCollarColor());
                }
            }
        }
        return catentity;
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        if (!this.isTamed()) {
            return false;
        }
        if (!(otherAnimal instanceof CatEntity)) {
            return false;
        }
        CatEntity catentity = (CatEntity)otherAnimal;
        return catentity.isTamed() && super.canMateWith(otherAnimal);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (worldIn.getMoonFactor() > 0.9f) {
            this.setCatType(this.rand.nextInt(11));
        } else {
            this.setCatType(this.rand.nextInt(10));
        }
        ServerWorld world = worldIn.getWorld();
        if (world instanceof ServerWorld && world.func_241112_a_().func_235010_a_(this.getPosition(), true, Structure.field_236374_j_).isValid()) {
            this.setCatType(10);
            this.enablePersistence();
        }
        return spawnDataIn;
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ActionResultType actionresulttype1;
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        Item item = itemstack.getItem();
        if (this.world.isRemote) {
            if (this.isTamed() && this.isOwner(p_230254_1_)) {
                return ActionResultType.SUCCESS;
            }
            return !this.isBreedingItem(itemstack) || !(this.getHealth() < this.getMaxHealth()) && this.isTamed() ? ActionResultType.PASS : ActionResultType.SUCCESS;
        }
        if (this.isTamed()) {
            if (this.isOwner(p_230254_1_)) {
                if (!(item instanceof DyeItem)) {
                    if (item.isFood() && this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                        this.consumeItemFromStack(p_230254_1_, itemstack);
                        this.heal(item.getFood().getHealing());
                        return ActionResultType.CONSUME;
                    }
                    ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
                    if (!actionresulttype.isSuccessOrConsume() || this.isChild()) {
                        this.func_233687_w_(!this.isSitting());
                    }
                    return actionresulttype;
                }
                DyeColor dyecolor = ((DyeItem)item).getDyeColor();
                if (dyecolor != this.getCollarColor()) {
                    this.setCollarColor(dyecolor);
                    if (!p_230254_1_.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    this.enablePersistence();
                    return ActionResultType.CONSUME;
                }
            }
        } else if (this.isBreedingItem(itemstack)) {
            this.consumeItemFromStack(p_230254_1_, itemstack);
            if (this.rand.nextInt(3) == 0) {
                this.setTamedBy(p_230254_1_);
                this.func_233687_w_(true);
                this.world.setEntityState(this, (byte)7);
            } else {
                this.world.setEntityState(this, (byte)6);
            }
            this.enablePersistence();
            return ActionResultType.CONSUME;
        }
        if ((actionresulttype1 = super.func_230254_b_(p_230254_1_, p_230254_2_)).isSuccessOrConsume()) {
            this.enablePersistence();
        }
        return actionresulttype1;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_ITEMS.test(stack);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5f;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return !this.isTamed() && this.ticksExisted > 2400;
    }

    @Override
    protected void setupTamedAI() {
        if (this.avoidPlayerGoal == null) {
            this.avoidPlayerGoal = new AvoidPlayerGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.removeGoal(this.avoidPlayerGoal);
        if (!this.isTamed()) {
            this.goalSelector.addGoal(4, this.avoidPlayerGoal);
        }
    }

    static class TemptGoal
    extends net.minecraft.entity.ai.goal.TemptGoal {
        @Nullable
        private PlayerEntity temptingPlayer;
        private final CatEntity cat;

        public TemptGoal(CatEntity catIn, double speedIn, Ingredient temptItemsIn, boolean scaredByPlayerMovementIn) {
            super((CreatureEntity)catIn, speedIn, temptItemsIn, scaredByPlayerMovementIn);
            this.cat = catIn;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.temptingPlayer == null && this.creature.getRNG().nextInt(600) == 0) {
                this.temptingPlayer = this.closestPlayer;
            } else if (this.creature.getRNG().nextInt(500) == 0) {
                this.temptingPlayer = null;
            }
        }

        @Override
        protected boolean isScaredByPlayerMovement() {
            return this.temptingPlayer != null && this.temptingPlayer.equals(this.closestPlayer) ? false : super.isScaredByPlayerMovement();
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && !this.cat.isTamed();
        }
    }

    static class MorningGiftGoal
    extends Goal {
        private final CatEntity cat;
        private PlayerEntity owner;
        private BlockPos bedPos;
        private int tickCounter;

        public MorningGiftGoal(CatEntity catIn) {
            this.cat = catIn;
        }

        @Override
        public boolean shouldExecute() {
            if (!this.cat.isTamed()) {
                return false;
            }
            if (this.cat.isSitting()) {
                return false;
            }
            LivingEntity livingentity = this.cat.getOwner();
            if (livingentity instanceof PlayerEntity) {
                this.owner = (PlayerEntity)livingentity;
                if (!livingentity.isSleeping()) {
                    return false;
                }
                if (this.cat.getDistanceSq(this.owner) > 100.0) {
                    return false;
                }
                BlockPos blockpos = this.owner.getPosition();
                BlockState blockstate = this.cat.world.getBlockState(blockpos);
                if (blockstate.getBlock().isIn(BlockTags.BEDS)) {
                    this.bedPos = blockstate.func_235903_d_(BedBlock.HORIZONTAL_FACING).map(p_234186_1_ -> blockpos.offset(p_234186_1_.getOpposite())).orElseGet(() -> new BlockPos(blockpos));
                    return !this.func_220805_g();
                }
            }
            return false;
        }

        private boolean func_220805_g() {
            for (CatEntity catentity : this.cat.world.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB(this.bedPos).grow(2.0))) {
                if (catentity == this.cat || !catentity.func_213416_eg() && !catentity.func_213409_eh()) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.func_220805_g();
        }

        @Override
        public void startExecuting() {
            if (this.bedPos != null) {
                this.cat.setSleeping(false);
                this.cat.getNavigator().tryMoveToXYZ(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
            }
        }

        @Override
        public void resetTask() {
            this.cat.func_213419_u(false);
            float f = this.cat.world.func_242415_f(1.0f);
            if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.cat.world.getRandom().nextFloat() < 0.7) {
                this.func_220804_h();
            }
            this.tickCounter = 0;
            this.cat.func_213415_v(false);
            this.cat.getNavigator().clearPath();
        }

        private void func_220804_h() {
            Random random = this.cat.getRNG();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            blockpos$mutable.setPos(this.cat.getPosition());
            this.cat.attemptTeleport(blockpos$mutable.getX() + random.nextInt(11) - 5, blockpos$mutable.getY() + random.nextInt(5) - 2, blockpos$mutable.getZ() + random.nextInt(11) - 5, false);
            blockpos$mutable.setPos(this.cat.getPosition());
            LootTable loottable = this.cat.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_CAT_MORNING_GIFT);
            LootContext.Builder lootcontext$builder = new LootContext.Builder((ServerWorld)this.cat.world).withParameter(LootParameters.field_237457_g_, this.cat.getPositionVec()).withParameter(LootParameters.THIS_ENTITY, this.cat).withRandom(random);
            for (ItemStack itemstack : loottable.generate(lootcontext$builder.build(LootParameterSets.GIFT))) {
                this.cat.world.addEntity(new ItemEntity(this.cat.world, (double)blockpos$mutable.getX() - (double)MathHelper.sin(this.cat.renderYawOffset * ((float)Math.PI / 180)), blockpos$mutable.getY(), (double)blockpos$mutable.getZ() + (double)MathHelper.cos(this.cat.renderYawOffset * ((float)Math.PI / 180)), itemstack));
            }
        }

        @Override
        public void tick() {
            if (this.owner != null && this.bedPos != null) {
                this.cat.setSleeping(false);
                this.cat.getNavigator().tryMoveToXYZ(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
                if (this.cat.getDistanceSq(this.owner) < 2.5) {
                    ++this.tickCounter;
                    if (this.tickCounter > 16) {
                        this.cat.func_213419_u(true);
                        this.cat.func_213415_v(false);
                    } else {
                        this.cat.faceEntity(this.owner, 45.0f, 45.0f);
                        this.cat.func_213415_v(true);
                    }
                } else {
                    this.cat.func_213419_u(false);
                }
            }
        }
    }

    static class AvoidPlayerGoal<T extends LivingEntity>
    extends AvoidEntityGoal<T> {
        private final CatEntity cat;

        public AvoidPlayerGoal(CatEntity catIn, Class<T> entityClassToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
            super(catIn, entityClassToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntityPredicates.CAN_AI_TARGET::test);
            this.cat = catIn;
        }

        @Override
        public boolean shouldExecute() {
            return !this.cat.isTamed() && super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.cat.isTamed() && super.shouldContinueExecuting();
        }
    }
}
