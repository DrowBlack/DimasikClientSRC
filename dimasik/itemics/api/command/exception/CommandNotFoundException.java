package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.api.command.argument.ICommandArgument;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.utils.Helper;
import java.util.List;

public class CommandNotFoundException
extends CommandException {
    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Command not found: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        Helper.HELPER.logDirect(this.getMessage());
    }
}
