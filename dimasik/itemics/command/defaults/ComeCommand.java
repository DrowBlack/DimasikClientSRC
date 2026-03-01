package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandInvalidStateException;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;

public class ComeCommand
extends Command {
    public ComeCommand(IItemics itemics) {
        super(itemics, "come");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        Entity entity = mc.getRenderViewEntity();
        if (entity == null) {
            throw new CommandInvalidStateException("render view entity is null");
        }
        this.itemics.getCustomGoalProcess().setGoalAndPath(new GoalBlock(entity.getPosition()));
        this.logDirect("Coming");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Start heading towards your camera";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The come command tells Itemics to head towards your camera.", "", "This can be useful in hacked clients where freecam doesn't move your player position.", "", "Usage:", "> come");
    }
}
