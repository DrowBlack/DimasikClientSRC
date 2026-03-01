package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.IDatatype;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.exception.CommandException;

public interface IDatatypeFor<T>
extends IDatatype {
    public T get(IDatatypeContext var1) throws CommandException;
}
