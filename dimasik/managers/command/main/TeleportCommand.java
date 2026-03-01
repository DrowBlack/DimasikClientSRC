package dimasik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;

public class TeleportCommand
extends Command {
    public TeleportCommand() {
        super("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "teleport", "tp");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!NumberUtils.isNumber(args[1])) {
            PlayerEntity entityPlayer = TeleportCommand.mc.world.getPlayers().stream().filter(player -> player.getName().getString().equalsIgnoreCase(args[1])).findFirst().orElse(null);
            if (entityPlayer == null) {
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.RED) + "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043d\u0430\u0439\u0442\u0438 \u0438\u0433\u0440\u043e\u043a\u0430 \u0441 \u0442\u0430\u043a\u0438\u043c \u043d\u0438\u043a\u043d\u0435\u0439\u043c\u043e\u043c!");
                return;
            }
            if (args[1].equals(entityPlayer.getName().getString())) {
                int i;
                double x = entityPlayer.getPosX();
                double y = entityPlayer.getPosY();
                double z = entityPlayer.getPosZ();
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                for (i = 0; i < 10; ++i) {
                    TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(TeleportCommand.mc.player.getPosX(), TeleportCommand.mc.player.getPosY(), TeleportCommand.mc.player.getPosZ(), false));
                }
                for (i = 0; i < 10; ++i) {
                    TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, false));
                    TeleportCommand.mc.player.setPosition(x, y, z);
                }
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                ChatUtils.addClientMessage("\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u0443\u044e \u043a \u0438\u0433\u0440\u043e\u043a\u0443 " + String.valueOf((Object)TextFormatting.GRAY) + entityPlayer.getName().getString());
            }
        }
        if (NumberUtils.isNumber(args[1])) {
            if (args.length >= 2) {
                double x = 0.0;
                double y = 0.0;
                double z = 0.0;
                if (args.length == 4) {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                    ChatUtils.addClientMessage("\u041f\u044b\u0442\u0430\u044e\u0441\u044c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 " + String.valueOf((Object)TextFormatting.LIGHT_PURPLE) + args[1] + " " + args[2] + " " + args[3]);
                } else if (args.length == 3) {
                    x = Double.parseDouble(args[1]);
                    y = 150.0;
                    z = Double.parseDouble(args[2]);
                    ChatUtils.addClientMessage("\u041f\u044b\u0442\u0430\u044e\u0441\u044c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 " + String.valueOf((Object)TextFormatting.LIGHT_PURPLE) + args[1] + " " + args[2]);
                } else if (args.length == 2) {
                    x = TeleportCommand.mc.player.getPosX();
                    y = TeleportCommand.mc.player.getPosY() + Double.parseDouble(args[1]);
                    z = TeleportCommand.mc.player.getPosZ();
                    ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GREEN) + "\u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043b\u0438\u0441\u044c \u043d\u0430 " + String.valueOf((Object)TextFormatting.WHITE) + args[1] + String.valueOf((Object)TextFormatting.GREEN) + " \u0431\u043b\u043e\u043a\u043e\u0432 \u0432\u0432\u0435\u0440\u0445");
                } else {
                    this.error();
                }
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                for (int i = 0; i <= 10; ++i) {
                    TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
                }
                TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(TeleportCommand.mc.player.getPosX(), 0.0, TeleportCommand.mc.player.getPosZ(), false));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            } else {
                this.error();
            }
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".tp <name>");
    }
}
