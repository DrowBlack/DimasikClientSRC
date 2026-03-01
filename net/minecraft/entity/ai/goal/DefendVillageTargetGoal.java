package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class DefendVillageTargetGoal
extends TargetGoal {
    private final IronGolemEntity irongolem;
    private LivingEntity villageAgressorTarget;
    private final EntityPredicate distancePredicate = new EntityPredicate().setDistance(64.0);

    public DefendVillageTargetGoal(IronGolemEntity ironGolemIn) {
        super(ironGolemIn, false, true);
        this.irongolem = ironGolemIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        AxisAlignedBB axisalignedbb = this.irongolem.getBoundingBox().grow(10.0, 8.0, 10.0);
        List<VillagerEntity> list = this.irongolem.world.getTargettableEntitiesWithinAABB(VillagerEntity.class, this.distancePredicate, this.irongolem, axisalignedbb);
        List<PlayerEntity> list1 = this.irongolem.world.getTargettablePlayersWithinAABB(this.distancePredicate, this.irongolem, axisalignedbb);
        for (LivingEntity livingEntity : list) {
            VillagerEntity villagerentity = (VillagerEntity)livingEntity;
            for (PlayerEntity playerentity : list1) {
                int i = villagerentity.getPlayerReputation(playerentity);
                if (i > -100) continue;
                this.villageAgressorTarget = playerentity;
            }
        }
        if (this.villageAgressorTarget == null) {
            return false;
        }
        return !(this.villageAgressorTarget instanceof PlayerEntity) || !this.villageAgressorTarget.isSpectator() && !((PlayerEntity)this.villageAgressorTarget).isCreative();
    }

    @Override
    public void startExecuting() {
        this.irongolem.setAttackTarget(this.villageAgressorTarget);
        super.startExecuting();
    }
}
