package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class PiglinMobsSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEAREST_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void update(ServerWorld worldIn, LivingEntity entityIn) {
        Brain<?> brain = entityIn.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, PiglinMobsSensor.findNearestRepellent(worldIn, entityIn));
        Optional<Object> optional = Optional.empty();
        Optional<Object> optional1 = Optional.empty();
        Optional<Object> optional2 = Optional.empty();
        Optional<Object> optional3 = Optional.empty();
        Optional<Object> optional4 = Optional.empty();
        Optional<Object> optional5 = Optional.empty();
        Optional<Object> optional6 = Optional.empty();
        int i = 0;
        ArrayList<AbstractPiglinEntity> list = Lists.newArrayList();
        ArrayList<AbstractPiglinEntity> list1 = Lists.newArrayList();
        for (LivingEntity livingentity : (List)brain.getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(ImmutableList.of())) {
            if (livingentity instanceof HoglinEntity) {
                HoglinEntity hoglinentity = (HoglinEntity)livingentity;
                if (hoglinentity.isChild() && !optional2.isPresent()) {
                    optional2 = Optional.of(hoglinentity);
                    continue;
                }
                if (!hoglinentity.func_234363_eJ_()) continue;
                ++i;
                if (optional1.isPresent() || !hoglinentity.func_234365_eM_()) continue;
                optional1 = Optional.of(hoglinentity);
                continue;
            }
            if (livingentity instanceof PiglinBruteEntity) {
                list.add((PiglinBruteEntity)livingentity);
                continue;
            }
            if (livingentity instanceof PiglinEntity) {
                PiglinEntity piglinentity = (PiglinEntity)livingentity;
                if (piglinentity.isChild() && !optional3.isPresent()) {
                    optional3 = Optional.of(piglinentity);
                    continue;
                }
                if (!piglinentity.func_242337_eM()) continue;
                list.add(piglinentity);
                continue;
            }
            if (livingentity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)livingentity;
                if (!optional5.isPresent() && EntityPredicates.CAN_HOSTILE_AI_TARGET.test(livingentity) && !PiglinTasks.func_234460_a_(playerentity)) {
                    optional5 = Optional.of(playerentity);
                }
                if (optional6.isPresent() || playerentity.isSpectator() || !PiglinTasks.func_234482_b_(playerentity)) continue;
                optional6 = Optional.of(playerentity);
                continue;
            }
            if (optional.isPresent() || !(livingentity instanceof WitherSkeletonEntity) && !(livingentity instanceof WitherEntity)) {
                if (optional4.isPresent() || !PiglinTasks.func_234459_a_(livingentity.getType())) continue;
                optional4 = Optional.of(livingentity);
                continue;
            }
            optional = Optional.of((MobEntity)livingentity);
        }
        for (LivingEntity livingentity1 : (List)brain.getMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of())) {
            if (!(livingentity1 instanceof AbstractPiglinEntity) || !((AbstractPiglinEntity)livingentity1).func_242337_eM()) continue;
            list1.add((AbstractPiglinEntity)livingentity1);
        }
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional1);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional2);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional4);
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional5);
        brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional6);
        brain.setMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS, list1);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
    }

    private static Optional<BlockPos> findNearestRepellent(ServerWorld world, LivingEntity livingEntity) {
        return BlockPos.getClosestMatchingPosition(livingEntity.getPosition(), 8, 4, pos -> PiglinMobsSensor.isRepellent(world, pos));
    }

    private static boolean isRepellent(ServerWorld world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        boolean flag = blockstate.isIn(BlockTags.PIGLIN_REPELLENTS);
        return flag && blockstate.isIn(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLit(blockstate) : flag;
    }
}
