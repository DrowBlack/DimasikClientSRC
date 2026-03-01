package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypeFor;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Direction;

public enum ForDirection implements IDatatypeFor<Direction>
{
    INSTANCE;


    @Override
    public Direction get(IDatatypeContext ctx) throws CommandException {
        return Direction.valueOf(ctx.getConsumer().getString().toUpperCase(Locale.US));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(Stream.of(Direction.values()).map(Direction::getName2).map(String::toLowerCase)).filterPrefix(ctx.getConsumer().getString()).stream();
    }
}
