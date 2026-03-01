package net.minecraft.entity;

import net.minecraft.entity.BoostHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public interface IRideable {
    public boolean boost();

    public void travelTowards(Vector3d var1);

    public float getMountedSpeed();

    default public boolean ride(MobEntity mount, BoostHelper helper, Vector3d p_233622_3_) {
        Entity entity;
        if (!mount.isAlive()) {
            return false;
        }
        Entity entity2 = entity = mount.getPassengers().isEmpty() ? null : mount.getPassengers().get(0);
        if (mount.isBeingRidden() && mount.canBeSteered() && entity instanceof PlayerEntity) {
            mount.prevRotationYaw = mount.rotationYaw = entity.rotationYaw;
            mount.rotationPitch = entity.rotationPitch * 0.5f;
            mount.setRotation(mount.rotationYaw, mount.rotationPitch);
            mount.renderYawOffset = mount.rotationYaw;
            mount.rotationYawHead = mount.rotationYaw;
            mount.stepHeight = 1.0f;
            mount.jumpMovementFactor = mount.getAIMoveSpeed() * 0.1f;
            if (helper.saddledRaw && helper.field_233611_b_++ > helper.boostTimeRaw) {
                helper.saddledRaw = false;
            }
            if (mount.canPassengerSteer()) {
                float f = this.getMountedSpeed();
                if (helper.saddledRaw) {
                    f += f * 1.15f * MathHelper.sin((float)helper.field_233611_b_ / (float)helper.boostTimeRaw * (float)Math.PI);
                }
                mount.setAIMoveSpeed(f);
                this.travelTowards(new Vector3d(0.0, 0.0, 1.0));
                mount.newPosRotationIncrements = 0;
            } else {
                mount.func_233629_a_(mount, false);
                mount.setMotion(Vector3d.ZERO);
            }
            return true;
        }
        mount.stepHeight = 0.5f;
        mount.jumpMovementFactor = 0.02f;
        this.travelTowards(p_233622_3_);
        return false;
    }
}
