package net.minecraft.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;

public class FindWaterGoal
extends Goal {
    private final CreatureEntity creature;

    public FindWaterGoal(CreatureEntity creature) {
        this.creature = creature;
    }

    @Override
    public boolean shouldExecute() {
        return this.creature.isOnGround() && !this.creature.world.getFluidState(this.creature.getPosition()).isTagged(FluidTags.WATER);
    }

    @Override
    public void startExecuting() {
        Vector3i blockpos = null;
        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(MathHelper.floor(this.creature.getPosX() - 2.0), MathHelper.floor(this.creature.getPosY() - 2.0), MathHelper.floor(this.creature.getPosZ() - 2.0), MathHelper.floor(this.creature.getPosX() + 2.0), MathHelper.floor(this.creature.getPosY()), MathHelper.floor(this.creature.getPosZ() + 2.0))) {
            if (!this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)) continue;
            blockpos = blockpos1;
            break;
        }
        if (blockpos != null) {
            this.creature.getMoveHelper().setMoveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 1.0);
        }
    }
}
