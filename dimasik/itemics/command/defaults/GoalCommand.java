package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.RelativeCoordinate;
import dimasik.itemics.api.command.datatypes.RelativeGoal;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.process.ICustomGoalProcess;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GoalCommand
extends Command {
    public GoalCommand(IItemics itemics) {
        super(itemics, "goal");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess goalProcess = this.itemics.getCustomGoalProcess();
        if (args.hasAny() && Arrays.asList("reset", "clear", "none").contains(args.peekString())) {
            args.requireMax(1);
            if (goalProcess.getGoal() != null) {
                goalProcess.setGoal(null);
                this.logDirect("Cleared goal");
            } else {
                this.logDirect("There was no goal to clear");
            }
        } else {
            args.requireMax(3);
            BetterBlockPos origin = this.itemics.getPlayerContext().playerFeet();
            Goal goal = (Goal)args.getDatatypePost(RelativeGoal.INSTANCE, origin);
            goalProcess.setGoal(goal);
            this.logDirect(String.format("Goal: %s", goal.toString()));
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        TabCompleteHelper helper = new TabCompleteHelper();
        if (args.hasExactlyOne()) {
            helper.append("reset", "clear", "none", "~");
        } else if (args.hasAtMost(3)) {
            while (args.has(2) && args.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) != null) {
                args.get();
                if (args.has(2)) continue;
                helper.append("~");
            }
        }
        return helper.filterPrefix(args.getString()).stream();
    }

    @Override
    public String getShortDesc() {
        return "Set or clear the goal";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The goal command allows you to set or clear Itemics's goal.", "", "Wherever a coordinate is expected, you can use ~ just like in regular Minecraft commands. Or, you can just use regular numbers.", "", "Usage:", "> goal - Set the goal to your current position", "> goal <reset/clear/none> - Erase the goal", "> goal <y> - Set the goal to a Y level", "> goal <x> <z> - Set the goal to an X,Z position", "> goal <x> <y> <z> - Set the goal to an X,Y,Z position");
    }
}
