package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.managers.command.api.Command;
import net.minecraft.command.ISuggestionProvider;

public class ReconnectCommand
extends Command {
    public ReconnectCommand() {
        super("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0435\u0440\u0435\u0437\u0430\u0445\u043e\u0434\u0438\u0442 \u043d\u0430 \u0433\u0440\u0438\u0444 ReallyWorld", "reconnect", "rct");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
    }

    @Override
    public void error() {
    }
}
