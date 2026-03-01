package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.RawUdpPacket;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import dimasik.managers.mods.voicechat.voice.common.NetworkMessage;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import net.minecraft.network.PacketBuffer;

public class ClientNetworkMessage {
    @Nullable
    public static NetworkMessage readPacketClient(RawUdpPacket packet, ClientVoicechatConnection client) throws IllegalAccessException, InstantiationException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvocationTargetException, NoSuchMethodException {
        byte[] data = packet.getData();
        PacketBuffer b = new PacketBuffer(Unpooled.wrappedBuffer(data));
        if (b.readByte() != -1) {
            Voicechat.LOGGER.debug("Received invalid packet from {}", client.getAddress());
            return null;
        }
        return NetworkMessage.readFromBytes(packet.getSocketAddress(), client.getData().getSecret(), b.readByteArray(), System.currentTimeMillis());
    }

    public static byte[] writeClient(ClientVoicechatConnection client, NetworkMessage networkMessage) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] payload = networkMessage.write(client.getData().getSecret());
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(17 + payload.length));
        buffer.writeByte(-1);
        buffer.writeUniqueId(client.getData().getPlayerUUID());
        buffer.writeByteArray(payload);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }
}
