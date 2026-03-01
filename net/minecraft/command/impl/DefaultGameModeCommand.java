package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class DefaultGameModeCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder literalargumentbuilder = (LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires(p_198342_0_ -> p_198342_0_.hasPermissionLevel(2));
        for (GameType gametype : GameType.values()) {
            if (gametype == GameType.NOT_SET) continue;
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes(p_198343_1_ -> DefaultGameModeCommand.setGameType((CommandSource)p_198343_1_.getSource(), gametype)));
        }
        dispatcher.register(literalargumentbuilder);
    }

    private static int setGameType(CommandSource commandSourceIn, GameType gamemode) {
        int i = 0;
        MinecraftServer minecraftserver = commandSourceIn.getServer();
        minecraftserver.setGameType(gamemode);
        if (minecraftserver.getForceGamemode()) {
            for (ServerPlayerEntity serverplayerentity : minecraftserver.getPlayerList().getPlayers()) {
                if (serverplayerentity.interactionManager.getGameType() == gamemode) continue;
                serverplayerentity.setGameType(gamemode);
                ++i;
            }
        }
        commandSourceIn.sendFeedback(new TranslationTextComponent("commands.defaultgamemode.success", gamemode.getDisplayName()), true);
        return i;
    }
}
