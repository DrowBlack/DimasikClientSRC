package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class SDisconnectLoginPacket
implements IPacket<IClientLoginNetHandler> {
    private ITextComponent reason;

    public SDisconnectLoginPacket() {
    }

    public SDisconnectLoginPacket(ITextComponent p_i46853_1_) {
        this.reason = p_i46853_1_;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.reason = ITextComponent.Serializer.getComponentFromJsonLenient(buf.readString(262144));
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeTextComponent(this.reason);
    }

    @Override
    public void processPacket(IClientLoginNetHandler handler) {
        handler.handleDisconnect(this);
    }

    public ITextComponent getReason() {
        return this.reason;
    }
}
