package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class StartAdmiringItemTask<E extends PiglinEntity>
extends Task<E> {
    public StartAdmiringItemTask() {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, E owner) {
        return !((LivingEntity)owner).getHeldItemOffhand().isEmpty() && ((LivingEntity)owner).getHeldItemOffhand().getItem() != Items.SHIELD;
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
        PiglinTasks.func_234477_a_(entityIn, true);
    }
}
