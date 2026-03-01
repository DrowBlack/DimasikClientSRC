package dimasik.managers.mods.voicechat.intercompatibility;

import com.mojang.brigadier.CommandDispatcher;
import dimasik.Load;
import dimasik.events.main.player.EventPlayerDisconnect;
import dimasik.events.main.player.EventPlayersJoin;
import dimasik.managers.mods.voicechat.api.VoicechatPlugin;
import dimasik.managers.mods.voicechat.events.ServerVoiceChatConnectedEvent;
import dimasik.managers.mods.voicechat.events.ServerVoiceChatDisconnectedEvent;
import dimasik.managers.mods.voicechat.events.VoiceChatCompatibilityCheckSucceededEvent;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.MCPNetManager;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.permission.MCPPermisions;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class MCPCommonCompatibilityManager
extends CommonCompatibilityManager {
    private final List<Consumer<MinecraftServer>> serverStartingEvents = new CopyOnWriteArrayList<Consumer<MinecraftServer>>();
    private final List<Consumer<MinecraftServer>> serverStoppingEvents = new CopyOnWriteArrayList<Consumer<MinecraftServer>>();
    private final List<Consumer<CommandDispatcher<CommandSource>>> registerServerCommandsEvents = new CopyOnWriteArrayList<Consumer<CommandDispatcher<CommandSource>>>();
    private final List<Consumer<ServerPlayerEntity>> playerLoggedInEvents = new CopyOnWriteArrayList<Consumer<ServerPlayerEntity>>();
    private final List<Consumer<ServerPlayerEntity>> playerLoggedOutEvents = new CopyOnWriteArrayList<Consumer<ServerPlayerEntity>>();
    private final List<Consumer<ServerPlayerEntity>> voicechatConnectEvents = new CopyOnWriteArrayList<Consumer<ServerPlayerEntity>>();
    private final List<Consumer<ServerPlayerEntity>> voicechatCompatibilityCheckSucceededEvents = new CopyOnWriteArrayList<Consumer<ServerPlayerEntity>>();
    private final List<Consumer<UUID>> voicechatDisconnectEvents = new CopyOnWriteArrayList<Consumer<UUID>>();
    private MCPNetManager netManager;

    public void event(EventPlayersJoin eventPlayersJoin) {
        this.playerLoggedInEvents.forEach(consumer -> consumer.accept(eventPlayersJoin.player));
    }

    public void lo1(EventPlayerDisconnect eventPlayerDisconnect) {
        this.playerLoggedInEvents.forEach(consumer -> consumer.accept(eventPlayerDisconnect.player));
    }

    @Override
    public String getModVersion() {
        return "N/A";
    }

    @Override
    public String getModName() {
        return "voicechat";
    }

    @Override
    public Path getGameDirectory() {
        return Minecraft.getInstance().gameDir.toPath();
    }

    @Override
    public void emitServerVoiceChatConnectedEvent(ServerPlayerEntity player) {
        this.voicechatConnectEvents.forEach(consumer -> consumer.accept(player));
        Load.getInstance().getEvents().call(new ServerVoiceChatConnectedEvent(player));
    }

    @Override
    public void emitServerVoiceChatDisconnectedEvent(UUID clientID) {
        this.voicechatDisconnectEvents.forEach(consumer -> consumer.accept(clientID));
        Load.getInstance().getEvents().call(new ServerVoiceChatDisconnectedEvent(clientID));
    }

    @Override
    public void emitPlayerCompatibilityCheckSucceeded(ServerPlayerEntity player) {
        this.voicechatCompatibilityCheckSucceededEvents.forEach(consumer -> consumer.accept(player));
        Load.getInstance().getEvents().call(new VoiceChatCompatibilityCheckSucceededEvent(player));
    }

    @Override
    public void onServerVoiceChatConnected(Consumer<ServerPlayerEntity> onVoiceChatConnected) {
        this.voicechatConnectEvents.add(onVoiceChatConnected);
    }

    @Override
    public void onServerVoiceChatDisconnected(Consumer<UUID> onVoiceChatDisconnected) {
        this.voicechatDisconnectEvents.add(onVoiceChatDisconnected);
    }

    @Override
    public void onServerStarting(Consumer<MinecraftServer> onServerStarting) {
        this.serverStartingEvents.add(onServerStarting);
    }

    @Override
    public void onServerStopping(Consumer<MinecraftServer> onServerStopping) {
        this.serverStoppingEvents.add(onServerStopping);
    }

    @Override
    public void onPlayerLoggedIn(Consumer<ServerPlayerEntity> onPlayerLoggedIn) {
        this.playerLoggedInEvents.add(onPlayerLoggedIn);
    }

    @Override
    public void onPlayerLoggedOut(Consumer<ServerPlayerEntity> onPlayerLoggedOut) {
        this.playerLoggedOutEvents.add(onPlayerLoggedOut);
    }

    @Override
    public void onPlayerCompatibilityCheckSucceeded(Consumer<ServerPlayerEntity> onPlayerCompatibilityCheckSucceeded) {
        this.voicechatCompatibilityCheckSucceededEvents.add(onPlayerCompatibilityCheckSucceeded);
    }

    @Override
    public void onRegisterServerCommands(Consumer<CommandDispatcher<CommandSource>> onRegisterServerCommands) {
        this.registerServerCommandsEvents.add(onRegisterServerCommands);
    }

    @Override
    public NetManager getNetManager() {
        if (this.netManager == null) {
            this.netManager = new MCPNetManager();
        }
        return this.netManager;
    }

    @Override
    public boolean isDevEnvironment() {
        return true;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return true;
    }

    @Override
    public List<VoicechatPlugin> loadPlugins() {
        return Collections.emptyList();
    }

    @Override
    public PermissionManager createPermissionManager() {
        return new MCPPermisions();
    }
}
