package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class KickCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires(p_198517_0_ -> p_198517_0_.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(p_198513_0_ -> KickCommand.kickPlayers((CommandSource)p_198513_0_.getSource(), EntityArgument.getPlayers(p_198513_0_, "targets"), new TranslationTextComponent("multiplayer.disconnect.kicked")))).then(Commands.argument("reason", MessageArgument.message()).executes(p_198516_0_ -> KickCommand.kickPlayers((CommandSource)p_198516_0_.getSource(), EntityArgument.getPlayers(p_198516_0_, "targets"), MessageArgument.getMessage(p_198516_0_, "reason"))))));
    }

    private static int kickPlayers(CommandSource source, Collection<ServerPlayerEntity> players, ITextComponent reason) {
        for (ServerPlayerEntity serverplayerentity : players) {
            serverplayerentity.connection.disconnect(reason);
            source.sendFeedback(new TranslationTextComponent("commands.kick.success", serverplayerentity.getDisplayName(), reason), true);
        }
        return players.size();
    }
}
