package dimasik.managers.mods.voicechat.voice.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;

public class PlayerState {
    private UUID uuid;
    private String name;
    private boolean disabled;
    private boolean disconnected;
    @Nullable
    private UUID group;

    public PlayerState(UUID uuid, String name, boolean disabled, boolean disconnected) {
        this.uuid = uuid;
        this.name = name;
        this.disabled = disabled;
        this.disconnected = disconnected;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    @Nullable
    public UUID getGroup() {
        return this.group;
    }

    public void setGroup(@Nullable UUID group) {
        this.group = group;
    }

    public boolean hasGroup() {
        return this.group != null;
    }

    public String toString() {
        return "{disabled=" + this.disabled + ", disconnected=" + this.disconnected + ", uuid=" + String.valueOf(this.uuid) + ", name=" + this.name + ", group=" + String.valueOf(this.group) + "}";
    }

    public static PlayerState fromBytes(PacketBuffer buf) {
        boolean disabled = buf.readBoolean();
        boolean disconnected = buf.readBoolean();
        UUID uuid = buf.readUniqueId();
        String name = buf.readString(Short.MAX_VALUE);
        PlayerState state = new PlayerState(uuid, name, disabled, disconnected);
        if (buf.readBoolean()) {
            state.setGroup(buf.readUniqueId());
        }
        return state;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(this.disabled);
        buf.writeBoolean(this.disconnected);
        buf.writeUniqueId(this.uuid);
        buf.writeString(this.name);
        buf.writeBoolean(this.hasGroup());
        if (this.hasGroup()) {
            buf.writeUniqueId(this.group);
        }
    }
}
