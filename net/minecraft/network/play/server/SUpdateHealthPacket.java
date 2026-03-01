package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SUpdateHealthPacket
implements IPacket<IClientPlayNetHandler> {
    private float health;
    private int foodLevel;
    private float saturationLevel;

    public SUpdateHealthPacket() {
    }

    public SUpdateHealthPacket(float healthIn, int foodLevelIn, float saturationLevelIn) {
        this.health = healthIn;
        this.foodLevel = foodLevelIn;
        this.saturationLevel = saturationLevelIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.health = buf.readFloat();
        this.foodLevel = buf.readVarInt();
        this.saturationLevel = buf.readFloat();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeFloat(this.health);
        buf.writeVarInt(this.foodLevel);
        buf.writeFloat(this.saturationLevel);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleUpdateHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }
}
