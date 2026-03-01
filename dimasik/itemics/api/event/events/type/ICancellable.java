package dimasik.itemics.api.event.events.type;

public interface ICancellable {
    public void cancel();

    public boolean isCancelled();
}
