package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.SecretPacket;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerVoiceEvents {
    private final Map<UUID, Integer> clientCompatibilities = new ConcurrentHashMap<UUID, Integer>();
    private Server server;

    public ServerVoiceEvents() {
        CommonCompatibilityManager.INSTANCE.onServerStarting(this::serverStarting);
        CommonCompatibilityManager.INSTANCE.onPlayerLoggedIn(this::playerLoggedIn);
        CommonCompatibilityManager.INSTANCE.onPlayerLoggedOut(this::playerLoggedOut);
        CommonCompatibilityManager.INSTANCE.onServerStopping(this::serverStopping);
        CommonCompatibilityManager.INSTANCE.onServerVoiceChatConnected(this::serverVoiceChatConnected);
        CommonCompatibilityManager.INSTANCE.onServerVoiceChatDisconnected(this::serverVoiceChatDisconnected);
        CommonCompatibilityManager.INSTANCE.onPlayerCompatibilityCheckSucceeded(this::playerCompatibilityCheckSucceeded);
        CommonCompatibilityManager.INSTANCE.getNetManager().requestSecretChannel.setServerListener((server, player, handler, packet) -> {
            Voicechat.LOGGER.info("Received secret request of {} ({})", player.getName().getString(), packet.getCompatibilityVersion());
            this.clientCompatibilities.put(player.getUniqueID(), packet.getCompatibilityVersion());
            if (packet.getCompatibilityVersion() != Voicechat.COMPATIBILITY_VERSION) {
                Voicechat.LOGGER.warn("Connected client {} has incompatible voice chat version (server={}, client={})", player.getName().getString(), Voicechat.COMPATIBILITY_VERSION, packet.getCompatibilityVersion());
                player.sendMessage(this.getIncompatibleMessage(packet.getCompatibilityVersion()), Util.DUMMY_UUID);
            } else {
                this.initializePlayerConnection(player);
            }
        });
    }

    public ITextComponent getIncompatibleMessage(int clientCompatibilityVersion) {
        if (clientCompatibilityVersion <= 6) {
            return new StringTextComponent(String.format(Voicechat.TRANSLATIONS.voicechatNotCompatibleMessage.get(), CommonCompatibilityManager.INSTANCE.getModVersion(), CommonCompatibilityManager.INSTANCE.getModName()));
        }
        return new TranslationTextComponent("message.voicechat.incompatible_version", new StringTextComponent(CommonCompatibilityManager.INSTANCE.getModVersion()).mergeStyle(TextFormatting.BOLD), new StringTextComponent(CommonCompatibilityManager.INSTANCE.getModName()).mergeStyle(TextFormatting.BOLD));
    }

    public boolean isCompatible(ServerPlayerEntity player) {
        return this.isCompatible(player.getUniqueID());
    }

    public boolean isCompatible(UUID playerUuid) {
        return this.clientCompatibilities.getOrDefault(playerUuid, -1) == Voicechat.COMPATIBILITY_VERSION;
    }

    public void serverStarting(MinecraftServer mcServer) {
        if (this.server != null) {
            this.server.close();
            this.server = null;
        }
        if (!CrossSideManager.get().shouldRunVoiceChatServer(mcServer)) {
            Voicechat.LOGGER.info("Disabling voice chat in singleplayer", new Object[0]);
            return;
        }
        try {
            this.server = new Server(mcServer);
            this.server.start();
            PluginManager.instance().onServerStarted();
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to start voice chat server", e);
        }
    }

    public void initializePlayerConnection(ServerPlayerEntity player) {
        if (this.server == null) {
            return;
        }
        CommonCompatibilityManager.INSTANCE.emitPlayerCompatibilityCheckSucceeded(player);
        UUID secret = this.server.generateNewSecret(player.getUniqueID());
        if (secret == null) {
            Voicechat.LOGGER.warn("Player already requested secret - ignoring", new Object[0]);
            return;
        }
        NetManager.sendToClient(player, new SecretPacket(player, secret, this.server.getPort(), Voicechat.SERVER_CONFIG));
        Voicechat.LOGGER.info("Sent secret to {}", player.getName().getString());
    }

    public void playerLoggedIn(final ServerPlayerEntity serverPlayer) {
        if (this.server != null) {
            this.server.onPlayerLoggedIn(serverPlayer);
        }
        if (!Voicechat.SERVER_CONFIG.forceVoiceChat.get().booleanValue()) {
            return;
        }
        final Timer timer = new Timer(serverPlayer.getGameProfile().getName() + "%s-login-timer", true);
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                timer.cancel();
                timer.purge();
                if (!serverPlayer.server.isServerRunning()) {
                    return;
                }
                if (!serverPlayer.connection.netManager.isChannelOpen()) {
                    return;
                }
                if (!ServerVoiceEvents.this.isCompatible(serverPlayer)) {
                    serverPlayer.server.execute(() -> serverPlayer2.connection.disconnect(new StringTextComponent(String.format(Voicechat.TRANSLATIONS.forceVoicechatKickMessage.get(), CommonCompatibilityManager.INSTANCE.getModName(), CommonCompatibilityManager.INSTANCE.getModVersion()))));
                }
            }
        }, Voicechat.SERVER_CONFIG.loginTimeout.get().intValue());
    }

    public void playerLoggedOut(ServerPlayerEntity player) {
        this.clientCompatibilities.remove(player.getUniqueID());
        if (this.server == null) {
            return;
        }
        this.server.onPlayerLoggedOut(player);
        Voicechat.LOGGER.info("Disconnecting client {}", player.getName().getString());
    }

    public void serverVoiceChatConnected(ServerPlayerEntity serverPlayer) {
        if (this.server == null) {
            return;
        }
        this.server.onPlayerVoicechatConnect(serverPlayer);
    }

    public void serverVoiceChatDisconnected(UUID uuid) {
        if (this.server == null) {
            return;
        }
        this.server.onPlayerVoicechatDisconnect(uuid);
    }

    public void playerCompatibilityCheckSucceeded(ServerPlayerEntity player) {
        if (this.server == null) {
            return;
        }
        this.server.onPlayerCompatibilityCheckSucceeded(player);
    }

    @Nullable
    public Server getServer() {
        return this.server;
    }

    public void serverStopping(MinecraftServer mcServer) {
        if (this.server != null) {
            this.server.close();
            this.server = null;
        }
    }
}
