package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.config.ServerConfig;
import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SecretPacket
implements Packet<SecretPacket> {
    public static final ResourceLocation SECRET = new ResourceLocation("voicechat", "secret");
    private UUID secret;
    private int serverPort;
    private UUID playerUUID;
    private ServerConfig.Codec codec;
    private int mtuSize;
    private double voiceChatDistance;
    private int keepAlive;
    private boolean groupsEnabled;
    private String voiceHost;
    private boolean allowRecording;

    public SecretPacket() {
    }

    public SecretPacket(ServerPlayerEntity player, UUID secret, int port, ServerConfig serverConfig) {
        this.secret = secret;
        this.serverPort = port;
        this.playerUUID = player.getUniqueID();
        this.codec = serverConfig.voiceChatCodec.get();
        this.mtuSize = serverConfig.voiceChatMtuSize.get();
        this.voiceChatDistance = serverConfig.voiceChatDistance.get();
        this.keepAlive = serverConfig.keepAlive.get();
        this.groupsEnabled = serverConfig.groupsEnabled.get();
        this.voiceHost = PluginManager.instance().getVoiceHost(serverConfig.voiceHost.get());
        this.allowRecording = serverConfig.allowRecording.get();
    }

    public UUID getSecret() {
        return this.secret;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
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

    public String getVoiceHost() {
        return this.voiceHost;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return SECRET;
    }

    public boolean allowRecording() {
        return this.allowRecording;
    }

    @Override
    public SecretPacket fromBytes(PacketBuffer buf) {
        this.secret = buf.readUniqueId();
        this.serverPort = buf.readInt();
        this.playerUUID = buf.readUniqueId();
        this.codec = ServerConfig.Codec.values()[buf.readByte()];
        this.mtuSize = buf.readInt();
        this.voiceChatDistance = buf.readDouble();
        this.keepAlive = buf.readInt();
        this.groupsEnabled = buf.readBoolean();
        this.voiceHost = buf.readString(Short.MAX_VALUE);
        this.allowRecording = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.secret);
        buf.writeInt(this.serverPort);
        buf.writeUniqueId(this.playerUUID);
        buf.writeByte(this.codec.ordinal());
        buf.writeInt(this.mtuSize);
        buf.writeDouble(this.voiceChatDistance);
        buf.writeInt(this.keepAlive);
        buf.writeBoolean(this.groupsEnabled);
        buf.writeString(this.voiceHost);
        buf.writeBoolean(this.allowRecording);
    }
}
