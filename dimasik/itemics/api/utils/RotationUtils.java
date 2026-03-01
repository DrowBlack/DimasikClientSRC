package dimasik.itemics.api.utils;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.api.utils.RayTraceUtils;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.VecUtils;
import java.util.Optional;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;

public final class RotationUtils {
    public static final double DEG_TO_RAD = Math.PI / 180;
    public static final double RAD_TO_DEG = 57.29577951308232;
    private static final Vector3d[] BLOCK_SIDE_MULTIPLIERS = new Vector3d[]{new Vector3d(0.5, 0.0, 0.5), new Vector3d(0.5, 1.0, 0.5), new Vector3d(0.5, 0.5, 0.0), new Vector3d(0.5, 0.5, 1.0), new Vector3d(0.0, 0.5, 0.5), new Vector3d(1.0, 0.5, 0.5)};

    private RotationUtils() {
    }

    public static Rotation calcRotationFromCoords(BlockPos orig, BlockPos dest) {
        return RotationUtils.calcRotationFromVec3d(new Vector3d(orig.getX(), orig.getY(), orig.getZ()), new Vector3d(dest.getX(), dest.getY(), dest.getZ()));
    }

    public static Rotation wrapAnglesToRelative(Rotation current, Rotation target) {
        if (current.yawIsReallyClose(target)) {
            return new Rotation(current.getYaw(), target.getPitch());
        }
        return target.subtract(current).normalize().add(current);
    }

    public static Rotation calcRotationFromVec3d(Vector3d orig, Vector3d dest, Rotation current) {
        return RotationUtils.wrapAnglesToRelative(current, RotationUtils.calcRotationFromVec3d(orig, dest));
    }

    private static Rotation calcRotationFromVec3d(Vector3d orig, Vector3d dest) {
        double[] delta = new double[]{orig.x - dest.x, orig.y - dest.y, orig.z - dest.z};
        double yaw = MathHelper.atan2(delta[0], -delta[2]);
        double dist = Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
        double pitch = MathHelper.atan2(delta[1], dist);
        return new Rotation((float)(yaw * 57.29577951308232), (float)(pitch * 57.29577951308232));
    }

    public static Vector3d calcVector3dFromRotation(Rotation rotation) {
        float f = MathHelper.cos(-rotation.getYaw() * ((float)Math.PI / 180) - (float)Math.PI);
        float f1 = MathHelper.sin(-rotation.getYaw() * ((float)Math.PI / 180) - (float)Math.PI);
        float f2 = -MathHelper.cos(-rotation.getPitch() * ((float)Math.PI / 180));
        float f3 = MathHelper.sin(-rotation.getPitch() * ((float)Math.PI / 180));
        return new Vector3d(f1 * f2, f3, f * f2);
    }

    public static Optional<Rotation> reachable(IPlayerContext ctx, BlockPos pos) {
        return RotationUtils.reachable(ctx.player(), pos, ctx.playerController().getBlockReachDistance());
    }

    public static Optional<Rotation> reachable(IPlayerContext ctx, BlockPos pos, boolean wouldSneak) {
        return RotationUtils.reachable(ctx.player(), pos, ctx.playerController().getBlockReachDistance(), wouldSneak);
    }

    public static Optional<Rotation> reachable(ClientPlayerEntity entity, BlockPos pos, double blockReachDistance) {
        return RotationUtils.reachable(entity, pos, blockReachDistance, false);
    }

    public static Optional<Rotation> reachable(ClientPlayerEntity entity, BlockPos pos, double blockReachDistance, boolean wouldSneak) {
        Optional<Rotation> possibleRotation;
        IItemics itemics = ItemicsAPI.getProvider().getItemicsForPlayer(entity);
        if (itemics.getPlayerContext().isLookingAt(pos)) {
            Rotation hypothetical = new Rotation(entity.packetYaw, entity.packetPitch + 1.0E-4f);
            if (wouldSneak) {
                RayTraceResult result = RayTraceUtils.rayTraceTowards(entity, hypothetical, blockReachDistance, true);
                if (result != null && result.getType() == RayTraceResult.Type.BLOCK && ((BlockRayTraceResult)result).getPos().equals(pos)) {
                    return Optional.of(hypothetical);
                }
            } else {
                return Optional.of(hypothetical);
            }
        }
        if ((possibleRotation = RotationUtils.reachableCenter(entity, pos, blockReachDistance, wouldSneak)).isPresent()) {
            return possibleRotation;
        }
        BlockState state = entity.world.getBlockState(pos);
        VoxelShape shape = state.getShape(entity.world, pos);
        if (shape.isEmpty()) {
            shape = VoxelShapes.fullCube();
        }
        for (Vector3d sideOffset : BLOCK_SIDE_MULTIPLIERS) {
            double xDiff = shape.getStart(Direction.Axis.X) * sideOffset.x + shape.getEnd(Direction.Axis.X) * (1.0 - sideOffset.x);
            double yDiff = shape.getStart(Direction.Axis.Y) * sideOffset.y + shape.getEnd(Direction.Axis.Y) * (1.0 - sideOffset.y);
            double zDiff = shape.getStart(Direction.Axis.Z) * sideOffset.z + shape.getEnd(Direction.Axis.Z) * (1.0 - sideOffset.z);
            possibleRotation = RotationUtils.reachableOffset(entity, pos, new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(xDiff, yDiff, zDiff), blockReachDistance, wouldSneak);
            if (!possibleRotation.isPresent()) continue;
            return possibleRotation;
        }
        return Optional.empty();
    }

    public static Optional<Rotation> reachableOffset(Entity entity, BlockPos pos, Vector3d offsetPos, double blockReachDistance, boolean wouldSneak) {
        Vector3d eyes = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getEyePosition(1.0f);
        Rotation rotation = RotationUtils.calcRotationFromVec3d(eyes, offsetPos, new Rotation(entity.rotationYaw, entity.rotationPitch));
        RayTraceResult result = RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, wouldSneak);
        if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
            if (((BlockRayTraceResult)result).getPos().equals(pos)) {
                return Optional.of(rotation);
            }
            if (entity.world.getBlockState(pos).getBlock() instanceof AbstractFireBlock && ((BlockRayTraceResult)result).getPos().equals(pos.down())) {
                return Optional.of(rotation);
            }
        }
        return Optional.empty();
    }

    public static Optional<Rotation> reachableCenter(Entity entity, BlockPos pos, double blockReachDistance, boolean wouldSneak) {
        return RotationUtils.reachableOffset(entity, pos, VecUtils.calculateBlockCenter(entity.world, pos), blockReachDistance, wouldSneak);
    }
}
