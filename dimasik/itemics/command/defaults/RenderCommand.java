package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RenderCommand
extends Command {
    public RenderCommand(IItemics itemics) {
        super(itemics, "render");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        BetterBlockPos origin = this.ctx.playerFeet();
        int renderDistance = (RenderCommand.mc.gameSettings.renderDistanceChunks + 1) * 16;
        RenderCommand.mc.worldRenderer.markBlockRangeForRenderUpdate(origin.x - renderDistance, 0, origin.z - renderDistance, origin.x + renderDistance, 255, origin.z + renderDistance);
        this.logDirect("Done");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Fix glitched chunks";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The render command fixes glitched chunk rendering without having to reload all of them.", "", "Usage:", "> render");
    }
}
