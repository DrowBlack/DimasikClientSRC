package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.process.ICustomGoalProcess;
import dimasik.itemics.cache.WorldScanner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PathCommand
extends Command {
    public PathCommand(IItemics itemics) {
        super(itemics, "path");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess customGoalProcess = this.itemics.getCustomGoalProcess();
        args.requireMax(0);
        WorldScanner.INSTANCE.repack(this.ctx);
        customGoalProcess.path();
        this.logDirect("Now pathing");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Start heading towards the goal";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The path command tells Itemics to head towards the current goal.", "", "Usage:", "> path - Start the pathing.");
    }
}
