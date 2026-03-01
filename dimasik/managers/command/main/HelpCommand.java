package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("\u0412\u0441\u043f\u043e\u043c\u043e\u0433\u0430\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u0430", "help");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        for (Command cmd : Load.getInstance().getHooks().getCommandManagers()) {
            if (cmd instanceof HelpCommand) continue;
            String name = cmd.getName().get(0).toString().replace("[", "").replace("]", "");
            StringTextComponent message = new StringTextComponent(String.valueOf((Object)TextFormatting.GRAY) + "[" + String.valueOf((Object)TextFormatting.RED) + name + String.valueOf((Object)TextFormatting.GRAY) + "] " + String.valueOf((Object)TextFormatting.GRAY) + "\u00bb " + String.valueOf((Object)TextFormatting.RESET) + cmd.getDesk());
            ChatUtils.addClientMessage(message.getString());
        }
    }

    @Override
    public void error() {
    }
}
