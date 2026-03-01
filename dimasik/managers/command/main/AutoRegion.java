package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.modules.player.RGExploit;
import dimasik.utils.client.ChatUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextFormatting;

public class AutoRegion
extends Command {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AutoRegion() {
        super("\u041f\u0440\u0438\u0432\u0430\u0442\u0438\u0442 \u0440\u0435\u0433\u0438\u043e\u043d", "rg", "region");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    private RGExploit getRGExploit() {
        return Load.getInstance().getHooks().getModuleManagers().getRgExploit();
    }

    private String getPos1Command() {
        RGExploit rgExploit = this.getRGExploit();
        if (rgExploit != null && rgExploit.rgmod.getSelected("\u0420\u0412")) {
            return "//1";
        }
        return "//pos1";
    }

    private String getPos2Command() {
        RGExploit rgExploit = this.getRGExploit();
        if (rgExploit != null && rgExploit.rgmod.getSelected("\u0420\u0412")) {
            return "//2";
        }
        return "//pos2";
    }

    @Override
    public void run(String[] args) {
        if (args.length == 6) {
            try {
                String nickOrX = args[1];
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                int radius = Integer.parseInt(args[4]);
                String name = args[5];
                int x1 = x - radius;
                int y1 = y - radius;
                int y2 = y + radius;
                int z1 = z - radius;
                int x2 = x + radius;
                int z2 = z + radius;
                String pos1Cmd = this.getPos1Command();
                String pos2Cmd = this.getPos2Command();
                Minecraft mc = Minecraft.getInstance();
                this.scheduler.schedule(() -> mc.player.sendChatMessage(pos1Cmd + " " + x1 + "," + y1 + "," + z1), 0L, TimeUnit.SECONDS);
                this.scheduler.schedule(() -> mc.player.sendChatMessage(pos2Cmd + " " + x2 + "," + y2 + "," + z2), 1L, TimeUnit.SECONDS);
                this.scheduler.schedule(() -> mc.player.sendChatMessage("/rg claim " + name), 2L, TimeUnit.SECONDS);
            }
            catch (NumberFormatException e) {
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u041e\u0448\u0438\u0431\u043a\u0430: \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0438 \u0440\u0430\u0434\u0438\u0443\u0441 \u0434\u043e\u043b\u0436\u043d\u044b \u0431\u044b\u0442\u044c \u0446\u0435\u043b\u044b\u043c\u0438 \u0447\u0438\u0441\u043b\u0430\u043c\u0438.");
            }
        } else {
            this.error();
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438" + String.valueOf((Object)TextFormatting.WHITE) + ":");
        ChatUtils.addClientMessage(".rg <x> <y> <z> <radius> <name region>" + String.valueOf((Object)TextFormatting.GRAY));
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u0434\u043b\u044f \u043f\u0430\u0440\u0442\u0438" + String.valueOf((Object)TextFormatting.WHITE) + ":");
        ChatUtils.addClientMessage(".rg <name player> <radius> <name region>" + String.valueOf((Object)TextFormatting.GRAY));
    }
}
