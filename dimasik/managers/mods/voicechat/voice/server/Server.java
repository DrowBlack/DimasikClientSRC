package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import dimasik.managers.mods.voicechat.api.VoicechatSocket;
import dimasik.managers.mods.voicechat.debug.CooldownTimer;
import dimasik.managers.mods.voicechat.debug.VoicechatUncaughtExceptionHandler;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.permission.PermissionManager;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.voice.common.AuthenticateAckPacket;
import dimasik.managers.mods.voicechat.voice.common.AuthenticatePacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckAckPacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckPacket;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.KeepAlivePacket;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.MicPacket;
import dimasik.managers.mods.voicechat.voice.common.NetworkMessage;
import dimasik.managers.mods.voicechat.voice.common.Packet;
import dimasik.managers.mods.voicechat.voice.common.PingPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import dimasik.managers.mods.voicechat.voice.server.ClientConnection;
import dimasik.managers.mods.voicechat.voice.server.Group;
import dimasik.managers.mods.voicechat.voice.server.PingManager;
import dimasik.managers.mods.voicechat.voice.server.PlayerStateManager;
import dimasik.managers.mods.voicechat.voice.server.ServerCategoryManager;
import dimasik.managers.mods.voicechat.voice.server.ServerGroupManager;
import dimasik.managers.mods.voicechat.voice.server.ServerWorldUtils;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TranslationTextComponent;

public class Server
extends Thread {
    private final Map<UUID, ClientConnection> connections;
    private final Map<UUID, ClientConnection> unCheckedConnections;
    private final Map<UUID, UUID> secrets;
    private int port;
    private final MinecraftServer server;
    private VoicechatSocket socket;
    private final ProcessThread processThread;
    private final BlockingQueue<RawUdpPacket> packetQueue;
    private final PingManager pingManager;
    private final PlayerStateManager playerStateManager;
    private final ServerGroupManager groupManager;
    private final ServerCategoryManager categoryManager;

    public Server(MinecraftServer server) {
        if (server instanceof DedicatedServer) {
            int configPort = Voicechat.SERVER_CONFIG.voiceChatPort.get();
            if (configPort < 0) {
                Voicechat.LOGGER.info("Using the Minecraft servers port as voice chat port", new Object[0]);
                this.port = ((DedicatedServer)server).getPort();
            } else {
                this.port = configPort;
            }
        } else {
            this.port = 0;
        }
        this.server = server;
        this.socket = PluginManager.instance().getSocketImplementation(server);
        this.connections = new ConcurrentHashMap<UUID, ClientConnection>();
        this.unCheckedConnections = new ConcurrentHashMap<UUID, ClientConnection>();
        this.secrets = new ConcurrentHashMap<UUID, UUID>();
        this.packetQueue = new LinkedBlockingQueue<RawUdpPacket>();
        this.pingManager = new PingManager(this);
        this.playerStateManager = new PlayerStateManager(this);
        this.groupManager = new ServerGroupManager(this);
        this.categoryManager = new ServerCategoryManager(this);
        this.setDaemon(true);
        this.setName("VoiceChatServerThread");
        this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
        this.processThread = new ProcessThread();
        this.processThread.start();
    }

    public void onPlayerLoggedIn(ServerPlayerEntity player) {
        this.playerStateManager.onPlayerLoggedIn(player);
    }

    public void onPlayerLoggedOut(ServerPlayerEntity player) {
        this.disconnectClient(player.getUniqueID());
        this.playerStateManager.onPlayerLoggedOut(player);
        this.groupManager.onPlayerLoggedOut(player);
    }

    public void onPlayerVoicechatConnect(ServerPlayerEntity player) {
        this.playerStateManager.onPlayerVoicechatConnect(player);
    }

    public void onPlayerVoicechatDisconnect(UUID uuid) {
        this.playerStateManager.onPlayerVoicechatDisconnect(uuid);
    }

    public void onPlayerCompatibilityCheckSucceeded(ServerPlayerEntity player) {
        this.playerStateManager.onPlayerCompatibilityCheckSucceeded(player);
        this.groupManager.onPlayerCompatibilityCheckSucceeded(player);
        this.categoryManager.onPlayerCompatibilityCheckSucceeded(player);
    }

    @Override
    public void run() {
        try {
            String bindAddress = this.getBindAddress();
            this.socket.open(this.port, bindAddress);
            if (bindAddress.isEmpty()) {
                Voicechat.LOGGER.info("Voice chat server started at port {}", this.socket.getLocalPort());
            } else {
                Voicechat.LOGGER.info("Voice chat server started at {}:{}", bindAddress, this.socket.getLocalPort());
            }
            while (!this.socket.isClosed()) {
                try {
                    this.packetQueue.add(this.socket.read());
                }
                catch (Exception exception) {}
            }
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Voice chat server error", e);
        }
    }

    private String getBindAddress() {
        String bindAddress = Voicechat.SERVER_CONFIG.voiceChatBindAddress.get();
        if (bindAddress.trim().equals("*")) {
            bindAddress = "";
        } else if (bindAddress.trim().isEmpty() && this.server instanceof DedicatedServer && !(bindAddress = ((DedicatedServer)this.server).getServerProperties().serverIp).trim().isEmpty()) {
            try {
                InetAddress address = InetAddress.getByName(bindAddress);
                if (address.isLoopbackAddress()) {
                    bindAddress = "";
                } else {
                    Voicechat.LOGGER.info("Using server-ip as bind address: {}", bindAddress);
                }
            }
            catch (Exception e) {
                Voicechat.LOGGER.warn("Invalid server-ip", e);
                bindAddress = "";
            }
        }
        return bindAddress;
    }

    public void changePort(int port) throws Exception {
        VoicechatSocket newSocket = PluginManager.instance().getSocketImplementation(this.server);
        newSocket.open(port, this.getBindAddress());
        VoicechatSocket old = this.socket;
        this.socket = newSocket;
        this.port = port;
        old.close();
        this.connections.clear();
        this.unCheckedConnections.clear();
        this.secrets.clear();
    }

    public UUID getSecret(UUID playerUUID) {
        if (this.hasSecret(playerUUID)) {
            return this.secrets.get(playerUUID);
        }
        SecureRandom r = new SecureRandom();
        UUID secret = new UUID(r.nextLong(), r.nextLong());
        this.secrets.put(playerUUID, secret);
        return secret;
    }

    @Nullable
    public UUID generateNewSecret(UUID playerUUID) {
        if (this.hasSecret(playerUUID)) {
            return null;
        }
        return this.getSecret(playerUUID);
    }

    public boolean hasSecret(UUID playerUUID) {
        return this.secrets.containsKey(playerUUID);
    }

    public void disconnectClient(UUID playerUUID) {
        this.connections.remove(playerUUID);
        this.unCheckedConnections.remove(playerUUID);
        this.secrets.remove(playerUUID);
        PluginManager.instance().onPlayerDisconnected(playerUUID);
    }

    public void close() {
        this.socket.close();
        this.processThread.close();
        PluginManager.instance().onServerStopped();
    }

    public boolean isClosed() {
        return !this.processThread.running;
    }

    public void onMicPacket(UUID playerUuid, MicPacket packet) {
        ServerPlayerEntity player = this.server.getPlayerList().getPlayerByUUID(playerUuid);
        if (player == null) {
            return;
        }
        if (!PermissionManager.INSTANCE.SPEAK_PERMISSION.hasPermission(player)) {
            CooldownTimer.run("no-speak-" + String.valueOf(playerUuid), 30000L, () -> player.sendStatusMessage(new TranslationTextComponent("message.voicechat.no_speak_permission"), true));
            return;
        }
        PlayerState state = this.playerStateManager.getState(player.getUniqueID());
        if (state == null) {
            return;
        }
        if (!PluginManager.instance().onMicPacket(player, state, packet)) {
            this.processMicPacket(player, state, packet);
        }
    }

    private void processMicPacket(ServerPlayerEntity player, PlayerState state, MicPacket packet) {
        if (state.hasGroup()) {
            Group group = this.groupManager.getGroup(state.getGroup());
            this.processGroupPacket(state, player, packet);
            if (group == null || group.isOpen()) {
                this.processProximityPacket(state, player, packet);
            }
            return;
        }
        this.processProximityPacket(state, player, packet);
    }

    private void processGroupPacket(PlayerState senderState, ServerPlayerEntity sender, MicPacket packet) {
        UUID groupId = senderState.getGroup();
        if (groupId == null) {
            return;
        }
        GroupSoundPacket groupSoundPacket = new GroupSoundPacket(senderState.getUuid(), senderState.getUuid(), packet.getData(), packet.getSequenceNumber(), null);
        for (PlayerState state : this.playerStateManager.getStates()) {
            ServerPlayerEntity p;
            if (!groupId.equals(state.getGroup()) || senderState.getUuid().equals(state.getUuid()) || (p = this.server.getPlayerList().getPlayerByUUID(state.getUuid())) == null) continue;
            ClientConnection connection = this.getConnection(state.getUuid());
            this.sendSoundPacket(sender, senderState, p, state, connection, groupSoundPacket, "group");
        }
    }

    private void processProximityPacket(PlayerState senderState, ServerPlayerEntity sender, MicPacket packet) {
        UUID groupId = senderState.getGroup();
        float distance = Utils.getDefaultDistanceServer();
        SoundPacket soundPacket = null;
        String source = null;
        if (sender.isSpectator()) {
            ServerPlayerEntity spectatingPlayer;
            Entity camera;
            if (Voicechat.SERVER_CONFIG.spectatorPlayerPossession.get().booleanValue() && (camera = sender.getSpectatingEntity()) instanceof ServerPlayerEntity && (spectatingPlayer = (ServerPlayerEntity)camera) != sender) {
                PlayerState receiverState = this.playerStateManager.getState(spectatingPlayer.getUniqueID());
                if (receiverState == null) {
                    return;
                }
                GroupSoundPacket groupSoundPacket = new GroupSoundPacket(senderState.getUuid(), senderState.getUuid(), packet.getData(), packet.getSequenceNumber(), null);
                ClientConnection connection = this.getConnection(receiverState.getUuid());
                this.sendSoundPacket(sender, senderState, spectatingPlayer, receiverState, connection, groupSoundPacket, "spectator");
                return;
            }
            if (Voicechat.SERVER_CONFIG.spectatorInteraction.get().booleanValue()) {
                soundPacket = new LocationSoundPacket(sender.getUniqueID(), sender.getUniqueID(), sender.getEyePosition(1.0f), packet.getData(), packet.getSequenceNumber(), distance, null);
                source = "spectator";
            }
        }
        if (soundPacket == null) {
            float crouchMultiplayer = sender.isCrouching() ? Voicechat.SERVER_CONFIG.crouchDistanceMultiplier.get().floatValue() : 1.0f;
            float whisperMultiplayer = packet.isWhispering() ? Voicechat.SERVER_CONFIG.whisperDistanceMultiplier.get().floatValue() : 1.0f;
            float multiplier = crouchMultiplayer * whisperMultiplayer;
            soundPacket = new PlayerSoundPacket(sender.getUniqueID(), sender.getUniqueID(), packet.getData(), packet.getSequenceNumber(), packet.isWhispering(), distance *= multiplier, null);
            source = "proximity";
        }
        this.broadcast(ServerWorldUtils.getPlayersInRange(sender.getServerWorld(), sender.getPositionVec(), this.getBroadcastRange(distance), p -> !p.getUniqueID().equals(sender.getUniqueID())), soundPacket, sender, senderState, groupId, source);
    }

    public void sendSoundPacket(@Nullable ServerPlayerEntity sender, @Nullable PlayerState senderState, ServerPlayerEntity receiver, PlayerState receiverState, @Nullable ClientConnection connection, SoundPacket<?> soundPacket, String source) {
        PluginManager.instance().onListenerAudio(receiver.getUniqueID(), soundPacket);
        if (connection == null) {
            return;
        }
        if (receiverState.isDisabled() || receiverState.isDisconnected()) {
            return;
        }
        if (PluginManager.instance().onSoundPacket(sender, senderState, receiver, receiverState, soundPacket, source)) {
            return;
        }
        if (!PermissionManager.INSTANCE.LISTEN_PERMISSION.hasPermission(receiver)) {
            CooldownTimer.run(String.format("no-listen-%s", receiver.getUniqueID()), 30000L, () -> receiver.sendStatusMessage(new TranslationTextComponent("message.voicechat.no_listen_permission"), true));
            return;
        }
        this.sendPacket(soundPacket, connection);
    }

    public double getBroadcastRange(float minRange) {
        double broadcastRange = Voicechat.SERVER_CONFIG.broadcastRange.get();
        if (broadcastRange < 0.0) {
            broadcastRange = Voicechat.SERVER_CONFIG.voiceChatDistance.get() + 1.0;
        }
        return Math.max(broadcastRange, (double)minRange);
    }

    public void broadcast(Collection<ServerPlayerEntity> players, SoundPacket<?> packet, @Nullable ServerPlayerEntity sender, @Nullable PlayerState senderState, @Nullable UUID groupId, String source) {
        for (ServerPlayerEntity player : players) {
            PlayerState state = this.playerStateManager.getState(player.getUniqueID());
            if (state == null || state.hasGroup() && state.getGroup().equals(groupId)) continue;
            Group receiverGroup = null;
            if (state.hasGroup()) {
                receiverGroup = this.groupManager.getGroup(state.getGroup());
            }
            if (receiverGroup != null && receiverGroup.isIsolated()) continue;
            ClientConnection connection = this.getConnection(state.getUuid());
            this.sendSoundPacket(sender, senderState, player, state, connection, packet, source);
        }
    }

    private void sendKeepAlives() {
        long timestamp = System.currentTimeMillis();
        this.connections.values().removeIf(connection -> {
            if (timestamp - connection.getLastKeepAliveResponse() >= (long)Voicechat.SERVER_CONFIG.keepAlive.get().intValue() * 10L) {
                this.secrets.remove(connection.getPlayerUUID());
                Voicechat.LOGGER.info("Player {} timed out", connection.getPlayerUUID());
                ServerPlayerEntity player = this.server.getPlayerList().getPlayerByUUID(connection.getPlayerUUID());
                if (player != null) {
                    Voicechat.LOGGER.info("Reconnecting player {}", player.getName().getString());
                    Voicechat.SERVER.initializePlayerConnection(player);
                } else {
                    Voicechat.LOGGER.warn("Reconnecting player {} failed (Could not find player)", connection.getPlayerUUID());
                }
                CommonCompatibilityManager.INSTANCE.emitServerVoiceChatDisconnectedEvent(connection.getPlayerUUID());
                PluginManager.instance().onPlayerDisconnected(connection.getPlayerUUID());
                return true;
            }
            return false;
        });
        for (ClientConnection connection2 : this.connections.values()) {
            this.sendPacket(new KeepAlivePacket(), connection2);
        }
    }

    @Nullable
    public ClientConnection getSender(NetworkMessage message) {
        return this.connections.values().stream().filter(connection -> connection.getAddress().equals(message.getAddress())).findAny().orElse(null);
    }

    @Nullable
    public ClientConnection getUnconnectedSender(NetworkMessage message) {
        return this.unCheckedConnections.values().stream().filter(connection -> connection.getAddress().equals(message.getAddress())).findAny().orElse(null);
    }

    public Map<UUID, ClientConnection> getConnections() {
        return this.connections;
    }

    @Nullable
    public ClientConnection getConnection(UUID playerID) {
        return this.connections.get(playerID);
    }

    public VoicechatSocket getSocket() {
        return this.socket;
    }

    public int getPort() {
        return this.socket.getLocalPort();
    }

    public boolean sendPacket(Packet<?> packet, ClientConnection connection) {
        try {
            this.sendPacketRaw(packet, connection);
            return true;
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to send voice chat packet to {}", connection.getPlayerUUID());
            return false;
        }
    }

    public void sendPacketRaw(Packet<?> packet, ClientConnection connection) throws Exception {
        connection.send(this, new NetworkMessage(packet));
    }

    public PingManager getPingManager() {
        return this.pingManager;
    }

    public PlayerStateManager getPlayerStateManager() {
        return this.playerStateManager;
    }

    public ServerGroupManager getGroupManager() {
        return this.groupManager;
    }

    public ServerCategoryManager getCategoryManager() {
        return this.categoryManager;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    private class ProcessThread
    extends Thread {
        private boolean running = true;
        private long lastKeepAlive = 0L;

        public ProcessThread() {
            this.setDaemon(true);
            this.setName("VoiceChatPacketProcessingThread");
            this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
        }

        @Override
        public void run() {
            while (this.running) {
                try {
                    Packet<MicPacket> packet;
                    AuthenticatePacket packet2;
                    UUID secret;
                    NetworkMessage message;
                    RawUdpPacket rawPacket;
                    Server.this.pingManager.checkTimeouts();
                    long keepAliveTime = System.currentTimeMillis();
                    if (keepAliveTime - this.lastKeepAlive > (long)Voicechat.SERVER_CONFIG.keepAlive.get().intValue()) {
                        Server.this.sendKeepAlives();
                        this.lastKeepAlive = keepAliveTime;
                    }
                    if ((rawPacket = Server.this.packetQueue.poll(10L, TimeUnit.MILLISECONDS)) == null) continue;
                    try {
                        message = NetworkMessage.readPacketServer(rawPacket, Server.this);
                    }
                    catch (Exception e) {
                        CooldownTimer.run("failed_reading_packet", () -> Voicechat.LOGGER.warn("Failed to read packet from {}", rawPacket.getSocketAddress()));
                        continue;
                    }
                    if (message == null) continue;
                    if (System.currentTimeMillis() - message.getTimestamp() > message.getTTL()) {
                        CooldownTimer.run("ttl", () -> {
                            Voicechat.LOGGER.warn("Dropping voice chat packets! Your Server might be overloaded!", new Object[0]);
                            Voicechat.LOGGER.warn("Packet queue has {} packets", Server.this.packetQueue.size());
                        });
                        continue;
                    }
                    if (message.getPacket() instanceof AuthenticatePacket && (secret = Server.this.secrets.get((packet2 = (AuthenticatePacket)message.getPacket()).getPlayerUUID())) != null && secret.equals(packet2.getSecret())) {
                        ClientConnection connection = Server.this.unCheckedConnections.get(packet2.getPlayerUUID());
                        if (connection == null) {
                            connection = Server.this.connections.get(packet2.getPlayerUUID());
                        }
                        if (connection == null) {
                            connection = new ClientConnection(packet2.getPlayerUUID(), message.getAddress());
                            Server.this.unCheckedConnections.put(packet2.getPlayerUUID(), connection);
                            Voicechat.LOGGER.info("Successfully authenticated player {}", packet2.getPlayerUUID());
                        }
                        Server.this.sendPacket(new AuthenticateAckPacket(), connection);
                    }
                    if (message.getPacket() instanceof ConnectionCheckPacket) {
                        ClientConnection connection = Server.this.getUnconnectedSender(message);
                        if (connection == null) {
                            connection = Server.this.getSender(message);
                            if (connection == null) continue;
                            Server.this.sendPacket(new ConnectionCheckAckPacket(), connection);
                            continue;
                        }
                        connection.setLastKeepAliveResponse(System.currentTimeMillis());
                        Server.this.connections.put(connection.getPlayerUUID(), connection);
                        Server.this.unCheckedConnections.remove(connection.getPlayerUUID());
                        Voicechat.LOGGER.info("Successfully validated connection of player {}", connection.getPlayerUUID());
                        ServerPlayerEntity player = Server.this.server.getPlayerList().getPlayerByUUID(connection.getPlayerUUID());
                        if (player != null) {
                            CommonCompatibilityManager.INSTANCE.emitServerVoiceChatConnectedEvent(player);
                            PluginManager.instance().onPlayerConnected(player);
                            Voicechat.LOGGER.info("Player {} ({}) successfully connected to voice chat", player.getName().getString(), connection.getPlayerUUID());
                        }
                        Server.this.sendPacket(new ConnectionCheckAckPacket(), connection);
                        continue;
                    }
                    ClientConnection conn = Server.this.getSender(message);
                    if (conn == null) continue;
                    if (message.getPacket() instanceof MicPacket) {
                        packet = (MicPacket)message.getPacket();
                        Server.this.onMicPacket(conn.getPlayerUUID(), (MicPacket)packet);
                        continue;
                    }
                    if (message.getPacket() instanceof PingPacket) {
                        packet = (PingPacket)message.getPacket();
                        Server.this.pingManager.onPongPacket((PingPacket)packet);
                        continue;
                    }
                    if (!(message.getPacket() instanceof KeepAlivePacket)) continue;
                    conn.setLastKeepAliveResponse(System.currentTimeMillis());
                }
                catch (Exception e) {
                    Voicechat.LOGGER.error("Voice chat server error", e);
                }
            }
        }

        public void close() {
            this.running = false;
        }
    }
}
