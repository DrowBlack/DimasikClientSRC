package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class ListCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes(p_198523_0_ -> ListCommand.listNames((CommandSource)p_198523_0_.getSource()))).then(Commands.literal("uuids").executes(p_208202_0_ -> ListCommand.listUUIDs((CommandSource)p_208202_0_.getSource()))));
    }

    private static int listNames(CommandSource source) {
        return ListCommand.listPlayers(source, PlayerEntity::getDisplayName);
    }

    private static int listUUIDs(CommandSource source) {
        return ListCommand.listPlayers(source, p_244373_0_ -> new TranslationTextComponent("commands.list.nameAndId", p_244373_0_.getName(), p_244373_0_.getGameProfile().getId()));
    }

    private static int listPlayers(CommandSource source, Function<ServerPlayerEntity, ITextComponent> nameExtractor) {
        PlayerList playerlist = source.getServer().getPlayerList();
        List<ServerPlayerEntity> list = playerlist.getPlayers();
        IFormattableTextComponent itextcomponent = TextComponentUtils.func_240649_b_(list, nameExtractor);
        source.sendFeedback(new TranslationTextComponent("commands.list.players", list.size(), playerlist.getMaxPlayers(), itextcomponent), false);
        return list.size();
    }
}
