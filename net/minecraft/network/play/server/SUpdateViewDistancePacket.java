package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SUpdateViewDistancePacket
implements IPacket<IClientPlayNetHandler> {
    private int viewDistance;

    public SUpdateViewDistancePacket() {
    }

    public SUpdateViewDistancePacket(int viewDistanceIn) {
        this.viewDistance = viewDistanceIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.viewDistance = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.viewDistance);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleUpdateViewDistancePacket(this);
    }

    public int getViewDistance() {
        return this.viewDistance;
    }
}
