package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.VoicechatConnection;
import dimasik.managers.mods.voicechat.api.events.JoinGroupEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.GroupEventImpl;

public class JoinGroupEventImpl
extends GroupEventImpl
implements JoinGroupEvent {
    public JoinGroupEventImpl(Group group, VoicechatConnection connection) {
        super(group, connection);
    }
}
