package dimasik.itemics.api.process;

import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.process.IItemicsProcess;

public interface ICustomGoalProcess
extends IItemicsProcess {
    public void setGoal(Goal var1);

    public void path();

    public Goal getGoal();

    default public void setGoalAndPath(Goal goal) {
        this.setGoal(goal);
        this.path();
    }
}
