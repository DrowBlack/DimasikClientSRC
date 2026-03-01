package dimasik.itemics.api.utils;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class VecUtils {
    private VecUtils() {
    }

    public static Vector3d calculateBlockCenter(World world, BlockPos pos) {
        BlockState b = world.getBlockState(pos);
        VoxelShape shape = b.getCollisionShape(world, pos);
        if (shape.isEmpty()) {
            return VecUtils.getBlockPosCenter(pos);
        }
        double xDiff = (shape.getStart(Direction.Axis.X) + shape.getEnd(Direction.Axis.X)) / 2.0;
        double yDiff = (shape.getStart(Direction.Axis.Y) + shape.getEnd(Direction.Axis.Y)) / 2.0;
        double zDiff = (shape.getStart(Direction.Axis.Z) + shape.getEnd(Direction.Axis.Z)) / 2.0;
        if (Double.isNaN(xDiff) || Double.isNaN(yDiff) || Double.isNaN(zDiff)) {
            throw new IllegalStateException(String.valueOf(b) + " " + String.valueOf(pos) + " " + String.valueOf(shape));
        }
        if (b.getBlock() instanceof AbstractFireBlock) {
            yDiff = 0.0;
        }
        return new Vector3d((double)pos.getX() + xDiff, (double)pos.getY() + yDiff, (double)pos.getZ() + zDiff);
    }

    public static Vector3d getBlockPosCenter(BlockPos pos) {
        return new Vector3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    public static double distanceToCenter(BlockPos pos, double x, double y, double z) {
        double xdiff = (double)pos.getX() + 0.5 - x;
        double ydiff = (double)pos.getY() + 0.5 - y;
        double zdiff = (double)pos.getZ() + 0.5 - z;
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff + zdiff * zdiff);
    }

    public static double entityDistanceToCenter(Entity entity, BlockPos pos) {
        return VecUtils.distanceToCenter(pos, entity.getPositionVec().x, entity.getPositionVec().y, entity.getPositionVec().z);
    }

    public static double entityFlatDistanceToCenter(Entity entity, BlockPos pos) {
        return VecUtils.distanceToCenter(pos, entity.getPositionVec().x, (double)pos.getY() + 0.5, entity.getPositionVec().z);
    }
}
