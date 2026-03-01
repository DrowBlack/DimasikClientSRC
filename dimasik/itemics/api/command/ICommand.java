package dimasik.itemics.api.command;

import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.utils.Helper;
import java.util.List;
import java.util.stream.Stream;

public interface ICommand
extends Helper {
    public void execute(String var1, IArgConsumer var2) throws CommandException;

    public Stream<String> tabComplete(String var1, IArgConsumer var2) throws CommandException;

    public String getShortDesc();

    public List<String> getLongDesc();

    public List<String> getNames();

    default public boolean hiddenFromHelp() {
        return false;
    }
}
