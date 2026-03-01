package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.events.PacketEvent;
import dimasik.managers.mods.voicechat.api.packets.Packet;

public interface SoundPacketEvent<T extends Packet>
extends PacketEvent<T> {
    public static final String SOURCE_GROUP = "group";
    public static final String SOURCE_PROXIMITY = "proximity";
    public static final String SOURCE_SPECTATOR = "spectator";
    public static final String SOURCE_PLUGIN = "plugin";

    public String getSource();
}
