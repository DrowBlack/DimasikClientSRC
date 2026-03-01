package dimasik.itemics.command.manager;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.api.command.argument.ICommandArgument;
import dimasik.itemics.api.command.exception.CommandUnhandledException;
import dimasik.itemics.api.command.exception.ICommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import dimasik.itemics.api.command.manager.ICommandManager;
import dimasik.itemics.api.command.registry.Registry;
import dimasik.itemics.command.argument.ArgConsumer;
import dimasik.itemics.command.argument.CommandArguments;
import dimasik.itemics.command.defaults.DefaultCommands;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Tuple;

public class CommandManager
implements ICommandManager {
    private final Registry<ICommand> registry = new Registry();
    private final Itemics itemics;

    public CommandManager(Itemics itemics) {
        this.itemics = itemics;
        DefaultCommands.createAll(itemics).forEach(this.registry::register);
    }

    @Override
    public IItemics getItemics() {
        return this.itemics;
    }

    @Override
    public Registry<ICommand> getRegistry() {
        return this.registry;
    }

    @Override
    public ICommand getCommand(String name) {
        for (ICommand command : this.registry.entries) {
            if (!command.getNames().contains(name.toLowerCase(Locale.US))) continue;
            return command;
        }
        return null;
    }

    @Override
    public boolean execute(String string) {
        return this.execute(CommandManager.expand(string));
    }

    @Override
    public boolean execute(Tuple<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        if (execution != null) {
            execution.execute();
        }
        return execution != null;
    }

    @Override
    public Stream<String> tabComplete(Tuple<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        return execution == null ? Stream.empty() : execution.tabComplete();
    }

    @Override
    public Stream<String> tabComplete(String prefix) {
        Tuple<String, List<ICommandArgument>> pair = CommandManager.expand(prefix, true);
        String label = pair.getA();
        List<ICommandArgument> args = pair.getB();
        if (args.isEmpty()) {
            return new TabCompleteHelper().addCommands(this.itemics.getCommandManager()).filterPrefix(label).stream();
        }
        return this.tabComplete(pair);
    }

    private ExecutionWrapper from(Tuple<String, List<ICommandArgument>> expanded) {
        String label = expanded.getA();
        ArgConsumer args = new ArgConsumer(this, expanded.getB());
        ICommand command = this.getCommand(label);
        return command == null ? null : new ExecutionWrapper(command, label, args);
    }

    private static Tuple<String, List<ICommandArgument>> expand(String string, boolean preserveEmptyLast) {
        String label = string.split("\\s", 2)[0];
        List<ICommandArgument> args = CommandArguments.from(string.substring(label.length()), preserveEmptyLast);
        return new Tuple<String, List<ICommandArgument>>(label, args);
    }

    public static Tuple<String, List<ICommandArgument>> expand(String string) {
        return CommandManager.expand(string, false);
    }

    private static final class ExecutionWrapper {
        private ICommand command;
        private String label;
        private ArgConsumer args;

        private ExecutionWrapper(ICommand command, String label, ArgConsumer args) {
            this.command = command;
            this.label = label;
            this.args = args;
        }

        private void execute() {
            try {
                this.command.execute(this.label, this.args);
            }
            catch (Throwable t) {
                ICommandException exception = t instanceof ICommandException ? (ICommandException)((Object)t) : new CommandUnhandledException(t);
                exception.handle(this.command, this.args.getArgs());
            }
        }

        private Stream<String> tabComplete() {
            try {
                return this.command.tabComplete(this.label, this.args);
            }
            catch (Throwable t) {
                return Stream.empty();
            }
        }
    }
}
