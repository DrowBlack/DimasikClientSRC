package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.IDatatype;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.exception.CommandException;

public interface IDatatypePost<T, O>
extends IDatatype {
    public T apply(IDatatypeContext var1, O var2) throws CommandException;
}
