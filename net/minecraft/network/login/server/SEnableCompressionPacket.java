package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SEnableCompressionPacket
implements IPacket<IClientLoginNetHandler> {
    private int compressionThreshold;

    public SEnableCompressionPacket() {
    }

    public SEnableCompressionPacket(int thresholdIn) {
        this.compressionThreshold = thresholdIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.compressionThreshold = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.compressionThreshold);
    }

    @Override
    public void processPacket(IClientLoginNetHandler handler) {
        handler.handleEnableCompression(this);
    }

    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }
}
