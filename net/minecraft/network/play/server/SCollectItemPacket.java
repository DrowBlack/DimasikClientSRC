package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SCollectItemPacket
implements IPacket<IClientPlayNetHandler> {
    private int collectedItemEntityId;
    private int entityId;
    private int collectedQuantity;

    public SCollectItemPacket() {
    }

    public SCollectItemPacket(int p_i47316_1_, int p_i47316_2_, int p_i47316_3_) {
        this.collectedItemEntityId = p_i47316_1_;
        this.entityId = p_i47316_2_;
        this.collectedQuantity = p_i47316_3_;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.collectedItemEntityId = buf.readVarInt();
        this.entityId = buf.readVarInt();
        this.collectedQuantity = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.collectedItemEntityId);
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.collectedQuantity);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleCollectItem(this);
    }

    public int getCollectedItemEntityID() {
        return this.collectedItemEntityId;
    }

    public int getEntityID() {
        return this.entityId;
    }

    public int getAmount() {
        return this.collectedQuantity;
    }
}
