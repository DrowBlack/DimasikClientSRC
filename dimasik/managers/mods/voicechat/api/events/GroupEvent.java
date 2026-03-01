package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.ServerEvent;
import javax.annotation.Nullable;

public interface GroupEvent
extends ServerEvent {
    @Nullable
    public Group getGroup();

    @Nullable
    public VoicechatConnection getConnection();
}
