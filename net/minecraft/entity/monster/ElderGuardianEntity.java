package net.minecraft.entity.monster;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ElderGuardianEntity
extends GuardianEntity {
    public static final float field_213629_b = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

    public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> type, World worldIn) {
        super((EntityType<? extends GuardianEntity>)type, worldIn);
        this.enablePersistence();
        if (this.wander != null) {
            this.wander.setExecutionChance(400);
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234283_m_() {
        return GuardianEntity.func_234292_eK_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3f).createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0).createMutableAttribute(Attributes.MAX_HEALTH, 80.0);
    }

    @Override
    public int getAttackDuration() {
        return 60;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        int i = 1200;
        if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
            Effect effect = Effects.MINING_FATIGUE;
            List<ServerPlayerEntity> list = ((ServerWorld)this.world).getPlayers(p_210138_1_ -> this.getDistanceSq((Entity)p_210138_1_) < 2500.0 && p_210138_1_.interactionManager.survivalOrAdventure());
            int j = 2;
            int k = 6000;
            int l = 1200;
            for (ServerPlayerEntity serverplayerentity : list) {
                if (serverplayerentity.isPotionActive(effect) && serverplayerentity.getActivePotionEffect(effect).getAmplifier() >= 2 && serverplayerentity.getActivePotionEffect(effect).getDuration() >= 1200) continue;
                serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241774_k_, this.isSilent() ? 0.0f : 1.0f));
                serverplayerentity.addPotionEffect(new EffectInstance(effect, 6000, 2));
            }
        }
        if (!this.detachHome()) {
            this.setHomePosAndDistance(this.getPosition(), 16);
        }
    }
}
