package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
    private final EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    public abstract boolean shouldExecute();

    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    public boolean isPreemptible() {
        return true;
    }

    public void startExecuting() {
    }

    public void resetTask() {
    }

    public void tick() {
    }

    public void setMutexFlags(EnumSet<Flag> flagSet) {
        this.flags.clear();
        this.flags.addAll(flagSet);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public EnumSet<Flag> getMutexFlags() {
        return this.flags;
    }

    public static enum Flag {
        MOVE,
        LOOK,
        JUMP,
        TARGET;

    }
}
