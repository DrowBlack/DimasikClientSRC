package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.CommandErrorMessageException;

public class CommandInvalidStateException
extends CommandErrorMessageException {
    public CommandInvalidStateException(String reason) {
        super(reason);
    }
}
