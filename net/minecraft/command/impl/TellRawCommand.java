package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentUtils;

public class TellRawCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires(p_198820_0_ -> p_198820_0_.hasPermissionLevel(2))).then(Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("message", ComponentArgument.component()).executes(p_198819_0_ -> {
            int i = 0;
            for (ServerPlayerEntity serverplayerentity : EntityArgument.getPlayers(p_198819_0_, "targets")) {
                serverplayerentity.sendMessage(TextComponentUtils.func_240645_a_((CommandSource)p_198819_0_.getSource(), ComponentArgument.getComponent(p_198819_0_, "message"), serverplayerentity, 0), Util.DUMMY_UUID);
                ++i;
            }
            return i;
        }))));
    }
}
