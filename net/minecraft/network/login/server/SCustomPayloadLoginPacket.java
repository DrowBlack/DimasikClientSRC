package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SCustomPayloadLoginPacket
implements IPacket<IClientLoginNetHandler> {
    private int transaction;
    private ResourceLocation channel;
    private PacketBuffer payload;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.transaction = buf.readVarInt();
        this.channel = buf.readResourceLocation();
        int i = buf.readableBytes();
        if (i < 0 || i > 0x100000) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.payload = new PacketBuffer(buf.readBytes(i));
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.transaction);
        buf.writeResourceLocation(this.channel);
        buf.writeBytes(this.payload.copy());
    }

    @Override
    public void processPacket(IClientLoginNetHandler handler) {
        handler.handleCustomPayloadLogin(this);
    }

    public int getTransaction() {
        return this.transaction;
    }
}
