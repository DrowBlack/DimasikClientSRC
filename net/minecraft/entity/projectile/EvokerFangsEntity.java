package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EvokerFangsEntity
extends Entity {
    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> p_i50170_1_, World p_i50170_2_) {
        super(p_i50170_1_, p_i50170_2_);
    }

    public EvokerFangsEntity(World worldIn, double x, double y, double z, float p_i47276_8_, int p_i47276_9_, LivingEntity casterIn) {
        this((EntityType<? extends EvokerFangsEntity>)EntityType.EVOKER_FANGS, worldIn);
        this.warmupDelayTicks = p_i47276_9_;
        this.setCaster(casterIn);
        this.rotationYaw = p_i47276_8_ * 57.295776f;
        this.setPosition(x, y, z);
    }

    @Override
    protected void registerData() {
    }

    public void setCaster(@Nullable LivingEntity p_190549_1_) {
        this.caster = p_190549_1_;
        this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
    }

    @Nullable
    public LivingEntity getCaster() {
        Entity entity;
        if (this.caster == null && this.casterUuid != null && this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntityByUuid(this.casterUuid)) instanceof LivingEntity) {
            this.caster = (LivingEntity)entity;
        }
        return this.caster;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUniqueId("Owner")) {
            this.casterUuid = compound.getUniqueId("Owner");
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUniqueId("Owner", this.casterUuid);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.getPosX() + (this.rand.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double d1 = this.getPosY() + 0.05 + this.rand.nextDouble();
                        double d2 = this.getPosZ() + (this.rand.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double d3 = (this.rand.nextDouble() * 2.0 - 1.0) * 0.3;
                        double d4 = 0.3 + this.rand.nextDouble() * 0.3;
                        double d5 = (this.rand.nextDouble() * 2.0 - 1.0) * 0.3;
                        this.world.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(0.2, 0.0, 0.2))) {
                    this.damage(livingentity);
                }
            }
            if (!this.sentSpikeEvent) {
                this.world.setEntityState(this, (byte)4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.remove();
            }
        }
    }

    private void damage(LivingEntity p_190551_1_) {
        LivingEntity livingentity = this.getCaster();
        if (p_190551_1_.isAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != livingentity) {
            if (livingentity == null) {
                p_190551_1_.attackEntityFrom(DamageSource.MAGIC, 6.0f);
            } else {
                if (livingentity.isOnSameTeam(p_190551_1_)) {
                    return;
                }
                p_190551_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, livingentity), 6.0f);
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        super.handleStatusUpdate(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0f, this.rand.nextFloat() * 0.2f + 0.85f, false);
            }
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0f;
        }
        int i = this.lifeTicks - 2;
        return i <= 0 ? 1.0f : 1.0f - ((float)i - partialTicks) / 20.0f;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
