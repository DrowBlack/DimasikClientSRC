package dimasik.managers.mods.voicechat.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class GroupNameSuggestionProvider
implements SuggestionProvider<CommandSource> {
    public static final GroupNameSuggestionProvider INSTANCE = new GroupNameSuggestionProvider();

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return builder.buildFuture();
        }
        server.getGroupManager().getGroups().values().stream().map(Group::getName).distinct().map(s -> {
            if (s.contains(" ")) {
                return String.format("\"%s\"", s);
            }
            return s;
        }).forEach(builder::suggest);
        return builder.buildFuture();
    }
}
