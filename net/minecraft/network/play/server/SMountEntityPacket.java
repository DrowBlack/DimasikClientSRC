package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SMountEntityPacket
implements IPacket<IClientPlayNetHandler> {
    private int entityId;
    private int vehicleEntityId;

    public SMountEntityPacket() {
    }

    public SMountEntityPacket(Entity entityIn, @Nullable Entity vehicleIn) {
        this.entityId = entityIn.getEntityId();
        this.vehicleEntityId = vehicleIn != null ? vehicleIn.getEntityId() : 0;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readInt();
        this.vehicleEntityId = buf.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.entityId);
        buf.writeInt(this.vehicleEntityId);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleEntityAttach(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public int getVehicleEntityId() {
        return this.vehicleEntityId;
    }
}
