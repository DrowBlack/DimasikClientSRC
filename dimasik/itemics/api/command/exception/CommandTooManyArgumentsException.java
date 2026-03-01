package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.CommandErrorMessageException;

public class CommandTooManyArgumentsException
extends CommandErrorMessageException {
    public CommandTooManyArgumentsException(int maxArgs) {
        super(String.format("Too many arguments (expected at most %d)", maxArgs));
    }
}
