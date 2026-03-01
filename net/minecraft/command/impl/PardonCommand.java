package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.BanList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class PardonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardon.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon").requires(p_198551_0_ -> p_198551_0_.hasPermissionLevel(3))).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198549_0_, p_198549_1_) -> ISuggestionProvider.suggest(((CommandSource)p_198549_0_.getSource()).getServer().getPlayerList().getBannedPlayers().getKeys(), p_198549_1_)).executes(p_198550_0_ -> PardonCommand.unbanPlayers((CommandSource)p_198550_0_.getSource(), GameProfileArgument.getGameProfiles(p_198550_0_, "targets")))));
    }

    private static int unbanPlayers(CommandSource source, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
        BanList banlist = source.getServer().getPlayerList().getBannedPlayers();
        int i = 0;
        for (GameProfile gameprofile : gameProfiles) {
            if (!banlist.isBanned(gameprofile)) continue;
            banlist.removeEntry(gameprofile);
            ++i;
            source.sendFeedback(new TranslationTextComponent("commands.pardon.success", TextComponentUtils.getDisplayName(gameprofile)), true);
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        return i;
    }
}
