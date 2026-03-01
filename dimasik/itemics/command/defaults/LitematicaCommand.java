package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LitematicaCommand
extends Command {
    public LitematicaCommand(IItemics itemics) {
        super(itemics, "litematica");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        int schematic = 0;
        if (args.hasAny()) {
            args.requireMax(1);
            if (args.is(Integer.class)) {
                schematic = args.getAs(Integer.class) - 1;
            }
        }
        try {
            this.itemics.getBuilderProcess().buildOpenLitematic(schematic);
        }
        catch (IndexOutOfBoundsException e) {
            this.logDirect("Pleas provide a valid index.");
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Builds the loaded schematic";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Build a schematic currently open in Litematica.", "", "Usage:", "> litematica", "> litematica <#>");
    }
}
