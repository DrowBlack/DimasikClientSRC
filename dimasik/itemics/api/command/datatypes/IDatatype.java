package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.exception.CommandException;
import java.util.stream.Stream;

public interface IDatatype {
    public Stream<String> tabComplete(IDatatypeContext var1) throws CommandException;
}
