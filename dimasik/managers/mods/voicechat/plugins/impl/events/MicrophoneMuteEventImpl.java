package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.MicrophoneMuteEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;

public class MicrophoneMuteEventImpl
extends ClientEventImpl
implements MicrophoneMuteEvent {
    private final boolean muted;

    public MicrophoneMuteEventImpl(boolean muted) {
        this.muted = muted;
    }

    @Override
    public boolean isDisabled() {
        return this.muted;
    }
}
