package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.managers.module.Module;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.client.KeyMappings;
import dimasik.utils.client.KeyUtils;
import net.minecraft.command.ISuggestionProvider;

public class BindCommand
extends Command {
    public BindCommand() {
        super("\u0411\u0438\u043d\u0434 \u0441\u0438\u0441\u0442\u0435\u043c\u0430", "bind");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length >= 2) {
            switch (args[1]) {
                case "clear": {
                    for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                        if (!module.hasBind() || module == Load.getInstance().getHooks().getModuleManagers().getClickGui()) continue;
                        module.setCurrentKey(-1);
                    }
                    break;
                }
                case "list": {
                    for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                        if (module.getCurrentKey() == -1) continue;
                        ChatUtils.addMessage(module.getName() + " " + KeyUtils.getKey(module.getCurrentKey()));
                    }
                    break;
                }
                case "add": {
                    if (args.length < 4) break;
                    int key = KeyMappings.keyMap.get(args[3].toUpperCase());
                    Object module = Load.getInstance().getHooks().getModuleManagers().findName(args[2]);
                    if (module == null || key == -1) break;
                    ((Module)module).setCurrentKey(key);
                    ChatUtils.addClientMessage("successful");
                    break;
                }
                case "remove": {
                    Object module;
                    if (args.length < 3 || (module = Load.getInstance().getHooks().getModuleManagers().findName(args[2])) == null) break;
                    ((Module)module).setCurrentKey(-1);
                }
            }
        }
    }

    @Override
    public void error() {
    }
}
