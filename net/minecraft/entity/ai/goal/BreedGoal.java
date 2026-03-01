package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BreedGoal
extends Goal {
    private static final EntityPredicate field_220689_d = new EntityPredicate().setDistance(8.0).allowInvulnerable().allowFriendlyFire().setLineOfSiteRequired();
    protected final AnimalEntity animal;
    private final Class<? extends AnimalEntity> mateClass;
    protected final World world;
    protected AnimalEntity targetMate;
    private int spawnBabyDelay;
    private final double moveSpeed;

    public BreedGoal(AnimalEntity animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public BreedGoal(AnimalEntity animal, double moveSpeed, Class<? extends AnimalEntity> mateClass) {
        this.animal = animal;
        this.world = animal.world;
        this.mateClass = mateClass;
        this.moveSpeed = moveSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.targetMate = this.getNearbyMate();
        return this.targetMate != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    @Override
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookController().setLookPositionWithEntity(this.targetMate, 10.0f, this.animal.getVerticalFaceSpeed());
        this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0) {
            this.spawnBaby();
        }
    }

    @Nullable
    private AnimalEntity getNearbyMate() {
        List<? extends AnimalEntity> list = this.world.getTargettableEntitiesWithinAABB(this.mateClass, field_220689_d, this.animal, this.animal.getBoundingBox().grow(8.0));
        double d0 = Double.MAX_VALUE;
        AnimalEntity animalentity = null;
        for (AnimalEntity animalEntity : list) {
            if (!this.animal.canMateWith(animalEntity) || !(this.animal.getDistanceSq(animalEntity) < d0)) continue;
            animalentity = animalEntity;
            d0 = this.animal.getDistanceSq(animalEntity);
        }
        return animalentity;
    }

    protected void spawnBaby() {
        this.animal.func_234177_a_((ServerWorld)this.world, this.targetMate);
    }
}
