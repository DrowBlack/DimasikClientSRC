package dimasik.managers.mods.voicechat.api.packets;

import dimasik.managers.mods.voicechat.api.packets.SoundPacket;

public interface StaticSoundPacket
extends SoundPacket {

    public static interface Builder<T extends Builder<T>>
    extends SoundPacket.Builder<T, StaticSoundPacket> {
    }
}
