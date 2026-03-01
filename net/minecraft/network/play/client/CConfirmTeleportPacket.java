package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CConfirmTeleportPacket
implements IPacket<IServerPlayNetHandler> {
    private int telportId;

    public CConfirmTeleportPacket() {
    }

    public CConfirmTeleportPacket(int teleportIdIn) {
        this.telportId = teleportIdIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.telportId = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.telportId);
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processConfirmTeleport(this);
    }

    public int getTeleportId() {
        return this.telportId;
    }
}
