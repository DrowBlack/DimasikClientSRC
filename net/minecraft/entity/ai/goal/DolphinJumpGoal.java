package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.JumpGoal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class DolphinJumpGoal
extends JumpGoal {
    private static final int[] JUMP_DISTANCES = new int[]{0, 1, 4, 5, 6, 7};
    private final DolphinEntity dolphin;
    private final int field_220712_c;
    private boolean inWater;

    public DolphinJumpGoal(DolphinEntity dolphin, int p_i50329_2_) {
        this.dolphin = dolphin;
        this.field_220712_c = p_i50329_2_;
    }

    @Override
    public boolean shouldExecute() {
        if (this.dolphin.getRNG().nextInt(this.field_220712_c) != 0) {
            return false;
        }
        Direction direction = this.dolphin.getAdjustedHorizontalFacing();
        int i = direction.getXOffset();
        int j = direction.getZOffset();
        BlockPos blockpos = this.dolphin.getPosition();
        for (int k : JUMP_DISTANCES) {
            if (this.canJumpTo(blockpos, i, j, k) && this.isAirAbove(blockpos, i, j, k)) continue;
            return false;
        }
        return true;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
        return this.dolphin.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.dolphin.world.getBlockState(blockpos).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.dolphin.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.dolphin.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
    }

    @Override
    public boolean shouldContinueExecuting() {
        double d0 = this.dolphin.getMotion().y;
        return !(d0 * d0 < (double)0.03f && this.dolphin.rotationPitch != 0.0f && Math.abs(this.dolphin.rotationPitch) < 10.0f && this.dolphin.isInWater() || this.dolphin.isOnGround());
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        Direction direction = this.dolphin.getAdjustedHorizontalFacing();
        this.dolphin.setMotion(this.dolphin.getMotion().add((double)direction.getXOffset() * 0.6, 0.7, (double)direction.getZOffset() * 0.6));
        this.dolphin.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        this.dolphin.rotationPitch = 0.0f;
    }

    @Override
    public void tick() {
        boolean flag = this.inWater;
        if (!flag) {
            FluidState fluidstate = this.dolphin.world.getFluidState(this.dolphin.getPosition());
            this.inWater = fluidstate.isTagged(FluidTags.WATER);
        }
        if (this.inWater && !flag) {
            this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0f, 1.0f);
        }
        Vector3d vector3d = this.dolphin.getMotion();
        if (vector3d.y * vector3d.y < (double)0.03f && this.dolphin.rotationPitch != 0.0f) {
            this.dolphin.rotationPitch = MathHelper.rotLerp(this.dolphin.rotationPitch, 0.0f, 0.2f);
        } else {
            double d0 = Math.sqrt(Entity.horizontalMag(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * 57.2957763671875;
            this.dolphin.rotationPitch = (float)d1;
        }
    }
}
