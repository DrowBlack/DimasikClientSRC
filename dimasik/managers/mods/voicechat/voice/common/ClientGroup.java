package dimasik.managers.mods.voicechat.voice.common;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.plugins.impl.GroupImpl;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ClientGroup {
    private final UUID id;
    private final String name;
    private final boolean hasPassword;
    private final boolean persistent;
    private final boolean hidden;
    private final Group.Type type;

    public ClientGroup(UUID id, String name, boolean hasPassword, boolean persistent, boolean hidden, Group.Type type) {
        this.id = id;
        this.name = name;
        this.hasPassword = hasPassword;
        this.persistent = persistent;
        this.hidden = hidden;
        this.type = type;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPassword() {
        return this.hasPassword;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public Group.Type getType() {
        return this.type;
    }

    public static ClientGroup fromBytes(PacketBuffer buf) {
        return new ClientGroup(buf.readUniqueId(), buf.readString(512), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), GroupImpl.TypeImpl.fromInt(buf.readShort()));
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUniqueId(this.id);
        buf.writeString(this.name, 512);
        buf.writeBoolean(this.hasPassword);
        buf.writeBoolean(this.persistent);
        buf.writeBoolean(this.hidden);
        buf.writeShort(GroupImpl.TypeImpl.toInt(this.type));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientGroup group = (ClientGroup)o;
        return Objects.equals(this.id, group.id);
    }
}
