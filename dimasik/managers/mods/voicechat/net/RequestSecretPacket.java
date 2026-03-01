package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RequestSecretPacket
implements Packet<RequestSecretPacket> {
    public static final ResourceLocation REQUEST_SECRET = new ResourceLocation("voicechat", "request_secret");
    private int compatibilityVersion;

    public RequestSecretPacket() {
    }

    public RequestSecretPacket(int compatibilityVersion) {
        this.compatibilityVersion = compatibilityVersion;
    }

    public int getCompatibilityVersion() {
        return this.compatibilityVersion;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return REQUEST_SECRET;
    }

    @Override
    public RequestSecretPacket fromBytes(PacketBuffer buf) {
        this.compatibilityVersion = buf.readInt();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.compatibilityVersion);
    }
}
