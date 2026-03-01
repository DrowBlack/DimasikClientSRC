package dimasik.itemics.api.event.listener;

import dimasik.itemics.api.event.listener.IGameEventListener;

public interface IEventBus
extends IGameEventListener {
    public void registerEventListener(IGameEventListener var1);
}
