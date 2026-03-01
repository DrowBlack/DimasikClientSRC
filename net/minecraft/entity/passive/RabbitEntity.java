package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class RabbitEntity
extends AnimalEntity {
    private static final DataParameter<Integer> RABBIT_TYPE = EntityDataManager.createKey(RabbitEntity.class, DataSerializers.VARINT);
    private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private int carrotTicks;

    public RabbitEntity(EntityType<? extends RabbitEntity> p_i50247_1_, World p_i50247_2_) {
        super((EntityType<? extends AnimalEntity>)p_i50247_1_, p_i50247_2_);
        this.jumpController = new JumpHelperController(this);
        this.moveController = new MoveHelperController(this);
        this.setMovementSpeed(0.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8));
        this.goalSelector.addGoal(3, new TemptGoal((CreatureEntity)this, 1.0, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.2, 2.2));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<WolfEntity>(this, WolfEntity.class, 10.0f, 2.2, 2.2));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<MonsterEntity>(this, MonsterEntity.class, 4.0f, 2.2, 2.2));
        this.goalSelector.addGoal(5, new RaidFarmGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6));
        this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0f));
    }

    @Override
    protected float getJumpUpwardsMotion() {
        if (!(this.collidedHorizontally || this.moveController.isUpdating() && this.moveController.getY() > this.getPosY() + 0.5)) {
            Path path = this.navigator.getPath();
            if (path != null && !path.isFinished()) {
                Vector3d vector3d = path.getPosition(this);
                if (vector3d.y > this.getPosY() + 0.5) {
                    return 0.5f;
                }
            }
            return this.moveController.getSpeed() <= 0.6 ? 0.2f : 0.3f;
        }
        return 0.5f;
    }

    @Override
    protected void jump() {
        double d1;
        super.jump();
        double d0 = this.moveController.getSpeed();
        if (d0 > 0.0 && (d1 = RabbitEntity.horizontalMag(this.getMotion())) < 0.01) {
            this.moveRelative(0.1f, new Vector3d(0.0, 0.0, 1.0));
        }
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)1);
        }
    }

    public float getJumpCompletion(float p_175521_1_) {
        return this.jumpDuration == 0 ? 0.0f : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
    }

    public void setMovementSpeed(double newSpeed) {
        this.getNavigator().setSpeed(newSpeed);
        this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f) * 0.8f);
        }
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(RABBIT_TYPE, 0);
    }

    @Override
    public void updateAITasks() {
        if (this.currentMoveTypeDuration > 0) {
            --this.currentMoveTypeDuration;
        }
        if (this.carrotTicks > 0) {
            this.carrotTicks -= this.rand.nextInt(3);
            if (this.carrotTicks < 0) {
                this.carrotTicks = 0;
            }
        }
        if (this.onGround) {
            JumpHelperController rabbitentity$jumphelpercontroller;
            LivingEntity livingentity;
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }
            if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0 && (livingentity = this.getAttackTarget()) != null && this.getDistanceSq(livingentity) < 16.0) {
                this.calculateRotationYaw(livingentity.getPosX(), livingentity.getPosZ());
                this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), this.moveController.getSpeed());
                this.startJumping();
                this.wasOnGround = true;
            }
            if (!(rabbitentity$jumphelpercontroller = (JumpHelperController)this.jumpController).getIsJumping()) {
                if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0) {
                    Path path = this.navigator.getPath();
                    Vector3d vector3d = new Vector3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());
                    if (path != null && !path.isFinished()) {
                        vector3d = path.getPosition(this);
                    }
                    this.calculateRotationYaw(vector3d.x, vector3d.z);
                    this.startJumping();
                }
            } else if (!rabbitentity$jumphelpercontroller.canJump()) {
                this.enableJumpControl();
            }
        }
        this.wasOnGround = this.onGround;
    }

    @Override
    public boolean func_230269_aK_() {
        return false;
    }

    private void calculateRotationYaw(double x, double z) {
        this.rotationYaw = (float)(MathHelper.atan2(z - this.getPosZ(), x - this.getPosX()) * 57.2957763671875) - 90.0f;
    }

    private void enableJumpControl() {
        ((JumpHelperController)this.jumpController).setCanJump(true);
    }

    private void disableJumpControl() {
        ((JumpHelperController)this.jumpController).setCanJump(false);
    }

    private void updateMoveTypeDuration() {
        this.currentMoveTypeDuration = this.moveController.getSpeed() < 2.2 ? 10 : 1;
    }

    private void checkLandingDelay() {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234224_eJ_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 3.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("RabbitType", this.getRabbitType());
        compound.putInt("MoreCarrotTicks", this.carrotTicks);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setRabbitType(compound.getInt("RabbitType"));
        this.carrotTicks = compound.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getRabbitType() == 99) {
            this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0f);
        }
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0f);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
    }

    private boolean isRabbitBreedingItem(Item itemIn) {
        return itemIn == Items.CARROT || itemIn == Items.GOLDEN_CARROT || itemIn == Blocks.DANDELION.asItem();
    }

    @Override
    public RabbitEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        RabbitEntity rabbitentity = EntityType.RABBIT.create(p_241840_1_);
        int i = this.getRandomRabbitType(p_241840_1_);
        if (this.rand.nextInt(20) != 0) {
            i = p_241840_2_ instanceof RabbitEntity && this.rand.nextBoolean() ? ((RabbitEntity)p_241840_2_).getRabbitType() : this.getRabbitType();
        }
        rabbitentity.setRabbitType(i);
        return rabbitentity;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return this.isRabbitBreedingItem(stack.getItem());
    }

    public int getRabbitType() {
        return this.dataManager.get(RABBIT_TYPE);
    }

    public void setRabbitType(int rabbitTypeId) {
        if (rabbitTypeId == 99) {
            this.getAttribute(Attributes.ARMOR).setBaseValue(8.0);
            this.goalSelector.addGoal(4, new EvilAttackGoal(this));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setCallsForHelp(new Class[0]));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<WolfEntity>((MobEntity)this, WolfEntity.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new TranslationTextComponent(Util.makeTranslationKey("entity", KILLER_BUNNY)));
            }
        }
        this.dataManager.set(RABBIT_TYPE, rabbitTypeId);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        int i = this.getRandomRabbitType(worldIn);
        if (spawnDataIn instanceof RabbitData) {
            i = ((RabbitData)spawnDataIn).typeData;
        } else {
            spawnDataIn = new RabbitData(i);
        }
        this.setRabbitType(i);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private int getRandomRabbitType(IWorld p_213610_1_) {
        Biome biome = p_213610_1_.getBiome(this.getPosition());
        int i = this.rand.nextInt(100);
        if (biome.getPrecipitation() == Biome.RainType.SNOW) {
            return i < 80 ? 1 : 3;
        }
        if (biome.getCategory() == Biome.Category.DESERT) {
            return 4;
        }
        return i < 50 ? 0 : (i < 90 ? 5 : 2);
    }

    public static boolean func_223321_c(EntityType<RabbitEntity> p_223321_0_, IWorld p_223321_1_, SpawnReason reason, BlockPos p_223321_3_, Random p_223321_4_) {
        BlockState blockstate = p_223321_1_.getBlockState(p_223321_3_.down());
        return (blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(Blocks.SNOW) || blockstate.isIn(Blocks.SAND)) && p_223321_1_.getLightSubtracted(p_223321_3_, 0) > 8;
    }

    private boolean isCarrotEaten() {
        return this.carrotTicks == 0;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 1) {
            this.func_233569_aL_();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public Vector3d func_241205_ce_() {
        return new Vector3d(0.0, 0.6f * this.getEyeHeight(), this.getWidth() * 0.4f);
    }

    public class JumpHelperController
    extends JumpController {
        private final RabbitEntity rabbit;
        private boolean canJump;

        public JumpHelperController(RabbitEntity rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean getIsJumping() {
            return this.isJumping;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn) {
            this.canJump = canJumpIn;
        }

        @Override
        public void tick() {
            if (this.isJumping) {
                this.rabbit.startJumping();
                this.isJumping = false;
            }
        }
    }

    static class MoveHelperController
    extends MovementController {
        private final RabbitEntity rabbit;
        private double nextJumpSpeed;

        public MoveHelperController(RabbitEntity rabbit) {
            super(rabbit);
            this.rabbit = rabbit;
        }

        @Override
        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.isJumping && !((JumpHelperController)this.rabbit.jumpController).getIsJumping()) {
                this.rabbit.setMovementSpeed(0.0);
            } else if (this.isUpdating()) {
                this.rabbit.setMovementSpeed(this.nextJumpSpeed);
            }
            super.tick();
        }

        @Override
        public void setMoveTo(double x, double y, double z, double speedIn) {
            if (this.rabbit.isInWater()) {
                speedIn = 1.5;
            }
            super.setMoveTo(x, y, z, speedIn);
            if (speedIn > 0.0) {
                this.nextJumpSpeed = speedIn;
            }
        }
    }

    static class PanicGoal
    extends net.minecraft.entity.ai.goal.PanicGoal {
        private final RabbitEntity rabbit;

        public PanicGoal(RabbitEntity rabbit, double speedIn) {
            super(rabbit, speedIn);
            this.rabbit = rabbit;
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.setMovementSpeed(this.speed);
        }
    }

    static class AvoidEntityGoal<T extends LivingEntity>
    extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T> {
        private final RabbitEntity rabbit;

        public AvoidEntityGoal(RabbitEntity rabbit, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_) {
            super(rabbit, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
            this.rabbit = rabbit;
        }

        @Override
        public boolean shouldExecute() {
            return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
        }
    }

    static class RaidFarmGoal
    extends MoveToBlockGoal {
        private final RabbitEntity rabbit;
        private boolean wantsToRaid;
        private boolean canRaid;

        public RaidFarmGoal(RabbitEntity rabbitIn) {
            super(rabbitIn, 0.7f, 16);
            this.rabbit = rabbitIn;
        }

        @Override
        public boolean shouldExecute() {
            if (this.runDelay <= 0) {
                if (!this.rabbit.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    return false;
                }
                this.canRaid = false;
                this.wantsToRaid = this.rabbit.isCarrotEaten();
                this.wantsToRaid = true;
            }
            return super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.canRaid && super.shouldContinueExecuting();
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.getLookController().setLookPosition((double)this.destinationBlock.getX() + 0.5, this.destinationBlock.getY() + 1, (double)this.destinationBlock.getZ() + 0.5, 10.0f, this.rabbit.getVerticalFaceSpeed());
            if (this.getIsAboveDestination()) {
                World world = this.rabbit.world;
                BlockPos blockpos = this.destinationBlock.up();
                BlockState blockstate = world.getBlockState(blockpos);
                Block block = blockstate.getBlock();
                if (this.canRaid && block instanceof CarrotBlock) {
                    Integer integer = blockstate.get(CarrotBlock.AGE);
                    if (integer == 0) {
                        world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                        world.destroyBlock(blockpos, true, this.rabbit);
                    } else {
                        world.setBlockState(blockpos, (BlockState)blockstate.with(CarrotBlock.AGE, integer - 1), 2);
                        world.playEvent(2001, blockpos, Block.getStateId(blockstate));
                    }
                    this.rabbit.carrotTicks = 40;
                }
                this.canRaid = false;
                this.runDelay = 10;
            }
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            BlockState blockstate;
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid && (block = (blockstate = worldIn.getBlockState(pos = pos.up())).getBlock()) instanceof CarrotBlock && ((CarrotBlock)block).isMaxAge(blockstate)) {
                this.canRaid = true;
                return true;
            }
            return false;
        }
    }

    static class EvilAttackGoal
    extends MeleeAttackGoal {
        public EvilAttackGoal(RabbitEntity rabbit) {
            super(rabbit, 1.4, true);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 4.0f + attackTarget.getWidth();
        }
    }

    public static class RabbitData
    extends AgeableEntity.AgeableData {
        public final int typeData;

        public RabbitData(int type) {
            super(1.0f);
            this.typeData = type;
        }
    }
}
