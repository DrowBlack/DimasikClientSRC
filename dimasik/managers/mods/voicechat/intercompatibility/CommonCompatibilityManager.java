package dimasik.managers.mods.voicechat.intercompatibility;

import com.mojang.brigadier.CommandDispatcher;
import dimasik.managers.mods.voicechat.api.VoicechatPlugin;
import dimasik.managers.mods.voicechat.api.VoicechatServerApi;
import dimasik.managers.mods.voicechat.intercompatibility.MCPCommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import dimasik.managers.mods.voicechat.plugins.impl.VoicechatServerApiImpl;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

public abstract class CommonCompatibilityManager {
    public static CommonCompatibilityManager INSTANCE = new MCPCommonCompatibilityManager();

    public abstract String getModVersion();

    public abstract String getModName();

    public abstract Path getGameDirectory();

    public abstract void emitServerVoiceChatConnectedEvent(ServerPlayerEntity var1);

    public abstract void emitServerVoiceChatDisconnectedEvent(UUID var1);

    public abstract void emitPlayerCompatibilityCheckSucceeded(ServerPlayerEntity var1);

    public abstract void onServerVoiceChatConnected(Consumer<ServerPlayerEntity> var1);

    public abstract void onServerVoiceChatDisconnected(Consumer<UUID> var1);

    public abstract void onServerStarting(Consumer<MinecraftServer> var1);

    public abstract void onServerStopping(Consumer<MinecraftServer> var1);

    public abstract void onPlayerLoggedIn(Consumer<ServerPlayerEntity> var1);

    public abstract void onPlayerLoggedOut(Consumer<ServerPlayerEntity> var1);

    public abstract void onPlayerCompatibilityCheckSucceeded(Consumer<ServerPlayerEntity> var1);

    public abstract void onRegisterServerCommands(Consumer<CommandDispatcher<CommandSource>> var1);

    public abstract NetManager getNetManager();

    public abstract boolean isDevEnvironment();

    public abstract boolean isDedicatedServer();

    public abstract boolean isModLoaded(String var1);

    public abstract List<VoicechatPlugin> loadPlugins();

    public abstract PermissionManager createPermissionManager();

    public VoicechatServerApi getServerApi() {
        return VoicechatServerApiImpl.INSTANCE;
    }

    public Object createRawApiEntity(Entity entity) {
        return entity;
    }

    public Object createRawApiPlayer(PlayerEntity player) {
        return player;
    }

    public Object createRawApiLevel(ServerWorld level) {
        return level;
    }
}
