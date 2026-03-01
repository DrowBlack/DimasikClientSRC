package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;

public class FollowSchoolLeaderGoal
extends Goal {
    private final AbstractGroupFishEntity taskOwner;
    private int navigateTimer;
    private int cooldown;

    public FollowSchoolLeaderGoal(AbstractGroupFishEntity taskOwnerIn) {
        this.taskOwner = taskOwnerIn;
        this.cooldown = this.getNewCooldown(taskOwnerIn);
    }

    protected int getNewCooldown(AbstractGroupFishEntity taskOwnerIn) {
        return 200 + taskOwnerIn.getRNG().nextInt(200) % 20;
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskOwner.isGroupLeader()) {
            return false;
        }
        if (this.taskOwner.hasGroupLeader()) {
            return true;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.cooldown = this.getNewCooldown(this.taskOwner);
        Predicate<AbstractGroupFishEntity> predicate = fish -> fish.canGroupGrow() || !fish.hasGroupLeader();
        List<AbstractGroupFishEntity> list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), this.taskOwner.getBoundingBox().grow(8.0, 8.0, 8.0), predicate);
        AbstractGroupFishEntity abstractgroupfishentity = list.stream().filter(AbstractGroupFishEntity::canGroupGrow).findAny().orElse(this.taskOwner);
        abstractgroupfishentity.func_212810_a(list.stream().filter(fish -> !fish.hasGroupLeader()));
        return this.taskOwner.hasGroupLeader();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.taskOwner.hasGroupLeader() && this.taskOwner.inRangeOfGroupLeader();
    }

    @Override
    public void startExecuting() {
        this.navigateTimer = 0;
    }

    @Override
    public void resetTask() {
        this.taskOwner.leaveGroup();
    }

    @Override
    public void tick() {
        if (--this.navigateTimer <= 0) {
            this.navigateTimer = 10;
            this.taskOwner.moveToGroupLeader();
        }
    }
}
