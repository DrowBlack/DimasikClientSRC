package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;

public class CLoginStartPacket
implements IPacket<IServerLoginNetHandler> {
    private GameProfile profile;

    public CLoginStartPacket() {
    }

    public CLoginStartPacket(GameProfile profileIn) {
        this.profile = profileIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.profile = new GameProfile(null, buf.readString(16));
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.profile.getName());
    }

    @Override
    public void processPacket(IServerLoginNetHandler handler) {
        handler.processLoginStart(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
