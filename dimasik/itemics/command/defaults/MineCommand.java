package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.BlockById;
import dimasik.itemics.api.command.datatypes.ForBlockOptionalMeta;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.utils.BlockOptionalMeta;
import dimasik.itemics.cache.WorldScanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MineCommand
extends Command {
    public MineCommand(IItemics itemics) {
        super(itemics, "mine");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        int quantity = args.getAsOrDefault(Integer.class, 0);
        args.requireMin(1);
        ArrayList<BlockOptionalMeta> boms = new ArrayList<BlockOptionalMeta>();
        while (args.hasAny()) {
            boms.add((BlockOptionalMeta)args.getDatatypeFor(ForBlockOptionalMeta.INSTANCE));
        }
        WorldScanner.INSTANCE.repack(this.ctx);
        this.logDirect(String.format("Mining %s", ((Object)boms).toString()));
        this.itemics.getMineProcess().mine(quantity, boms.toArray(new BlockOptionalMeta[0]));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return args.tabCompleteDatatype(BlockById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Mine some blocks";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The mine command allows you to tell Itemics to search for and mine individual blocks.", "", "The specified blocks can be ores, or any other block.", "", "Also see the legitMine settings (see #set l legitMine).", "", "Usage:", "> mine diamond_ore - Mines all diamonds it can find.");
    }
}
