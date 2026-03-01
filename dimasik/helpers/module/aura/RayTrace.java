package dimasik.helpers.module.aura;

import dimasik.helpers.interfaces.IFastAccess;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class RayTrace
implements IFastAccess {
    public static Entity getMouseOver(Entity target, float yaw, float pitch, double distance) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && RayTrace.mc.world != null) {
            AxisAlignedBB axisalignedbb;
            RayTraceResult objectMouseOver = null;
            boolean flag = distance > 3.0;
            Vector3d startVec = entity.getEyePosition(1.0f);
            Vector3d directionVec = RayTrace.getVectorForRotation(pitch, yaw);
            Vector3d endVec = startVec.add(directionVec.x * distance, directionVec.y * distance, directionVec.z * distance);
            EntityRayTraceResult entityraytraceresult = RayTrace.rayTraceEntities(entity, startVec, endVec, axisalignedbb = target.getBoundingBox().grow(target.getCollisionBorderSize()), p_lambda$getMouseOver$0_0_ -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith(), distance);
            if (entityraytraceresult != null) {
                if (flag && startVec.distanceTo(startVec) > distance) {
                    objectMouseOver = BlockRayTraceResult.createMiss(startVec, null, new BlockPos(startVec));
                }
                if (distance < distance || objectMouseOver == null) {
                    objectMouseOver = entityraytraceresult;
                }
            }
            if (objectMouseOver == null) {
                return null;
            }
            try {
                return ((EntityRayTraceResult)objectMouseOver).getEntity();
            }
            catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        double closestDistance = distance;
        Entity entity = null;
        Vector3d closestHitVec = null;
        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (!(closestDistance >= 0.0)) continue;
                entity = entity1;
                closestHitVec = startVec;
                closestDistance = 0.0;
                continue;
            }
            if (!optional.isPresent()) continue;
            Vector3d vector3d1 = optional.get();
            double d3 = startVec.distanceTo(optional.get());
            if (!(d3 < closestDistance) && closestDistance != 0.0) continue;
            boolean flag1 = false;
            if (!flag1 && entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                if (closestDistance != 0.0) continue;
                entity = entity1;
                closestHitVec = vector3d1;
                continue;
            }
            entity = entity1;
            closestHitVec = vector3d1;
            closestDistance = d3;
        }
        return entity == null ? null : new EntityRayTraceResult(entity, closestHitVec);
    }

    public static RayTraceResult rayTrace(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        Vector3d startVec = RayTrace.mc.player.getEyePosition(1.0f);
        Vector3d directionVec = RayTrace.getVectorForRotation(pitch, yaw);
        Vector3d endVec = startVec.add(directionVec.x * rayTraceDistance, directionVec.y * rayTraceDistance, directionVec.z * rayTraceDistance);
        return RayTrace.mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static RayTraceResult rayTraceResult(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        RayTraceResult object = null;
        if (entity != null && RayTrace.mc.world != null) {
            float partialTicks = mc.getRenderPartialTicks();
            double distance = rayTraceDistance;
            object = RayTrace.rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            double d1 = distance;
            if (RayTrace.mc.playerController.extendedReach()) {
                distance = d1 = 6.0;
            } else if (distance > 3.0) {
                flag = true;
            }
            d1 *= d1;
            if (object != null) {
                d1 = object.getHitVec().squareDistanceTo(vector3d);
            }
            Vector3d vector3d1 = RayTrace.getVectorForRotation(pitch, yaw);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0f;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(distance)).grow(1.0, 1.0, 1.0);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, p_lambda$getMouseOver$0_0_ -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith(), d1);
            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vector3d vector3d3 = entityraytraceresult.getHitVec();
                double d2 = vector3d.squareDistanceTo(vector3d3);
                if (flag && d2 > 9.0) {
                    object = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                } else if (d2 < d1 || object == null) {
                    object = entityraytraceresult;
                }
            }
        }
        return object;
    }

    public static RayTraceResult blockResult(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        RayTraceResult object = null;
        if (entity != null && RayTrace.mc.world != null) {
            float partialTicks = mc.getRenderPartialTicks();
            double distance = rayTraceDistance;
            object = RayTrace.rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            double d1 = distance;
            if (RayTrace.mc.playerController.extendedReach()) {
                distance = d1 = 6.0;
            }
            d1 *= d1;
            if (object != null) {
                d1 = object.getHitVec().squareDistanceTo(vector3d);
            }
            Vector3d vector3d1 = RayTrace.getVectorForRotation(pitch, yaw);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0f;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(distance)).grow(1.0, 1.0, 1.0);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, p_lambda$getMouseOver$0_0_ -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith(), d1);
            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vector3d vector3d3 = entityraytraceresult.getHitVec();
                double d2 = vector3d.squareDistanceTo(vector3d3);
                object = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
            }
        }
        return object;
    }

    public static boolean rayTraceWithBlock(double rayTraceDistance, float yaw, float pitch, Entity entity, Entity target) {
        RayTraceResult object = RayTrace.rayTraceResult(rayTraceDistance, yaw, pitch, entity);
        if (object instanceof EntityRayTraceResult) {
            return ((EntityRayTraceResult)object).getEntity().getEntityId() == target.getEntityId();
        }
        return false;
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

    @Generated
    private RayTrace() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
