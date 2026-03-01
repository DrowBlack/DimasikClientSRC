package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity
extends ZombieEntity
implements IRangedAttackMob {
    private boolean swimmingUp;
    protected final SwimmerPathNavigator waterNavigator;
    protected final GroundPathNavigator groundNavigator;

    public DrownedEntity(EntityType<? extends DrownedEntity> type, World worldIn) {
        super((EntityType<? extends ZombieEntity>)type, worldIn);
        this.stepHeight = 1.0f;
        this.moveController = new MoveHelperController(this);
        this.setPathPriority(PathNodeType.WATER, 0.0f);
        this.waterNavigator = new SwimmerPathNavigator(this, worldIn);
        this.groundNavigator = new GroundPathNavigator(this, worldIn);
    }

    @Override
    protected void applyEntityAI() {
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TridentAttackGoal(this, 1.0, 40, 10.0f));
        this.goalSelector.addGoal(2, new AttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new GoToBeachGoal(this, 1.0));
        this.goalSelector.addGoal(6, new SwimUpGoal(this, 1.0, this.world.getSeaLevel()));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, DrownedEntity.class).setCallsForHelp(ZombifiedPiglinEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAttack));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillagerEntity>((MobEntity)this, AbstractVillagerEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, true, false, TurtleEntity.TARGET_DRY_BABY));
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (this.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty() && this.rand.nextFloat() < 0.03f) {
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.inventoryHandsDropChances[EquipmentSlotType.OFFHAND.getIndex()] = 2.0f;
        }
        return spawnDataIn;
    }

    public static boolean func_223332_b(EntityType<DrownedEntity> p_223332_0_, IServerWorld p_223332_1_, SpawnReason reason, BlockPos p_223332_3_, Random p_223332_4_) {
        boolean flag;
        Optional<RegistryKey<Biome>> optional = p_223332_1_.func_242406_i(p_223332_3_);
        boolean bl = flag = p_223332_1_.getDifficulty() != Difficulty.PEACEFUL && DrownedEntity.isValidLightLevel(p_223332_1_, p_223332_3_, p_223332_4_) && (reason == SpawnReason.SPAWNER || p_223332_1_.getFluidState(p_223332_3_).isTagged(FluidTags.WATER));
        if (!Objects.equals(optional, Optional.of(Biomes.RIVER)) && !Objects.equals(optional, Optional.of(Biomes.FROZEN_RIVER))) {
            return p_223332_4_.nextInt(40) == 0 && DrownedEntity.func_223333_a(p_223332_1_, p_223332_3_) && flag;
        }
        return p_223332_4_.nextInt(15) == 0 && flag;
    }

    private static boolean func_223333_a(IWorld p_223333_0_, BlockPos p_223333_1_) {
        return p_223333_1_.getY() < p_223333_0_.getSeaLevel() - 5;
    }

    @Override
    protected boolean canBreakDoors() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.ENTITY_DROWNED_AMBIENT_WATER : SoundEvents.ENTITY_DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isInWater() ? SoundEvents.ENTITY_DROWNED_HURT_WATER : SoundEvents.ENTITY_DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.ENTITY_DROWNED_DEATH_WATER : SoundEvents.ENTITY_DROWNED_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_DROWNED_STEP;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkullDrop() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        if ((double)this.rand.nextFloat() > 0.9) {
            int i = this.rand.nextInt(16);
            if (i < 10) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        if (existing.getItem() == Items.NAUTILUS_SHELL) {
            return false;
        }
        if (existing.getItem() == Items.TRIDENT) {
            if (candidate.getItem() == Items.TRIDENT) {
                return candidate.getDamage() < existing.getDamage();
            }
            return false;
        }
        return candidate.getItem() == Items.TRIDENT ? true : super.shouldExchangeEquipment(candidate, existing);
    }

    @Override
    protected boolean shouldDrown() {
        return false;
    }

    @Override
    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public boolean shouldAttack(@Nullable LivingEntity p_204714_1_) {
        if (p_204714_1_ != null) {
            return !this.world.isDaytime() || p_204714_1_.isInWater();
        }
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return !this.isSwimming();
    }

    private boolean func_204715_dF() {
        if (this.swimmingUp) {
            return true;
        }
        LivingEntity livingentity = this.getAttackTarget();
        return livingentity != null && livingentity.isInWater();
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
            this.moveRelative(0.01f, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.world.isRemote) {
            if (this.isServerWorld() && this.isInWater() && this.func_204715_dF()) {
                this.navigator = this.waterNavigator;
                this.setSwimming(true);
            } else {
                this.navigator = this.groundNavigator;
                this.setSwimming(false);
            }
        }
    }

    protected boolean isCloseToPathTarget() {
        double d0;
        BlockPos blockpos;
        Path path = this.getNavigator().getPath();
        return path != null && (blockpos = path.getTarget()) != null && (d0 = this.getDistanceSq(blockpos.getX(), blockpos.getY(), blockpos.getZ())) < 4.0;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        TridentEntity tridententity = new TridentEntity(this.world, (LivingEntity)this, new ItemStack(Items.TRIDENT));
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333) - tridententity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        tridententity.shoot(d0, d1 + d3 * (double)0.2f, d2, 1.6f, 14 - this.world.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.world.addEntity(tridententity);
    }

    public void setSwimmingUp(boolean p_204713_1_) {
        this.swimmingUp = p_204713_1_;
    }

    static class MoveHelperController
    extends MovementController {
        private final DrownedEntity drowned;

        public MoveHelperController(DrownedEntity p_i48909_1_) {
            super(p_i48909_1_);
            this.drowned = p_i48909_1_;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = this.drowned.getAttackTarget();
            if (this.drowned.func_204715_dF() && this.drowned.isInWater()) {
                if (livingentity != null && livingentity.getPosY() > this.drowned.getPosY() || this.drowned.swimmingUp) {
                    this.drowned.setMotion(this.drowned.getMotion().add(0.0, 0.002, 0.0));
                }
                if (this.action != MovementController.Action.MOVE_TO || this.drowned.getNavigator().noPath()) {
                    this.drowned.setAIMoveSpeed(0.0f);
                    return;
                }
                double d0 = this.posX - this.drowned.getPosX();
                double d1 = this.posY - this.drowned.getPosY();
                double d2 = this.posZ - this.drowned.getPosZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875) - 90.0f;
                this.drowned.renderYawOffset = this.drowned.rotationYaw = this.limitAngle(this.drowned.rotationYaw, f, 90.0f);
                float f1 = (float)(this.speed * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = MathHelper.lerp(0.125f, this.drowned.getAIMoveSpeed(), f1);
                this.drowned.setAIMoveSpeed(f2);
                this.drowned.setMotion(this.drowned.getMotion().add((double)f2 * d0 * 0.005, (double)f2 * d1 * 0.1, (double)f2 * d2 * 0.005));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setMotion(this.drowned.getMotion().add(0.0, -0.008, 0.0));
                }
                super.tick();
            }
        }
    }

    static class GoToWaterGoal
    extends Goal {
        private final CreatureEntity field_204730_a;
        private double field_204731_b;
        private double field_204732_c;
        private double field_204733_d;
        private final double field_204734_e;
        private final World field_204735_f;

        public GoToWaterGoal(CreatureEntity p_i48910_1_, double p_i48910_2_) {
            this.field_204730_a = p_i48910_1_;
            this.field_204734_e = p_i48910_2_;
            this.field_204735_f = p_i48910_1_.world;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            if (!this.field_204735_f.isDaytime()) {
                return false;
            }
            if (this.field_204730_a.isInWater()) {
                return false;
            }
            Vector3d vector3d = this.func_204729_f();
            if (vector3d == null) {
                return false;
            }
            this.field_204731_b = vector3d.x;
            this.field_204732_c = vector3d.y;
            this.field_204733_d = vector3d.z;
            return true;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.field_204730_a.getNavigator().noPath();
        }

        @Override
        public void startExecuting() {
            this.field_204730_a.getNavigator().tryMoveToXYZ(this.field_204731_b, this.field_204732_c, this.field_204733_d, this.field_204734_e);
        }

        @Nullable
        private Vector3d func_204729_f() {
            Random random = this.field_204730_a.getRNG();
            BlockPos blockpos = this.field_204730_a.getPosition();
            for (int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (!this.field_204735_f.getBlockState(blockpos1).isIn(Blocks.WATER)) continue;
                return Vector3d.copyCenteredHorizontally(blockpos1);
            }
            return null;
        }
    }

    static class TridentAttackGoal
    extends RangedAttackGoal {
        private final DrownedEntity field_204728_a;

        public TridentAttackGoal(IRangedAttackMob p_i48907_1_, double p_i48907_2_, int p_i48907_4_, float p_i48907_5_) {
            super(p_i48907_1_, p_i48907_2_, p_i48907_4_, p_i48907_5_);
            this.field_204728_a = (DrownedEntity)p_i48907_1_;
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && this.field_204728_a.getHeldItemMainhand().getItem() == Items.TRIDENT;
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            this.field_204728_a.setAggroed(true);
            this.field_204728_a.setActiveHand(Hand.MAIN_HAND);
        }

        @Override
        public void resetTask() {
            super.resetTask();
            this.field_204728_a.resetActiveHand();
            this.field_204728_a.setAggroed(false);
        }
    }

    static class AttackGoal
    extends ZombieAttackGoal {
        private final DrownedEntity field_204726_g;

        public AttackGoal(DrownedEntity p_i48913_1_, double p_i48913_2_, boolean p_i48913_4_) {
            super(p_i48913_1_, p_i48913_2_, p_i48913_4_);
            this.field_204726_g = p_i48913_1_;
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.field_204726_g.shouldAttack(this.field_204726_g.getAttackTarget());
        }
    }

    static class GoToBeachGoal
    extends MoveToBlockGoal {
        private final DrownedEntity drowned;

        public GoToBeachGoal(DrownedEntity p_i48911_1_, double p_i48911_2_) {
            super(p_i48911_1_, p_i48911_2_, 8, 2);
            this.drowned = p_i48911_1_;
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && !this.drowned.world.isDaytime() && this.drowned.isInWater() && this.drowned.getPosY() >= (double)(this.drowned.world.getSeaLevel() - 3);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting();
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            BlockPos blockpos = pos.up();
            return worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up()) ? worldIn.getBlockState(pos).canSpawnMobs(worldIn, pos, this.drowned) : false;
        }

        @Override
        public void startExecuting() {
            this.drowned.setSwimmingUp(false);
            this.drowned.navigator = this.drowned.groundNavigator;
            super.startExecuting();
        }

        @Override
        public void resetTask() {
            super.resetTask();
        }
    }

    static class SwimUpGoal
    extends Goal {
        private final DrownedEntity field_204736_a;
        private final double field_204737_b;
        private final int targetY;
        private boolean obstructed;

        public SwimUpGoal(DrownedEntity p_i48908_1_, double p_i48908_2_, int p_i48908_4_) {
            this.field_204736_a = p_i48908_1_;
            this.field_204737_b = p_i48908_2_;
            this.targetY = p_i48908_4_;
        }

        @Override
        public boolean shouldExecute() {
            return !this.field_204736_a.world.isDaytime() && this.field_204736_a.isInWater() && this.field_204736_a.getPosY() < (double)(this.targetY - 2);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute() && !this.obstructed;
        }

        @Override
        public void tick() {
            if (this.field_204736_a.getPosY() < (double)(this.targetY - 1) && (this.field_204736_a.getNavigator().noPath() || this.field_204736_a.isCloseToPathTarget())) {
                Vector3d vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_204736_a, 4, 8, new Vector3d(this.field_204736_a.getPosX(), this.targetY - 1, this.field_204736_a.getPosZ()));
                if (vector3d == null) {
                    this.obstructed = true;
                    return;
                }
                this.field_204736_a.getNavigator().tryMoveToXYZ(vector3d.x, vector3d.y, vector3d.z, this.field_204737_b);
            }
        }

        @Override
        public void startExecuting() {
            this.field_204736_a.setSwimmingUp(true);
            this.obstructed = false;
        }

        @Override
        public void resetTask() {
            this.field_204736_a.setSwimmingUp(false);
        }
    }
}
