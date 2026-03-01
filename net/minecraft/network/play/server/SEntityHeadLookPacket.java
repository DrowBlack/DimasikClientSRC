package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public class SEntityHeadLookPacket
implements IPacket<IClientPlayNetHandler> {
    private int entityId;
    private byte yaw;

    public SEntityHeadLookPacket() {
    }

    public SEntityHeadLookPacket(Entity entityIn, byte yawIn) {
        this.entityId = entityIn.getEntityId();
        this.yaw = yawIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.yaw = buf.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.yaw);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleEntityHeadLook(this);
    }

    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    public byte getYaw() {
        return this.yaw;
    }
}
