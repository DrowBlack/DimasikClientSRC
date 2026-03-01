package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;

public class CCustomPayloadPacket
implements IPacket<IServerPlayNetHandler> {
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    private ResourceLocation channel;
    private PacketBuffer data;

    public CCustomPayloadPacket() {
    }

    public CCustomPayloadPacket(ResourceLocation channelIn, PacketBuffer dataIn) {
        this.channel = channelIn;
        this.data = dataIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.channel = buf.readResourceLocation();
        int i = buf.readableBytes();
        if (i < 0 || i > Short.MAX_VALUE) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new PacketBuffer(buf.readBytes(i));
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeResourceLocation(this.channel);
        buf.writeBytes(this.data);
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
}
