package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.CreateGroupEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.GroupEventImpl;
import javax.annotation.Nullable;

public class CreateGroupEventImpl
extends GroupEventImpl
implements CreateGroupEvent {
    public CreateGroupEventImpl(Group group, @Nullable VoicechatConnection connection) {
        super(group, connection);
    }
}
