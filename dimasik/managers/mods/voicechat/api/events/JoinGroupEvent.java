package dimasik.managers.mods.voicechat.api.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.GroupEvent;

public interface JoinGroupEvent
extends GroupEvent {
    @Override
    public Group getGroup();

    @Override
    public VoicechatConnection getConnection();
}
