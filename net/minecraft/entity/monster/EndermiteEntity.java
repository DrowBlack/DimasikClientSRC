package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity
extends MonsterEntity {
    private int lifetime;
    private boolean playerSpawned;

    public EndermiteEntity(EntityType<? extends EndermiteEntity> type, World worldIn) {
        super((EntityType<? extends MonsterEntity>)type, worldIn);
        this.experienceValue = 3;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setCallsForHelp(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.13f;
    }

    public static AttributeModifierMap.MutableAttribute func_234288_m_() {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ENDERMITE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMITE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15f, 1.0f);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.lifetime = compound.getInt("Lifetime");
        this.playerSpawned = compound.getBoolean("PlayerSpawned");
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Lifetime", this.lifetime);
        compound.putBoolean("PlayerSpawned", this.playerSpawned);
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
    public double getYOffset() {
        return 0.1;
    }

    public boolean isSpawnedByPlayer() {
        return this.playerSpawned;
    }

    public void setSpawnedByPlayer(boolean spawnedByPlayer) {
        this.playerSpawned = spawnedByPlayer;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.world.isRemote) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5), this.getPosYRandom(), this.getPosZRandom(0.5), (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0);
            }
        } else {
            if (!this.isNoDespawnRequired()) {
                ++this.lifetime;
            }
            if (this.lifetime >= 2400) {
                this.remove();
            }
        }
    }

    public static boolean func_223328_b(EntityType<EndermiteEntity> p_223328_0_, IWorld p_223328_1_, SpawnReason reason, BlockPos p_223328_3_, Random p_223328_4_) {
        if (EndermiteEntity.canMonsterSpawn(p_223328_0_, p_223328_1_, reason, p_223328_3_, p_223328_4_)) {
            PlayerEntity playerentity = p_223328_1_.getClosestPlayer((double)p_223328_3_.getX() + 0.5, (double)p_223328_3_.getY() + 0.5, (double)p_223328_3_.getZ() + 0.5, 5.0, true);
            return playerentity == null;
        }
        return false;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.ARTHROPOD;
    }
}
