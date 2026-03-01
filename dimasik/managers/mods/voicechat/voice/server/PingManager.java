package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.voice.common.PingPacket;
import dimasik.managers.mods.voicechat.voice.server.ClientConnection;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PingManager {
    private final Map<UUID, Ping> listeners;
    private final Server server;

    public PingManager(Server server) {
        this.server = server;
        this.listeners = new HashMap<UUID, Ping>();
    }

    public void onPongPacket(PingPacket packet) {
        Voicechat.LOGGER.info("Received pong {}", packet.getId());
        Ping ping = this.listeners.remove(packet.getId());
        if (ping == null) {
            return;
        }
        ping.listener.onPong(ping.attempt, System.currentTimeMillis() - packet.getTimestamp());
    }

    public void checkTimeouts() {
        if (this.listeners.isEmpty()) {
            return;
        }
        List timedOut = this.listeners.entrySet().stream().filter(uuidPingEntry -> ((Ping)uuidPingEntry.getValue()).isTimedOut()).collect(Collectors.toList());
        for (Map.Entry pingEntry : timedOut) {
            Ping ping = (Ping)pingEntry.getValue();
            if (ping.attempt >= ping.maxAttempts) {
                this.listeners.remove(pingEntry.getKey());
                ping.listener.onTimeout(ping.attempt);
                continue;
            }
            ping.listener.onFailedAttempt(ping.attempt);
            try {
                ping.send();
            }
            catch (Exception e) {
                ping.listener.onTimeout(ping.attempt);
                Voicechat.LOGGER.warn("Failed to send ping {} after attempt {}", ping.id, ping.attempt);
            }
        }
    }

    public void sendPing(ClientConnection connection, long timeout, int attempts, PingListener listener) throws Exception {
        Ping ping = new Ping(connection, listener, timeout, attempts);
        this.listeners.put(ping.id, ping);
        ping.send();
    }

    private class Ping {
        private final UUID id = UUID.randomUUID();
        private final ClientConnection connection;
        private final PingListener listener;
        private long timestamp;
        private final long timeout;
        private final int maxAttempts;
        private int attempt;

        public Ping(ClientConnection connection, PingListener listener, long timeout, int maxAttempts) {
            this.connection = connection;
            this.listener = listener;
            this.timeout = timeout;
            this.maxAttempts = maxAttempts;
            this.attempt = 0;
        }

        public boolean isTimedOut() {
            return System.currentTimeMillis() - this.timestamp >= this.timeout;
        }

        public void send() throws Exception {
            this.timestamp = System.currentTimeMillis();
            ++this.attempt;
            PingManager.this.server.sendPacketRaw(new PingPacket(this.id, this.timestamp), this.connection);
            Voicechat.LOGGER.info("Sent ping {} attempt {}", this.id, this.attempt);
        }
    }

    public static interface PingListener {
        public void onPong(int var1, long var2);

        public void onFailedAttempt(int var1);

        public void onTimeout(int var1);
    }
}
