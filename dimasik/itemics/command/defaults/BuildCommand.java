package dimasik.itemics.command.defaults;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.RelativeBlockPos;
import dimasik.itemics.api.command.datatypes.RelativeFile;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandInvalidStateException;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.commons.io.FilenameUtils;

public class BuildCommand
extends Command {
    private static final File schematicsDir = new File(BuildCommand.mc.gameDir, "schematics");

    public BuildCommand(IItemics itemics) {
        super(itemics, "build");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        BetterBlockPos buildOrigin;
        File file = ((File)args.getDatatypePost(RelativeFile.INSTANCE, schematicsDir)).getAbsoluteFile();
        if (FilenameUtils.getExtension(file.getAbsolutePath()).isEmpty()) {
            file = new File(file.getAbsolutePath() + "." + (String)Itemics.settings().schematicFallbackExtension.value);
        }
        BetterBlockPos origin = this.ctx.playerFeet();
        if (args.hasAny()) {
            args.requireMax(3);
            buildOrigin = (BetterBlockPos)args.getDatatypePost(RelativeBlockPos.INSTANCE, origin);
        } else {
            args.requireMax(0);
            buildOrigin = origin;
        }
        boolean success = this.itemics.getBuilderProcess().build(file.getName(), file, (Vector3i)buildOrigin);
        if (!success) {
            throw new CommandInvalidStateException("Couldn't load the schematic. Make sure to use the FULL file name, including the extension (e.g. blah.schematic).");
        }
        this.logDirect(String.format("Successfully loaded schematic for building\nOrigin: %s", buildOrigin));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return RelativeFile.tabComplete(args, schematicsDir);
        }
        if (args.has(2)) {
            args.get();
            return args.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Build a schematic";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Build a schematic from a file.", "", "Usage:", "> build <filename> - Loads and builds '<filename>.schematic'", "> build <filename> <x> <y> <z> - Custom position");
    }
}
