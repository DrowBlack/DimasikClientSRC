package net.minecraft.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EvokerEntity
extends SpellcastingIllagerEntity {
    private SheepEntity wololoTarget;

    public EvokerEntity(EntityType<? extends EvokerEntity> type, World worldIn) {
        super((EntityType<? extends SpellcastingIllagerEntity>)type, worldIn);
        this.experienceValue = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.addGoal(4, new SummonSpellGoal());
        this.goalSelector.addGoal(5, new AttackSpellGoal());
        this.goalSelector.addGoal(6, new WololoSpellGoal());
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractRaiderEntity.class).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillagerEntity>((MobEntity)this, AbstractVillagerEntity.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, false));
    }

    public static AttributeModifierMap.MutableAttribute func_234289_eI_() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5).createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0).createMutableAttribute(Attributes.MAX_HEALTH, 24.0);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == null) {
            return false;
        }
        if (entityIn == this) {
            return true;
        }
        if (super.isOnSameTeam(entityIn)) {
            return true;
        }
        if (entityIn instanceof VexEntity) {
            return this.isOnSameTeam(((VexEntity)entityIn).getOwner());
        }
        if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entityIn.getTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    private void setWololoTarget(@Nullable SheepEntity wololoTargetIn) {
        this.wololoTarget = wololoTargetIn;
    }

    @Nullable
    private SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public void applyWaveBonus(int wave, boolean p_213660_2_) {
    }

    class CastingSpellGoal
    extends SpellcastingIllagerEntity.CastingASpellGoal {
        private CastingSpellGoal() {
            super(EvokerEntity.this);
        }

        @Override
        public void tick() {
            if (EvokerEntity.this.getAttackTarget() != null) {
                EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getAttackTarget(), EvokerEntity.this.getHorizontalFaceSpeed(), EvokerEntity.this.getVerticalFaceSpeed());
            } else if (EvokerEntity.this.getWololoTarget() != null) {
                EvokerEntity.this.getLookController().setLookPositionWithEntity(EvokerEntity.this.getWololoTarget(), EvokerEntity.this.getHorizontalFaceSpeed(), EvokerEntity.this.getVerticalFaceSpeed());
            }
        }
    }

    class SummonSpellGoal
    extends SpellcastingIllagerEntity.UseSpellGoal {
        private final EntityPredicate field_220843_e;

        private SummonSpellGoal() {
            super(EvokerEntity.this);
            this.field_220843_e = new EntityPredicate().setDistance(16.0).setLineOfSiteRequired().setUseInvisibilityCheck().allowInvulnerable().allowFriendlyFire();
        }

        @Override
        public boolean shouldExecute() {
            if (!super.shouldExecute()) {
                return false;
            }
            int i = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(VexEntity.class, this.field_220843_e, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0)).size();
            return EvokerEntity.this.rand.nextInt(8) + 1 > i;
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void castSpell() {
            ServerWorld serverworld = (ServerWorld)EvokerEntity.this.world;
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos = EvokerEntity.this.getPosition().add(-2 + EvokerEntity.this.rand.nextInt(5), 1, -2 + EvokerEntity.this.rand.nextInt(5));
                VexEntity vexentity = EntityType.VEX.create(EvokerEntity.this.world);
                vexentity.moveToBlockPosAndAngles(blockpos, 0.0f, 0.0f);
                vexentity.onInitialSpawn(serverworld, EvokerEntity.this.world.getDifficultyForLocation(blockpos), SpawnReason.MOB_SUMMONED, null, null);
                vexentity.setOwner(EvokerEntity.this);
                vexentity.setBoundOrigin(blockpos);
                vexentity.setLimitedLife(20 * (30 + EvokerEntity.this.rand.nextInt(90)));
                serverworld.func_242417_l(vexentity);
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpellType() {
            return SpellcastingIllagerEntity.SpellType.SUMMON_VEX;
        }
    }

    class AttackSpellGoal
    extends SpellcastingIllagerEntity.UseSpellGoal {
        private AttackSpellGoal() {
            super(EvokerEntity.this);
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected void castSpell() {
            LivingEntity livingentity = EvokerEntity.this.getAttackTarget();
            double d0 = Math.min(livingentity.getPosY(), EvokerEntity.this.getPosY());
            double d1 = Math.max(livingentity.getPosY(), EvokerEntity.this.getPosY()) + 1.0;
            float f = (float)MathHelper.atan2(livingentity.getPosZ() - EvokerEntity.this.getPosZ(), livingentity.getPosX() - EvokerEntity.this.getPosX());
            if (EvokerEntity.this.getDistanceSq(livingentity) < 9.0) {
                for (int i = 0; i < 5; ++i) {
                    float f1 = f + (float)i * (float)Math.PI * 0.4f;
                    this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f1) * 1.5, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f1) * 1.5, d0, d1, f1, 0);
                }
                for (int k = 0; k < 8; ++k) {
                    float f2 = f + (float)k * (float)Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f2) * 2.5, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f2) * 2.5, d0, d1, f2, 3);
                }
            } else {
                for (int l = 0; l < 16; ++l) {
                    double d2 = 1.25 * (double)(l + 1);
                    int j = 1 * l;
                    this.spawnFangs(EvokerEntity.this.getPosX() + (double)MathHelper.cos(f) * d2, EvokerEntity.this.getPosZ() + (double)MathHelper.sin(f) * d2, d0, d1, f, j);
                }
            }
        }

        private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_, int p_190876_10_) {
            BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
            boolean flag = false;
            double d0 = 0.0;
            do {
                BlockState blockstate1;
                VoxelShape voxelshape;
                BlockPos blockpos1;
                BlockState blockstate;
                if (!(blockstate = EvokerEntity.this.world.getBlockState(blockpos1 = blockpos.down())).isSolidSide(EvokerEntity.this.world, blockpos1, Direction.UP)) continue;
                if (!EvokerEntity.this.world.isAirBlock(blockpos) && !(voxelshape = (blockstate1 = EvokerEntity.this.world.getBlockState(blockpos)).getCollisionShape(EvokerEntity.this.world, blockpos)).isEmpty()) {
                    d0 = voxelshape.getEnd(Direction.Axis.Y);
                }
                flag = true;
                break;
            } while ((blockpos = blockpos.down()).getY() >= MathHelper.floor(p_190876_5_) - 1);
            if (flag) {
                EvokerEntity.this.world.addEntity(new EvokerFangsEntity(EvokerEntity.this.world, p_190876_1_, (double)blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EvokerEntity.this));
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpellType() {
            return SpellcastingIllagerEntity.SpellType.FANGS;
        }
    }

    public class WololoSpellGoal
    extends SpellcastingIllagerEntity.UseSpellGoal {
        private final EntityPredicate wololoTargetFlags;

        public WololoSpellGoal() {
            super(EvokerEntity.this);
            this.wololoTargetFlags = new EntityPredicate().setDistance(16.0).allowInvulnerable().setCustomPredicate(p_220844_0_ -> ((SheepEntity)p_220844_0_).getFleeceColor() == DyeColor.BLUE);
        }

        @Override
        public boolean shouldExecute() {
            if (EvokerEntity.this.getAttackTarget() != null) {
                return false;
            }
            if (EvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (EvokerEntity.this.ticksExisted < this.spellCooldown) {
                return false;
            }
            if (!EvokerEntity.this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            List<SheepEntity> list = EvokerEntity.this.world.getTargettableEntitiesWithinAABB(SheepEntity.class, this.wololoTargetFlags, EvokerEntity.this, EvokerEntity.this.getBoundingBox().grow(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            EvokerEntity.this.setWololoTarget(list.get(EvokerEntity.this.rand.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return EvokerEntity.this.getWololoTarget() != null && this.spellWarmup > 0;
        }

        @Override
        public void resetTask() {
            super.resetTask();
            EvokerEntity.this.setWololoTarget(null);
        }

        @Override
        protected void castSpell() {
            SheepEntity sheepentity = EvokerEntity.this.getWololoTarget();
            if (sheepentity != null && sheepentity.isAlive()) {
                sheepentity.setFleeceColor(DyeColor.RED);
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpellType() {
            return SpellcastingIllagerEntity.SpellType.WOLOLO;
        }
    }
}
