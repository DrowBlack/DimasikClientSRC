package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class SOpenBookWindowPacket
implements IPacket<IClientPlayNetHandler> {
    private Hand hand;

    public SOpenBookWindowPacket() {
    }

    public SOpenBookWindowPacket(Hand hand) {
        this.hand = hand;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.hand = buf.readEnumValue(Hand.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.hand);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleOpenBookPacket(this);
    }

    public Hand getHand() {
        return this.hand;
    }
}
