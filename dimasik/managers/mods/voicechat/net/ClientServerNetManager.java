package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.net.Channel;
import dimasik.managers.mods.voicechat.net.ClientServerChannel;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.Packet;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CCustomPayloadPacket;

public abstract class ClientServerNetManager
extends NetManager {
    public static void sendToServer(Packet<?> packet) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getConnection() == null) {
                return;
            }
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            packet.toBytes(buffer);
            CCustomPayloadPacket customPacket = new CCustomPayloadPacket(packet.getIdentifier(), buffer);
            minecraft.getConnection().sendPacket(customPacket);
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to send packet {} to server: {}", packet.getIdentifier(), e.getMessage());
        }
    }

    public static <T extends Packet<T>> void setClientListener(Channel<T> channel, ClientReceiver<T> packetReceiver) {
        if (!(channel instanceof ClientServerChannel)) {
            throw new IllegalStateException("Channel is not a ClientServerChannel");
        }
        ClientServerChannel c = (ClientServerChannel)channel;
        c.setClientListener(packetReceiver);
    }

    public static interface ClientReceiver<T extends Packet<T>> {
        public void onPacket(Minecraft var1, ClientPlayNetHandler var2, T var3);
    }
}
