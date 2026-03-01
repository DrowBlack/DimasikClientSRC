package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingApproachPhase
extends Phase {
    private static final EntityPredicate field_221118_b = new EntityPredicate().setDistance(128.0);
    private Path currentPath;
    private Vector3d targetLocation;

    public LandingApproachPhase(EnderDragonEntity dragonIn) {
        super(dragonIn);
    }

    public PhaseType<LandingApproachPhase> getType() {
        return PhaseType.LANDING_APPROACH;
    }

    @Override
    public void initPhase() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Override
    public void serverTick() {
        double d0;
        double d = d0 = this.targetLocation == null ? 0.0 : this.targetLocation.squareDistanceTo(this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ());
        if (d0 < 100.0 || d0 > 22500.0 || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
            this.findNewTarget();
        }
    }

    @Override
    @Nullable
    public Vector3d getTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isFinished()) {
            int j;
            int i = this.dragon.initPathPoints();
            BlockPos blockpos = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
            PlayerEntity playerentity = this.dragon.world.getClosestPlayer(field_221118_b, blockpos.getX(), blockpos.getY(), blockpos.getZ());
            if (playerentity != null) {
                Vector3d vector3d = new Vector3d(playerentity.getPosX(), 0.0, playerentity.getPosZ()).normalize();
                j = this.dragon.getNearestPpIdx(-vector3d.x * 40.0, 105.0, -vector3d.z * 40.0);
            } else {
                j = this.dragon.getNearestPpIdx(40.0, blockpos.getY(), 0.0);
            }
            PathPoint pathpoint = new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.currentPath = this.dragon.findPath(i, j, pathpoint);
            if (this.currentPath != null) {
                this.currentPath.incrementPathIndex();
            }
        }
        this.navigateToNextPathNode();
        if (this.currentPath != null && this.currentPath.isFinished()) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
        }
    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            double d2;
            BlockPos vector3i = this.currentPath.func_242948_g();
            this.currentPath.incrementPathIndex();
            double d0 = vector3i.getX();
            double d1 = vector3i.getZ();
            while ((d2 = (double)((float)vector3i.getY() + this.dragon.getRNG().nextFloat() * 20.0f)) < (double)vector3i.getY()) {
            }
            this.targetLocation = new Vector3d(d0, d2, d1);
        }
    }
}
