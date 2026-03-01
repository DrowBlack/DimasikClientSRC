package dimasik.managers.mods.voicechat.api.events;

public interface Event {
    public boolean isCancellable();

    public boolean cancel();

    public boolean isCancelled();
}
