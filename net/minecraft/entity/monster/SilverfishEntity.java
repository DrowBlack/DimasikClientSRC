package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class SilverfishEntity
extends MonsterEntity {
    private SummonSilverfishGoal summonSilverfish;

    public SilverfishEntity(EntityType<? extends SilverfishEntity> typeIn, World worldIn) {
        super((EntityType<? extends MonsterEntity>)typeIn, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.summonSilverfish = new SummonSilverfishGoal(this);
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(3, this.summonSilverfish);
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new HideInStoneGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    public double getYOffset() {
        return 0.1;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.13f;
    }

    public static AttributeModifierMap.MutableAttribute func_234301_m_() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SILVERFISH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.summonSilverfish != null) {
            this.summonSilverfish.notifyHurt();
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void tick() {
        this.renderYawOffset = this.rotationYaw;
        super.tick();
    }

    @Override
    public void setRenderYawOffset(float offset) {
        this.rotationYaw = offset;
        super.setRenderYawOffset(offset);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
        return SilverfishBlock.canContainSilverfish(worldIn.getBlockState(pos.down())) ? 10.0f : super.getBlockPathWeight(pos, worldIn);
    }

    public static boolean func_223331_b(EntityType<SilverfishEntity> p_223331_0_, IWorld p_223331_1_, SpawnReason reason, BlockPos p_223331_3_, Random p_223331_4_) {
        if (SilverfishEntity.canMonsterSpawn(p_223331_0_, p_223331_1_, reason, p_223331_3_, p_223331_4_)) {
            PlayerEntity playerentity = p_223331_1_.getClosestPlayer((double)p_223331_3_.getX() + 0.5, (double)p_223331_3_.getY() + 0.5, (double)p_223331_3_.getZ() + 0.5, 5.0, true);
            return playerentity == null;
        }
        return false;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }

    static class SummonSilverfishGoal
    extends Goal {
        private final SilverfishEntity silverfish;
        private int lookForFriends;

        public SummonSilverfishGoal(SilverfishEntity silverfishIn) {
            this.silverfish = silverfishIn;
        }

        public void notifyHurt() {
            if (this.lookForFriends == 0) {
                this.lookForFriends = 20;
            }
        }

        @Override
        public boolean shouldExecute() {
            return this.lookForFriends > 0;
        }

        @Override
        public void tick() {
            --this.lookForFriends;
            if (this.lookForFriends <= 0) {
                World world = this.silverfish.world;
                Random random = this.silverfish.getRNG();
                BlockPos blockpos = this.silverfish.getPosition();
                int i = 0;
                while (i <= 5 && i >= -5) {
                    int j = 0;
                    while (j <= 10 && j >= -10) {
                        int k = 0;
                        while (k <= 10 && k >= -10) {
                            BlockPos blockpos1 = blockpos.add(j, i, k);
                            BlockState blockstate = world.getBlockState(blockpos1);
                            Block block = blockstate.getBlock();
                            if (block instanceof SilverfishBlock) {
                                if (world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                                    world.destroyBlock(blockpos1, true, this.silverfish);
                                } else {
                                    world.setBlockState(blockpos1, ((SilverfishBlock)block).getMimickedBlock().getDefaultState(), 3);
                                }
                                if (random.nextBoolean()) {
                                    return;
                                }
                            }
                            k = (k <= 0 ? 1 : 0) - k;
                        }
                        j = (j <= 0 ? 1 : 0) - j;
                    }
                    i = (i <= 0 ? 1 : 0) - i;
                }
            }
        }
    }

    static class HideInStoneGoal
    extends RandomWalkingGoal {
        private Direction facing;
        private boolean doMerge;

        public HideInStoneGoal(SilverfishEntity silverfishIn) {
            super(silverfishIn, 1.0, 10);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            if (this.creature.getAttackTarget() != null) {
                return false;
            }
            if (!this.creature.getNavigator().noPath()) {
                return false;
            }
            Random random = this.creature.getRNG();
            if (this.creature.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && random.nextInt(10) == 0) {
                this.facing = Direction.getRandomDirection(random);
                BlockPos blockpos = new BlockPos(this.creature.getPosX(), this.creature.getPosY() + 0.5, this.creature.getPosZ()).offset(this.facing);
                BlockState blockstate = this.creature.world.getBlockState(blockpos);
                if (SilverfishBlock.canContainSilverfish(blockstate)) {
                    this.doMerge = true;
                    return true;
                }
            }
            this.doMerge = false;
            return super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting() {
            return this.doMerge ? false : super.shouldContinueExecuting();
        }

        @Override
        public void startExecuting() {
            if (!this.doMerge) {
                super.startExecuting();
            } else {
                World iworld = this.creature.world;
                BlockPos blockpos = new BlockPos(this.creature.getPosX(), this.creature.getPosY() + 0.5, this.creature.getPosZ()).offset(this.facing);
                BlockState blockstate = iworld.getBlockState(blockpos);
                if (SilverfishBlock.canContainSilverfish(blockstate)) {
                    iworld.setBlockState(blockpos, SilverfishBlock.infest(blockstate.getBlock()), 3);
                    this.creature.spawnExplosionParticle();
                    this.creature.remove();
                }
            }
        }
    }
}
