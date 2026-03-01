package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity
extends AbstractFishEntity {
    private static final DataParameter<Integer> PUFF_STATE = EntityDataManager.createKey(PufferfishEntity.class, DataSerializers.VARINT);
    private int puffTimer;
    private int deflateTimer;
    private static final Predicate<LivingEntity> ENEMY_MATCHER = p_210139_0_ -> {
        if (p_210139_0_ == null) {
            return false;
        }
        if (!(p_210139_0_ instanceof PlayerEntity) || !p_210139_0_.isSpectator() && !((PlayerEntity)p_210139_0_).isCreative()) {
            return p_210139_0_.getCreatureAttribute() != CreatureAttribute.WATER;
        }
        return false;
    };

    public PufferfishEntity(EntityType<? extends PufferfishEntity> p_i50248_1_, World p_i50248_2_) {
        super((EntityType<? extends AbstractFishEntity>)p_i50248_1_, p_i50248_2_);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PUFF_STATE, 0);
    }

    public int getPuffState() {
        return this.dataManager.get(PUFF_STATE);
    }

    public void setPuffState(int p_203714_1_) {
        this.dataManager.set(PUFF_STATE, p_203714_1_);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (PUFF_STATE.equals(key)) {
            this.recalculateSize();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("PuffState", this.getPuffState());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setPuffState(compound.getInt("PuffState"));
    }

    @Override
    protected ItemStack getFishBucket() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new PuffGoal(this));
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.isAlive() && this.isServerWorld()) {
            if (this.puffTimer > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.puffTimer > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(2);
                }
                ++this.puffTimer;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTimer > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(0);
                }
                ++this.deflateTimer;
            }
        }
        super.tick();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.isAlive() && this.getPuffState() > 0) {
            for (MobEntity mobEntity : this.world.getEntitiesWithinAABB(MobEntity.class, this.getBoundingBox().grow(0.3), ENEMY_MATCHER)) {
                if (!mobEntity.isAlive()) continue;
                this.attack(mobEntity);
            }
        }
    }

    private void attack(MobEntity p_205719_1_) {
        int i = this.getPuffState();
        if (p_205719_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 1 + i)) {
            p_205719_1_.addPotionEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
            this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0f, 1.0f);
        }
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        int i = this.getPuffState();
        if (entityIn instanceof ServerPlayerEntity && i > 0 && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 1 + i)) {
            if (!this.isSilent()) {
                ((ServerPlayerEntity)entityIn).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241773_j_, 0.0f));
            }
            entityIn.addPotionEffect(new EffectInstance(Effects.POISON, 60 * i, 0));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return super.getSize(poseIn).scale(PufferfishEntity.getPuffSize(this.getPuffState()));
    }

    private static float getPuffSize(int p_213806_0_) {
        switch (p_213806_0_) {
            case 0: {
                return 0.5f;
            }
            case 1: {
                return 0.7f;
            }
        }
        return 1.0f;
    }

    static class PuffGoal
    extends Goal {
        private final PufferfishEntity fish;

        public PuffGoal(PufferfishEntity fish) {
            this.fish = fish;
        }

        @Override
        public boolean shouldExecute() {
            List<LivingEntity> list = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0), ENEMY_MATCHER);
            return !list.isEmpty();
        }

        @Override
        public void startExecuting() {
            this.fish.puffTimer = 1;
            this.fish.deflateTimer = 0;
        }

        @Override
        public void resetTask() {
            this.fish.puffTimer = 0;
        }

        @Override
        public boolean shouldContinueExecuting() {
            List<LivingEntity> list = this.fish.world.getEntitiesWithinAABB(LivingEntity.class, this.fish.getBoundingBox().grow(2.0), ENEMY_MATCHER);
            return !list.isEmpty();
        }
    }
}
