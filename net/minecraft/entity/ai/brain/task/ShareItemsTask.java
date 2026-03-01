package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.server.ServerWorld;

public class ShareItemsTask
extends Task<VillagerEntity> {
    private Set<Item> field_220588_a = ImmutableSet.of();

    public ShareItemsTask() {
        super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
        return BrainUtil.isCorrectVisibleType(owner.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        return this.shouldExecute(worldIn, entityIn);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        VillagerEntity villagerentity = (VillagerEntity)entityIn.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        BrainUtil.lookApproachEachOther(entityIn, villagerentity, 0.5f);
        this.field_220588_a = ShareItemsTask.func_220585_a(entityIn, villagerentity);
    }

    @Override
    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
        VillagerEntity villagerentity = (VillagerEntity)owner.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (!(owner.getDistanceSq(villagerentity) > 5.0)) {
            BrainUtil.lookApproachEachOther(owner, villagerentity, 0.5f);
            owner.func_242368_a(worldIn, villagerentity, gameTime);
            if (owner.canAbondonItems() && (owner.getVillagerData().getProfession() == VillagerProfession.FARMER || villagerentity.wantsMoreFood())) {
                ShareItemsTask.func_220586_a(owner, VillagerEntity.FOOD_VALUES.keySet(), villagerentity);
            }
            if (villagerentity.getVillagerData().getProfession() == VillagerProfession.FARMER && owner.getVillagerInventory().count(Items.WHEAT) > Items.WHEAT.getMaxStackSize() / 2) {
                ShareItemsTask.func_220586_a(owner, ImmutableSet.of(Items.WHEAT), villagerentity);
            }
            if (!this.field_220588_a.isEmpty() && owner.getVillagerInventory().hasAny(this.field_220588_a)) {
                ShareItemsTask.func_220586_a(owner, this.field_220588_a, villagerentity);
            }
        }
    }

    @Override
    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        entityIn.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
    }

    private static Set<Item> func_220585_a(VillagerEntity p_220585_0_, VillagerEntity p_220585_1_) {
        ImmutableSet<Item> immutableset = p_220585_1_.getVillagerData().getProfession().getSpecificItems();
        ImmutableSet<Item> immutableset1 = p_220585_0_.getVillagerData().getProfession().getSpecificItems();
        return immutableset.stream().filter(p_220587_1_ -> !immutableset1.contains(p_220587_1_)).collect(Collectors.toSet());
    }

    private static void func_220586_a(VillagerEntity p_220586_0_, Set<Item> p_220586_1_, LivingEntity p_220586_2_) {
        Inventory inventory = p_220586_0_.getVillagerInventory();
        ItemStack itemstack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            int j;
            Item item;
            ItemStack itemstack1 = inventory.getStackInSlot(i);
            if (itemstack1.isEmpty() || !p_220586_1_.contains(item = itemstack1.getItem())) continue;
            if (itemstack1.getCount() > itemstack1.getMaxStackSize() / 2) {
                j = itemstack1.getCount() / 2;
            } else {
                if (itemstack1.getCount() <= 24) continue;
                j = itemstack1.getCount() - 24;
            }
            itemstack1.shrink(j);
            itemstack = new ItemStack(item, j);
            break;
        }
        if (!itemstack.isEmpty()) {
            BrainUtil.spawnItemNearEntity(p_220586_0_, itemstack, p_220586_2_.getPositionVec());
        }
    }
}
