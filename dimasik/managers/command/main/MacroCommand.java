package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.managers.macro.MacroManagers;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.client.KeyMappings;
import dimasik.utils.client.KeyUtils;
import net.minecraft.command.ISuggestionProvider;

public class MacroCommand
extends Command {
    public MacroCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0430\u043a\u0440\u043e\u0441\u0430\u043c\u0438", "macro", "macros");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        MacroManagers macros = Load.getInstance().getHooks().getMacroManagers();
        if (args.length > 1) {
            switch (args[1]) {
                case "add": {
                    String buttonName = args[2].toUpperCase();
                    Integer keycode = null;
                    try {
                        keycode = KeyMappings.keyMap.get(buttonName);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (keycode != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i < args.length; ++i) {
                            sb.append(args[i]).append(" ");
                        }
                        sb.setLength(sb.length() - 1);
                        macros.add(sb.toString(), keycode);
                        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u0438\u043b \u043c\u0430\u043a\u0440\u043e\u0441 \u043d\u0430 \u043a\u043b\u0430\u0432\u0438\u0448\u0443: " + buttonName + "!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0422\u0430\u043a\u043e\u0439 \u043a\u043b\u0430\u0432\u0438\u0448\u0438 \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
                    break;
                }
                case "clear": {
                    macros.clears();
                    ChatUtils.addClientMessage("\u041e\u0447\u0438\u0441\u0442\u0438\u043b \u0441\u043f\u0438\u0441\u043e\u043a \u043c\u0430\u043a\u0440\u043e\u0441\u043e\u0432!");
                    break;
                }
                case "remove": 
                case "delete": {
                    String buttonName = args[2].toUpperCase();
                    Integer keycode = null;
                    try {
                        keycode = KeyMappings.keyMap.get(buttonName);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (keycode != null) {
                        macros.delete(keycode);
                        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0438\u043b \u043c\u0430\u043a\u0440\u043e\u0441 \u043d\u0430 \u043a\u043b\u0430\u0432\u0438\u0448\u0435: " + buttonName + "!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0422\u0430\u043a\u043e\u0439 \u043a\u043b\u0430\u0432\u0438\u0448\u0438 \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
                    break;
                }
                case "list": {
                    macros.forEach(macro -> ChatUtils.addClientMessage(macro.getMessage() + " " + KeyUtils.getKey(macro.getKey())));
                }
            }
        } else {
            this.error();
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".macro add <key> <command>");
        ChatUtils.addClientMessage(".macro remove <key>");
        ChatUtils.addClientMessage(".macro clear");
        ChatUtils.addClientMessage(".macro list");
    }
}
