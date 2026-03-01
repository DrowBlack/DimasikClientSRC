package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;

public class FlyingMovementController
extends MovementController {
    private final int field_226323_i_;
    private final boolean field_226324_j_;

    public FlyingMovementController(MobEntity p_i225710_1_, int p_i225710_2_, boolean p_i225710_3_) {
        super(p_i225710_1_);
        this.field_226323_i_ = p_i225710_2_;
        this.field_226324_j_ = p_i225710_3_;
    }

    @Override
    public void tick() {
        if (this.action == MovementController.Action.MOVE_TO) {
            this.action = MovementController.Action.WAIT;
            this.mob.setNoGravity(true);
            double d0 = this.posX - this.mob.getPosX();
            double d1 = this.posY - this.mob.getPosY();
            double d2 = this.posZ - this.mob.getPosZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < 2.500000277905201E-7) {
                this.mob.setMoveVertical(0.0f);
                this.mob.setMoveForward(0.0f);
                return;
            }
            float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875) - 90.0f;
            this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, f, 90.0f);
            float f1 = this.mob.isOnGround() ? (float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) : (float)(this.speed * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
            this.mob.setAIMoveSpeed(f1);
            double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f2 = (float)(-(MathHelper.atan2(d1, d4) * 57.2957763671875));
            this.mob.rotationPitch = this.limitAngle(this.mob.rotationPitch, f2, this.field_226323_i_);
            this.mob.setMoveVertical(d1 > 0.0 ? f1 : -f1);
        } else {
            if (!this.field_226324_j_) {
                this.mob.setNoGravity(false);
            }
            this.mob.setMoveVertical(0.0f);
            this.mob.setMoveForward(0.0f);
        }
    }
}
