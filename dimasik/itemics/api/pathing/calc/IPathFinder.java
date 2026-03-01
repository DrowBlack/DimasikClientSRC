package dimasik.itemics.api.pathing.calc;

import dimasik.itemics.api.pathing.calc.IPath;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.utils.PathCalculationResult;
import java.util.Optional;

public interface IPathFinder {
    public Goal getGoal();

    public PathCalculationResult calculate(long var1, long var3);

    public boolean isFinished();

    public Optional<IPath> pathToMostRecentNodeConsidered();

    public Optional<IPath> bestPathSoFar();
}
