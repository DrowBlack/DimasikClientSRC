package dimasik.events.main.misc;

import dimasik.events.api.main.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.entity.Entity;

public class AttackEvent
extends EventCancellable {
    public Entity entity;

    @Generated
    public AttackEvent(Entity entity) {
        this.entity = entity;
    }
}
