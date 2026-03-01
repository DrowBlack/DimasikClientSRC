package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.BlockById;
import dimasik.itemics.api.command.datatypes.ForBlockOptionalMeta;
import dimasik.itemics.api.command.datatypes.RelativeCoordinate;
import dimasik.itemics.api.command.datatypes.RelativeGoal;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.pathing.goals.Goal;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.BlockOptionalMeta;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GotoCommand
extends Command {
    protected GotoCommand(IItemics itemics) {
        super(itemics, "goto");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (args.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) != null) {
            args.requireMax(3);
            BetterBlockPos origin = this.itemics.getPlayerContext().playerFeet();
            Goal goal = (Goal)args.getDatatypePost(RelativeGoal.INSTANCE, origin);
            this.logDirect(String.format("Going to: %s", goal.toString()));
            this.itemics.getCustomGoalProcess().setGoalAndPath(goal);
            return;
        }
        args.requireMax(1);
        BlockOptionalMeta destination = (BlockOptionalMeta)args.getDatatypeFor(ForBlockOptionalMeta.INSTANCE);
        this.itemics.getGetToBlockProcess().getToBlock(destination);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return args.tabCompleteDatatype(BlockById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Go to a coordinate or block";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The goto command tells Itemics to head towards a given goal or block.", "", "Wherever a coordinate is expected, you can use ~ just like in regular Minecraft commands. Or, you can just use regular numbers.", "", "Usage:", "> goto <block> - Go to a block, wherever it is in the world", "> goto <y> - Go to a Y level", "> goto <x> <z> - Go to an X,Z position", "> goto <x> <y> <z> - Go to an X,Y,Z position");
    }
}
