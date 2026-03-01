package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class SPlaySoundEventPacket
implements IPacket<IClientPlayNetHandler> {
    private int soundType;
    private BlockPos soundPos;
    private int soundData;
    private boolean serverWide;

    public SPlaySoundEventPacket() {
    }

    public SPlaySoundEventPacket(int soundTypeIn, BlockPos soundPosIn, int soundDataIn, boolean serverWideIn) {
        this.soundType = soundTypeIn;
        this.soundPos = soundPosIn.toImmutable();
        this.soundData = soundDataIn;
        this.serverWide = serverWideIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.soundType = buf.readInt();
        this.soundPos = buf.readBlockPos();
        this.soundData = buf.readInt();
        this.serverWide = buf.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.soundType);
        buf.writeBlockPos(this.soundPos);
        buf.writeInt(this.soundData);
        buf.writeBoolean(this.serverWide);
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleEffect(this);
    }

    public boolean isSoundServerwide() {
        return this.serverWide;
    }

    public int getSoundType() {
        return this.soundType;
    }

    public int getSoundData() {
        return this.soundData;
    }

    public BlockPos getSoundPos() {
        return this.soundPos;
    }
}
