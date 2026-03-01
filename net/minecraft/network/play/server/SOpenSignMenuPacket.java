package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class SOpenSignMenuPacket
implements IPacket<IClientPlayNetHandler> {
    private BlockPos signPosition;

    public SOpenSignMenuPacket() {
    }

    public SOpenSignMenuPacket(BlockPos posIn) {
        this.signPosition = posIn;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleSignEditorOpen(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.signPosition = buf.readBlockPos();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.signPosition);
    }

    public BlockPos getSignPosition() {
        return this.signPosition;
    }
}
