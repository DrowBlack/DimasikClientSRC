package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MoveToSkylightTask;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class GoOutsideAfterRaidTask
extends MoveToSkylightTask {
    public GoOutsideAfterRaidTask(float speed) {
        super(speed);
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
        Raid raid = worldIn.findRaid(owner.getPosition());
        return raid != null && raid.isVictory() && super.shouldExecute(worldIn, owner);
    }
}
