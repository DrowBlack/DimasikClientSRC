package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CUpdateBeaconPacket
implements IPacket<IServerPlayNetHandler> {
    private int primaryEffect;
    private int secondaryEffect;

    public CUpdateBeaconPacket() {
    }

    public CUpdateBeaconPacket(int primaryEffectIn, int secondaryEffectIn) {
        this.primaryEffect = primaryEffectIn;
        this.secondaryEffect = secondaryEffectIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.primaryEffect = buf.readVarInt();
        this.secondaryEffect = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.primaryEffect);
        buf.writeVarInt(this.secondaryEffect);
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processUpdateBeacon(this);
    }

    public int getPrimaryEffect() {
        return this.primaryEffect;
    }

    public int getSecondaryEffect() {
        return this.secondaryEffect;
    }
}
