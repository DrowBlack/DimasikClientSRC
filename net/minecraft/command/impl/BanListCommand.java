package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TranslationTextComponent;

public class BanListCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("banlist").requires(p_198233_0_ -> p_198233_0_.hasPermissionLevel(3))).executes(p_198231_0_ -> {
            PlayerList playerlist = ((CommandSource)p_198231_0_.getSource()).getServer().getPlayerList();
            return BanListCommand.sendBanList((CommandSource)p_198231_0_.getSource(), Lists.newArrayList(Iterables.concat(playerlist.getBannedPlayers().getEntries(), playerlist.getBannedIPs().getEntries())));
        })).then(Commands.literal("ips").executes(p_198228_0_ -> BanListCommand.sendBanList((CommandSource)p_198228_0_.getSource(), ((CommandSource)p_198228_0_.getSource()).getServer().getPlayerList().getBannedIPs().getEntries())))).then(Commands.literal("players").executes(p_198232_0_ -> BanListCommand.sendBanList((CommandSource)p_198232_0_.getSource(), ((CommandSource)p_198232_0_.getSource()).getServer().getPlayerList().getBannedPlayers().getEntries()))));
    }

    private static int sendBanList(CommandSource source, Collection<? extends BanEntry<?>> bannedPlayerList) {
        if (bannedPlayerList.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.banlist.none"), false);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.banlist.list", bannedPlayerList.size()), false);
            for (BanEntry<?> banentry : bannedPlayerList) {
                source.sendFeedback(new TranslationTextComponent("commands.banlist.entry", banentry.getDisplayName(), banentry.getBannedBy(), banentry.getBanReason()), false);
            }
        }
        return bannedPlayerList.size();
    }
}
