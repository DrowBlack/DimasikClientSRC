package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.managers.command.api.Command;
import dimasik.managers.staff.StaffManagers;
import dimasik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;

public class StaffCommand
extends Command {
    public StaffCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0430\u0444\u0444\u043e\u043c", "staff", "personal");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        StaffManagers staff = Load.getInstance().getHooks().getStaffManagers();
        if (args.length > 1) {
            switch (args[1]) {
                case "add": {
                    if (!staff.is(args[2])) {
                        staff.add(args[2]);
                        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u0438\u043b \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440\u0430 \u0441 \u043d\u0438\u043a\u043e\u043c: " + args[2] + "!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440 \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
                    break;
                }
                case "remove": {
                    if (staff.is(args[2])) {
                        staff.remove(args[2]);
                        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0438\u043b \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440\u0430 \u0441 \u043d\u0438\u043a\u043e\u043c: " + args[2] + "!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u043e\u0433\u043e \u043c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440\u0430 \u043d\u0435\u0442 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435!");
                    break;
                }
                case "list": {
                    ChatUtils.addClientMessage(staff.get().toString().replace("[", "").replace("]", ""));
                    break;
                }
                case "clear": {
                    if (!staff.isEmpty()) {
                        staff.clears();
                        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0447\u0438\u0441\u0442\u0438\u043b \u0441\u043f\u0438\u0441\u043e\u043a \u043c\u043e\u0434\u0435\u0440\u0430\u0446\u0438\u0438!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0421\u043f\u0438\u0441\u043e\u043a \u043c\u043e\u0434\u0435\u0440\u0430\u0446\u0438\u0438 \u043f\u0443\u0441\u0442!");
                }
            }
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".staff add <name>");
        ChatUtils.addClientMessage(".staff remove <name>");
        ChatUtils.addClientMessage(".staff clear");
        ChatUtils.addClientMessage(".staff list");
    }
}
