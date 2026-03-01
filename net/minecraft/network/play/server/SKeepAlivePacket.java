package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SKeepAlivePacket
implements IPacket<IClientPlayNetHandler> {
    private long id;

    public SKeepAlivePacket() {
    }

    public SKeepAlivePacket(long idIn) {
        this.id = idIn;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.id = buf.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.id);
    }

    public long getId() {
        return this.id;
    }
}
