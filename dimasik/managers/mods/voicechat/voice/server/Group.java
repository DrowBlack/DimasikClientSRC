package dimasik.managers.mods.voicechat.voice.server;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import java.util.UUID;
import javax.annotation.Nullable;

public class Group {
    private UUID id;
    private String name;
    @Nullable
    private String password;
    private boolean persistent;
    private boolean hidden;
    private Group.Type type;

    public Group(UUID id, String name, @Nullable String password, boolean persistent, boolean hidden, Group.Type type) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.persistent = persistent;
        this.hidden = hidden;
        this.type = type;
    }

    public Group(UUID id, String name, @Nullable String password, boolean persistent) {
        this(id, name, password, persistent, false, Group.Type.NORMAL);
    }

    public Group(UUID id, String name, @Nullable String password) {
        this(id, name, password, false);
    }

    public Group(UUID id, String name) {
        this(id, name, null);
    }

    public Group() {
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public String getPassword() {
        return this.password;
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

    public boolean isOpen() {
        return this.type == Group.Type.OPEN;
    }

    public boolean isNormal() {
        return this.type == Group.Type.NORMAL;
    }

    public boolean isIsolated() {
        return this.type == Group.Type.ISOLATED;
    }

    public ClientGroup toClientGroup() {
        return new ClientGroup(this.id, this.name, this.password != null, this.persistent, this.hidden, this.type);
    }
}
