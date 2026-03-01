package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.Difficulty;

public class SServerDifficultyPacket
implements IPacket<IClientPlayNetHandler> {
    private Difficulty difficulty;
    private boolean difficultyLocked;

    public SServerDifficultyPacket() {
    }

    public SServerDifficultyPacket(Difficulty difficultyIn, boolean difficultyLockedIn) {
        this.difficulty = difficultyIn;
        this.difficultyLocked = difficultyLockedIn;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleServerDifficulty(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.difficulty = Difficulty.byId(buf.readUnsignedByte());
        this.difficultyLocked = buf.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.difficulty.getId());
        buf.writeBoolean(this.difficultyLocked);
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }
}
