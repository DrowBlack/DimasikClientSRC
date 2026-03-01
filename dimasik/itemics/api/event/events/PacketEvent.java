package dimasik.itemics.api.event.events;

import dimasik.itemics.api.event.events.type.EventState;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;

public final class PacketEvent {
    private final NetworkManager networkManager;
    private final EventState state;
    private final IPacket<?> packet;

    public PacketEvent(NetworkManager networkManager, EventState state, IPacket<?> packet) {
        this.networkManager = networkManager;
        this.state = state;
        this.packet = packet;
    }

    public final NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public final EventState getState() {
        return this.state;
    }

    public final IPacket<?> getPacket() {
        return this.packet;
    }

    public final <T extends IPacket<?>> T cast() {
        return (T)this.packet;
    }
}
