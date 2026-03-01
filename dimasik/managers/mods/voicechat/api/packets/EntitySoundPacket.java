package dimasik.managers.mods.voicechat.api.packets;

import dimasik.managers.mods.voicechat.api.packets.SoundPacket;
import java.util.UUID;

public interface EntitySoundPacket
extends SoundPacket {
    public UUID getEntityUuid();

    public boolean isWhispering();

    public float getDistance();

    public static interface Builder<T extends Builder<T>>
    extends SoundPacket.Builder<T, EntitySoundPacket> {
        public T entityUuid(UUID var1);

        public T whispering(boolean var1);

        public T distance(float var1);
    }
}
