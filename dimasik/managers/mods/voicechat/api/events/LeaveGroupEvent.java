package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.GroupEvent;
import javax.annotation.Nullable;

public interface LeaveGroupEvent
extends GroupEvent {
    @Override
    @Nullable
    public Group getGroup();

    @Override
    public VoicechatConnection getConnection();
}
