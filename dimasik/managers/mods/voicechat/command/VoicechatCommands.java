package dimasik.managers.mods.voicechat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.command.GroupNameSuggestionProvider;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.permission.Permission;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.server.ClientConnection;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.PingManager;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class VoicechatCommands {
    public static final String VOICECHAT_COMMAND = "voicechat";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> literalBuilder = Commands.literal(VOICECHAT_COMMAND);
        literalBuilder.executes(commandSource -> VoicechatCommands.help(dispatcher, commandSource));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("help").executes(commandSource -> VoicechatCommands.help(dispatcher, commandSource)));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)((LiteralArgumentBuilder)Commands.literal("test").requires(commandSource -> VoicechatCommands.checkPermission(commandSource, PermissionManager.INSTANCE.ADMIN_PERMISSION))).then(Commands.argument("target", EntityArgument.player()).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Server server = Voicechat.SERVER.getServer();
            if (server == null) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.voice_chat_unavailable"), false);
                return 1;
            }
            if (!Voicechat.SERVER.isCompatible(player)) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.player_no_voicechat", player.getDisplayName(), CommonCompatibilityManager.INSTANCE.getModName()), false);
                return 1;
            }
            ClientConnection clientConnection = server.getConnections().get(player.getUniqueID());
            if (clientConnection == null) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.client_not_connected"), false);
                return 1;
            }
            try {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.sending_ping"), false);
                server.getPingManager().sendPing(clientConnection, 500L, 10, new PingManager.PingListener(){

                    @Override
                    public void onPong(int attempts, long pingMilliseconds) {
                        if (attempts <= 1) {
                            ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.ping_received", pingMilliseconds), false);
                        } else {
                            ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.ping_received_attempt", pingMilliseconds, attempts), false);
                        }
                    }

                    @Override
                    public void onFailedAttempt(int attempts) {
                        ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.ping_retry"), false);
                    }

                    @Override
                    public void onTimeout(int attempts) {
                        ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.ping_timed_out", attempts), false);
                    }
                });
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.ping_sent_waiting"), false);
            }
            catch (Exception e) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.failed_to_send_ping", e.getMessage()), false);
                Voicechat.LOGGER.warn("Failed to send ping", e);
                return 1;
            }
            return 1;
        })));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("invite").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("target", EntityArgument.player()).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            ServerPlayerEntity source = ((CommandSource)commandSource.getSource()).getServer().getCommandSource().asPlayer();
            Server server = Voicechat.SERVER.getServer();
            if (server == null) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.voice_chat_unavailable"), false);
                return 1;
            }
            PlayerState state = server.getPlayerStateManager().getState(source.getUniqueID());
            if (state == null || !state.hasGroup()) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.not_in_group"), false);
                return 1;
            }
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            Group group = server.getGroupManager().getGroup(state.getGroup());
            if (group == null) {
                return 1;
            }
            if (!Voicechat.SERVER.isCompatible(player)) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.player_no_voicechat", player.getDisplayName(), CommonCompatibilityManager.INSTANCE.getModName()), false);
                return 1;
            }
            String passwordSuffix = group.getPassword() == null ? "" : " \"" + group.getPassword() + "\"";
            ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.invite_successful", player.getDisplayName()), false);
            return 1;
        })));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("join").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("group_id", UUIDArgument.func_239194_a_()).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            UUID groupID = UUIDArgument.func_239195_a_(commandSource, "group_id");
            return VoicechatCommands.joinGroupById((CommandSource)commandSource.getSource(), groupID, null);
        })));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("join").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("group_id", UUIDArgument.func_239194_a_()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("password", StringArgumentType.string()).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            UUID groupID = UUIDArgument.func_239195_a_(commandSource, "group_id");
            String password = StringArgumentType.getString(commandSource, "password");
            return VoicechatCommands.joinGroupById((CommandSource)commandSource.getSource(), groupID, password.isEmpty() ? null : password);
        }))));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("join").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("group_name", StringArgumentType.string()).suggests(GroupNameSuggestionProvider.INSTANCE).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            String groupName = StringArgumentType.getString(commandSource, "group_name");
            return VoicechatCommands.joinGroupByName((CommandSource)commandSource.getSource(), groupName, null);
        })));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("join").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("group_name", StringArgumentType.string()).suggests(GroupNameSuggestionProvider.INSTANCE).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("password", StringArgumentType.string()).executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            String groupName = StringArgumentType.getString(commandSource, "group_name");
            String password = StringArgumentType.getString(commandSource, "password");
            return VoicechatCommands.joinGroupByName((CommandSource)commandSource.getSource(), groupName, password.isEmpty() ? null : password);
        }))));
        literalBuilder.then((ArgumentBuilder<CommandSource, ?>)Commands.literal("leave").executes(commandSource -> {
            if (VoicechatCommands.checkNoVoicechat(commandSource)) {
                return 0;
            }
            if (!Voicechat.SERVER_CONFIG.groupsEnabled.get().booleanValue()) {
                ((CommandSource)commandSource.getSource()).sendErrorMessage(new TranslationTextComponent("message.voicechat.groups_disabled"));
                return 1;
            }
            Server server = Voicechat.SERVER.getServer();
            if (server == null) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.voice_chat_unavailable"), false);
                return 1;
            }
            ServerPlayerEntity source = ((CommandSource)commandSource.getSource()).getServer().getCommandSource().asPlayer();
            PlayerState state = server.getPlayerStateManager().getState(source.getUniqueID());
            if (state == null || !state.hasGroup()) {
                ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.not_in_group"), false);
                return 1;
            }
            server.getGroupManager().leaveGroup(source);
            ((CommandSource)commandSource.getSource()).sendFeedback(new TranslationTextComponent("message.voicechat.leave_successful"), false);
            return 1;
        }));
        dispatcher.register(literalBuilder);
    }

    private static Server joinGroup(CommandSource source) throws CommandSyntaxException {
        if (!Voicechat.SERVER_CONFIG.groupsEnabled.get().booleanValue()) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.groups_disabled"));
            return null;
        }
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.voice_chat_unavailable"));
            return null;
        }
        ServerPlayerEntity player = source.getServer().getCommandSource().asPlayer();
        if (!PermissionManager.INSTANCE.GROUPS_PERMISSION.hasPermission(player)) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.no_group_permission"));
            return null;
        }
        return server;
    }

    private static int joinGroupByName(CommandSource source, String groupName, @Nullable String password) throws CommandSyntaxException {
        Server server = VoicechatCommands.joinGroup(source);
        if (server == null) {
            return 1;
        }
        List groups = server.getGroupManager().getGroups().values().stream().filter(group -> group.getName().equals(groupName)).collect(Collectors.toList());
        if (groups.isEmpty()) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.group_does_not_exist"));
            return 1;
        }
        if (groups.size() > 1) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.group_name_not_unique"));
            return 1;
        }
        return VoicechatCommands.joinGroup(source, server, ((Group)groups.get(0)).getId(), password);
    }

    private static int joinGroupById(CommandSource source, UUID groupID, @Nullable String password) throws CommandSyntaxException {
        Server server = VoicechatCommands.joinGroup(source);
        if (server == null) {
            return 1;
        }
        return VoicechatCommands.joinGroup(source, server, groupID, password);
    }

    private static int joinGroup(CommandSource source, Server server, UUID groupID, @Nullable String password) throws CommandSyntaxException {
        Group group = server.getGroupManager().getGroup(groupID);
        if (group == null) {
            source.sendErrorMessage(new TranslationTextComponent("message.voicechat.group_does_not_exist"));
            return 1;
        }
        server.getGroupManager().joinGroup(group, source.getServer().getCommandSource().asPlayer(), password);
        source.sendFeedback(new TranslationTextComponent("message.voicechat.join_successful", new StringTextComponent(group.getName()).mergeStyle(TextFormatting.GRAY)), false);
        return 1;
    }

    private static int help(CommandDispatcher<CommandSource> dispatcher, CommandContext<CommandSource> commandSource) {
        if (VoicechatCommands.checkNoVoicechat(commandSource)) {
            return 0;
        }
        CommandNode voicechatCommand = dispatcher.getRoot().getChild(VOICECHAT_COMMAND);
        Map<CommandNode<CommandSource>, String> map = dispatcher.getSmartUsage(voicechatCommand, commandSource.getSource());
        for (Map.Entry<CommandNode<CommandSource>, String> entry : map.entrySet()) {
            commandSource.getSource().sendFeedback(new StringTextComponent("/voicechat " + entry.getValue()), false);
        }
        return map.size();
    }

    private static boolean checkNoVoicechat(CommandContext<CommandSource> commandSource) {
        try {
            ServerPlayerEntity player = commandSource.getSource().getServer().getCommandSource().asPlayer();
            if (Voicechat.SERVER.isCompatible(player)) {
                return false;
            }
            commandSource.getSource().sendErrorMessage(new StringTextComponent(String.format(Voicechat.TRANSLATIONS.voicechatNeededForCommandMessage.get(), CommonCompatibilityManager.INSTANCE.getModName())));
            return true;
        }
        catch (Exception e) {
            commandSource.getSource().sendErrorMessage(new StringTextComponent(Voicechat.TRANSLATIONS.playerCommandMessage.get()));
            return true;
        }
    }

    private static boolean checkPermission(CommandSource stack, Permission permission) {
        try {
            return permission.hasPermission(stack.getServer().getCommandSource().asPlayer());
        }
        catch (CommandSyntaxException e) {
            return false;
        }
    }
}
