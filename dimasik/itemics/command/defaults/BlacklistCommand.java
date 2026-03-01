package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandInvalidStateException;
import dimasik.itemics.api.process.IGetToBlockProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BlacklistCommand
extends Command {
    public BlacklistCommand(IItemics itemics) {
        super(itemics, "blacklist");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        IGetToBlockProcess proc = this.itemics.getGetToBlockProcess();
        if (!proc.isActive()) {
            throw new CommandInvalidStateException("GetToBlockProcess is not currently active");
        }
        if (!proc.blacklistClosest()) {
            throw new CommandInvalidStateException("No known locations, unable to blacklist");
        }
        this.logDirect("Blacklisted closest instances");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Blacklist closest block";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("While going to a block this command blacklists the closest block so that Itemics won't attempt to get to it.", "", "Usage:", "> blacklist");
    }
}
