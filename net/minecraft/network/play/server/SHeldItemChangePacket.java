package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SHeldItemChangePacket
implements IPacket<IClientPlayNetHandler> {
    private int heldItemHotbarIndex;

    public SHeldItemChangePacket() {
    }

    public SHeldItemChangePacket(int hotbarIndexIn) {
        this.heldItemHotbarIndex = hotbarIndexIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.heldItemHotbarIndex = buf.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.heldItemHotbarIndex);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleHeldItemChange(this);
    }

    public int getHeldItemHotbarIndex() {
        return this.heldItemHotbarIndex;
    }
}
