package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.api.command.IItemicsChatControl;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandNotFoundException;
import dimasik.itemics.api.command.helpers.Paginator;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class HelpCommand
extends Command {
    public HelpCommand(IItemics itemics) {
        super(itemics, "help", "?");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);
        if (!args.hasAny() || args.is(Integer.class)) {
            Paginator.paginate(args, new Paginator(this.itemics.getCommandManager().getRegistry().descendingStream().filter(command -> !command.hiddenFromHelp()).collect(Collectors.toList())), () -> this.logDirect("All Itemics commands (clickable):"), command -> {
                String names = String.join((CharSequence)"/", command.getNames());
                String name = command.getNames().get(0);
                StringTextComponent shortDescComponent = new StringTextComponent(" - " + command.getShortDesc());
                shortDescComponent.setStyle(shortDescComponent.getStyle().setFormatting(TextFormatting.DARK_GRAY));
                StringTextComponent namesComponent = new StringTextComponent(names);
                namesComponent.setStyle(namesComponent.getStyle().setFormatting(TextFormatting.WHITE));
                StringTextComponent hoverComponent = new StringTextComponent("");
                hoverComponent.setStyle(hoverComponent.getStyle().setFormatting(TextFormatting.GRAY));
                hoverComponent.append(namesComponent);
                hoverComponent.appendString("\n" + command.getShortDesc());
                hoverComponent.appendString("\n\nClick to view full help");
                String clickCommand = IItemicsChatControl.FORCE_COMMAND_PREFIX + String.format("%s %s", label, command.getNames().get(0));
                StringTextComponent component = new StringTextComponent(name);
                component.setStyle(component.getStyle().setFormatting(TextFormatting.GRAY));
                component.append(shortDescComponent);
                component.setStyle(component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent)).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand)));
                return component;
            }, IItemicsChatControl.FORCE_COMMAND_PREFIX + label);
        } else {
            String commandName = args.getString().toLowerCase();
            ICommand command2 = this.itemics.getCommandManager().getCommand(commandName);
            if (command2 == null) {
                throw new CommandNotFoundException(commandName);
            }
            this.logDirect(String.format("%s - %s", String.join((CharSequence)" / ", command2.getNames()), command2.getShortDesc()));
            this.logDirect("");
            command2.getLongDesc().forEach(this::logDirect);
            this.logDirect("");
            StringTextComponent returnComponent = new StringTextComponent("Click to return to the help menu");
            returnComponent.setStyle(returnComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, IItemicsChatControl.FORCE_COMMAND_PREFIX + label)));
            this.logDirect(returnComponent);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper().addCommands(this.itemics.getCommandManager()).filterPrefix(args.getString()).stream();
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "View all commands or help on specific ones";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Using this command, you can view detailed help information on how to use certain commands of Itemics.", "", "Usage:", "> help - Lists all commands and their short descriptions.", "> help <command> - Displays help information on a specific command.");
    }
}
