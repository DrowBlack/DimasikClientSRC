package net.minecraft.entity.ai.goal;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.InteractDoorGoal;

public class OpenDoorGoal
extends InteractDoorGoal {
    private final boolean closeDoor;
    private int closeDoorTemporisation;

    public OpenDoorGoal(MobEntity entitylivingIn, boolean shouldClose) {
        super(entitylivingIn);
        this.entity = entitylivingIn;
        this.closeDoor = shouldClose;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.closeDoor && this.closeDoorTemporisation > 0 && super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        this.closeDoorTemporisation = 20;
        this.toggleDoor(true);
    }

    @Override
    public void resetTask() {
        this.toggleDoor(false);
    }

    @Override
    public void tick() {
        --this.closeDoorTemporisation;
        super.tick();
    }
}
