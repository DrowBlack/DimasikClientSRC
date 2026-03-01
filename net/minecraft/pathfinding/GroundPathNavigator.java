package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.PathType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class GroundPathNavigator
extends PathNavigator {
    private boolean shouldAvoidSun;

    public GroundPathNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Override
    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }

    @Override
    protected boolean canNavigate() {
        return this.entity.isOnGround() || this.isInLiquid() || this.entity.isPassenger();
    }

    @Override
    protected Vector3d getEntityPosition() {
        return new Vector3d(this.entity.getPosX(), this.getPathablePosY(), this.entity.getPosZ());
    }

    @Override
    public Path getPathToPos(BlockPos pos, int p_179680_2_) {
        if (this.world.getBlockState(pos).isAir()) {
            BlockPos blockpos = pos.down();
            while (blockpos.getY() > 0 && this.world.getBlockState(blockpos).isAir()) {
                blockpos = blockpos.down();
            }
            if (blockpos.getY() > 0) {
                return super.getPathToPos(blockpos.up(), p_179680_2_);
            }
            while (blockpos.getY() < this.world.getHeight() && this.world.getBlockState(blockpos).isAir()) {
                blockpos = blockpos.up();
            }
            pos = blockpos;
        }
        if (!this.world.getBlockState(pos).getMaterial().isSolid()) {
            return super.getPathToPos(pos, p_179680_2_);
        }
        BlockPos blockpos1 = pos.up();
        while (blockpos1.getY() < this.world.getHeight() && this.world.getBlockState(blockpos1).getMaterial().isSolid()) {
            blockpos1 = blockpos1.up();
        }
        return super.getPathToPos(blockpos1, p_179680_2_);
    }

    @Override
    public Path getPathToEntity(Entity entityIn, int p_75494_2_) {
        return this.getPathToPos(entityIn.getPosition(), p_75494_2_);
    }

    private int getPathablePosY() {
        if (this.entity.isInWater() && this.getCanSwim()) {
            int i = MathHelper.floor(this.entity.getPosY());
            Block block = this.world.getBlockState(new BlockPos(this.entity.getPosX(), (double)i, this.entity.getPosZ())).getBlock();
            int j = 0;
            while (block == Blocks.WATER) {
                block = this.world.getBlockState(new BlockPos(this.entity.getPosX(), (double)(++i), this.entity.getPosZ())).getBlock();
                if (++j <= 16) continue;
                return MathHelper.floor(this.entity.getPosY());
            }
            return i;
        }
        return MathHelper.floor(this.entity.getPosY() + 0.5);
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.shouldAvoidSun) {
            if (this.world.canSeeSky(new BlockPos(this.entity.getPosX(), this.entity.getPosY() + 0.5, this.entity.getPosZ()))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
                PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
                if (!this.world.canSeeSky(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z))) continue;
                this.currentPath.setCurrentPathLength(i);
                return;
            }
        }
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor(posVec31.x);
        int j = MathHelper.floor(posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 < 1.0E-8) {
            return false;
        }
        double d3 = 1.0 / Math.sqrt(d2);
        if (!this.isSafeToStandAt(i, MathHelper.floor(posVec31.y), j, sizeX += 2, sizeY, sizeZ += 2, posVec31, d0 *= d3, d1 *= d3)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        double d4 = 1.0 / Math.abs(d0);
        double d5 = 1.0 / Math.abs(d1);
        double d6 = (double)i - posVec31.x;
        double d7 = (double)j - posVec31.z;
        if (d0 >= 0.0) {
            d6 += 1.0;
        }
        if (d1 >= 0.0) {
            d7 += 1.0;
        }
        d6 /= d0;
        d7 /= d1;
        int k = d0 < 0.0 ? -1 : 1;
        int l = d1 < 0.0 ? -1 : 1;
        int i1 = MathHelper.floor(posVec32.x);
        int j1 = MathHelper.floor(posVec32.z);
        int k1 = i1 - i;
        int l1 = j1 - j;
        while (k1 * k > 0 || l1 * l > 0) {
            if (d6 < d7) {
                d6 += d4;
                k1 = i1 - (i += k);
            } else {
                d7 += d5;
                l1 = j1 - (j += l);
            }
            if (this.isSafeToStandAt(i, MathHelper.floor(posVec31.y), j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) continue;
            return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;
        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        }
        for (int k = i; k < i + sizeX; ++k) {
            for (int l = j; l < j + sizeZ; ++l) {
                double d0 = (double)k + 0.5 - vec31.x;
                double d1 = (double)l + 0.5 - vec31.z;
                if (d0 * p_179683_8_ + d1 * p_179683_10_ < 0.0) continue;
                PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                if (!this.func_230287_a_(pathnodetype)) {
                    return false;
                }
                pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                float f = this.entity.getPathPriority(pathnodetype);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (pathnodetype != PathNodeType.DAMAGE_FIRE && pathnodetype != PathNodeType.DANGER_FIRE && pathnodetype != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean func_230287_a_(PathNodeType p_230287_1_) {
        if (p_230287_1_ == PathNodeType.WATER) {
            return false;
        }
        if (p_230287_1_ == PathNodeType.LAVA) {
            return false;
        }
        return p_230287_1_ != PathNodeType.OPEN;
    }

    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            double d1;
            double d0 = (double)blockpos.getX() + 0.5 - p_179692_7_.x;
            if (d0 * p_179692_8_ + (d1 = (double)blockpos.getZ() + 0.5 - p_179692_7_.z) * p_179692_10_ < 0.0 || this.world.getBlockState(blockpos).allowsMovement(this.world, blockpos, PathType.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setBreakDoors(boolean canBreakDoors) {
        this.nodeProcessor.setCanOpenDoors(canBreakDoors);
    }

    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }

    public void setAvoidSun(boolean avoidSun) {
        this.shouldAvoidSun = avoidSun;
    }
}
