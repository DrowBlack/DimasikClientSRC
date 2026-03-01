package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.ClientVoicechatSocket;
import dimasik.managers.mods.voicechat.debug.VoicechatUncaughtExceptionHandler;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.ClientPluginManager;
import dimasik.managers.mods.voicechat.voice.client.ClientNetworkMessage;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.InitializationData;
import dimasik.managers.mods.voicechat.voice.common.AuthenticateAckPacket;
import dimasik.managers.mods.voicechat.voice.common.AuthenticatePacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckAckPacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckPacket;
import dimasik.managers.mods.voicechat.voice.common.KeepAlivePacket;
import dimasik.managers.mods.voicechat.voice.common.NetworkMessage;
import dimasik.managers.mods.voicechat.voice.common.Packet;
import dimasik.managers.mods.voicechat.voice.common.PingPacket;
import dimasik.managers.mods.voicechat.voice.common.SoundPacket;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientVoicechatConnection
extends Thread {
    private ClientVoicechat client;
    private final InitializationData data;
    private final ClientVoicechatSocket socket;
    private final InetAddress address;
    private boolean running;
    private boolean authenticated;
    private boolean connected;
    private final AuthThread authThread;
    private long lastKeepAlive;

    public ClientVoicechatConnection(ClientVoicechat client, InitializationData data) throws Exception {
        this.client = client;
        this.data = data;
        this.address = InetAddress.getByName(data.getServerIP());
        this.socket = ClientPluginManager.instance().getClientSocketImplementation();
        this.lastKeepAlive = -1L;
        this.running = true;
        this.authThread = new AuthThread();
        this.authThread.start();
        this.setDaemon(true);
        this.setName("VoiceChatConnectionThread");
        this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
        this.socket.open();
    }

    public InitializationData getData() {
        return this.data;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public ClientVoicechatSocket getSocket() {
        return this.socket;
    }

    public boolean isInitialized() {
        return this.authenticated && this.connected;
    }

    @Override
    public void run() {
        block8: {
            try {
                while (this.running) {
                    Packet packet;
                    NetworkMessage in = ClientNetworkMessage.readPacketClient(this.socket.read(), this);
                    if (in == null) continue;
                    if (in.getPacket() instanceof AuthenticateAckPacket) {
                        if (this.authenticated) continue;
                        Voicechat.LOGGER.info("Server acknowledged authentication", new Object[0]);
                        this.authenticated = true;
                        continue;
                    }
                    if (in.getPacket() instanceof ConnectionCheckAckPacket) {
                        if (!this.authenticated || this.connected) continue;
                        Voicechat.LOGGER.info("Server acknowledged connection check", new Object[0]);
                        this.connected = true;
                        ClientCompatibilityManager.INSTANCE.emitVoiceChatConnectedEvent(this);
                        this.lastKeepAlive = System.currentTimeMillis();
                        continue;
                    }
                    if (in.getPacket() instanceof SoundPacket) {
                        packet = (SoundPacket)in.getPacket();
                        this.client.processSoundPacket((SoundPacket)packet);
                        continue;
                    }
                    if (in.getPacket() instanceof PingPacket) {
                        packet = (PingPacket)in.getPacket();
                        Voicechat.LOGGER.info("Received ping {}, sending pong...", ((PingPacket)packet).getId());
                        this.sendToServer(new NetworkMessage(packet));
                        continue;
                    }
                    if (!(in.getPacket() instanceof KeepAlivePacket)) continue;
                    this.lastKeepAlive = System.currentTimeMillis();
                    this.sendToServer(new NetworkMessage(new KeepAlivePacket()));
                }
            }
            catch (InterruptedException in) {
            }
            catch (Exception e) {
                if (!this.running) break block8;
                Voicechat.LOGGER.error("Failed to process packet from server", e);
            }
        }
    }

    public void close() {
        Voicechat.LOGGER.info("Disconnecting voicechat", new Object[0]);
        this.running = false;
        this.socket.close();
        this.authThread.close();
    }

    public boolean isConnected() {
        return this.running && !this.socket.isClosed();
    }

    public boolean sendToServer(NetworkMessage message) {
        if (!this.isConnected()) {
            return false;
        }
        try {
            this.socket.send(ClientNetworkMessage.writeClient(this, message), new InetSocketAddress(this.address, this.data.getServerPort()));
            return true;
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to send voice chat packet - Disconnecting", e);
            this.disconnect();
            return false;
        }
    }

    public void checkTimeout() {
        if (this.lastKeepAlive >= 0L && System.currentTimeMillis() - this.lastKeepAlive > (long)this.data.getKeepAlive() * 10L) {
            Voicechat.LOGGER.info("Connection timeout", new Object[0]);
            this.disconnect();
        }
    }

    public void disconnect() {
        ClientCompatibilityManager.INSTANCE.emitVoiceChatDisconnectedEvent();
    }

    private class AuthThread
    extends Thread {
        private boolean running = true;
        private int authLogMessageCount;
        private int validateLogMessageCount;

        public AuthThread() {
            this.setDaemon(true);
            this.setName("VoiceChatAuthenticationThread");
            AuthThread.setDefaultUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
        }

        @Override
        public void run() {
            while (!(!this.running || ClientVoicechatConnection.this.authenticated && ClientVoicechatConnection.this.connected)) {
                if (!ClientVoicechatConnection.this.authenticated) {
                    this.validateLogMessageCount = 0;
                    if (this.authLogMessageCount < 10) {
                        Voicechat.LOGGER.info("Trying to authenticate voice chat connection", new Object[0]);
                        ++this.authLogMessageCount;
                    } else if (this.authLogMessageCount == 10) {
                        Voicechat.LOGGER.warn("Trying to authenticate voice chat connection (this message will not be logged again)", new Object[0]);
                        ++this.authLogMessageCount;
                    }
                    ClientVoicechatConnection.this.sendToServer(new NetworkMessage(new AuthenticatePacket(ClientVoicechatConnection.this.data.getPlayerUUID(), ClientVoicechatConnection.this.data.getSecret())));
                } else {
                    this.authLogMessageCount = 0;
                    if (this.validateLogMessageCount < 10) {
                        Voicechat.LOGGER.info("Trying to validate voice chat connection", new Object[0]);
                        ++this.validateLogMessageCount;
                    } else if (this.validateLogMessageCount == 10) {
                        Voicechat.LOGGER.warn("Trying to validate voice chat connection (this message will not be logged again)", new Object[0]);
                        ++this.validateLogMessageCount;
                    }
                    ClientVoicechatConnection.this.sendToServer(new NetworkMessage(new ConnectionCheckPacket()));
                }
                Utils.sleep(1000);
            }
        }

        public void close() {
            this.running = false;
        }
    }
}
