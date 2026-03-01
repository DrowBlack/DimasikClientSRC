package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class DolphinEntity
extends WaterMobEntity {
    private static final DataParameter<BlockPos> TREASURE_POS = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> GOT_FISH = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> MOISTNESS = EntityDataManager.createKey(DolphinEntity.class, DataSerializers.VARINT);
    private static final EntityPredicate field_213810_bA = new EntityPredicate().setDistance(10.0).allowFriendlyFire().allowInvulnerable().setLineOfSiteRequired();
    public static final Predicate<ItemEntity> ITEM_SELECTOR = p_205023_0_ -> !p_205023_0_.cannotPickup() && p_205023_0_.isAlive() && p_205023_0_.isInWater();

    public DolphinEntity(EntityType<? extends DolphinEntity> type, World worldIN) {
        super((EntityType<? extends WaterMobEntity>)type, worldIN);
        this.moveController = new MoveHelperController(this);
        this.lookController = new DolphinLookController(this, 10);
        this.setCanPickUpLoot(true);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setAir(this.getMaxAir());
        this.rotationPitch = 0.0f;
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    protected void updateAir(int p_209207_1_) {
    }

    public void setTreasurePos(BlockPos posIn) {
        this.dataManager.set(TREASURE_POS, posIn);
    }

    public BlockPos getTreasurePos() {
        return this.dataManager.get(TREASURE_POS);
    }

    public boolean hasGotFish() {
        return this.dataManager.get(GOT_FISH);
    }

    public void setGotFish(boolean p_208008_1_) {
        this.dataManager.set(GOT_FISH, p_208008_1_);
    }

    public int getMoistness() {
        return this.dataManager.get(MOISTNESS);
    }

    public void setMoistness(int p_211137_1_) {
        this.dataManager.set(MOISTNESS, p_211137_1_);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TREASURE_POS, BlockPos.ZERO);
        this.dataManager.register(GOT_FISH, false);
        this.dataManager.register(MOISTNESS, 2400);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("TreasurePosX", this.getTreasurePos().getX());
        compound.putInt("TreasurePosY", this.getTreasurePos().getY());
        compound.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        compound.putBoolean("GotFish", this.hasGotFish());
        compound.putInt("Moistness", this.getMoistness());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        int i = compound.getInt("TreasurePosX");
        int j = compound.getInt("TreasurePosY");
        int k = compound.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(i, j, k));
        super.readAdditional(compound);
        this.setGotFish(compound.getBoolean("GotFish"));
        this.setMoistness(compound.getInt("Moistness"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(0, new FindWaterGoal(this));
        this.goalSelector.addGoal(1, new SwimToTreasureGoal(this));
        this.goalSelector.addGoal(2, new SwimWithPlayerGoal(this, 4.0));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2f, true));
        this.goalSelector.addGoal(8, new PlayWithItemsGoal());
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<GuardianEntity>(this, GuardianEntity.class, 8.0f, 1.0, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, GuardianEntity.class).setCallsForHelp(new Class[0]));
    }

    public static AttributeModifierMap.MutableAttribute func_234190_eK_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 10.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 1.2f).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new SwimmerPathNavigator(this, worldIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.applyEnchantments(this, entityIn);
            this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0f, 1.0f);
        }
        return flag;
    }

    @Override
    public int getMaxAir() {
        return 4800;
    }

    @Override
    protected int determineNextAir(int currentAir) {
        return this.getMaxAir();
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.3f;
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 1;
    }

    @Override
    public int getHorizontalFaceSpeed() {
        return 1;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return true;
    }

    @Override
    public boolean canPickUpItem(ItemStack itemstackIn) {
        EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
        if (!this.getItemStackFromSlot(equipmentslottype).isEmpty()) {
            return false;
        }
        return equipmentslottype == EquipmentSlotType.MAINHAND && super.canPickUpItem(itemstackIn);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        ItemStack itemstack;
        if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && this.canEquipItem(itemstack = itemEntity.getItem())) {
            this.triggerItemPickupTrigger(itemEntity);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
            this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0f;
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAIDisabled()) {
            this.setAir(this.getMaxAir());
        } else {
            if (this.isInWaterRainOrBubbleColumn()) {
                this.setMoistness(2400);
            } else {
                this.setMoistness(this.getMoistness() - 1);
                if (this.getMoistness() <= 0) {
                    this.attackEntityFrom(DamageSource.DRYOUT, 1.0f);
                }
                if (this.onGround) {
                    this.setMotion(this.getMotion().add((this.rand.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.rand.nextFloat() * 2.0f - 1.0f) * 0.2f));
                    this.rotationYaw = this.rand.nextFloat() * 360.0f;
                    this.onGround = false;
                    this.isAirBorne = true;
                }
            }
            if (this.world.isRemote && this.isInWater() && this.getMotion().lengthSquared() > 0.03) {
                Vector3d vector3d = this.getLook(0.0f);
                float f = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)) * 0.3f;
                float f1 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180)) * 0.3f;
                float f2 = 1.2f - this.rand.nextFloat() * 0.7f;
                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(ParticleTypes.DOLPHIN, this.getPosX() - vector3d.x * (double)f2 + (double)f, this.getPosY() - vector3d.y, this.getPosZ() - vector3d.z * (double)f2 + (double)f1, 0.0, 0.0, 0.0);
                    this.world.addParticle(ParticleTypes.DOLPHIN, this.getPosX() - vector3d.x * (double)f2 - (double)f, this.getPosY() - vector3d.y, this.getPosZ() - vector3d.z * (double)f2 - (double)f1, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 38) {
            this.func_208401_a(ParticleTypes.HAPPY_VILLAGER);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    private void func_208401_a(IParticleData p_208401_1_) {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.01;
            double d1 = this.rand.nextGaussian() * 0.01;
            double d2 = this.rand.nextGaussian() * 0.01;
            this.world.addParticle(p_208401_1_, this.getPosXRandom(1.0), this.getPosYRandom() + 0.2, this.getPosZRandom(1.0), d0, d1, d2);
        }
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
        if (!itemstack.isEmpty() && itemstack.getItem().isIn(ItemTags.FISHES)) {
            if (!this.world.isRemote) {
                this.playSound(SoundEvents.ENTITY_DOLPHIN_EAT, 1.0f, 1.0f);
            }
            this.setGotFish(true);
            if (!p_230254_1_.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        return super.func_230254_b_(p_230254_1_, p_230254_2_);
    }

    public static boolean func_223364_b(EntityType<DolphinEntity> p_223364_0_, IWorld p_223364_1_, SpawnReason reason, BlockPos p_223364_3_, Random p_223364_4_) {
        if (p_223364_3_.getY() > 45 && p_223364_3_.getY() < p_223364_1_.getSeaLevel()) {
            Optional<RegistryKey<Biome>> optional = p_223364_1_.func_242406_i(p_223364_3_);
            return (!Objects.equals(optional, Optional.of(Biomes.OCEAN)) || !Objects.equals(optional, Optional.of(Biomes.DEEP_OCEAN))) && p_223364_1_.getFluidState(p_223364_3_).isTagged(FluidTags.WATER);
        }
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_DOLPHIN_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    protected boolean closeToTarget() {
        BlockPos blockpos = this.getNavigator().getTargetPos();
        return blockpos != null ? blockpos.withinDistance(this.getPositionVec(), 12.0) : false;
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return true;
    }

    static class MoveHelperController
    extends MovementController {
        private final DolphinEntity dolphin;

        public MoveHelperController(DolphinEntity dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        @Override
        public void tick() {
            if (this.dolphin.isInWater()) {
                this.dolphin.setMotion(this.dolphin.getMotion().add(0.0, 0.005, 0.0));
            }
            if (this.action == MovementController.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
                double d2;
                double d1;
                double d0 = this.posX - this.dolphin.getPosX();
                double d3 = d0 * d0 + (d1 = this.posY - this.dolphin.getPosY()) * d1 + (d2 = this.posZ - this.dolphin.getPosZ()) * d2;
                if (d3 < 2.500000277905201E-7) {
                    this.mob.setMoveForward(0.0f);
                } else {
                    float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875) - 90.0f;
                    this.dolphin.renderYawOffset = this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, f, 10.0f);
                    this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
                    float f1 = (float)(this.speed * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (this.dolphin.isInWater()) {
                        this.dolphin.setAIMoveSpeed(f1 * 0.02f);
                        float f2 = -((float)(MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * 57.2957763671875));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0f, 85.0f);
                        this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, f2, 5.0f);
                        float f3 = MathHelper.cos(this.dolphin.rotationPitch * ((float)Math.PI / 180));
                        float f4 = MathHelper.sin(this.dolphin.rotationPitch * ((float)Math.PI / 180));
                        this.dolphin.moveForward = f3 * f1;
                        this.dolphin.moveVertical = -f4 * f1;
                    } else {
                        this.dolphin.setAIMoveSpeed(f1 * 0.1f);
                    }
                }
            } else {
                this.dolphin.setAIMoveSpeed(0.0f);
                this.dolphin.setMoveStrafing(0.0f);
                this.dolphin.setMoveVertical(0.0f);
                this.dolphin.setMoveForward(0.0f);
            }
        }
    }

    static class SwimToTreasureGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private boolean field_208058_b;

        SwimToTreasureGoal(DolphinEntity dolphinIn) {
            this.dolphin = dolphinIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean isPreemptible() {
            return false;
        }

        @Override
        public boolean shouldExecute() {
            return this.dolphin.hasGotFish() && this.dolphin.getAir() >= 100;
        }

        @Override
        public boolean shouldContinueExecuting() {
            BlockPos blockpos = this.dolphin.getTreasurePos();
            return !new BlockPos((double)blockpos.getX(), this.dolphin.getPosY(), (double)blockpos.getZ()).withinDistance(this.dolphin.getPositionVec(), 4.0) && !this.field_208058_b && this.dolphin.getAir() >= 100;
        }

        @Override
        public void startExecuting() {
            if (this.dolphin.world instanceof ServerWorld) {
                ServerWorld serverworld = (ServerWorld)this.dolphin.world;
                this.field_208058_b = false;
                this.dolphin.getNavigator().clearPath();
                BlockPos blockpos = this.dolphin.getPosition();
                Structure<IFeatureConfig> structure = (double)serverworld.rand.nextFloat() >= 0.5 ? Structure.field_236377_m_ : Structure.field_236373_i_;
                BlockPos blockpos1 = serverworld.func_241117_a_(structure, blockpos, 50, false);
                if (blockpos1 == null) {
                    Structure<IFeatureConfig> structure1 = structure.equals(Structure.field_236377_m_) ? Structure.field_236373_i_ : Structure.field_236377_m_;
                    BlockPos blockpos2 = serverworld.func_241117_a_(structure1, blockpos, 50, false);
                    if (blockpos2 == null) {
                        this.field_208058_b = true;
                        return;
                    }
                    this.dolphin.setTreasurePos(blockpos2);
                } else {
                    this.dolphin.setTreasurePos(blockpos1);
                }
                serverworld.setEntityState(this.dolphin, (byte)38);
            }
        }

        @Override
        public void resetTask() {
            BlockPos blockpos = this.dolphin.getTreasurePos();
            if (new BlockPos((double)blockpos.getX(), this.dolphin.getPosY(), (double)blockpos.getZ()).withinDistance(this.dolphin.getPositionVec(), 4.0) || this.field_208058_b) {
                this.dolphin.setGotFish(false);
            }
        }

        @Override
        public void tick() {
            World world = this.dolphin.world;
            if (this.dolphin.closeToTarget() || this.dolphin.getNavigator().noPath()) {
                BlockPos blockpos;
                Vector3d vector3d = Vector3d.copyCentered(this.dolphin.getTreasurePos());
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.dolphin, 16, 1, vector3d, 0.3926991f);
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 4, vector3d);
                }
                if (!(vector3d1 == null || world.getFluidState(blockpos = new BlockPos(vector3d1)).isTagged(FluidTags.WATER) && world.getBlockState(blockpos).allowsMovement(world, blockpos, PathType.WATER))) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.dolphin, 8, 5, vector3d);
                }
                if (vector3d1 == null) {
                    this.field_208058_b = true;
                    return;
                }
                this.dolphin.getLookController().setLookPosition(vector3d1.x, vector3d1.y, vector3d1.z, this.dolphin.getHorizontalFaceSpeed() + 20, this.dolphin.getVerticalFaceSpeed());
                this.dolphin.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, 1.3);
                if (world.rand.nextInt(80) == 0) {
                    world.setEntityState(this.dolphin, (byte)38);
                }
            }
        }
    }

    static class SwimWithPlayerGoal
    extends Goal {
        private final DolphinEntity dolphin;
        private final double speed;
        private PlayerEntity targetPlayer;

        SwimWithPlayerGoal(DolphinEntity dolphinIn, double speedIn) {
            this.dolphin = dolphinIn;
            this.speed = speedIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            this.targetPlayer = this.dolphin.world.getClosestPlayer(field_213810_bA, this.dolphin);
            if (this.targetPlayer == null) {
                return false;
            }
            return this.targetPlayer.isSwimming() && this.dolphin.getAttackTarget() != this.targetPlayer;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.targetPlayer != null && this.targetPlayer.isSwimming() && this.dolphin.getDistanceSq(this.targetPlayer) < 256.0;
        }

        @Override
        public void startExecuting() {
            this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
        }

        @Override
        public void resetTask() {
            this.targetPlayer = null;
            this.dolphin.getNavigator().clearPath();
        }

        @Override
        public void tick() {
            this.dolphin.getLookController().setLookPositionWithEntity(this.targetPlayer, this.dolphin.getHorizontalFaceSpeed() + 20, this.dolphin.getVerticalFaceSpeed());
            if (this.dolphin.getDistanceSq(this.targetPlayer) < 6.25) {
                this.dolphin.getNavigator().clearPath();
            } else {
                this.dolphin.getNavigator().tryMoveToEntityLiving(this.targetPlayer, this.speed);
            }
            if (this.targetPlayer.isSwimming() && this.targetPlayer.world.rand.nextInt(6) == 0) {
                this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
            }
        }
    }

    class PlayWithItemsGoal
    extends Goal {
        private int field_205154_b;

        private PlayWithItemsGoal() {
        }

        @Override
        public boolean shouldExecute() {
            if (this.field_205154_b > DolphinEntity.this.ticksExisted) {
                return false;
            }
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0, 8.0, 8.0), ITEM_SELECTOR);
            return !list.isEmpty() || !DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
        }

        @Override
        public void startExecuting() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0, 8.0, 8.0), ITEM_SELECTOR);
            if (!list.isEmpty()) {
                DolphinEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), 1.2f);
                DolphinEntity.this.playSound(SoundEvents.ENTITY_DOLPHIN_PLAY, 1.0f, 1.0f);
            }
            this.field_205154_b = 0;
        }

        @Override
        public void resetTask() {
            ItemStack itemstack = DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                this.func_220810_a(itemstack);
                DolphinEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                this.field_205154_b = DolphinEntity.this.ticksExisted + DolphinEntity.this.rand.nextInt(100);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = DolphinEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, DolphinEntity.this.getBoundingBox().grow(8.0, 8.0, 8.0), ITEM_SELECTOR);
            ItemStack itemstack = DolphinEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                this.func_220810_a(itemstack);
                DolphinEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                DolphinEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), 1.2f);
            }
        }

        private void func_220810_a(ItemStack p_220810_1_) {
            if (!p_220810_1_.isEmpty()) {
                double d0 = DolphinEntity.this.getPosYEye() - (double)0.3f;
                ItemEntity itementity = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.getPosX(), d0, DolphinEntity.this.getPosZ(), p_220810_1_);
                itementity.setPickupDelay(40);
                itementity.setThrowerId(DolphinEntity.this.getUniqueID());
                float f = 0.3f;
                float f1 = DolphinEntity.this.rand.nextFloat() * ((float)Math.PI * 2);
                float f2 = 0.02f * DolphinEntity.this.rand.nextFloat();
                itementity.setMotion(0.3f * -MathHelper.sin(DolphinEntity.this.rotationYaw * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.rotationPitch * ((float)Math.PI / 180)) + MathHelper.cos(f1) * f2, 0.3f * MathHelper.sin(DolphinEntity.this.rotationPitch * ((float)Math.PI / 180)) * 1.5f, 0.3f * MathHelper.cos(DolphinEntity.this.rotationYaw * ((float)Math.PI / 180)) * MathHelper.cos(DolphinEntity.this.rotationPitch * ((float)Math.PI / 180)) + MathHelper.sin(f1) * f2);
                DolphinEntity.this.world.addEntity(itementity);
            }
        }
    }
}
