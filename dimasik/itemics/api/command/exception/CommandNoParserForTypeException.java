package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.CommandUnhandledException;

public class CommandNoParserForTypeException
extends CommandUnhandledException {
    public CommandNoParserForTypeException(Class<?> klass) {
        super(String.format("Could not find a handler for type %s", klass.getSimpleName()));
    }
}
