package dimasik.itemics.api.command.manager;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.api.command.argument.ICommandArgument;
import dimasik.itemics.api.command.registry.Registry;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.Tuple;

public interface ICommandManager {
    public IItemics getItemics();

    public Registry<ICommand> getRegistry();

    public ICommand getCommand(String var1);

    public boolean execute(String var1);

    public boolean execute(Tuple<String, List<ICommandArgument>> var1);

    public Stream<String> tabComplete(Tuple<String, List<ICommandArgument>> var1);

    public Stream<String> tabComplete(String var1);
}
