package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.DestroyOpenALContextEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;

public class DestroyOpenALContextEventImpl
extends ClientEventImpl
implements DestroyOpenALContextEvent {
    protected long context;
    protected long device;

    public DestroyOpenALContextEventImpl(long context, long device) {
        this.context = context;
        this.device = device;
    }

    @Override
    public long getContext() {
        return this.context;
    }

    @Override
    public long getDevice() {
        return this.device;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
