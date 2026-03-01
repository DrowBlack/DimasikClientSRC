package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.pathing.goals.GoalXZ;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ThisWayCommand
extends Command {
    public ThisWayCommand(IItemics itemics) {
        super(itemics, "thisway", "forward");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireExactly(1);
        GoalXZ goal = GoalXZ.fromDirection(this.ctx.playerFeetAsVec(), this.ctx.player().rotationYawHead, args.getAs(Double.class));
        this.itemics.getCustomGoalProcess().setGoal(goal);
        this.logDirect(String.format("Goal: %s", goal));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Travel in your current direction";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Creates a GoalXZ some amount of blocks in the direction you're currently looking", "", "Usage:", "> thisway <distance> - makes a GoalXZ distance blocks in front of you");
    }
}
