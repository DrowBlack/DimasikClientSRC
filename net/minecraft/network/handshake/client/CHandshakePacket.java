package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.util.SharedConstants;

public class CHandshakePacket
implements IPacket<IHandshakeNetHandler> {
    private int protocolVersion;
    private String ip;
    private int port;
    private ProtocolType requestedState;

    public CHandshakePacket() {
    }

    public CHandshakePacket(String p_i47613_1_, int p_i47613_2_, ProtocolType p_i47613_3_) {
        this.protocolVersion = SharedConstants.getVersion().getProtocolVersion();
        this.ip = p_i47613_1_;
        this.port = p_i47613_2_;
        this.requestedState = p_i47613_3_;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.protocolVersion = buf.readVarInt();
        this.ip = buf.readString(255);
        this.port = buf.readUnsignedShort();
        this.requestedState = ProtocolType.getById(buf.readVarInt());
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.protocolVersion);
        buf.writeString(this.ip);
        buf.writeShort(this.port);
        buf.writeVarInt(this.requestedState.getId());
    }

    @Override
    public void processPacket(IHandshakeNetHandler handler) {
        handler.processHandshake(this);
    }

    public ProtocolType getRequestedState() {
        return this.requestedState;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }
}
