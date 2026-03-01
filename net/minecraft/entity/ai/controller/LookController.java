package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class LookController {
    protected final MobEntity mob;
    protected float deltaLookYaw;
    protected float deltaLookPitch;
    protected boolean isLooking;
    protected double posX;
    protected double posY;
    protected double posZ;

    public LookController(MobEntity mob) {
        this.mob = mob;
    }

    public void setLookPosition(Vector3d lookVector) {
        this.setLookPosition(lookVector.x, lookVector.y, lookVector.z);
    }

    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
        this.setLookPosition(entityIn.getPosX(), LookController.getEyePosition(entityIn), entityIn.getPosZ(), deltaYaw, deltaPitch);
    }

    public void setLookPosition(double x, double y, double z) {
        this.setLookPosition(x, y, z, this.mob.getFaceRotSpeed(), this.mob.getVerticalFaceSpeed());
    }

    public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    public void tick() {
        if (this.shouldResetPitch()) {
            this.mob.rotationPitch = 0.0f;
        }
        if (this.isLooking) {
            this.isLooking = false;
            this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.getTargetYaw(), this.deltaLookYaw);
            this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, this.getTargetPitch(), this.deltaLookPitch);
        } else {
            this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.mob.renderYawOffset, 10.0f);
        }
        if (!this.mob.getNavigator().noPath()) {
            this.mob.rotationYawHead = MathHelper.func_219800_b(this.mob.rotationYawHead, this.mob.renderYawOffset, this.mob.getHorizontalFaceSpeed());
        }
    }

    protected boolean shouldResetPitch() {
        return true;
    }

    public boolean getIsLooking() {
        return this.isLooking;
    }

    public double getLookPosX() {
        return this.posX;
    }

    public double getLookPosY() {
        return this.posY;
    }

    public double getLookPosZ() {
        return this.posZ;
    }

    protected float getTargetPitch() {
        double d0 = this.posX - this.mob.getPosX();
        double d1 = this.posY - this.mob.getPosYEye();
        double d2 = this.posZ - this.mob.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        return (float)(-(MathHelper.atan2(d1, d3) * 57.2957763671875));
    }

    protected float getTargetYaw() {
        double d0 = this.posX - this.mob.getPosX();
        double d1 = this.posZ - this.mob.getPosZ();
        return (float)(MathHelper.atan2(d1, d0) * 57.2957763671875) - 90.0f;
    }

    protected float clampedRotate(float from, float to, float maxDelta) {
        float f = MathHelper.wrapSubtractDegrees(from, to);
        float f1 = MathHelper.clamp(f, -maxDelta, maxDelta);
        return from + f1;
    }

    private static double getEyePosition(Entity entity) {
        return entity instanceof LivingEntity ? entity.getPosYEye() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
    }
}
