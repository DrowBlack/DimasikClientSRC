package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.vector.Vector3d;

public class EntityPosWrapper
implements IPosWrapper {
    private final Entity entity;
    private final boolean eyePos;

    public EntityPosWrapper(Entity entity, boolean eyePos) {
        this.entity = entity;
        this.eyePos = eyePos;
    }

    @Override
    public Vector3d getPos() {
        return this.eyePos ? this.entity.getPositionVec().add(0.0, this.entity.getEyeHeight(), 0.0) : this.entity.getPositionVec();
    }

    @Override
    public BlockPos getBlockPos() {
        return this.entity.getPosition();
    }

    @Override
    public boolean isVisibleTo(LivingEntity entity) {
        if (!(this.entity instanceof LivingEntity)) {
            return true;
        }
        Optional<List<LivingEntity>> optional = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
    }

    public String toString() {
        return "EntityTracker for " + String.valueOf(this.entity);
    }
}
