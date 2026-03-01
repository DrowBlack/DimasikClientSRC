package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.AddGroupPacket;
import dimasik.managers.mods.voicechat.net.JoinedGroupPacket;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.RemoveGroupPacket;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.PlayerStateManager;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerGroupManager {
    private final Map<UUID, Group> groups;
    private final Server server;

    public ServerGroupManager(Server server) {
        this.server = server;
        this.groups = new ConcurrentHashMap<UUID, Group>();
        CommonCompatibilityManager.INSTANCE.getNetManager().joinGroupChannel.setServerListener((srv, player, handler, packet) -> {
            if (!Voicechat.SERVER_CONFIG.groupsEnabled.get().booleanValue()) {
                return;
            }
            if (!PermissionManager.INSTANCE.GROUPS_PERMISSION.hasPermission(player)) {
                player.sendStatusMessage(new TranslationTextComponent("message.voicechat.no_group_permission"), true);
                return;
            }
            this.joinGroup(this.groups.get(packet.getGroup()), player, packet.getPassword());
        });
        CommonCompatibilityManager.INSTANCE.getNetManager().createGroupChannel.setServerListener((srv, player, handler, packet) -> {
            if (!Voicechat.SERVER_CONFIG.groupsEnabled.get().booleanValue()) {
                return;
            }
            if (!PermissionManager.INSTANCE.GROUPS_PERMISSION.hasPermission(player)) {
                player.sendStatusMessage(new TranslationTextComponent("message.voicechat.no_group_permission"), true);
                return;
            }
            if (!Voicechat.GROUP_REGEX.matcher(packet.getName()).matches()) {
                Voicechat.LOGGER.warn("Player {} tried to create a group with an invalid name: {}", player.getName().getString(), packet.getName());
                return;
            }
            this.addGroup(new Group(UUID.randomUUID(), packet.getName(), packet.getPassword(), false, false, packet.getType()), player);
        });
        CommonCompatibilityManager.INSTANCE.getNetManager().leaveGroupChannel.setServerListener((srv, player, handler, packet) -> this.leaveGroup(player));
    }

    public void onPlayerCompatibilityCheckSucceeded(ServerPlayerEntity player) {
        Voicechat.LOGGER.debug("Synchronizing {} groups with {}", this.groups.size(), player.getName().getString());
        for (Group category : this.groups.values()) {
            this.broadcastAddGroup(category);
        }
    }

    public void onPlayerLoggedOut(ServerPlayerEntity player) {
        this.cleanupGroups();
    }

    private PlayerStateManager getStates() {
        return this.server.getPlayerStateManager();
    }

    public void addGroup(Group group, @Nullable ServerPlayerEntity player) {
        if (PluginManager.instance().onCreateGroup(player, group)) {
            return;
        }
        this.groups.put(group.getId(), group);
        this.broadcastAddGroup(group);
        if (player == null) {
            return;
        }
        PlayerStateManager manager = this.getStates();
        manager.setGroup(player, group.getId());
        NetManager.sendToClient(player, new JoinedGroupPacket(group.getId(), false));
    }

    public void joinGroup(@Nullable Group group, ServerPlayerEntity player, @Nullable String password) {
        if (PluginManager.instance().onJoinGroup(player, group)) {
            return;
        }
        if (group == null) {
            NetManager.sendToClient(player, new JoinedGroupPacket(null, false));
            return;
        }
        if (group.getPassword() != null && !group.getPassword().equals(password)) {
            NetManager.sendToClient(player, new JoinedGroupPacket(null, true));
            return;
        }
        PlayerStateManager manager = this.getStates();
        manager.setGroup(player, group.getId());
        NetManager.sendToClient(player, new JoinedGroupPacket(group.getId(), false));
    }

    public void leaveGroup(ServerPlayerEntity player) {
        if (PluginManager.instance().onLeaveGroup(player)) {
            return;
        }
        PlayerStateManager manager = this.getStates();
        manager.setGroup(player, null);
        NetManager.sendToClient(player, new JoinedGroupPacket(null, false));
        this.cleanupGroups();
    }

    public void cleanupGroups() {
        PlayerStateManager manager = this.getStates();
        List usedGroups = manager.getStates().stream().filter(PlayerState::hasGroup).map(PlayerState::getGroup).distinct().collect(Collectors.toList());
        List groupsToRemove = this.groups.entrySet().stream().filter(entry -> !((Group)entry.getValue()).isPersistent()).map(Map.Entry::getKey).filter(uuid -> !usedGroups.contains(uuid)).collect(Collectors.toList());
        for (UUID uuid2 : groupsToRemove) {
            this.removeGroup(uuid2);
        }
    }

    public boolean removeGroup(UUID groupId) {
        Group group = this.groups.get(groupId);
        if (group == null) {
            return false;
        }
        PlayerStateManager manager = this.getStates();
        if (manager.getStates().stream().anyMatch(state -> state.hasGroup() && state.getGroup().equals(groupId))) {
            return false;
        }
        if (PluginManager.instance().onRemoveGroup(group)) {
            return false;
        }
        this.groups.remove(groupId);
        this.broadcastRemoveGroup(groupId);
        return true;
    }

    @Nullable
    public Group getGroup(UUID groupID) {
        return this.groups.get(groupID);
    }

    private void broadcastAddGroup(Group group) {
        AddGroupPacket packet = new AddGroupPacket(group.toClientGroup());
        this.server.getServer().getPlayerList().getPlayers().forEach(p -> NetManager.sendToClient(p, packet));
    }

    private void broadcastRemoveGroup(UUID group) {
        RemoveGroupPacket packet = new RemoveGroupPacket(group);
        this.server.getServer().getPlayerList().getPlayers().forEach(p -> NetManager.sendToClient(p, packet));
    }

    @Nullable
    public Group getPlayerGroup(ServerPlayerEntity player) {
        PlayerState state = this.server.getPlayerStateManager().getState(player.getUniqueID());
        if (state == null) {
            return null;
        }
        UUID groupId = state.getGroup();
        if (groupId == null) {
            return null;
        }
        return this.getGroup(groupId);
    }

    public Map<UUID, Group> getGroups() {
        return this.groups;
    }
}
