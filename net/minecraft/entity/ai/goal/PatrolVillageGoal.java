package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class PatrolVillageGoal
extends RandomWalkingGoal {
    public PatrolVillageGoal(CreatureEntity creature, double speed) {
        super(creature, speed, 240, false);
    }

    @Override
    @Nullable
    protected Vector3d getPosition() {
        Vector3d vector3d;
        float f = this.creature.world.rand.nextFloat();
        if (this.creature.world.rand.nextFloat() < 0.3f) {
            return this.func_234031_j_();
        }
        if (f < 0.7f) {
            vector3d = this.func_234032_k_();
            if (vector3d == null) {
                vector3d = this.func_234033_l_();
            }
        } else {
            vector3d = this.func_234033_l_();
            if (vector3d == null) {
                vector3d = this.func_234032_k_();
            }
        }
        return vector3d == null ? this.func_234031_j_() : vector3d;
    }

    @Nullable
    private Vector3d func_234031_j_() {
        return RandomPositionGenerator.getLandPos(this.creature, 10, 7);
    }

    @Nullable
    private Vector3d func_234032_k_() {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        List<VillagerEntity> list = serverworld.getEntitiesWithinAABB(EntityType.VILLAGER, this.creature.getBoundingBox().grow(32.0), this::canSpawnGolems);
        if (list.isEmpty()) {
            return null;
        }
        VillagerEntity villagerentity = list.get(this.creature.world.rand.nextInt(list.size()));
        Vector3d vector3d = villagerentity.getPositionVec();
        return RandomPositionGenerator.func_234133_a_(this.creature, 10, 7, vector3d);
    }

    @Nullable
    private Vector3d func_234033_l_() {
        SectionPos sectionpos = this.func_234034_m_();
        if (sectionpos == null) {
            return null;
        }
        BlockPos blockpos = this.func_234029_a_(sectionpos);
        return blockpos == null ? null : RandomPositionGenerator.func_234133_a_(this.creature, 10, 7, Vector3d.copyCenteredHorizontally(blockpos));
    }

    @Nullable
    private SectionPos func_234034_m_() {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        List list = SectionPos.getAllInBox(SectionPos.from(this.creature), 2).filter(p_234030_1_ -> serverworld.sectionsToVillage((SectionPos)p_234030_1_) == 0).collect(Collectors.toList());
        return list.isEmpty() ? null : (SectionPos)list.get(serverworld.rand.nextInt(list.size()));
    }

    @Nullable
    private BlockPos func_234029_a_(SectionPos p_234029_1_) {
        ServerWorld serverworld = (ServerWorld)this.creature.world;
        PointOfInterestManager pointofinterestmanager = serverworld.getPointOfInterestManager();
        List list = pointofinterestmanager.func_219146_b(p_234027_0_ -> true, p_234029_1_.getCenter(), 8, PointOfInterestManager.Status.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
        return list.isEmpty() ? null : (BlockPos)list.get(serverworld.rand.nextInt(list.size()));
    }

    private boolean canSpawnGolems(VillagerEntity villager) {
        return villager.canSpawnGolems(this.creature.world.getGameTime());
    }
}
