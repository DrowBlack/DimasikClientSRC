package net.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IAngerable {
    public int getAngerTime();

    public void setAngerTime(int var1);

    @Nullable
    public UUID getAngerTarget();

    public void setAngerTarget(@Nullable UUID var1);

    public void func_230258_H__();

    default public void writeAngerNBT(CompoundNBT nbt) {
        nbt.putInt("AngerTime", this.getAngerTime());
        if (this.getAngerTarget() != null) {
            nbt.putUniqueId("AngryAt", this.getAngerTarget());
        }
    }

    default public void readAngerNBT(ServerWorld world, CompoundNBT nbt) {
        this.setAngerTime(nbt.getInt("AngerTime"));
        if (!nbt.hasUniqueId("AngryAt")) {
            this.setAngerTarget(null);
        } else {
            UUID uuid = nbt.getUniqueId("AngryAt");
            this.setAngerTarget(uuid);
            Entity entity = world.getEntityByUuid(uuid);
            if (entity != null) {
                if (entity instanceof MobEntity) {
                    this.setRevengeTarget((MobEntity)entity);
                }
                if (entity.getType() == EntityType.PLAYER) {
                    this.func_230246_e_((PlayerEntity)entity);
                }
            }
        }
    }

    default public void func_241359_a_(ServerWorld p_241359_1_, boolean p_241359_2_) {
        LivingEntity livingentity = this.getAttackTarget();
        UUID uuid = this.getAngerTarget();
        if ((livingentity == null || livingentity.getShouldBeDead()) && uuid != null && p_241359_1_.getEntityByUuid(uuid) instanceof MobEntity) {
            this.func_241356_K__();
        } else {
            if (livingentity != null && !Objects.equals(uuid, livingentity.getUniqueID())) {
                this.setAngerTarget(livingentity.getUniqueID());
                this.func_230258_H__();
            }
            if (!(this.getAngerTime() <= 0 || livingentity != null && livingentity.getType() == EntityType.PLAYER && p_241359_2_)) {
                this.setAngerTime(this.getAngerTime() - 1);
                if (this.getAngerTime() == 0) {
                    this.func_241356_K__();
                }
            }
        }
    }

    default public boolean func_233680_b_(LivingEntity p_233680_1_) {
        if (!EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_233680_1_)) {
            return false;
        }
        return p_233680_1_.getType() == EntityType.PLAYER && this.func_241357_a_(p_233680_1_.world) ? true : p_233680_1_.getUniqueID().equals(this.getAngerTarget());
    }

    default public boolean func_241357_a_(World p_241357_1_) {
        return p_241357_1_.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.func_233678_J__() && this.getAngerTarget() == null;
    }

    default public boolean func_233678_J__() {
        return this.getAngerTime() > 0;
    }

    default public void func_233681_b_(PlayerEntity p_233681_1_) {
        if (p_233681_1_.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS) && p_233681_1_.getUniqueID().equals(this.getAngerTarget())) {
            this.func_241356_K__();
        }
    }

    default public void func_241355_J__() {
        this.func_241356_K__();
        this.func_230258_H__();
    }

    default public void func_241356_K__() {
        this.setRevengeTarget(null);
        this.setAngerTarget(null);
        this.setAttackTarget(null);
        this.setAngerTime(0);
    }

    public void setRevengeTarget(@Nullable LivingEntity var1);

    public void func_230246_e_(@Nullable PlayerEntity var1);

    public void setAttackTarget(@Nullable LivingEntity var1);

    @Nullable
    public LivingEntity getAttackTarget();
}
