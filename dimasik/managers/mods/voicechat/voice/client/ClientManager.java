package dimasik.managers.mods.voicechat.voice.client;

import dimasik.Load;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.debug.DebugOverlay;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.RequestSecretPacket;
import dimasik.managers.mods.voicechat.net.SecretPacket;
import dimasik.managers.mods.voicechat.voice.client.ClientCategoryManager;
import dimasik.managers.mods.voicechat.voice.client.ClientGroupManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import dimasik.managers.mods.voicechat.voice.client.InitializationData;
import dimasik.managers.mods.voicechat.voice.client.KeyEvents;
import dimasik.managers.mods.voicechat.voice.client.PTTKeyHandler;
import dimasik.managers.mods.voicechat.voice.client.RenderEvents;
import dimasik.managers.mods.voicechat.voice.server.Server;
import dimasik.utils.client.ChatUtils;
import io.netty.channel.local.LocalAddress;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientManager {
    @Nullable
    private ClientVoicechat client;
    private final ClientPlayerStateManager playerStateManager = new ClientPlayerStateManager();
    private final ClientGroupManager groupManager = new ClientGroupManager();
    private final ClientCategoryManager categoryManager = new ClientCategoryManager();
    private final PTTKeyHandler pttKeyHandler = PTTKeyHandler.getInstance();
    private final RenderEvents renderEvents = new RenderEvents();
    private final DebugOverlay debugOverlay = new DebugOverlay();
    private static final KeyEvents keyEvents = new KeyEvents();
    private final Minecraft minecraft = Minecraft.getInstance();
    private static ClientManager instance;

    public ClientManager() {
        ClientCompatibilityManager.INSTANCE.onJoinWorld(this::onJoinWorld);
        ClientCompatibilityManager.INSTANCE.onDisconnect(this::onDisconnect);
        ClientCompatibilityManager.INSTANCE.onPublishServer(this::onPublishServer);
        ClientCompatibilityManager.INSTANCE.onVoiceChatConnected(connection -> {
            if (this.client != null) {
                this.client.onVoiceChatConnected((ClientVoicechatConnection)connection);
            }
        });
        ClientCompatibilityManager.INSTANCE.onVoiceChatDisconnected(() -> {
            if (this.client != null) {
                this.client.onVoiceChatDisconnected();
            }
        });
        ClientServerNetManager.setClientListener(CommonCompatibilityManager.INSTANCE.getNetManager().secretChannel, (client, handler, packet) -> this.authenticate((SecretPacket)packet));
    }

    private void authenticate(SecretPacket secretPacket) {
        if (this.client == null) {
            Voicechat.LOGGER.error("Received secret without a client being present", new Object[0]);
            return;
        }
        Voicechat.LOGGER.info("Received secret", new Object[0]);
        if (this.client.getConnection() != null) {
            ClientCompatibilityManager.INSTANCE.emitVoiceChatDisconnectedEvent();
        }
        ChatUtils.addClientMessage("VoiceChat connected.");
        ClientPlayNetHandler connection = this.minecraft.getConnection();
        if (connection != null) {
            try {
                SocketAddress socketAddress = ClientCompatibilityManager.INSTANCE.getSocketAddress(connection.getNetworkManager());
                this.client.connect(new InitializationData(ClientManager.resolveAddress(socketAddress), secretPacket));
            }
            catch (Exception e) {
                Voicechat.LOGGER.error("Failed to connect to voice chat server", e);
            }
        }
    }

    private static String resolveAddress(SocketAddress socketAddress) throws IOException {
        if (socketAddress instanceof LocalAddress) {
            return "127.0.0.1";
        }
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new IOException(String.format("Failed to determine server address with SocketAddress of type %s", socketAddress.getClass().getSimpleName()));
        }
        InetSocketAddress address = (InetSocketAddress)socketAddress;
        InetAddress inetAddress = address.getAddress();
        if (inetAddress == null) {
            return address.getHostString();
        }
        return inetAddress.getHostAddress();
    }

    private void onJoinWorld() {
        if (!Load.getInstance().getHooks().getModuleManagers().getVoiceChat().isToggled()) {
            return;
        }
        if (VoicechatClient.CLIENT_CONFIG.muteOnJoin.get().booleanValue()) {
            this.playerStateManager.setMuted(true);
        }
        if (this.client != null) {
            Voicechat.LOGGER.info("Disconnecting from previous connection due to server change", new Object[0]);
            this.onDisconnect();
        }
        Voicechat.LOGGER.info("Sending secret request to the server", new Object[0]);
        ClientServerNetManager.sendToServer(new RequestSecretPacket(18));
        this.client = new ClientVoicechat();
    }

    private void onDisconnect() {
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
        ClientCompatibilityManager.INSTANCE.emitVoiceChatDisconnectedEvent();
    }

    private void onPublishServer(int port) {
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return;
        }
        try {
            ClientVoicechatConnection connection;
            Voicechat.LOGGER.info("Changing voice chat port to {}", port);
            server.changePort(port);
            ClientVoicechat client = ClientManager.getClient();
            if (client != null && (connection = client.getConnection()) != null) {
                Voicechat.LOGGER.info("Force disconnecting due to port change", new Object[0]);
                connection.disconnect();
            }
            ClientServerNetManager.sendToServer(new RequestSecretPacket(Voicechat.COMPATIBILITY_VERSION));
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to change voice chat port", e);
        }
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("message.voicechat.server_port", server.getPort()));
    }

    @Nullable
    public static ClientVoicechat getClient() {
        return ClientManager.instance().client;
    }

    public static ClientPlayerStateManager getPlayerStateManager() {
        return ClientManager.instance().playerStateManager;
    }

    public static ClientGroupManager getGroupManager() {
        return ClientManager.instance().groupManager;
    }

    public static ClientCategoryManager getCategoryManager() {
        return ClientManager.instance().categoryManager;
    }

    public static PTTKeyHandler getPttKeyHandler() {
        return ClientManager.instance().pttKeyHandler;
    }

    public static RenderEvents getRenderEvents() {
        return ClientManager.instance().renderEvents;
    }

    public static DebugOverlay getDebugOverlay() {
        return ClientManager.instance().debugOverlay;
    }

    public static KeyEvents getKeyEvents() {
        return keyEvents;
    }

    public static synchronized ClientManager instance() {
        if (instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }
}
