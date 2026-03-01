package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.GroupEvent;
import javax.annotation.Nullable;

public interface CreateGroupEvent
extends GroupEvent {
    @Override
    public Group getGroup();

    @Override
    @Nullable
    public VoicechatConnection getConnection();
}
