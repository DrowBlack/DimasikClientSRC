package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TurtleEntity
extends AnimalEntity {
    private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DIGGING = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(TurtleEntity.class, DataSerializers.BOOLEAN);
    private int isDigging;
    public static final Predicate<LivingEntity> TARGET_DRY_BABY = p_213616_0_ -> p_213616_0_.isChild() && !p_213616_0_.isInWater();

    public TurtleEntity(EntityType<? extends TurtleEntity> type, World worldIn) {
        super((EntityType<? extends AnimalEntity>)type, worldIn);
        this.setPathPriority(PathNodeType.WATER, 0.0f);
        this.moveController = new MoveHelperController(this);
        this.stepHeight = 1.0f;
    }

    public void setHome(BlockPos position) {
        this.dataManager.set(HOME_POS, position);
    }

    private BlockPos getHome() {
        return this.dataManager.get(HOME_POS);
    }

    private void setTravelPos(BlockPos position) {
        this.dataManager.set(TRAVEL_POS, position);
    }

    private BlockPos getTravelPos() {
        return this.dataManager.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.dataManager.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.dataManager.set(HAS_EGG, hasEgg);
    }

    public boolean isDigging() {
        return this.dataManager.get(IS_DIGGING);
    }

    private void setDigging(boolean isDigging) {
        this.isDigging = isDigging ? 1 : 0;
        this.dataManager.set(IS_DIGGING, isDigging);
    }

    private boolean isGoingHome() {
        return this.dataManager.get(GOING_HOME);
    }

    private void setGoingHome(boolean isGoingHome) {
        this.dataManager.set(GOING_HOME, isGoingHome);
    }

    private boolean isTravelling() {
        return this.dataManager.get(TRAVELLING);
    }

    private void setTravelling(boolean isTravelling) {
        this.dataManager.set(TRAVELLING, isTravelling);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HOME_POS, BlockPos.ZERO);
        this.dataManager.register(HAS_EGG, false);
        this.dataManager.register(TRAVEL_POS, BlockPos.ZERO);
        this.dataManager.register(GOING_HOME, false);
        this.dataManager.register(TRAVELLING, false);
        this.dataManager.register(IS_DIGGING, false);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("HomePosX", this.getHome().getX());
        compound.putInt("HomePosY", this.getHome().getY());
        compound.putInt("HomePosZ", this.getHome().getZ());
        compound.putBoolean("HasEgg", this.hasEgg());
        compound.putInt("TravelPosX", this.getTravelPos().getX());
        compound.putInt("TravelPosY", this.getTravelPos().getY());
        compound.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        int i = compound.getInt("HomePosX");
        int j = compound.getInt("HomePosY");
        int k = compound.getInt("HomePosZ");
        this.setHome(new BlockPos(i, j, k));
        super.readAdditional(compound);
        this.setHasEgg(compound.getBoolean("HasEgg"));
        int l = compound.getInt("TravelPosX");
        int i1 = compound.getInt("TravelPosY");
        int j1 = compound.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(l, i1, j1));
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setHome(this.getPosition());
        this.setTravelPos(BlockPos.ZERO);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static boolean func_223322_c(EntityType<TurtleEntity> p_223322_0_, IWorld p_223322_1_, SpawnReason reason, BlockPos p_223322_3_, Random p_223322_4_) {
        return p_223322_3_.getY() < p_223322_1_.getSeaLevel() + 4 && TurtleEggBlock.hasProperHabitat(p_223322_1_, p_223322_3_) && p_223322_1_.getLightSubtracted(p_223322_3_, 0) > 8;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0));
        this.goalSelector.addGoal(1, new LayEggGoal(this, 1.0));
        this.goalSelector.addGoal(2, new PlayerTemptGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
        this.goalSelector.addGoal(3, new GoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(4, new GoHomeGoal(this, 1.0));
        this.goalSelector.addGoal(7, new TravelGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(9, new WanderGoal(this, 1.0, 100));
    }

    public static AttributeModifierMap.MutableAttribute func_234228_eK_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 30.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    @Override
    public int getTalkInterval() {
        return 200;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return !this.isInWater() && this.onGround && !this.isChild() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float volume) {
        super.playSwimSound(volume * 1.5f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_TURTLE_SWIM;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isChild() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return this.isChild() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        SoundEvent soundevent = this.isChild() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
        this.playSound(soundevent, 0.15f, 1.0f);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.15f;
    }

    @Override
    public float getRenderScale() {
        return this.isChild() ? 0.3f : 1.0f;
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new Navigator(this, worldIn);
    }

    @Override
    @Nullable
    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.TURTLE.create(p_241840_1_);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (!this.isGoingHome() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
            return 10.0f;
        }
        return TurtleEggBlock.hasProperHabitat(worldIn, pos) ? 10.0f : worldIn.getBrightness(pos) - 0.5f;
    }

    @Override
    public void livingTick() {
        BlockPos blockpos;
        super.livingTick();
        if (this.isAlive() && this.isDigging() && this.isDigging >= 1 && this.isDigging % 5 == 0 && TurtleEggBlock.hasProperHabitat(this.world, blockpos = this.getPosition())) {
            this.world.playEvent(2001, blockpos, Block.getStateId(Blocks.SAND.getDefaultState()));
        }
    }

    @Override
    protected void onGrowingAdult() {
        super.onGrowingAdult();
        if (!this.isChild() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.entityDropItem(Items.SCUTE, 1);
        }
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(0.1f, travelVector);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9));
            if (!(this.getAttackTarget() != null || this.isGoingHome() && this.getHome().withinDistance(this.getPositionVec(), 20.0))) {
                this.setMotion(this.getMotion().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return false;
    }

    @Override
    public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
        this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static class MoveHelperController
    extends MovementController {
        private final TurtleEntity turtle;

        MoveHelperController(TurtleEntity turtleIn) {
            super(turtleIn);
            this.turtle = turtleIn;
        }

        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setMotion(this.turtle.getMotion().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 16.0)) {
                    this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isChild()) {
                    this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 3.0f, 0.06f));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0f, 0.06f));
            }
        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.action == MovementController.Action.MOVE_TO && !this.turtle.getNavigator().noPath()) {
                double d0 = this.posX - this.turtle.getPosX();
                double d1 = this.posY - this.turtle.getPosY();
                double d2 = this.posZ - this.turtle.getPosZ();
                double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875) - 90.0f;
                this.turtle.renderYawOffset = this.turtle.rotationYaw = this.limitAngle(this.turtle.rotationYaw, f, 90.0f);
                float f1 = (float)(this.speed * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.turtle.setAIMoveSpeed(MathHelper.lerp(0.125f, this.turtle.getAIMoveSpeed(), f1));
                this.turtle.setMotion(this.turtle.getMotion().add(0.0, (double)this.turtle.getAIMoveSpeed() * (d1 /= d3) * 0.1, 0.0));
            } else {
                this.turtle.setAIMoveSpeed(0.0f);
            }
        }
    }

    static class PanicGoal
    extends net.minecraft.entity.ai.goal.PanicGoal {
        PanicGoal(TurtleEntity turtle, double speedIn) {
            super(turtle, speedIn);
        }

        @Override
        public boolean shouldExecute() {
            if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
                return false;
            }
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 7, 4);
            if (blockpos != null) {
                this.randPosX = blockpos.getX();
                this.randPosY = blockpos.getY();
                this.randPosZ = blockpos.getZ();
                return true;
            }
            return this.findRandomPosition();
        }
    }

    static class MateGoal
    extends BreedGoal {
        private final TurtleEntity turtle;

        MateGoal(TurtleEntity turtle, double speedIn) {
            super(turtle, speedIn);
            this.turtle = turtle;
        }

        @Override
        public boolean shouldExecute() {
            return super.shouldExecute() && !this.turtle.hasEgg();
        }

        @Override
        protected void spawnBaby() {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
                serverplayerentity = this.targetMate.getLoveCause();
            }
            if (serverplayerentity != null) {
                serverplayerentity.addStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            Random random = this.animal.getRNG();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), random.nextInt(7) + 1));
            }
        }
    }

    static class LayEggGoal
    extends MoveToBlockGoal {
        private final TurtleEntity turtle;

        LayEggGoal(TurtleEntity turtle, double speedIn) {
            super(turtle, speedIn, 16);
            this.turtle = turtle;
        }

        @Override
        public boolean shouldExecute() {
            return this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0) ? super.shouldExecute() : false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return super.shouldContinueExecuting() && this.turtle.hasEgg() && this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 9.0);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos blockpos = this.turtle.getPosition();
            if (!this.turtle.isInWater() && this.getIsAboveDestination()) {
                if (this.turtle.isDigging < 1) {
                    this.turtle.setDigging(true);
                } else if (this.turtle.isDigging > 200) {
                    World world = this.turtle.world;
                    world.playSound(null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3f, 0.9f + world.rand.nextFloat() * 0.2f);
                    world.setBlockState(this.destinationBlock.up(), (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.rand.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setDigging(false);
                    this.turtle.setInLove(600);
                }
                if (this.turtle.isDigging()) {
                    ++this.turtle.isDigging;
                }
            }
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return !worldIn.isAirBlock(pos.up()) ? false : TurtleEggBlock.isProperHabitat(worldIn, pos);
        }
    }

    static class PlayerTemptGoal
    extends Goal {
        private static final EntityPredicate field_220834_a = new EntityPredicate().setDistance(10.0).allowFriendlyFire().allowInvulnerable();
        private final TurtleEntity turtle;
        private final double speed;
        private PlayerEntity tempter;
        private int cooldown;
        private final Set<Item> temptItems;

        PlayerTemptGoal(TurtleEntity turtle, double speedIn, Item temptItem) {
            this.turtle = turtle;
            this.speed = speedIn;
            this.temptItems = Sets.newHashSet(temptItem);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean shouldExecute() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            }
            this.tempter = this.turtle.world.getClosestPlayer(field_220834_a, this.turtle);
            if (this.tempter == null) {
                return false;
            }
            return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
        }

        private boolean isTemptedBy(ItemStack p_203131_1_) {
            return this.temptItems.contains(p_203131_1_.getItem());
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.shouldExecute();
        }

        @Override
        public void resetTask() {
            this.tempter = null;
            this.turtle.getNavigator().clearPath();
            this.cooldown = 100;
        }

        @Override
        public void tick() {
            this.turtle.getLookController().setLookPositionWithEntity(this.tempter, this.turtle.getHorizontalFaceSpeed() + 20, this.turtle.getVerticalFaceSpeed());
            if (this.turtle.getDistanceSq(this.tempter) < 6.25) {
                this.turtle.getNavigator().clearPath();
            } else {
                this.turtle.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
            }
        }
    }

    static class GoToWaterGoal
    extends MoveToBlockGoal {
        private final TurtleEntity turtle;

        private GoToWaterGoal(TurtleEntity turtle, double speedIn) {
            super(turtle, turtle.isChild() ? 2.0 : speedIn, 24);
            this.turtle = turtle;
            this.field_203112_e = -1;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.turtle.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.turtle.world, this.destinationBlock);
        }

        @Override
        public boolean shouldExecute() {
            if (this.turtle.isChild() && !this.turtle.isInWater()) {
                return super.shouldExecute();
            }
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
        }

        @Override
        public boolean shouldMove() {
            return this.timeoutCounter % 160 == 0;
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).isIn(Blocks.WATER);
        }
    }

    static class GoHomeGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean field_203129_c;
        private int field_203130_d;

        GoHomeGoal(TurtleEntity turtle, double speedIn) {
            this.turtle = turtle;
            this.speed = speedIn;
        }

        @Override
        public boolean shouldExecute() {
            if (this.turtle.isChild()) {
                return false;
            }
            if (this.turtle.hasEgg()) {
                return true;
            }
            if (this.turtle.getRNG().nextInt(700) != 0) {
                return false;
            }
            return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 64.0);
        }

        @Override
        public void startExecuting() {
            this.turtle.setGoingHome(true);
            this.field_203129_c = false;
            this.field_203130_d = 0;
        }

        @Override
        public void resetTask() {
            this.turtle.setGoingHome(false);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.turtle.getHome().withinDistance(this.turtle.getPositionVec(), 7.0) && !this.field_203129_c && this.field_203130_d <= 600;
        }

        @Override
        public void tick() {
            BlockPos blockpos = this.turtle.getHome();
            boolean flag = blockpos.withinDistance(this.turtle.getPositionVec(), 16.0);
            if (flag) {
                ++this.field_203130_d;
            }
            if (this.turtle.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockpos);
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, vector3d, 0.3141592741012573);
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, vector3d);
                }
                if (vector3d1 != null && !flag && !this.turtle.world.getBlockState(new BlockPos(vector3d1)).isIn(Blocks.WATER)) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 16, 5, vector3d);
                }
                if (vector3d1 == null) {
                    this.field_203129_c = true;
                    return;
                }
                this.turtle.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }
        }
    }

    static class TravelGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean field_203139_c;

        TravelGoal(TurtleEntity turtle, double speedIn) {
            this.turtle = turtle;
            this.speed = speedIn;
        }

        @Override
        public boolean shouldExecute() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        @Override
        public void startExecuting() {
            int i = 512;
            int j = 4;
            Random random = this.turtle.rand;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int i1 = random.nextInt(1025) - 512;
            if ((double)l + this.turtle.getPosY() > (double)(this.turtle.world.getSeaLevel() - 1)) {
                l = 0;
            }
            BlockPos blockpos = new BlockPos((double)k + this.turtle.getPosX(), (double)l + this.turtle.getPosY(), (double)i1 + this.turtle.getPosZ());
            this.turtle.setTravelPos(blockpos);
            this.turtle.setTravelling(true);
            this.field_203139_c = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigator().noPath()) {
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.turtle.getTravelPos());
                Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, vector3d, 0.3141592741012573);
                if (vector3d1 == null) {
                    vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, vector3d);
                }
                if (vector3d1 != null) {
                    int i = MathHelper.floor(vector3d1.x);
                    int j = MathHelper.floor(vector3d1.z);
                    int k = 34;
                    if (!this.turtle.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        vector3d1 = null;
                    }
                }
                if (vector3d1 == null) {
                    this.field_203139_c = true;
                    return;
                }
                this.turtle.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.turtle.getNavigator().noPath() && !this.field_203139_c && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void resetTask() {
            this.turtle.setTravelling(false);
            super.resetTask();
        }
    }

    static class WanderGoal
    extends RandomWalkingGoal {
        private final TurtleEntity turtle;

        private WanderGoal(TurtleEntity turtle, double speedIn, int chance) {
            super(turtle, speedIn, chance);
            this.turtle = turtle;
        }

        @Override
        public boolean shouldExecute() {
            return !this.creature.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
        }
    }

    static class Navigator
    extends SwimmerPathNavigator {
        Navigator(TurtleEntity turtle, World worldIn) {
            super(turtle, worldIn);
        }

        @Override
        protected boolean canNavigate() {
            return true;
        }

        @Override
        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkAndSwimNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }

        @Override
        public boolean canEntityStandOnPos(BlockPos pos) {
            TurtleEntity turtleentity;
            if (this.entity instanceof TurtleEntity && (turtleentity = (TurtleEntity)this.entity).isTravelling()) {
                return this.world.getBlockState(pos).isIn(Blocks.WATER);
            }
            return !this.world.getBlockState(pos.down()).isAir();
        }
    }
}
