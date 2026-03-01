package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.BlockById;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypeFor;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.utils.BlockOptionalMeta;
import java.util.stream.Stream;

public enum ForBlockOptionalMeta implements IDatatypeFor<BlockOptionalMeta>
{
    INSTANCE;


    @Override
    public BlockOptionalMeta get(IDatatypeContext ctx) throws CommandException {
        return new BlockOptionalMeta(ctx.getConsumer().getString());
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        return ctx.getConsumer().tabCompleteDatatype(BlockById.INSTANCE);
    }
}
