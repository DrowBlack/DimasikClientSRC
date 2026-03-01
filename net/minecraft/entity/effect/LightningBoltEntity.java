package net.minecraft.entity.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LightningBoltEntity
extends Entity {
    private int lightningState;
    public long boltVertex;
    private int boltLivingTime;
    private boolean effectOnly;
    @Nullable
    private ServerPlayerEntity caster;

    public LightningBoltEntity(EntityType<? extends LightningBoltEntity> p_i231491_1_, World world) {
        super(p_i231491_1_, world);
        this.ignoreFrustumCheck = true;
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
    }

    public void setEffectOnly(boolean effectOnly) {
        this.effectOnly = effectOnly;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    public void setCaster(@Nullable ServerPlayerEntity casterIn) {
        this.caster = casterIn;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.lightningState == 2) {
            Difficulty difficulty = this.world.getDifficulty();
            if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
                this.igniteBlocks(4);
            }
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0f, 0.8f + this.rand.nextFloat() * 0.2f);
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0f, 0.5f + this.rand.nextFloat() * 0.2f);
        }
        --this.lightningState;
        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.remove();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();
                this.igniteBlocks(0);
            }
        }
        if (this.lightningState >= 0) {
            if (!(this.world instanceof ServerWorld)) {
                this.world.setTimeLightningFlash(2);
            } else if (!this.effectOnly) {
                double d0 = 3.0;
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, new AxisAlignedBB(this.getPosX() - 3.0, this.getPosY() - 3.0, this.getPosZ() - 3.0, this.getPosX() + 3.0, this.getPosY() + 6.0 + 3.0, this.getPosZ() + 3.0), Entity::isAlive);
                for (Entity entity : list) {
                    entity.func_241841_a((ServerWorld)this.world, this);
                }
                if (this.caster != null) {
                    CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.caster, list);
                }
            }
        }
    }

    private void igniteBlocks(int extraIgnitions) {
        if (!this.effectOnly && !this.world.isRemote && this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            BlockPos blockpos = this.getPosition();
            BlockState blockstate = AbstractFireBlock.getFireForPlacement(this.world, blockpos);
            if (this.world.getBlockState(blockpos).isAir() && blockstate.isValidPosition(this.world, blockpos)) {
                this.world.setBlockState(blockpos, blockstate);
            }
            for (int i = 0; i < extraIgnitions; ++i) {
                BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
                blockstate = AbstractFireBlock.getFireForPlacement(this.world, blockpos1);
                if (!this.world.getBlockState(blockpos1).isAir() || !blockstate.isValidPosition(this.world, blockpos1)) continue;
                this.world.setBlockState(blockpos1, blockstate);
            }
        }
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = 64.0 * LightningBoltEntity.getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
