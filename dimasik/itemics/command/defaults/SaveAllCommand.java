package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SaveAllCommand
extends Command {
    public SaveAllCommand(IItemics itemics) {
        super(itemics, "saveall");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        this.ctx.worldData().getCachedWorld().save();
        this.logDirect("Saved");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Saves Itemics's cache for this world";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The saveall command saves Itemics's world cache.", "", "Usage:", "> saveall");
    }
}
