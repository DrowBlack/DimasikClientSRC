package dimasik.events.main.block;

import dimasik.events.api.main.Event;
import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.util.math.BlockPos;

public class EventCollision
extends EventCancellable
implements Event {
    private final BlockPos blockPos;

    @Generated
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Generated
    public EventCollision(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
