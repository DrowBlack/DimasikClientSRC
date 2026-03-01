package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.VoicechatDisableEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;

public class VoicechatDisableEventImpl
extends ClientEventImpl
implements VoicechatDisableEvent {
    private final boolean disabled;

    public VoicechatDisableEventImpl(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean isDisabled() {
        return this.disabled;
    }
}
