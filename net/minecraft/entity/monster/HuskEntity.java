package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class HuskEntity
extends ZombieEntity {
    public HuskEntity(EntityType<? extends HuskEntity> type, World worldIn) {
        super((EntityType<? extends ZombieEntity>)type, worldIn);
    }

    public static boolean func_223334_b(EntityType<HuskEntity> p_223334_0_, IServerWorld p_223334_1_, SpawnReason reason, BlockPos p_223334_3_, Random p_223334_4_) {
        return HuskEntity.canMonsterSpawnInLight(p_223334_0_, p_223334_1_, reason, p_223334_3_, p_223334_4_) && (reason == SpawnReason.SPAWNER || p_223334_1_.canSeeSky(p_223334_3_));
    }

    @Override
    protected boolean shouldBurnInDay() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_HUSK_STEP;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof LivingEntity) {
            float f = this.world.getDifficultyForLocation(this.getPosition()).getAdditionalDifficulty();
            ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 140 * (int)f));
        }
        return flag;
    }

    @Override
    protected boolean shouldDrown() {
        return true;
    }

    @Override
    protected void onDrowned() {
        this.func_234341_c_(EntityType.ZOMBIE);
        if (!this.isSilent()) {
            this.world.playEvent(null, 1041, this.getPosition(), 0);
        }
    }

    @Override
    protected ItemStack getSkullDrop() {
        return ItemStack.EMPTY;
    }
}
