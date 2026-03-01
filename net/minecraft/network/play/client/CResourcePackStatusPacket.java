package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CResourcePackStatusPacket
implements IPacket<IServerPlayNetHandler> {
    private Action action;

    public CResourcePackStatusPacket() {
    }

    public CResourcePackStatusPacket(Action p_i47156_1_) {
        this.action = p_i47156_1_;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.action = buf.readEnumValue(Action.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.handleResourcePackStatus(this);
    }

    public static enum Action {
        SUCCESSFULLY_LOADED,
        DECLINED,
        FAILED_DOWNLOAD,
        ACCEPTED;

    }
}
