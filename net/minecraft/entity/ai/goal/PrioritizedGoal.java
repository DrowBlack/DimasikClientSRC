package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.goal.Goal;

public class PrioritizedGoal
extends Goal {
    private final Goal inner;
    private final int priority;
    private boolean running;

    public PrioritizedGoal(int priorityIn, Goal goalIn) {
        this.priority = priorityIn;
        this.inner = goalIn;
    }

    public boolean isPreemptedBy(PrioritizedGoal other) {
        return this.isPreemptible() && other.getPriority() < this.getPriority();
    }

    @Override
    public boolean shouldExecute() {
        return this.inner.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.inner.shouldContinueExecuting();
    }

    @Override
    public boolean isPreemptible() {
        return this.inner.isPreemptible();
    }

    @Override
    public void startExecuting() {
        if (!this.running) {
            this.running = true;
            this.inner.startExecuting();
        }
    }

    @Override
    public void resetTask() {
        if (this.running) {
            this.running = false;
            this.inner.resetTask();
        }
    }

    @Override
    public void tick() {
        this.inner.tick();
    }

    @Override
    public void setMutexFlags(EnumSet<Goal.Flag> flagSet) {
        this.inner.setMutexFlags(flagSet);
    }

    @Override
    public EnumSet<Goal.Flag> getMutexFlags() {
        return this.inner.getMutexFlags();
    }

    public boolean isRunning() {
        return this.running;
    }

    public int getPriority() {
        return this.priority;
    }

    public Goal getGoal() {
        return this.inner;
    }

    public boolean equals(@Nullable Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? this.inner.equals(((PrioritizedGoal)p_equals_1_).inner) : false;
    }

    public int hashCode() {
        return this.inner.hashCode();
    }
}
