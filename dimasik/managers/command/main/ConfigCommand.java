package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.managers.config.main.ModuleConfig;
import dimasik.managers.module.Module;
import dimasik.utils.client.ChatUtils;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;

public class ConfigCommand
extends Command {
    public ConfigCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430\u043c\u0438", "cfg", "config");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            switch (args[1]) {
                case "dir": {
                    try {
                        Runtime.getRuntime().exec("explorer " + new File(Minecraft.getInstance().gameDir, "dimasik/configs/custom").getAbsolutePath());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    ChatUtils.addClientMessage("\u041e\u0442\u043a\u0440\u044b\u043b \u043f\u0430\u043f\u043a\u0443 \u0441 \u043a\u043e\u043d\u0444\u0438\u0433\u0430\u043c\u0438.");
                    break;
                }
                case "list": {
                    ChatUtils.addClientMessage(Load.getInstance().getHooks().getConfigManagers().getConfigs().toString().replace("[", "").replace("]", ""));
                    break;
                }
                case "load": {
                    if (args.length > 2) {
                        if (Load.getInstance().getHooks().getConfigManagers().getConfigs().contains(args[2])) {
                            Load.getInstance().getHooks().getConfigManagers().load(args[2], "dimasik/configs/custom");
                            ChatUtils.addClientMessage("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u043b \u043a\u043e\u043d\u0444\u0438\u0433 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c: " + args[2] + ".");
                        } else {
                            ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
                        }
                        return;
                    }
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).setName("module");
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).setPath(new File(ConfigCommand.mc.gameDir, "dimasik/configs/custom"));
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).fastLoad();
                    ChatUtils.addClientMessage("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u043b \u043a\u043e\u043d\u0444\u0438\u0433.");
                    break;
                }
                case "save": {
                    if (args.length > 2) {
                        Load.getInstance().getHooks().getConfigManagers().save(args[2], "dimasik/configs/custom");
                        ChatUtils.addClientMessage("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u043b \u043a\u043e\u043d\u0444\u0438\u0433 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c: " + args[2] + ".");
                        return;
                    }
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).setName("module");
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).setPath(new File(ConfigCommand.mc.gameDir, "dimasik/configs/custom"));
                    ((ModuleConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ModuleConfig.class)).fastSave();
                    ChatUtils.addClientMessage("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u043b \u043a\u043e\u043d\u0444\u0438\u0433.");
                    break;
                }
                case "toggle": {
                    for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                        if (!module.isToggled()) continue;
                        module.toggle();
                    }
                    break;
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
        ChatUtils.addClientMessage(".cfg save <name>");
        ChatUtils.addClientMessage(".cfg load <name>");
        ChatUtils.addClientMessage(".cfg dir");
        ChatUtils.addClientMessage(".cfg list");
    }
}
