package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public abstract class Phase
implements IPhase {
    protected final EnderDragonEntity dragon;

    public Phase(EnderDragonEntity dragonIn) {
        this.dragon = dragonIn;
    }

    @Override
    public boolean getIsStationary() {
        return false;
    }

    @Override
    public void clientTick() {
    }

    @Override
    public void serverTick() {
    }

    @Override
    public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc, @Nullable PlayerEntity plyr) {
    }

    @Override
    public void initPhase() {
    }

    @Override
    public void removeAreaEffect() {
    }

    @Override
    public float getMaxRiseOrFall() {
        return 0.6f;
    }

    @Override
    @Nullable
    public Vector3d getTargetLocation() {
        return null;
    }

    @Override
    public float func_221113_a(DamageSource p_221113_1_, float p_221113_2_) {
        return p_221113_2_;
    }

    @Override
    public float getYawFactor() {
        float f = MathHelper.sqrt(Entity.horizontalMag(this.dragon.getMotion())) + 1.0f;
        float f1 = Math.min(f, 40.0f);
        return 0.7f / f1 / f;
    }
}
