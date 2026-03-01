package dimasik.managers.hook.main;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dimasik.Load;
import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.command.api.Command;
import dimasik.managers.command.main.AutoContractCommand;
import dimasik.managers.command.main.AutoRegion;
import dimasik.managers.command.main.BindCommand;
import dimasik.managers.command.main.BlockESPCommand;
import dimasik.managers.command.main.ConfigCommand;
import dimasik.managers.command.main.FriendCommand;
import dimasik.managers.command.main.GpsCommand;
import dimasik.managers.command.main.HelpCommand;
import dimasik.managers.command.main.MacroCommand;
import dimasik.managers.command.main.RctCommand;
import dimasik.managers.command.main.StaffCommand;
import dimasik.managers.command.main.TargetCommand;
import dimasik.managers.command.main.TeleportCommand;
import dimasik.managers.command.main.VClipCommand;
import dimasik.managers.command.main.WayCommand;
import dimasik.utils.client.ChatUtils;
import java.util.ArrayList;
import lombok.Generated;
import net.minecraft.command.ISuggestionProvider;

public class CommandManagers
extends ArrayList<Command>
implements IManager<Command>,
IFinderModules<Command> {
    private boolean message;
    private final CommandDispatcher<ISuggestionProvider> commandDispatcher = new CommandDispatcher();

    public CommandManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new ConfigCommand());
        this.register(new FriendCommand());
        this.register(new VClipCommand());
        this.register(new TeleportCommand());
        this.register(new MacroCommand());
        this.register(new GpsCommand());
        this.register(new StaffCommand());
        this.register(new AutoRegion());
        this.register(new HelpCommand());
        this.register(new BindCommand());
        this.register(new AutoContractCommand());
        this.register(new BlockESPCommand());
        this.register(new TargetCommand());
        this.register(new WayCommand());
        this.register(new RctCommand());
    }

    @Override
    public <T extends Command> T findName(String name) {
        return (T)((Command)this.stream().filter(command -> command.getName().equals(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Command> T findClass(Class<T> clazz) {
        return (T)((Command)this.stream().filter(command -> command.getClass() == clazz).findAny().orElse(null));
    }

    @Override
    public void register(Command command) {
        this.add(command);
    }

    public void run(String message) {
        if (ClientManagers.isUnHook()) {
            this.setMessage(false);
            return;
        }
        if (message.startsWith(".")) {
            for (Command command : Load.getInstance().getHooks().getCommandManagers()) {
                for (String name : command.getName()) {
                    if (!message.startsWith("." + name)) continue;
                    try {
                        command.run(message.split(" "));
                    }
                    catch (Exception ex) {
                        command.error();
                        ex.printStackTrace();
                    }
                    this.setMessage(true);
                    return;
                }
            }
            ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
            this.setMessage(true);
        } else {
            this.setMessage(false);
        }
    }

    public void registerRun(String message) {
        if (message.startsWith(".")) {
            if ((message = message.substring(1)).isEmpty()) {
                return;
            }
            try {
                this.commandDispatcher.execute(message, null);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }
    }

    @Generated
    public void setMessage(boolean message) {
        this.message = message;
    }

    @Generated
    public boolean isMessage() {
        return this.message;
    }

    @Generated
    public CommandDispatcher<ISuggestionProvider> getCommandDispatcher() {
        return this.commandDispatcher;
    }
}
