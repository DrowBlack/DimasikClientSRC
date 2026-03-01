package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextFormatting;

public class AutoContractCommand
extends Command {
    public static String nickName = null;
    private final TimerUtils timerUtil = new TimerUtils();

    public AutoContractCommand() {
        super("\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u0447\u0442\u043e\u0431\u044b \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0446\u0435\u043b\u044c \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438 AutoContract", "contract", "cnt");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args[1].contains("info")) {
            ChatUtils.addClientMessage("\u0410\u043a\u0442\u0438\u0432\u043d\u0430\u044f \u0446\u0435\u043b\u044c: " + nickName);
        } else if (!args[1].isEmpty()) {
            nickName = args[1];
            ChatUtils.addClientMessage("\u0412\u0430\u0448\u0430 \u0446\u0435\u043b\u044c: " + nickName);
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438");
        ChatUtils.addClientMessage(".contract info");
        ChatUtils.addClientMessage(".contract " + String.valueOf((Object)TextFormatting.DARK_GRAY) + "<name>");
    }
}
