package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal
extends Goal {
    private final AnimalEntity childAnimal;
    private AnimalEntity parentAnimal;
    private final double moveSpeed;
    private int delayCounter;

    public FollowParentGoal(AnimalEntity animal, double speed) {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    @Override
    public boolean shouldExecute() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        }
        List<?> list = this.childAnimal.world.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getBoundingBox().grow(8.0, 4.0, 8.0));
        AnimalEntity animalentity = null;
        double d0 = Double.MAX_VALUE;
        for (AnimalEntity animalentity1 : list) {
            double d1;
            if (animalentity1.getGrowingAge() < 0 || (d1 = this.childAnimal.getDistanceSq(animalentity1)) > d0) continue;
            d0 = d1;
            animalentity = animalentity1;
        }
        if (animalentity == null) {
            return false;
        }
        if (d0 < 9.0) {
            return false;
        }
        this.parentAnimal = animalentity;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.childAnimal.getGrowingAge() >= 0) {
            return false;
        }
        if (!this.parentAnimal.isAlive()) {
            return false;
        }
        double d0 = this.childAnimal.getDistanceSq(this.parentAnimal);
        return !(d0 < 9.0) && !(d0 > 256.0);
    }

    @Override
    public void startExecuting() {
        this.delayCounter = 0;
    }

    @Override
    public void resetTask() {
        this.parentAnimal = null;
    }

    @Override
    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
        }
    }
}
