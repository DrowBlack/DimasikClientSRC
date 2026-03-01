package dimasik.itemics.api.utils;

import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.RotationUtils;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class RayTraceUtils {
    private RayTraceUtils() {
    }

    public static RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance) {
        return RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, false);
    }

    public static RayTraceResult rayTrace(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        Vector3d startVec = Helper.mc.player.getEyePosition(1.0f);
        Vector3d directionVec = RayTraceUtils.getVectorForRotation(pitch, yaw);
        Vector3d endVec = startVec.add(directionVec.x * rayTraceDistance, directionVec.y * rayTraceDistance, directionVec.z * rayTraceDistance);
        return Helper.mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float yawRadians = -yaw * ((float)Math.PI / 180) - (float)Math.PI;
        float pitchRadians = -pitch * ((float)Math.PI / 180);
        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = -MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);
        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }

    public static boolean rayTraceWithBlock(double rayTraceDistance, float yaw, float pitch, Entity entity, Entity target, boolean blocks) {
        RayTraceResult object = null;
        if (target == null) {
            return false;
        }
        if (entity != null && Helper.mc.world != null) {
            float partialTicks = Helper.mc.getRenderPartialTicks();
            double distance = rayTraceDistance;
            object = RayTraceUtils.rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            double d1 = distance;
            d1 *= d1;
            if (object != null) {
                d1 = object.getHitVec().squareDistanceTo(vector3d);
            }
            Vector3d vector3d1 = RayTraceUtils.getVectorForRotation(pitch, yaw);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0f;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(distance)).grow(1.0, 1.0, 1.0);
            boolean traced = RayTraceUtils.tracedTo(entity, vector3d, vector3d2, axisalignedbb, p_lambda$getMouseOver$0_0_ -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith(), d1, target);
            return traced;
        }
        return false;
    }

    public static boolean tracedTo(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance, Entity target) {
        World world = shooter.world;
        double d0 = distance;
        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (!(d0 >= 0.0)) continue;
                if (entity1 == target) {
                    return true;
                }
                d0 = 0.0;
                continue;
            }
            if (!optional.isPresent()) continue;
            Vector3d vector3d1 = optional.get();
            double d1 = startVec.squareDistanceTo(vector3d1);
            if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                if (d0 != 0.0 || entity1 != target) continue;
                return true;
            }
            if (entity1 == target) {
                return true;
            }
            d0 = d1;
        }
        return false;
    }

    public static boolean rayTraceSingleEntity(float yaw, float pitch, double distance, Entity entity) {
        Vector3d eyeVec = Helper.mc.player.getEyePosition(1.0f);
        Vector3d lookVec = Helper.mc.player.getVectorForRotation(pitch, yaw);
        Vector3d extendedVec = eyeVec.add(lookVec.scale(distance));
        AxisAlignedBB AABB = entity.getBoundingBox();
        return AABB.contains(eyeVec) || AABB.rayTrace(eyeVec, extendedVec).isPresent();
    }

    public static RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance, boolean wouldSneak) {
        Vector3d start = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getEyePosition(1.0f);
        Vector3d direction = RotationUtils.calcVector3dFromRotation(rotation);
        Vector3d end = start.add(direction.x * blockReachDistance, direction.y * blockReachDistance, direction.z * blockReachDistance);
        return entity.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static Vector3d inferSneakingEyePosition(Entity entity) {
        return new Vector3d(entity.getPosX(), entity.getPosY() + IPlayerContext.eyeHeight(true), entity.getPosZ());
    }
}
