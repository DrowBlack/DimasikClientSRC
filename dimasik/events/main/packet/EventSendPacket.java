package dimasik.events.main.packet;

import dimasik.events.api.main.callables.EventCancellable;
import net.minecraft.network.IPacket;

public class EventSendPacket
extends EventCancellable {
    private final IPacket<?> packet;

    public EventSendPacket(IPacket packet) {
        this.packet = packet;
    }

    public IPacket getPacket() {
        return this.packet;
    }
}
