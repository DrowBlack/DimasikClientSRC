package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class SPlayEntityEffectPacket
implements IPacket<IClientPlayNetHandler> {
    private int entityId;
    private byte effectId;
    private byte amplifier;
    private int duration;
    private byte flags;

    public SPlayEntityEffectPacket() {
    }

    public SPlayEntityEffectPacket(int entityIdIn, EffectInstance effect) {
        this.entityId = entityIdIn;
        this.effectId = (byte)(Effect.getId(effect.getPotion()) & 0xFF);
        this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
        this.duration = effect.getDuration() > Short.MAX_VALUE ? Short.MAX_VALUE : effect.getDuration();
        this.flags = 0;
        if (effect.isAmbient()) {
            this.flags = (byte)(this.flags | 1);
        }
        if (effect.doesShowParticles()) {
            this.flags = (byte)(this.flags | 2);
        }
        if (effect.isShowIcon()) {
            this.flags = (byte)(this.flags | 4);
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.effectId = buf.readByte();
        this.amplifier = buf.readByte();
        this.duration = buf.readVarInt();
        this.flags = buf.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.effectId);
        buf.writeByte(this.amplifier);
        buf.writeVarInt(this.duration);
        buf.writeByte(this.flags);
    }

    public boolean isMaxDuration() {
        return this.duration == Short.MAX_VALUE;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleEntityEffect(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public byte getEffectId() {
        return this.effectId;
    }

    public byte getAmplifier() {
        return this.amplifier;
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean doesShowParticles() {
        return (this.flags & 2) == 2;
    }

    public boolean getIsAmbient() {
        return (this.flags & 1) == 1;
    }

    public boolean shouldShowIcon() {
        return (this.flags & 4) == 4;
    }
}
