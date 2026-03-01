package dimasik.utils.math;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.utils.math.CustRandom;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public final class MathUtils
extends MathHelper
implements IFastAccess {
    public static float interpolate(float prev, float to, float value) {
        return prev + (to - prev) * value;
    }

    public static Double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static Vector3d interpolate(Vector3d end, Vector3d start, float multiple) {
        return new Vector3d(MathUtils.interpolate(end.getX(), start.getX(), (double)multiple), MathUtils.interpolate(end.getY(), start.getY(), (double)multiple), MathUtils.interpolate(end.getZ(), start.getZ(), (double)multiple));
    }

    public static double normalize(double scale, double prev, double last) {
        return prev + scale * (last - prev);
    }

    public static float normalize(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static float step(float current, float target, float step) {
        float difference = target - current;
        if (Math.abs(difference) <= step) {
            return target;
        }
        return current + Math.signum(difference) * step;
    }

    public static double random(double min, double max) {
        return MathUtils.interpolate(max, min, (double)((float)Math.random()));
    }

    public static float random(float min, float max) {
        return (float)(Math.random() * (double)(max - min) + (double)min);
    }

    public static float random1(float min, float max) {
        return new CustRandom().randomNumber(0.0f, 1.0f, false) * (max - min) + min;
    }

    public static double round(double num, double increment) {
        double v = (double)Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double getBps(Entity entity, int decimal) {
        double x = entity.getPosX() - entity.prevPosX;
        double y = entity.getPosY() - entity.prevPosY;
        double z = entity.getPosZ() - entity.prevPosZ;
        double speed = Math.sqrt(x * x + y * y + z * z) * 20.0;
        return MathHelper.roundHalfUp(speed, decimal);
    }

    public static double getScale(Vector3d position, double size) {
        Vector3d cam = MathUtils.mc.getRenderManager().info.getProjectedView();
        double distance = cam.distanceTo(position);
        double fov = MathUtils.mc.gameRenderer.getFOVModifier(MathUtils.mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        double scale = (double)((float)Math.max(1.0, 1000.0 / distance)) * (size / 25.0);
        scale /= (double)((float)(fov == 70.0 ? 1.0 : fov / 70.0));
        if (distance > 50.0) {
            scale *= distance / 50.0;
        }
        return Math.min(scale, 2.0);
    }

    public static boolean isRangeVector(Vector2f aura, float range) {
        if (MathUtils.mc.player != null) {
            return MathUtils.wrapDegrees(MathUtils.mc.player.rotationYaw) <= MathUtils.wrapDegrees(aura.x) + range && MathUtils.wrapDegrees(MathUtils.mc.player.rotationYaw) >= MathUtils.wrapDegrees(aura.x) - range && MathUtils.mc.player.rotationPitch <= aura.y + range && MathUtils.mc.player.rotationPitch >= aura.y - range;
        }
        return false;
    }

    public static boolean canSeen(Vector3d vec) {
        Vector3d vector3d = MathUtils.mc.player.getPositionVec().add(0.0, MathUtils.mc.player.getEyeHeight(), 0.0);
        return MathUtils.mc.world.rayTraceBlocks(new RayTraceContext(vector3d, vec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, MathUtils.mc.player)).getType() == RayTraceResult.Type.MISS;
    }

    public static float randomInRange(float min, float max) {
        return (float)(Math.random() * (double)(max - min)) + min;
    }

    public static int randomInRange(int min, int max) {
        return (int)(Math.random() * (double)(max - min)) + min;
    }

    public static void scaleElements(float xCenter, float yCenter, float scale, Runnable runnable) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(xCenter, yCenter, 0.0f);
        RenderSystem.scalef(scale, scale, 1.0f);
        RenderSystem.translatef(-xCenter, -yCenter, 0.0f);
        runnable.run();
        RenderSystem.popMatrix();
    }

    public static Vector2f rotationToEntity(Entity target) {
        Vector3d vector3d = target.getPositionVec().subtract(Minecraft.getInstance().player.getPositionVec());
        double magnitude = Math.hypot(vector3d.x, vector3d.z);
        return new Vector2f((float)Math.toDegrees(Math.atan2(vector3d.z, vector3d.x)) - 90.0f, (float)(-Math.toDegrees(Math.atan2(vector3d.y, magnitude))));
    }

    public static boolean isYawRange(float yaw, float value, float radius) {
        float globalYaw = MathHelper.wrapDegrees(yaw);
        if (globalYaw >= 0.0f && globalYaw <= 90.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= 90.0f && globalYaw <= 180.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 90.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= -180.0f && globalYaw <= -90.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 180.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        if (globalYaw >= -90.0f && globalYaw <= 0.0f) {
            float max;
            float yaws = MathHelper.wrapDegrees(yaw - 270.0f);
            float min = MathHelper.wrapDegrees(value - radius);
            if (min < (max = MathHelper.wrapDegrees(value + radius))) {
                return yaws >= min && yaws <= max;
            }
            return yaws >= min || yaws <= max;
        }
        return false;
    }

    public static boolean clump(double value, double one, double too) {
        return value > one && value < too;
    }

    @Generated
    private MathUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
