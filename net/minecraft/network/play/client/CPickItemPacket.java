package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CPickItemPacket
implements IPacket<IServerPlayNetHandler> {
    private int pickIndex;

    public CPickItemPacket() {
    }

    public CPickItemPacket(int pickIndexIn) {
        this.pickIndex = pickIndexIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.pickIndex = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.pickIndex);
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processPickItem(this);
    }

    public int getPickIndex() {
        return this.pickIndex;
    }
}
