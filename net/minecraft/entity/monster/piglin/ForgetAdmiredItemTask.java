package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.world.server.ServerWorld;

public class ForgetAdmiredItemTask<E extends PiglinEntity>
extends Task<E> {
    private final int field_234541_b_;

    public ForgetAdmiredItemTask(int p_i231574_1_) {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.REGISTERED));
        this.field_234541_b_ = p_i231574_1_;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, E owner) {
        if (!((LivingEntity)owner).getHeldItemOffhand().isEmpty()) {
            return false;
        }
        Optional<ItemEntity> optional = ((PiglinEntity)owner).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (!optional.isPresent()) {
            return true;
        }
        return !optional.get().isEntityInRange((Entity)owner, this.field_234541_b_);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
        ((PiglinEntity)entityIn).getBrain().removeMemory(MemoryModuleType.ADMIRING_ITEM);
    }
}
