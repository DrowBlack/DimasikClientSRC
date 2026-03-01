package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.config.ServerConfig;
import dimasik.managers.mods.voicechat.net.SecretPacket;
import java.net.URI;
import java.util.UUID;

public class InitializationData {
    private final String serverIP;
    private final int serverPort;
    private final UUID playerUUID;
    private final UUID secret;
    private final ServerConfig.Codec codec;
    private final int mtuSize;
    private final double voiceChatDistance;
    private final int keepAlive;
    private final boolean groupsEnabled;
    private final boolean allowRecording;

    public InitializationData(String serverIP, SecretPacket secretPacket) {
        HostData hostData = InitializationData.parseAddress(secretPacket.getVoiceHost(), serverIP, secretPacket.getServerPort());
        this.serverIP = hostData.ip;
        this.serverPort = hostData.port;
        this.playerUUID = secretPacket.getPlayerUUID();
        this.secret = secretPacket.getSecret();
        this.codec = secretPacket.getCodec();
        this.mtuSize = secretPacket.getMtuSize();
        this.voiceChatDistance = secretPacket.getVoiceChatDistance();
        this.keepAlive = secretPacket.getKeepAlive();
        this.groupsEnabled = secretPacket.groupsEnabled();
        this.allowRecording = secretPacket.allowRecording();
    }

    private static HostData parseAddress(String voiceHost, String serverIP, int serverPort) {
        String ip = serverIP;
        int port = serverPort;
        if (!voiceHost.isEmpty()) {
            try {
                URI uri = new URI("voicechat://" + voiceHost);
                String host = uri.getHost();
                int hostPort = uri.getPort();
                if (host != null) {
                    ip = host;
                }
                if (hostPort > 0) {
                    port = hostPort;
                }
            }
            catch (Exception e) {
                Voicechat.LOGGER.warn("Failed to parse voice host", e);
            }
        }
        return new HostData(ip, port);
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public UUID getSecret() {
        return this.secret;
    }

    public ServerConfig.Codec getCodec() {
        return this.codec;
    }

    public int getMtuSize() {
        return this.mtuSize;
    }

    public double getVoiceChatDistance() {
        return this.voiceChatDistance;
    }

    public int getKeepAlive() {
        return this.keepAlive;
    }

    public boolean groupsEnabled() {
        return this.groupsEnabled;
    }

    public boolean allowRecording() {
        return this.allowRecording;
    }

    private static class HostData {
        private final String ip;
        private final int port;

        public HostData(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
