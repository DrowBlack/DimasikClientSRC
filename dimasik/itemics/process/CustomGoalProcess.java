package dimasik.itemics.process;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.process.ICustomGoalProcess;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.utils.ItemicsProcessHelper;

public final class CustomGoalProcess
extends ItemicsProcessHelper
implements ICustomGoalProcess {
    private Goal goal;
    private State state;

    public CustomGoalProcess(Itemics itemics) {
        super(itemics);
    }

    @Override
    public void setGoal(Goal goal) {
        this.goal = goal;
        if (this.state == State.NONE) {
            this.state = State.GOAL_SET;
        }
        if (this.state == State.EXECUTING) {
            this.state = State.PATH_REQUESTED;
        }
    }

    @Override
    public void path() {
        this.state = State.PATH_REQUESTED;
    }

    @Override
    public Goal getGoal() {
        return this.goal;
    }

    @Override
    public boolean isActive() {
        return this.state != State.NONE;
    }

    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        switch (this.state) {
            case GOAL_SET: {
                return new PathingCommand(this.goal, PathingCommandType.CANCEL_AND_SET_GOAL);
            }
            case PATH_REQUESTED: {
                PathingCommand ret = new PathingCommand(this.goal, PathingCommandType.FORCE_REVALIDATE_GOAL_AND_PATH);
                this.state = State.EXECUTING;
                return ret;
            }
            case EXECUTING: {
                if (calcFailed) {
                    this.onLostControl();
                    return new PathingCommand(this.goal, PathingCommandType.CANCEL_AND_SET_GOAL);
                }
                if (this.goal == null || this.goal.isInGoal(this.ctx.playerFeet()) && this.goal.isInGoal(this.itemics.getPathingBehavior().pathStart())) {
                    this.onLostControl();
                    if (((Boolean)Itemics.settings().disconnectOnArrival.value).booleanValue()) {
                        this.ctx.world().sendQuittingDisconnectingPacket();
                    }
                    if (((Boolean)Itemics.settings().notificationOnPathComplete.value).booleanValue()) {
                        this.logNotification("Pathing complete", false);
                    }
                    return new PathingCommand(this.goal, PathingCommandType.CANCEL_AND_SET_GOAL);
                }
                return new PathingCommand(this.goal, PathingCommandType.SET_GOAL_AND_PATH);
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public void onLostControl() {
        this.state = State.NONE;
        this.goal = null;
    }

    @Override
    public String displayName0() {
        return "Custom Goal " + String.valueOf(this.goal);
    }

    protected static enum State {
        NONE,
        GOAL_SET,
        PATH_REQUESTED,
        EXECUTING;

    }
}
