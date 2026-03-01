package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.CommandException;

public abstract class CommandErrorMessageException
extends CommandException {
    protected CommandErrorMessageException(String reason) {
        super(reason);
    }

    protected CommandErrorMessageException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
