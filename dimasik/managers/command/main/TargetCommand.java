package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.managers.target.TargetManagers;
import dimasik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;

public class TargetCommand
extends Command {
    public TargetCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0442\u0430\u0440\u0433\u0435\u0442\u043e\u043c \u0434\u043b\u044f \u043a\u0438\u043b\u043b\u0430\u0443\u0440\u044b", "target");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        int pasta = 0;
        if (args.length > 0 && args[0].startsWith(".target")) {
            pasta = 1;
        }
        int pasta2 = args.length - pasta;
        TargetManagers targetManagers = Load.getInstance().getHooks().getTargetManagers();
        if (pasta2 == 0) {
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u041d\u0435 \u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.WHITE) + "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0442\u0430\u0440\u0433\u0435\u0442: .target " + String.valueOf((Object)TextFormatting.GRAY) + "<name>");
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.WHITE) + "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0442\u0430\u0440\u0433\u0435\u0442: .target " + String.valueOf((Object)TextFormatting.GRAY) + "clear");
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.WHITE) + "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0442\u0435\u043a\u0443\u0449\u0435\u043c \u0442\u0430\u0440\u0433\u0435\u0442\u0435: .target " + String.valueOf((Object)TextFormatting.GRAY) + "info");
            return;
        }
        String arg = args[pasta];
        if (arg.equalsIgnoreCase("clear")) {
            targetManagers.clear();
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u0422\u0430\u0440\u0433\u0435\u0442\u044b \u043e\u0447\u0438\u0449\u0435\u043d\u044b.");
            return;
        }
        if (arg.equalsIgnoreCase("info")) {
            if (targetManagers.isActive()) {
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u0422\u0435\u043a\u0443\u0449\u0438\u0439 \u0442\u0430\u0440\u0433\u0435\u0442: " + String.valueOf((Object)TextFormatting.RED) + targetManagers.getName());
            } else {
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u0422\u0430\u0440\u0433\u0435\u0442 \u043d\u0435 \u043f\u0440\u043e\u043f\u0438\u0441\u0430\u043d, \u0423\u043a\u0430\u0436\u0438\u0442\u0435 -> .target <name>.");
            }
            return;
        }
        String name = arg;
        if (name.isEmpty()) {
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u041d\u0438\u043a \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0443\u0441\u0442\u044b\u043c!");
            return;
        }
        boolean nayden = false;
        Load.getInstance();
        for (PlayerEntity playerEntity : Load.mc.world.getPlayers()) {
            if (!playerEntity.getGameProfile().getName().equalsIgnoreCase(name)) continue;
            nayden = true;
            break;
        }
        if (!nayden) {
            ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u0418\u0433\u0440\u043e\u043a \u0441 \u043d\u0438\u043a\u043e\u043c '" + name + "' \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435! (\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043e\u0440\u0440\u0435\u043a. \u0438\u043c\u044f)");
            return;
        }
        targetManagers.set(name);
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u0422\u0430\u0440\u0433\u0435\u0442 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d \u0441 \u043d\u0438\u043a\u043e\u043c: " + String.valueOf((Object)TextFormatting.RED) + name);
    }

    @Override
    public void error() {
    }
}
