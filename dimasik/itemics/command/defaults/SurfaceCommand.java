package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.AirBlock;

public class SurfaceCommand
extends Command {
    protected SurfaceCommand(IItemics itemics) {
        super(itemics, "surface", "top");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        int startingYPos;
        BetterBlockPos playerPos = this.itemics.getPlayerContext().playerFeet();
        int surfaceLevel = this.itemics.getPlayerContext().world().getSeaLevel();
        int worldHeight = this.itemics.getPlayerContext().world().getHeight();
        if (playerPos.getY() > surfaceLevel && SurfaceCommand.mc.world.getBlockState(playerPos.up()).getBlock() instanceof AirBlock) {
            this.logDirect("Already at surface");
            return;
        }
        for (int currentIteratedY = startingYPos = Math.max(playerPos.getY(), surfaceLevel); currentIteratedY < worldHeight; ++currentIteratedY) {
            BetterBlockPos newPos = new BetterBlockPos(playerPos.getX(), currentIteratedY, playerPos.getZ());
            if (SurfaceCommand.mc.world.getBlockState(newPos).getBlock() instanceof AirBlock || newPos.getY() <= playerPos.getY()) continue;
            GoalBlock goal = new GoalBlock(newPos.up());
            this.logDirect(String.format("Going to: %s", ((Object)goal).toString()));
            this.itemics.getCustomGoalProcess().setGoalAndPath(goal);
            return;
        }
        this.logDirect("No higher location found");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Used to get out of caves, mines, ...";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The surface/top command tells Itemics to head towards the closest surface-like area.", "", "This can be the surface or the highest available air space, depending on circumstances.", "", "Usage:", "> surface - Used to get out of caves, mines, ...", "> top - Used to get out of caves, mines, ...");
    }
}
