package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.exception.CommandException;

public interface IDatatypePostFunction<T, O> {
    public T apply(O var1) throws CommandException;
}
