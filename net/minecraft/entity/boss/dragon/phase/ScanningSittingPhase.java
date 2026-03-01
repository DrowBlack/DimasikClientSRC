package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.boss.dragon.phase.SittingPhase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ScanningSittingPhase
extends SittingPhase {
    private static final EntityPredicate field_221115_b = new EntityPredicate().setDistance(150.0);
    private final EntityPredicate field_221116_c = new EntityPredicate().setDistance(20.0).setCustomPredicate(p_221114_1_ -> Math.abs(p_221114_1_.getPosY() - dragonIn.getPosY()) <= 10.0);
    private int scanningTime;

    public ScanningSittingPhase(EnderDragonEntity dragonIn) {
        super(dragonIn);
    }

    @Override
    public void serverTick() {
        ++this.scanningTime;
        PlayerEntity livingentity = this.dragon.world.getClosestPlayer(this.field_221116_c, this.dragon, this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
        if (livingentity != null) {
            if (this.scanningTime > 25) {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
            } else {
                Vector3d vector3d = new Vector3d(livingentity.getPosX() - this.dragon.getPosX(), 0.0, livingentity.getPosZ() - this.dragon.getPosZ()).normalize();
                Vector3d vector3d1 = new Vector3d(MathHelper.sin(this.dragon.rotationYaw * ((float)Math.PI / 180)), 0.0, -MathHelper.cos(this.dragon.rotationYaw * ((float)Math.PI / 180))).normalize();
                float f = (float)vector3d1.dotProduct(vector3d);
                float f1 = (float)(Math.acos(f) * 57.2957763671875) + 0.5f;
                if (f1 < 0.0f || f1 > 10.0f) {
                    float f2;
                    double d0 = livingentity.getPosX() - this.dragon.dragonPartHead.getPosX();
                    double d1 = livingentity.getPosZ() - this.dragon.dragonPartHead.getPosZ();
                    double d2 = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(d0, d1) * 57.2957763671875 - (double)this.dragon.rotationYaw), -100.0, 100.0);
                    this.dragon.field_226525_bB_ *= 0.8f;
                    float f3 = f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0f;
                    if (f2 > 40.0f) {
                        f2 = 40.0f;
                    }
                    this.dragon.field_226525_bB_ = (float)((double)this.dragon.field_226525_bB_ + d2 * (double)(0.7f / f2 / f3));
                    this.dragon.rotationYaw += this.dragon.field_226525_bB_;
                }
            }
        } else if (this.scanningTime >= 100) {
            livingentity = this.dragon.world.getClosestPlayer(field_221115_b, this.dragon, this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            if (livingentity != null) {
                this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
                this.dragon.getPhaseManager().getPhase(PhaseType.CHARGING_PLAYER).setTarget(new Vector3d(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ()));
            }
        }
    }

    @Override
    public void initPhase() {
        this.scanningTime = 0;
    }

    public PhaseType<ScanningSittingPhase> getType() {
        return PhaseType.SITTING_SCANNING;
    }
}
