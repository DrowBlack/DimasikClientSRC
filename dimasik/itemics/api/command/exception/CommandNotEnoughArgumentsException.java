package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.CommandErrorMessageException;

public class CommandNotEnoughArgumentsException
extends CommandErrorMessageException {
    public CommandNotEnoughArgumentsException(int minArgs) {
        super(String.format("Not enough arguments (expected at least %d)", minArgs));
    }
}
