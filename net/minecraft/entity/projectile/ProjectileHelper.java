package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class ProjectileHelper {
    public static RayTraceResult func_234618_a_(Entity p_234618_0_, Predicate<Entity> p_234618_1_) {
        EntityRayTraceResult raytraceresult1;
        Vector3d vector3d2;
        Vector3d vector3d = p_234618_0_.getMotion();
        World world = p_234618_0_.world;
        Vector3d vector3d1 = p_234618_0_.getPositionVec();
        RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vector3d1, vector3d2 = vector3d1.add(vector3d), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_234618_0_));
        if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) {
            vector3d2 = raytraceresult.getHitVec();
        }
        if ((raytraceresult1 = ProjectileHelper.rayTraceEntities(world, p_234618_0_, vector3d1, vector3d2, p_234618_0_.getBoundingBox().expand(p_234618_0_.getMotion()).grow(1.0), p_234618_1_)) != null) {
            raytraceresult = raytraceresult1;
        }
        return raytraceresult;
    }

    @Nullable
    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        double d0 = distance;
        Entity entity = null;
        Vector3d vector3d = null;
        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
            Vector3d vector3d1;
            double d1;
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (!(d0 >= 0.0)) continue;
                entity = entity1;
                vector3d = optional.orElse(startVec);
                d0 = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((d1 = startVec.squareDistanceTo(vector3d1 = optional.get())) < d0) && d0 != 0.0) continue;
            if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                if (d0 != 0.0) continue;
                entity = entity1;
                vector3d = vector3d1;
                continue;
            }
            entity = entity1;
            vector3d = vector3d1;
            d0 = d1;
        }
        return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
    }

    @Nullable
    public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        for (Entity entity1 : worldIn.getEntitiesInAABBexcluding(projectile, boundingBox, filter)) {
            double d1;
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(0.3f);
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (!optional.isPresent() || !((d1 = startVec.squareDistanceTo(optional.get())) < d0)) continue;
            entity = entity1;
            d0 = d1;
        }
        return entity == null ? null : new EntityRayTraceResult(entity);
    }

    public static final void rotateTowardsMovement(Entity projectile, float rotationSpeed) {
        Vector3d vector3d = projectile.getMotion();
        if (vector3d.lengthSquared() != 0.0) {
            float f = MathHelper.sqrt(Entity.horizontalMag(vector3d));
            projectile.rotationYaw = (float)(MathHelper.atan2(vector3d.z, vector3d.x) * 57.2957763671875) + 90.0f;
            projectile.rotationPitch = (float)(MathHelper.atan2(f, vector3d.y) * 57.2957763671875) - 90.0f;
            while (projectile.rotationPitch - projectile.prevRotationPitch < -180.0f) {
                projectile.prevRotationPitch -= 360.0f;
            }
            while (projectile.rotationPitch - projectile.prevRotationPitch >= 180.0f) {
                projectile.prevRotationPitch += 360.0f;
            }
            while (projectile.rotationYaw - projectile.prevRotationYaw < -180.0f) {
                projectile.prevRotationYaw -= 360.0f;
            }
            while (projectile.rotationYaw - projectile.prevRotationYaw >= 180.0f) {
                projectile.prevRotationYaw += 360.0f;
            }
            projectile.rotationPitch = MathHelper.lerp(rotationSpeed, projectile.prevRotationPitch, projectile.rotationPitch);
            projectile.rotationYaw = MathHelper.lerp(rotationSpeed, projectile.prevRotationYaw, projectile.rotationYaw);
        }
    }

    public static Hand getHandWith(LivingEntity living, Item itemIn) {
        return living.getHeldItemMainhand().getItem() == itemIn ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static AbstractArrowEntity fireArrow(LivingEntity shooter, ItemStack arrowStack, float distanceFactor) {
        ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(shooter.world, arrowStack, shooter);
        abstractarrowentity.setEnchantmentEffectsFromEntity(shooter, distanceFactor);
        if (arrowStack.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity) {
            ((ArrowEntity)abstractarrowentity).setPotionEffect(arrowStack);
        }
        return abstractarrowentity;
    }
}
