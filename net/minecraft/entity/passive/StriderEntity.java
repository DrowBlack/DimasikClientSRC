package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.BoostHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEquipable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRideable;
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
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class StriderEntity
extends AnimalEntity
implements IRideable,
IEquipable {
    private static final Ingredient field_234308_bu_ = Ingredient.fromItems(Items.WARPED_FUNGUS);
    private static final Ingredient field_234309_bv_ = Ingredient.fromItems(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final DataParameter<Integer> field_234310_bw_ = EntityDataManager.createKey(StriderEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> field_234311_bx_ = EntityDataManager.createKey(StriderEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> field_234312_by_ = EntityDataManager.createKey(StriderEntity.class, DataSerializers.BOOLEAN);
    private final BoostHelper field_234313_bz_;
    private TemptGoal field_234306_bA_;
    private PanicGoal field_234307_bB_;

    public StriderEntity(EntityType<? extends StriderEntity> p_i231562_1_, World p_i231562_2_) {
        super((EntityType<? extends AnimalEntity>)p_i231562_1_, p_i231562_2_);
        this.field_234313_bz_ = new BoostHelper(this.dataManager, field_234310_bw_, field_234312_by_);
        this.preventEntitySpawning = true;
        this.setPathPriority(PathNodeType.WATER, -1.0f);
        this.setPathPriority(PathNodeType.LAVA, 0.0f);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0f);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0f);
    }

    public static boolean func_234314_c_(EntityType<StriderEntity> p_234314_0_, IWorld p_234314_1_, SpawnReason p_234314_2_, BlockPos p_234314_3_, Random p_234314_4_) {
        BlockPos.Mutable blockpos$mutable = p_234314_3_.toMutable();
        do {
            blockpos$mutable.move(Direction.UP);
        } while (p_234314_1_.getFluidState(blockpos$mutable).isTagged(FluidTags.LAVA));
        return p_234314_1_.getBlockState(blockpos$mutable).isAir();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (field_234310_bw_.equals(key) && this.world.isRemote) {
            this.field_234313_bz_.updateData();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(field_234310_bw_, 0);
        this.dataManager.register(field_234311_bx_, false);
        this.dataManager.register(field_234312_by_, false);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.field_234313_bz_.setSaddledToNBT(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.field_234313_bz_.setSaddledFromNBT(compound);
    }

    @Override
    public boolean isHorseSaddled() {
        return this.field_234313_bz_.getSaddled();
    }

    @Override
    public boolean func_230264_L__() {
        return this.isAlive() && !this.isChild();
    }

    @Override
    public void func_230266_a_(@Nullable SoundCategory p_230266_1_) {
        this.field_234313_bz_.setSaddledFromBoolean(true);
        if (p_230266_1_ != null) {
            this.world.playMovingSound(null, this, SoundEvents.ENTITY_STRIDER_SADDLE, p_230266_1_, 0.5f, 1.0f);
        }
    }

    @Override
    protected void registerGoals() {
        this.field_234307_bB_ = new PanicGoal(this, 1.65);
        this.goalSelector.addGoal(1, this.field_234307_bB_);
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.field_234306_bA_ = new TemptGoal((CreatureEntity)this, 1.4, false, field_234309_bv_);
        this.goalSelector.addGoal(3, this.field_234306_bA_);
        this.goalSelector.addGoal(4, new MoveToLavaGoal(this, 1.5));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new RandomWalkingGoal(this, 1.0, 60));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(9, new LookAtGoal(this, StriderEntity.class, 8.0f));
    }

    public void func_234319_t_(boolean p_234319_1_) {
        this.dataManager.set(field_234311_bx_, p_234319_1_);
    }

    public boolean func_234315_eI_() {
        return this.getRidingEntity() instanceof StriderEntity ? ((StriderEntity)this.getRidingEntity()).func_234315_eI_() : this.dataManager.get(field_234311_bx_).booleanValue();
    }

    @Override
    public boolean func_230285_a_(Fluid p_230285_1_) {
        return p_230285_1_.isIn(FluidTags.LAVA);
    }

    @Override
    public double getMountedYOffset() {
        float f = Math.min(0.25f, this.limbSwingAmount);
        float f1 = this.limbSwing;
        return (double)this.getHeight() - 0.19 + (double)(0.12f * MathHelper.cos(f1 * 1.5f) * 2.0f * f);
    }

    @Override
    public boolean canBeSteered() {
        Entity entity = this.getControllingPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity playerentity = (PlayerEntity)entity;
        return playerentity.getHeldItemMainhand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || playerentity.getHeldItemOffhand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
    }

    @Override
    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        Vector3d[] avector3d = new Vector3d[]{StriderEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw), StriderEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw - 22.5f), StriderEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw + 22.5f), StriderEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw - 45.0f), StriderEntity.func_233559_a_(this.getWidth(), livingEntity.getWidth(), livingEntity.rotationYaw + 45.0f)};
        LinkedHashSet<BlockPos> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (Vector3d vector3d : avector3d) {
            blockpos$mutable.setPos(this.getPosX() + vector3d.x, d0, this.getPosZ() + vector3d.z);
            for (double d2 = d0; d2 > d1; d2 -= 1.0) {
                set.add(blockpos$mutable.toImmutable());
                blockpos$mutable.move(Direction.DOWN);
            }
        }
        for (BlockPos blockpos : set) {
            double d3;
            if (this.world.getFluidState(blockpos).isTagged(FluidTags.LAVA) || !TransportationHelper.func_234630_a_(d3 = this.world.func_242403_h(blockpos))) continue;
            Vector3d vector3d1 = Vector3d.copyCenteredWithVerticalOffset(blockpos, d3);
            for (Pose pose : livingEntity.getAvailablePoses()) {
                AxisAlignedBB axisalignedbb = livingEntity.getPoseAABB(pose);
                if (!TransportationHelper.func_234631_a_(this.world, livingEntity, axisalignedbb.offset(vector3d1))) continue;
                livingEntity.setPose(pose);
                return vector3d1;
            }
        }
        return new Vector3d(this.getPosX(), this.getBoundingBox().maxY, this.getPosZ());
    }

    @Override
    public void travel(Vector3d travelVector) {
        this.setAIMoveSpeed(this.func_234316_eJ_());
        this.ride(this, this.field_234313_bz_, travelVector);
    }

    public float func_234316_eJ_() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.func_234315_eI_() ? 0.66f : 1.0f);
    }

    @Override
    public float getMountedSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.func_234315_eI_() ? 0.23f : 0.55f);
    }

    @Override
    public void travelTowards(Vector3d travelVec) {
        super.travel(travelVec);
    }

    @Override
    protected float determineNextStepDistance() {
        return this.distanceWalkedOnStepModified + 0.6f;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0f, 1.0f);
    }

    @Override
    public boolean boost() {
        return this.field_234313_bz_.boost(this.getRNG());
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        this.doBlockCollisions();
        if (this.isInLava()) {
            this.fallDistance = 0.0f;
        } else {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void tick() {
        if (this.func_241398_eP_() && this.rand.nextInt(140) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY, 1.0f, this.getSoundPitch());
        } else if (this.func_241397_eO_() && this.rand.nextInt(60) == 0) {
            this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT, 1.0f, this.getSoundPitch());
        }
        BlockState blockstate = this.world.getBlockState(this.getPosition());
        BlockState blockstate1 = this.getStateBelow();
        boolean flag = blockstate.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockstate1.isIn(BlockTags.STRIDER_WARM_BLOCKS) || this.func_233571_b_(FluidTags.LAVA) > 0.0;
        this.func_234319_t_(!flag);
        super.tick();
        this.func_234318_eL_();
        this.doBlockCollisions();
    }

    private boolean func_241397_eO_() {
        return this.field_234307_bB_ != null && this.field_234307_bB_.isRunning();
    }

    private boolean func_241398_eP_() {
        return this.field_234306_bA_ != null && this.field_234306_bA_.isRunning();
    }

    @Override
    protected boolean func_230286_q_() {
        return true;
    }

    private void func_234318_eL_() {
        if (this.isInLava()) {
            ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
            if (iselectioncontext.func_216378_a(FlowingFluidBlock.LAVA_COLLISION_SHAPE, this.getPosition(), true) && !this.world.getFluidState(this.getPosition().up()).isTagged(FluidTags.LAVA)) {
                this.onGround = true;
            } else {
                this.setMotion(this.getMotion().scale(0.5).add(0.0, 0.05, 0.0));
            }
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234317_eK_() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.175f).createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return !this.func_241397_eO_() && !this.func_241398_eP_() ? SoundEvents.ENTITY_STRIDER_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_STRIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRIDER_DEATH;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && !this.areEyesInFluid(FluidTags.LAVA);
    }

    @Override
    public boolean isWaterSensitive() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return new LavaPathNavigator(this, worldIn);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        if (worldIn.getBlockState(pos).getFluidState().isTagged(FluidTags.LAVA)) {
            return 10.0f;
        }
        return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0f;
    }

    @Override
    public StriderEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.STRIDER.create(p_241840_1_);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return field_234308_bu_.test(stack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isHorseSaddled()) {
            this.entityDropItem(Items.SADDLE);
        }
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        boolean flag = this.isBreedingItem(p_230254_1_.getHeldItem(p_230254_2_));
        if (!flag && this.isHorseSaddled() && !this.isBeingRidden() && !p_230254_1_.isSecondaryUseActive()) {
            if (!this.world.isRemote) {
                p_230254_1_.startRiding(this);
            }
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
        if (!actionresulttype.isSuccessOrConsume()) {
            ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
            return itemstack.getItem() == Items.SADDLE ? itemstack.interactWithEntity(p_230254_1_, this, p_230254_2_) : ActionResultType.PASS;
        }
        if (flag && !this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
        }
        return actionresulttype;
    }

    @Override
    public Vector3d func_241205_ce_() {
        return new Vector3d(0.0, 0.6f * this.getEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData object;
        if (this.isChild()) {
            return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        }
        if (this.rand.nextInt(30) == 0) {
            MobEntity mobentity = EntityType.ZOMBIFIED_PIGLIN.create(worldIn.getWorld());
            object = this.func_242331_a(worldIn, difficultyIn, mobentity, new ZombieEntity.GroupData(ZombieEntity.func_241399_a_(this.rand), false));
            mobentity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
            this.func_230266_a_(null);
        } else if (this.rand.nextInt(10) == 0) {
            AgeableEntity ageableentity = EntityType.STRIDER.create(worldIn.getWorld());
            ageableentity.setGrowingAge(-24000);
            object = this.func_242331_a(worldIn, difficultyIn, ageableentity, null);
        } else {
            object = new AgeableEntity.AgeableData(0.5f);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, object, dataTag);
    }

    private ILivingEntityData func_242331_a(IServerWorld p_242331_1_, DifficultyInstance p_242331_2_, MobEntity p_242331_3_, @Nullable ILivingEntityData p_242331_4_) {
        p_242331_3_.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0f);
        p_242331_3_.onInitialSpawn(p_242331_1_, p_242331_2_, SpawnReason.JOCKEY, p_242331_4_, null);
        p_242331_3_.startRiding(this, true);
        return new AgeableEntity.AgeableData(0.0f);
    }

    static class MoveToLavaGoal
    extends MoveToBlockGoal {
        private final StriderEntity field_242332_g;

        private MoveToLavaGoal(StriderEntity p_i241913_1_, double p_i241913_2_) {
            super(p_i241913_1_, p_i241913_2_, 8, 2);
            this.field_242332_g = p_i241913_1_;
        }

        @Override
        public BlockPos func_241846_j() {
            return this.destinationBlock;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !this.field_242332_g.isInLava() && this.shouldMoveTo(this.field_242332_g.world, this.destinationBlock);
        }

        @Override
        public boolean shouldExecute() {
            return !this.field_242332_g.isInLava() && super.shouldExecute();
        }

        @Override
        public boolean shouldMove() {
            return this.timeoutCounter % 20 == 0;
        }

        @Override
        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
            return worldIn.getBlockState(pos).isIn(Blocks.LAVA) && worldIn.getBlockState(pos.up()).allowsMovement(worldIn, pos, PathType.LAND);
        }
    }

    static class LavaPathNavigator
    extends GroundPathNavigator {
        LavaPathNavigator(StriderEntity p_i231565_1_, World p_i231565_2_) {
            super(p_i231565_1_, p_i231565_2_);
        }

        @Override
        protected PathFinder getPathFinder(int p_179679_1_) {
            this.nodeProcessor = new WalkNodeProcessor();
            return new PathFinder(this.nodeProcessor, p_179679_1_);
        }

        @Override
        protected boolean func_230287_a_(PathNodeType p_230287_1_) {
            return p_230287_1_ != PathNodeType.LAVA && p_230287_1_ != PathNodeType.DAMAGE_FIRE && p_230287_1_ != PathNodeType.DANGER_FIRE ? super.func_230287_a_(p_230287_1_) : true;
        }

        @Override
        public boolean canEntityStandOnPos(BlockPos pos) {
            return this.world.getBlockState(pos).isIn(Blocks.LAVA) || super.canEntityStandOnPos(pos);
        }
    }
}
