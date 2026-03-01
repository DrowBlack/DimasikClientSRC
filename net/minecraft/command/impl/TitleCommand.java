package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class TitleCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires(p_198847_0_ -> p_198847_0_.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)Commands.literal("clear").executes(p_198838_0_ -> TitleCommand.clear((CommandSource)p_198838_0_.getSource(), EntityArgument.getPlayers(p_198838_0_, "targets"))))).then(Commands.literal("reset").executes(p_198841_0_ -> TitleCommand.reset((CommandSource)p_198841_0_.getSource(), EntityArgument.getPlayers(p_198841_0_, "targets"))))).then(Commands.literal("title").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("title", ComponentArgument.component()).executes(p_198837_0_ -> TitleCommand.show((CommandSource)p_198837_0_.getSource(), EntityArgument.getPlayers(p_198837_0_, "targets"), ComponentArgument.getComponent(p_198837_0_, "title"), STitlePacket.Type.TITLE))))).then(Commands.literal("subtitle").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("title", ComponentArgument.component()).executes(p_198842_0_ -> TitleCommand.show((CommandSource)p_198842_0_.getSource(), EntityArgument.getPlayers(p_198842_0_, "targets"), ComponentArgument.getComponent(p_198842_0_, "title"), STitlePacket.Type.SUBTITLE))))).then(Commands.literal("actionbar").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("title", ComponentArgument.component()).executes(p_198836_0_ -> TitleCommand.show((CommandSource)p_198836_0_.getSource(), EntityArgument.getPlayers(p_198836_0_, "targets"), ComponentArgument.getComponent(p_198836_0_, "title"), STitlePacket.Type.ACTIONBAR))))).then(Commands.literal("times").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("stay", IntegerArgumentType.integer(0)).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes(p_198843_0_ -> TitleCommand.setTimes((CommandSource)p_198843_0_.getSource(), EntityArgument.getPlayers(p_198843_0_, "targets"), IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"), IntegerArgumentType.getInteger(p_198843_0_, "stay"), IntegerArgumentType.getInteger(p_198843_0_, "fadeOut")))))))));
    }

    private static int clear(CommandSource source, Collection<ServerPlayerEntity> targets) {
        STitlePacket stitlepacket = new STitlePacket(STitlePacket.Type.CLEAR, null);
        for (ServerPlayerEntity serverplayerentity : targets) {
            serverplayerentity.connection.sendPacket(stitlepacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.title.cleared.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.title.cleared.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int reset(CommandSource source, Collection<ServerPlayerEntity> targets) {
        STitlePacket stitlepacket = new STitlePacket(STitlePacket.Type.RESET, null);
        for (ServerPlayerEntity serverplayerentity : targets) {
            serverplayerentity.connection.sendPacket(stitlepacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.title.reset.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.title.reset.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int show(CommandSource source, Collection<ServerPlayerEntity> targets, ITextComponent message, STitlePacket.Type type) throws CommandSyntaxException {
        for (ServerPlayerEntity serverplayerentity : targets) {
            serverplayerentity.connection.sendPacket(new STitlePacket(type, TextComponentUtils.func_240645_a_(source, message, serverplayerentity, 0)));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int setTimes(CommandSource source, Collection<ServerPlayerEntity> target, int fadeIn, int stay, int fadeOut) {
        STitlePacket stitlepacket = new STitlePacket(fadeIn, stay, fadeOut);
        for (ServerPlayerEntity serverplayerentity : target) {
            serverplayerentity.connection.sendPacket(stitlepacket);
        }
        if (target.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.title.times.single", target.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.title.times.multiple", target.size()), true);
        }
        return target.size();
    }
}
