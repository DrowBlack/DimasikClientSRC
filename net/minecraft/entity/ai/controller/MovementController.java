package net.minecraft.entity.ai.controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;

public class MovementController {
    protected final MobEntity mob;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double speed;
    protected float moveForward;
    protected float moveStrafe;
    protected Action action = Action.WAIT;

    public MovementController(MobEntity mob) {
        this.mob = mob;
    }

    public boolean isUpdating() {
        return this.action == Action.MOVE_TO;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setMoveTo(double x, double y, double z, double speedIn) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.speed = speedIn;
        if (this.action != Action.JUMPING) {
            this.action = Action.MOVE_TO;
        }
    }

    public void strafe(float forward, float strafe) {
        this.action = Action.STRAFE;
        this.moveForward = forward;
        this.moveStrafe = strafe;
        this.speed = 0.25;
    }

    public void tick() {
        if (this.action == Action.STRAFE) {
            float f8;
            float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float)this.speed * f;
            float f2 = this.moveForward;
            float f3 = this.moveStrafe;
            float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0f) {
                f4 = 1.0f;
            }
            f4 = f1 / f4;
            float f5 = MathHelper.sin(this.mob.rotationYaw * ((float)Math.PI / 180));
            float f6 = MathHelper.cos(this.mob.rotationYaw * ((float)Math.PI / 180));
            float f7 = (f2 *= f4) * f6 - (f3 *= f4) * f5;
            if (!this.func_234024_b_(f7, f8 = f3 * f6 + f2 * f5)) {
                this.moveForward = 1.0f;
                this.moveStrafe = 0.0f;
            }
            this.mob.setAIMoveSpeed(f1);
            this.mob.setMoveForward(this.moveForward);
            this.mob.setMoveStrafing(this.moveStrafe);
            this.action = Action.WAIT;
        } else if (this.action == Action.MOVE_TO) {
            this.action = Action.WAIT;
            double d0 = this.posX - this.mob.getPosX();
            double d1 = this.posZ - this.mob.getPosZ();
            double d2 = this.posY - this.mob.getPosY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
            if (d3 < 2.500000277905201E-7) {
                this.mob.setMoveForward(0.0f);
                return;
            }
            float f9 = (float)(MathHelper.atan2(d1, d0) * 57.2957763671875) - 90.0f;
            this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, f9, 90.0f);
            this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockpos = this.mob.getPosition();
            BlockState blockstate = this.mob.world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.world, blockpos);
            if (d2 > (double)this.mob.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0f, this.mob.getWidth()) || !voxelshape.isEmpty() && this.mob.getPosY() < voxelshape.getEnd(Direction.Axis.Y) + (double)blockpos.getY() && !block.isIn(BlockTags.DOORS) && !block.isIn(BlockTags.FENCES)) {
                this.mob.getJumpController().setJumping();
                this.action = Action.JUMPING;
            }
        } else if (this.action == Action.JUMPING) {
            this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.action = Action.WAIT;
            }
        } else {
            this.mob.setMoveForward(0.0f);
        }
    }

    private boolean func_234024_b_(float p_234024_1_, float p_234024_2_) {
        NodeProcessor nodeprocessor;
        PathNavigator pathnavigator = this.mob.getNavigator();
        return pathnavigator == null || (nodeprocessor = pathnavigator.getNodeProcessor()) == null || nodeprocessor.getPathNodeType(this.mob.world, MathHelper.floor(this.mob.getPosX() + (double)p_234024_1_), MathHelper.floor(this.mob.getPosY()), MathHelper.floor(this.mob.getPosZ() + (double)p_234024_2_)) == PathNodeType.WALKABLE;
    }

    protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
        float f1;
        float f = MathHelper.wrapDegrees(targetAngle - sourceAngle);
        if (f > maximumChange) {
            f = maximumChange;
        }
        if (f < -maximumChange) {
            f = -maximumChange;
        }
        if ((f1 = sourceAngle + f) < 0.0f) {
            f1 += 360.0f;
        } else if (f1 > 360.0f) {
            f1 -= 360.0f;
        }
        return f1;
    }

    public double getX() {
        return this.posX;
    }

    public double getY() {
        return this.posY;
    }

    public double getZ() {
        return this.posZ;
    }

    public static enum Action {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;

    }
}
