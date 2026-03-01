package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.events.RemoveGroupEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.GroupEventImpl;

public class RemoveGroupEventImpl
extends GroupEventImpl
implements RemoveGroupEvent {
    public RemoveGroupEventImpl(Group group) {
        super(group, null);
    }

    @Override
    public boolean isCancellable() {
        return super.isCancellable() && this.group.isPersistent();
    }
}
