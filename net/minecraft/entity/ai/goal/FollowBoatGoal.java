package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.BoatGoals;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FollowBoatGoal
extends Goal {
    private int field_205143_a;
    private final CreatureEntity swimmer;
    private PlayerEntity player;
    private BoatGoals field_205146_d;

    public FollowBoatGoal(CreatureEntity swimmer) {
        this.swimmer = swimmer;
    }

    @Override
    public boolean shouldExecute() {
        List<BoatEntity> list = this.swimmer.world.getEntitiesWithinAABB(BoatEntity.class, this.swimmer.getBoundingBox().grow(5.0));
        boolean flag = false;
        for (BoatEntity boatentity : list) {
            Entity entity = boatentity.getControllingPassenger();
            if (!(entity instanceof PlayerEntity) || !(MathHelper.abs(((PlayerEntity)entity).moveStrafing) > 0.0f) && !(MathHelper.abs(((PlayerEntity)entity).moveForward) > 0.0f)) continue;
            flag = true;
            break;
        }
        return this.player != null && (MathHelper.abs(this.player.moveStrafing) > 0.0f || MathHelper.abs(this.player.moveForward) > 0.0f) || flag;
    }

    @Override
    public boolean isPreemptible() {
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.player != null && this.player.isPassenger() && (MathHelper.abs(this.player.moveStrafing) > 0.0f || MathHelper.abs(this.player.moveForward) > 0.0f);
    }

    @Override
    public void startExecuting() {
        for (BoatEntity boatentity : this.swimmer.world.getEntitiesWithinAABB(BoatEntity.class, this.swimmer.getBoundingBox().grow(5.0))) {
            if (boatentity.getControllingPassenger() == null || !(boatentity.getControllingPassenger() instanceof PlayerEntity)) continue;
            this.player = (PlayerEntity)boatentity.getControllingPassenger();
            break;
        }
        this.field_205143_a = 0;
        this.field_205146_d = BoatGoals.GO_TO_BOAT;
    }

    @Override
    public void resetTask() {
        this.player = null;
    }

    @Override
    public void tick() {
        boolean flag;
        boolean bl = flag = MathHelper.abs(this.player.moveStrafing) > 0.0f || MathHelper.abs(this.player.moveForward) > 0.0f;
        float f = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (flag ? 0.01f : 0.0f) : 0.015f;
        this.swimmer.moveRelative(f, new Vector3d(this.swimmer.moveStrafing, this.swimmer.moveVertical, this.swimmer.moveForward));
        this.swimmer.move(MoverType.SELF, this.swimmer.getMotion());
        if (--this.field_205143_a <= 0) {
            this.field_205143_a = 10;
            if (this.field_205146_d == BoatGoals.GO_TO_BOAT) {
                BlockPos blockpos = this.player.getPosition().offset(this.player.getHorizontalFacing().getOpposite());
                blockpos = blockpos.add(0, -1, 0);
                this.swimmer.getNavigator().tryMoveToXYZ(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 1.0);
                if (this.swimmer.getDistance(this.player) < 4.0f) {
                    this.field_205143_a = 0;
                    this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
                }
            } else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION) {
                Direction direction = this.player.getAdjustedHorizontalFacing();
                BlockPos blockpos1 = this.player.getPosition().offset(direction, 10);
                this.swimmer.getNavigator().tryMoveToXYZ(blockpos1.getX(), blockpos1.getY() - 1, blockpos1.getZ(), 1.0);
                if (this.swimmer.getDistance(this.player) > 12.0f) {
                    this.field_205143_a = 0;
                    this.field_205146_d = BoatGoals.GO_TO_BOAT;
                }
            }
        }
    }
}
