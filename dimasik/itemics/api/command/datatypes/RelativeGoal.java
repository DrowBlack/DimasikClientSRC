package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypePost;
import dimasik.itemics.api.command.datatypes.RelativeCoordinate;
import dimasik.itemics.api.command.datatypes.RelativeGoalBlock;
import dimasik.itemics.api.command.datatypes.RelativeGoalXZ;
import dimasik.itemics.api.command.datatypes.RelativeGoalYLevel;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.pathing.goals.GoalXZ;
import dimasik.itemics.api.pathing.goals.GoalYLevel;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;

public enum RelativeGoal implements IDatatypePost<Goal, BetterBlockPos>
{
    INSTANCE;


    @Override
    public Goal apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        IArgConsumer consumer;
        GoalBlock goalBlock;
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        if ((goalBlock = (GoalBlock)(consumer = ctx.getConsumer()).peekDatatypePostOrNull(RelativeGoalBlock.INSTANCE, origin)) != null) {
            return goalBlock;
        }
        GoalXZ goalXZ = (GoalXZ)consumer.peekDatatypePostOrNull(RelativeGoalXZ.INSTANCE, origin);
        if (goalXZ != null) {
            return goalXZ;
        }
        GoalYLevel goalYLevel = (GoalYLevel)consumer.peekDatatypePostOrNull(RelativeGoalYLevel.INSTANCE, origin);
        if (goalYLevel != null) {
            return goalYLevel;
        }
        return new GoalBlock(origin);
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        return ctx.getConsumer().tabCompleteDatatype(RelativeCoordinate.INSTANCE);
    }
}
