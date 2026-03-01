package dimasik.itemics.utils;

import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.process.PathingCommand;
import dimasik.itemics.api.process.PathingCommandType;
import dimasik.itemics.pathing.movement.CalculationContext;

public class PathingCommandContext
extends PathingCommand {
    public final CalculationContext desiredCalcContext;

    public PathingCommandContext(Goal goal, PathingCommandType commandType, CalculationContext context) {
        super(goal, commandType);
        this.desiredCalcContext = context;
    }
}
