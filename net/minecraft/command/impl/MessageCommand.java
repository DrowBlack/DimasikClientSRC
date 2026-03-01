package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register((LiteralArgumentBuilder)Commands.literal("msg").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("message", MessageArgument.message()).executes(p_198539_0_ -> MessageCommand.sendPrivateMessage((CommandSource)p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"))))));
        dispatcher.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(literalcommandnode));
        dispatcher.register((LiteralArgumentBuilder)Commands.literal("w").redirect(literalcommandnode));
    }

    private static int sendPrivateMessage(CommandSource source, Collection<ServerPlayerEntity> recipients, ITextComponent message) {
        Consumer<ITextComponent> consumer;
        UUID uuid = source.getEntity() == null ? Util.DUMMY_UUID : source.getEntity().getUniqueID();
        Entity entity = source.getEntity();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            consumer = p_244374_2_ -> serverplayerentity.sendMessage(new TranslationTextComponent("commands.message.display.outgoing", p_244374_2_, message).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC), serverplayerentity.getUniqueID());
        } else {
            consumer = p_244375_2_ -> source.sendFeedback(new TranslationTextComponent("commands.message.display.outgoing", p_244375_2_, message).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC), false);
        }
        for (ServerPlayerEntity serverplayerentity1 : recipients) {
            consumer.accept(serverplayerentity1.getDisplayName());
            serverplayerentity1.sendMessage(new TranslationTextComponent("commands.message.display.incoming", source.getDisplayName(), message).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC), uuid);
        }
        return recipients.size();
    }
}
