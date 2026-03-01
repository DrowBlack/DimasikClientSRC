package dimasik.itemics.event;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.event.events.BlockInteractEvent;
import dimasik.itemics.api.event.events.ChatEvent;
import dimasik.itemics.api.event.events.ChunkEvent;
import dimasik.itemics.api.event.events.PacketEvent;
import dimasik.itemics.api.event.events.PathEvent;
import dimasik.itemics.api.event.events.PlayerUpdateEvent;
import dimasik.itemics.api.event.events.RenderEvent;
import dimasik.itemics.api.event.events.RotationMoveEvent;
import dimasik.itemics.api.event.events.SprintStateEvent;
import dimasik.itemics.api.event.events.TabCompleteEvent;
import dimasik.itemics.api.event.events.TickEvent;
import dimasik.itemics.api.event.events.WorldEvent;
import dimasik.itemics.api.event.events.type.EventState;
import dimasik.itemics.api.event.listener.IEventBus;
import dimasik.itemics.api.event.listener.IGameEventListener;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.cache.WorldProvider;
import dimasik.itemics.utils.BlockStateInterface;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class GameEventHandler
implements IEventBus,
Helper {
    private final Itemics itemics;
    private final List<IGameEventListener> listeners = new CopyOnWriteArrayList<IGameEventListener>();

    public GameEventHandler(Itemics itemics) {
        this.itemics = itemics;
    }

    @Override
    public final void onTick(TickEvent event) {
        if (event.getType() == TickEvent.Type.IN) {
            try {
                this.itemics.bsi = new BlockStateInterface(this.itemics.getPlayerContext(), true);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                this.itemics.bsi = null;
            }
        } else {
            this.itemics.bsi = null;
        }
        this.listeners.forEach(l -> l.onTick(event));
    }

    @Override
    public final void onPlayerUpdate(PlayerUpdateEvent event) {
        this.listeners.forEach(l -> l.onPlayerUpdate(event));
    }

    @Override
    public final void onSendChatMessage(ChatEvent event) {
        this.listeners.forEach(l -> l.onSendChatMessage(event));
    }

    @Override
    public void onPreTabComplete(TabCompleteEvent event) {
        this.listeners.forEach(l -> l.onPreTabComplete(event));
    }

    @Override
    public final void onChunkEvent(ChunkEvent event) {
        boolean isPreUnload;
        EventState state = event.getState();
        ChunkEvent.Type type = event.getType();
        boolean isPostPopulate = state == EventState.POST && (type == ChunkEvent.Type.POPULATE_FULL || type == ChunkEvent.Type.POPULATE_PARTIAL);
        World world = this.itemics.getPlayerContext().world();
        boolean bl = isPreUnload = state == EventState.PRE && type == ChunkEvent.Type.UNLOAD && world.getChunkProvider().getChunk(event.getX(), event.getZ(), null, false) != null;
        if (isPostPopulate || isPreUnload) {
            this.itemics.getWorldProvider().ifWorldLoaded(worldData -> {
                Chunk chunk = world.getChunk(event.getX(), event.getZ());
                worldData.getCachedWorld().queueForPacking(chunk);
            });
        }
        this.listeners.forEach(l -> l.onChunkEvent(event));
    }

    @Override
    public final void onRenderPass(RenderEvent event) {
        this.listeners.forEach(l -> l.onRenderPass(event));
    }

    @Override
    public final void onWorldEvent(WorldEvent event) {
        WorldProvider cache = this.itemics.getWorldProvider();
        if (event.getState() == EventState.POST) {
            cache.closeWorld();
            if (event.getWorld() != null) {
                cache.initWorld(event.getWorld().getDimensionKey());
            }
        }
        this.listeners.forEach(l -> l.onWorldEvent(event));
    }

    @Override
    public final void onSendPacket(PacketEvent event) {
        this.listeners.forEach(l -> l.onSendPacket(event));
    }

    @Override
    public final void onReceivePacket(PacketEvent event) {
        this.listeners.forEach(l -> l.onReceivePacket(event));
    }

    @Override
    public void onPlayerRotationMove(RotationMoveEvent event) {
        this.listeners.forEach(l -> l.onPlayerRotationMove(event));
    }

    @Override
    public void onPlayerSprintState(SprintStateEvent event) {
        this.listeners.forEach(l -> l.onPlayerSprintState(event));
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        this.listeners.forEach(l -> l.onBlockInteract(event));
    }

    @Override
    public void onPlayerDeath() {
        this.listeners.forEach(IGameEventListener::onPlayerDeath);
    }

    @Override
    public void onPathEvent(PathEvent event) {
        this.listeners.forEach(l -> l.onPathEvent(event));
    }

    @Override
    public final void registerEventListener(IGameEventListener listener) {
        this.listeners.add(listener);
    }
}
