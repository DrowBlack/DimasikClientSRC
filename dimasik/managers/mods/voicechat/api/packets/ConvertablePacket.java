package dimasik.managers.mods.voicechat.api.packets;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.packets.EntitySoundPacket;
import dimasik.managers.mods.voicechat.api.packets.LocationalSoundPacket;
import dimasik.managers.mods.voicechat.api.packets.StaticSoundPacket;
import java.util.UUID;

public interface ConvertablePacket {
    public EntitySoundPacket.Builder<?> entitySoundPacketBuilder();

    public LocationalSoundPacket.Builder<?> locationalSoundPacketBuilder();

    public StaticSoundPacket.Builder<?> staticSoundPacketBuilder();

    @Deprecated
    public EntitySoundPacket toEntitySoundPacket(UUID var1, boolean var2);

    @Deprecated
    public LocationalSoundPacket toLocationalSoundPacket(Position var1);

    @Deprecated
    public StaticSoundPacket toStaticSoundPacket();
}
