package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.exception.ICommandException;

public abstract class CommandException
extends Exception
implements ICommandException {
    protected CommandException(String reason) {
        super(reason);
    }

    protected CommandException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
