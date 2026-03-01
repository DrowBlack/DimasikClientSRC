package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import dimasik.managers.mods.voicechat.debug.PingHandler;
import dimasik.managers.mods.voicechat.voice.common.AES;
import dimasik.managers.mods.voicechat.voice.common.AuthenticateAckPacket;
import dimasik.managers.mods.voicechat.voice.common.AuthenticatePacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckAckPacket;
import dimasik.managers.mods.voicechat.voice.common.ConnectionCheckPacket;
import dimasik.managers.mods.voicechat.voice.common.GroupSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.KeepAlivePacket;
import dimasik.managers.mods.voicechat.voice.common.LocationSoundPacket;
import dimasik.managers.mods.voicechat.voice.common.MicPacket;
import dimasik.managers.mods.voicechat.voice.common.Packet;
import dimasik.managers.mods.voicechat.voice.common.PingPacket;
import dimasik.managers.mods.voicechat.voice.common.PlayerSoundPacket;
import dimasik.managers.mods.voicechat.voice.server.ClientConnection;
import dimasik.managers.mods.voicechat.voice.server.Server;
import io.netty.buffer.Unpooled;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import net.minecraft.network.PacketBuffer;

public class NetworkMessage {
    public static final byte MAGIC_BYTE = -1;
    private final long timestamp;
    private Packet<? extends Packet> packet;
    private SocketAddress address;
    private static final Map<Byte, Class<? extends Packet>> packetRegistry = new HashMap<Byte, Class<? extends Packet>>();

    public NetworkMessage(long timestamp, Packet<?> packet) {
        this(timestamp);
        this.packet = packet;
    }

    public NetworkMessage(Packet<?> packet) {
        this(System.currentTimeMillis());
        this.packet = packet;
    }

    private NetworkMessage(long timestamp) {
        this.timestamp = timestamp;
    }

    @Nonnull
    public Packet<? extends Packet> getPacket() {
        return this.packet;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getTTL() {
        return this.packet.getTTL();
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    @Nullable
    public static NetworkMessage readPacketServer(RawUdpPacket packet, Server server) throws IllegalAccessException, InstantiationException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvocationTargetException, NoSuchMethodException {
        byte[] data = packet.getData();
        PacketBuffer b = new PacketBuffer(Unpooled.wrappedBuffer(data));
        if (b.readByte() != -1) {
            Voicechat.LOGGER.debug("Received invalid packet from {}", packet.getSocketAddress());
            return null;
        }
        UUID playerID = b.readUniqueId();
        if (!server.hasSecret(playerID)) {
            if (PingHandler.onPacket(server, packet.getSocketAddress(), playerID, b)) {
                return null;
            }
            Voicechat.LOGGER.debug("Player " + String.valueOf(playerID) + " does not have a secret", new Object[0]);
            return null;
        }
        return NetworkMessage.readFromBytes(packet.getSocketAddress(), server.getSecret(playerID), b.readByteArray(), packet.getTimestamp());
    }

    @Nullable
    public static NetworkMessage readFromBytes(SocketAddress socketAddress, UUID secret, byte[] encryptedPayload, long timestamp) throws InstantiationException, IllegalAccessException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, NoSuchMethodException, InvocationTargetException {
        byte[] decrypt;
        try {
            decrypt = AES.decrypt(secret, encryptedPayload);
        }
        catch (Exception e) {
            Voicechat.LOGGER.debug("Failed to decrypt packet from {}", socketAddress);
            return null;
        }
        PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(decrypt));
        byte packetType = buffer.readByte();
        Class<? extends Packet> packetClass = packetRegistry.get(packetType);
        if (packetClass == null) {
            Voicechat.LOGGER.debug("Got invalid packet ID {}", packetType);
            return null;
        }
        Packet p = packetClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        NetworkMessage message = new NetworkMessage(timestamp);
        message.address = socketAddress;
        message.packet = p.fromBytes(buffer);
        return message;
    }

    private static byte getPacketType(Packet<? extends Packet> packet) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : packetRegistry.entrySet()) {
            if (!packet.getClass().equals(entry.getValue())) continue;
            return entry.getKey();
        }
        return -1;
    }

    public byte[] writeServer(Server server, ClientConnection connection) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] payload = this.write(server.getSecret(connection.getPlayerUUID()));
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(1 + payload.length));
        buffer.writeByte(-1);
        buffer.writeByteArray(payload);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    public byte[] write(UUID secret) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        byte type = NetworkMessage.getPacketType(this.packet);
        if (type < 0) {
            throw new IllegalArgumentException("Packet type not found");
        }
        buffer.writeByte(type);
        this.packet.toBytes(buffer);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return AES.encrypt(secret, bytes);
    }

    static {
        packetRegistry.put((byte)1, MicPacket.class);
        packetRegistry.put((byte)2, PlayerSoundPacket.class);
        packetRegistry.put((byte)3, GroupSoundPacket.class);
        packetRegistry.put((byte)4, LocationSoundPacket.class);
        packetRegistry.put((byte)5, AuthenticatePacket.class);
        packetRegistry.put((byte)6, AuthenticateAckPacket.class);
        packetRegistry.put((byte)7, PingPacket.class);
        packetRegistry.put((byte)8, KeepAlivePacket.class);
        packetRegistry.put((byte)9, ConnectionCheckPacket.class);
        packetRegistry.put((byte)10, ConnectionCheckAckPacket.class);
    }
}
