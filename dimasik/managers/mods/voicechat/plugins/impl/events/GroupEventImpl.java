package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.GroupEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ServerEventImpl;
import javax.annotation.Nullable;

public class GroupEventImpl
extends ServerEventImpl
implements GroupEvent {
    @Nullable
    protected Group group;
    protected VoicechatConnection connection;

    public GroupEventImpl(@Nullable Group group, VoicechatConnection connection) {
        this.group = group;
        this.connection = connection;
    }

    @Override
    @Nullable
    public Group getGroup() {
        return this.group;
    }

    @Override
    public VoicechatConnection getConnection() {
        return this.connection;
    }
}
