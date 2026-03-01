package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.util.math.vector.Vector3d;

public class LlamaFollowCaravanGoal
extends Goal {
    public final LlamaEntity llama;
    private double speedModifier;
    private int distCheckCounter;

    public LlamaFollowCaravanGoal(LlamaEntity llamaIn, double speedModifierIn) {
        this.llama = llamaIn;
        this.speedModifier = speedModifierIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.llama.getLeashed() && !this.llama.inCaravan()) {
            List<Entity> list = this.llama.world.getEntitiesInAABBexcluding(this.llama, this.llama.getBoundingBox().grow(9.0, 4.0, 9.0), entity -> {
                EntityType<?> entitytype = entity.getType();
                return entitytype == EntityType.LLAMA || entitytype == EntityType.TRADER_LLAMA;
            });
            MobEntity llamaentity = null;
            double d0 = Double.MAX_VALUE;
            for (Entity entity2 : list) {
                double d1;
                LlamaEntity llamaentity1 = (LlamaEntity)entity2;
                if (!llamaentity1.inCaravan() || llamaentity1.hasCaravanTrail() || (d1 = this.llama.getDistanceSq(llamaentity1)) > d0) continue;
                d0 = d1;
                llamaentity = llamaentity1;
            }
            if (llamaentity == null) {
                for (Entity entity1 : list) {
                    double d2;
                    LlamaEntity llamaentity2 = (LlamaEntity)entity1;
                    if (!llamaentity2.getLeashed() || llamaentity2.hasCaravanTrail() || (d2 = this.llama.getDistanceSq(llamaentity2)) > d0) continue;
                    d0 = d2;
                    llamaentity = llamaentity2;
                }
            }
            if (llamaentity == null) {
                return false;
            }
            if (d0 < 4.0) {
                return false;
            }
            if (!llamaentity.getLeashed() && !this.firstIsLeashed((LlamaEntity)llamaentity, 1)) {
                return false;
            }
            this.llama.joinCaravan((LlamaEntity)llamaentity);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
            double d0 = this.llama.getDistanceSq(this.llama.getCaravanHead());
            if (d0 > 676.0) {
                if (this.speedModifier <= 3.0) {
                    this.speedModifier *= 1.2;
                    this.distCheckCounter = 40;
                    return true;
                }
                if (this.distCheckCounter == 0) {
                    return false;
                }
            }
            if (this.distCheckCounter > 0) {
                --this.distCheckCounter;
            }
            return true;
        }
        return false;
    }

    @Override
    public void resetTask() {
        this.llama.leaveCaravan();
        this.speedModifier = 2.1;
    }

    @Override
    public void tick() {
        if (this.llama.inCaravan() && !(this.llama.getLeashHolder() instanceof LeashKnotEntity)) {
            LlamaEntity llamaentity = this.llama.getCaravanHead();
            double d0 = this.llama.getDistance(llamaentity);
            float f = 2.0f;
            Vector3d vector3d = new Vector3d(llamaentity.getPosX() - this.llama.getPosX(), llamaentity.getPosY() - this.llama.getPosY(), llamaentity.getPosZ() - this.llama.getPosZ()).normalize().scale(Math.max(d0 - 2.0, 0.0));
            this.llama.getNavigator().tryMoveToXYZ(this.llama.getPosX() + vector3d.x, this.llama.getPosY() + vector3d.y, this.llama.getPosZ() + vector3d.z, this.speedModifier);
        }
    }

    private boolean firstIsLeashed(LlamaEntity llama, int p_190858_2_) {
        if (p_190858_2_ > 8) {
            return false;
        }
        if (llama.inCaravan()) {
            if (llama.getCaravanHead().getLeashed()) {
                return true;
            }
            LlamaEntity llamaentity = llama.getCaravanHead();
            return this.firstIsLeashed(llamaentity, ++p_190858_2_);
        }
        return false;
    }
}
