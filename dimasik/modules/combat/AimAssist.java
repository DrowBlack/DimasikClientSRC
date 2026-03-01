package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Easing;
import dimasik.helpers.animation.GhostAnim;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.math.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class AimAssist
extends Module {
    private final MultiOption targets = new MultiOption("\u0426\u0435\u043b\u0438", new MultiOptionValue("\u0418\u0433\u0440\u043e\u043a\u0438", true), new MultiOptionValue("\u0413\u043e\u043b\u044b\u0435", true));
    private final CheckboxOption saveTarget = new CheckboxOption("\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0446\u0435\u043b\u044c", true);
    private final SliderOption fov = new SliderOption("\u041f\u043e\u043b\u0435 \u0437\u0440\u0435\u043d\u0438\u044f", 90.0f, 1.0f, 180.0f).increment(1.0f);
    private final CheckboxOption fovCircle = new CheckboxOption("\u041a\u0440\u0443\u0433 FOV'a", false).visible(() -> ((Float)this.fov.getValue()).floatValue() <= 90.0f);
    private final CheckboxOption rayAim = new CheckboxOption("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u043e\u0432\u043e\u0434\u043a\u0430", false);
    private final CheckboxOption verticalAim = new CheckboxOption("\u041d\u0430\u0432\u043e\u0434\u043a\u0430 \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438", true);
    private final SliderOption speed = new SliderOption("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 2.0f, 1.0f, 10.0f).increment(0.1f);
    private final SliderOption maxDistance = new SliderOption("\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 4.0f, 1.0f, 12.0f).increment(0.1f);
    private float yaw;
    private float pitch;
    private LivingEntity target = null;
    GhostAnim anim = new GhostAnim().setSpeed(170).setSize(1.0f).setEasing(Easing.EASE_IN_OUT_QUART);
    private final EventListener<EventUpdate> update = this::onUpdate;
    private final EventListener<EventRender2D.Post> onRender2D = this::onRender2D;

    public AimAssist() {
        super("AimAssist", Category.COMBAT);
        this.settings(this.targets, this.saveTarget, this.fov, this.fovCircle, this.rayAim, this.verticalAim, this.speed, this.maxDistance);
    }

    public void onUpdate(EventUpdate event) {
        LivingEntity foundTarget = this.findTarget();
        if ((foundTarget == null || this.target == null || !AimAssist.mc.world.getAllEntities().contains(this.target) || this.target.isDead()) && !((Boolean)this.saveTarget.getValue()).booleanValue() || this.target == null && ((Boolean)this.saveTarget.getValue()).booleanValue()) {
            this.target = foundTarget;
        }
        if (this.target == null || this.getFOV(this.target) > ((Float)this.fov.getValue()).floatValue() || AimAssist.mc.player.getDistance(this.target) > ((Float)this.maxDistance.getValue()).floatValue()) {
            this.yaw = AimAssist.mc.player.rotationYaw;
            this.pitch = AimAssist.mc.player.rotationPitch;
            return;
        }
        this.yaw = AimAssist.mc.player.rotationYaw;
        this.pitch = AimAssist.mc.player.rotationPitch;
        Vector3d pos = AimAssist.getBestVector(this.target, 0.0f);
        float shortestYawPath = (float)(((Math.toDegrees(Math.atan2(pos.z, pos.x)) - 90.0 - (double)this.yaw) % 360.0 + 540.0) % 360.0 - 180.0);
        float targetYaw = this.yaw + shortestYawPath;
        float targetPitch = (float)(-Math.toDegrees(Math.atan2(pos.y, Math.hypot(pos.x, pos.z))));
        this.yaw = this.fixDelta((float)this.yaw, (float)this.pitch).x;
        this.pitch = this.fixDelta((float)this.yaw, (float)this.pitch).y;
        this.yaw = MathUtils.interpolate(this.yaw, targetYaw, this.getAIRotationSpeed());
        this.pitch = (Boolean)this.verticalAim.getValue() != false ? MathUtils.interpolate(this.pitch, targetPitch, 0.05f) : AimAssist.mc.player.rotationPitch;
        Vector2f corrected = this.correctRotation(this.yaw, this.pitch);
        this.yaw = corrected.x;
        this.pitch = corrected.y;
        AimAssist.mc.player.rotationYaw = this.yaw;
        AimAssist.mc.player.rotationPitch = this.pitch;
    }

    public void onRender2D(EventRender2D.Post event) {
        if (((Boolean)this.fovCircle.getValue()).booleanValue() && ((Float)this.fov.getValue()).floatValue() <= 90.0f) {
            VisualHelpers.drawCircle((float)event.getMainWindow().getScaledWidth() / 2.0f, (float)event.getMainWindow().getScaledHeight() / 2.0f, 0.0f, 360.0f, ((Float)this.fov.getValue()).floatValue() * 2.0f, -16711936, 2.0f);
        }
    }

    private float getAIRotationSpeed() {
        return ((Float)this.speed.getValue()).floatValue() / 30.0f;
    }

    private float getFOV(LivingEntity entity) {
        double dx = entity.getPosX() - AimAssist.mc.player.getPosX();
        double dz = entity.getPosZ() - AimAssist.mc.player.getPosZ();
        float yawToTarget = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        return Math.abs(MathUtils.wrapDegrees(yawToTarget - AimAssist.mc.player.rotationYaw));
    }

    private LivingEntity findTarget() {
        LivingEntity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        float maxFov = ((Float)this.fov.getValue()).floatValue();
        float maxDist = ((Float)this.maxDistance.getValue()).floatValue();
        for (Entity ent : AimAssist.mc.world.getAllEntities()) {
            double dist;
            LivingEntity living;
            if (!(ent instanceof LivingEntity) || (living = (LivingEntity)ent) == AimAssist.mc.player || living.isDead() || !(living instanceof PlayerEntity) && !(living instanceof MobEntity) || (dist = (double)AimAssist.mc.player.getDistance(living)) > (double)maxDist) continue;
            double dx = living.getPosX() - AimAssist.mc.player.getPosX();
            double dz = living.getPosZ() - AimAssist.mc.player.getPosZ();
            float yawToTarget = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
            float yawDiff = Math.abs(MathUtils.wrapDegrees(yawToTarget - AimAssist.mc.player.rotationYaw));
            if (yawDiff > maxFov || !(dist < bestDistance)) continue;
            bestDistance = dist;
            bestTarget = living;
        }
        return bestTarget;
    }

    private Vector2f fixDelta(float yaw, float pitch) {
        return new Vector2f(yaw, pitch);
    }

    private Vector2f correctRotation(float yaw, float pitch) {
        return new Vector2f(yaw, pitch);
    }

    public static Vector3d getBestVector(LivingEntity target, float jitterOnBoxValue) {
        double yExpand = MathHelper.clamp(AimAssist.mc.player.getPosYEye() - target.getPosYEye(), (double)(target.getHeight() / 2.0f), (double)target.getHeight()) / (double)(AimAssist.mc.player.isElytraFlying() ? 10.0f : (!AimAssist.mc.gameSettings.keyBindJump.isKeyDown() && AimAssist.mc.player.isOnGround() ? (target.isSneaking() ? 0.8f : 0.6f) : 1.0f));
        Vector3d finalVector = target.getPositionVec().add(0.0, yExpand, 0.0);
        return finalVector.add(jitterOnBoxValue, jitterOnBoxValue / 2.0f, jitterOnBoxValue).subtract(AimAssist.mc.player.getEyePosition(1.0f)).normalize();
    }
}
