package dimasik.itemics.api.command.exception;

import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.api.command.argument.ICommandArgument;
import dimasik.itemics.api.command.exception.ICommandException;
import dimasik.itemics.api.utils.Helper;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class CommandUnhandledException
extends RuntimeException
implements ICommandException {
    public CommandUnhandledException(String message) {
        super(message);
    }

    public CommandUnhandledException(Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        Helper.HELPER.logDirect("An unhandled exception occurred. The error is in your game's log, please report this at https://github.com/cabaletta/itemics/issues", TextFormatting.RED);
        this.printStackTrace();
    }
}
