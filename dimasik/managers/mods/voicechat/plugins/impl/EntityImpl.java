package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.Entity;
import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import java.util.Objects;
import java.util.UUID;

public class EntityImpl
implements Entity {
    protected net.minecraft.entity.Entity entity;

    public EntityImpl(net.minecraft.entity.Entity entity) {
        this.entity = entity;
    }

    @Override
    public UUID getUuid() {
        return this.entity.getUniqueID();
    }

    @Override
    public Object getEntity() {
        return CommonCompatibilityManager.INSTANCE.createRawApiEntity(this.entity);
    }

    @Override
    public Position getPosition() {
        return new PositionImpl(this.entity.getPositionVec());
    }

    public net.minecraft.entity.Entity getRealEntity() {
        return this.entity;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        EntityImpl entity1 = (EntityImpl)object;
        return Objects.equals(this.entity, entity1.entity);
    }

    public int hashCode() {
        return this.entity != null ? this.entity.hashCode() : 0;
    }
}
