package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.cache.WorldScanner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RepackCommand
extends Command {
    public RepackCommand(IItemics itemics) {
        super(itemics, "repack", "rescan");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        this.logDirect(String.format("Queued %d chunks for repacking", WorldScanner.INSTANCE.repack(this.ctx)));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Re-cache chunks";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Repack chunks around you. This basically re-caches them.", "", "Usage:", "> repack - Repack chunks.");
    }
}
