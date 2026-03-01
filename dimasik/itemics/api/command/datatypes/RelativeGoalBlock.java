package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypePost;
import dimasik.itemics.api.command.datatypes.RelativeCoordinate;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;
import net.minecraft.util.math.MathHelper;

public enum RelativeGoalBlock implements IDatatypePost<GoalBlock, BetterBlockPos>
{
    INSTANCE;


    @Override
    public GoalBlock apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        IArgConsumer consumer = ctx.getConsumer();
        return new GoalBlock(MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.x))), MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.y))), MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.z))));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        IArgConsumer consumer = ctx.getConsumer();
        if (consumer.hasAtMost(3)) {
            return consumer.tabCompleteDatatype(RelativeCoordinate.INSTANCE);
        }
        return Stream.empty();
    }
}
