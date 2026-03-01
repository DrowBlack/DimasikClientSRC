package dimasik.helpers.module.aura;

import dimasik.Load;
import dimasik.events.main.input.EventMoveInput;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.module.aura.RayTrace;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.modules.combat.AntiBot;
import dimasik.modules.combat.Aura;
import dimasik.modules.combat.ElytraTarget;
import dimasik.utils.math.GCDUtils;
import dimasik.utils.math.MathUtils;
import dimasik.utils.math.RandomNumberUtils;
import dimasik.utils.player.MoveUtils;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import lombok.Generated;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class AuraHelpers
implements IFastAccess {
    public final RandomNumberUtils randomX = new RandomNumberUtils(1.0f, 12.0f, 0.1f);
    public final RandomNumberUtils randomY = new RandomNumberUtils(1.0f, 7.0f, 0.1f);
    int tick;
    float smoothedHead = 0.0f;
    boolean smoothness = true;
    private static float distance;
    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils snapBypass = new TimerUtils();
    TimerUtils axyenno;
    private final TimerUtils stopWatch = new TimerUtils();
    public boolean attacksave = false;
    private double speed = 0.0;
    private double prevSpeed = 0.0;
    boolean axyeenn = false;

    public LivingEntity sortEntities(LivingEntity target, float distance, float preDistance, MultiOption targets) {
        if (AuraHelpers.mc.player.isElytraFlying() && target != null && target.isAlive()) {
            return target;
        }
        List<LivingEntity> validTargets = this.validEntities(distance, preDistance, targets);
        return validTargets.stream().min(Comparator.comparing(e -> e != target).thenComparingDouble(e -> e.getDistanceSq(AuraHelpers.mc.player)).thenComparingInt(Entity::getEntityId)).orElse(null);
    }

    private List<LivingEntity> validEntities(float distance, float preDistance, MultiOption targets) {
        ArrayList<LivingEntity> validTargets = new ArrayList<LivingEntity>();
        String onlyTarget = Load.getInstance().getHooks().getTargetManagers().isActive() ? Load.getInstance().getHooks().getTargetManagers().getName() : null;
        for (Entity entity : AuraHelpers.mc.world.getAllEntities()) {
            PlayerEntity playerEntity;
            LivingEntity living;
            PlayerEntity playerEntity2;
            if (onlyTarget != null && (!(entity instanceof PlayerEntity) || !(playerEntity2 = (PlayerEntity)entity).getGameProfile().getName().equalsIgnoreCase(onlyTarget))) continue;
            boolean player = targets.getSelected("Players");
            boolean mobs = targets.getSelected("Mobs");
            boolean naked = targets.getSelected("Naked");
            boolean friends = targets.getSelected("Friends");
            boolean creative = targets.getSelected("Creative");
            if (!(entity instanceof LivingEntity) || !((double)(living = (LivingEntity)entity).getHealth() > 0.0)) continue;
            if (entity instanceof PlayerEntity) {
                playerEntity = (PlayerEntity)entity;
                if (entity != AuraHelpers.mc.world.getEntityByID(1337)) {
                    boolean isCreative;
                    boolean isFriend = !Load.getInstance().getHooks().getFriendManagers().is(playerEntity.getGameProfile().getName()) || friends;
                    boolean isNaked = playerEntity.getTotalArmorValue() > 0 || naked;
                    boolean bl = isCreative = !playerEntity.isCreative() || creative;
                    if (this.isValidEntities(playerEntity, distance, preDistance) && isFriend && isNaked && isCreative && player) {
                        validTargets.add(playerEntity);
                    }
                }
            }
            if (entity instanceof PlayerEntity) {
                playerEntity = (PlayerEntity)entity;
                if (friends && Load.getInstance().getHooks().getFriendManagers().is(playerEntity.getGameProfile().getName()) && this.isValidEntities(playerEntity, distance, preDistance)) {
                    validTargets.add(playerEntity);
                }
            }
            if (!(entity instanceof MobEntity)) continue;
            MobEntity mobEntity = (MobEntity)entity;
            if (!mobs || !this.isValidEntities(mobEntity, distance, preDistance)) continue;
            validTargets.add(mobEntity);
        }
        return validTargets;
    }

    private boolean isValidEntities(LivingEntity entity, float distance, float preDistance) {
        if (AntiBot.checkBot(entity)) {
            return false;
        }
        if (entity == AuraHelpers.mc.player) {
            return false;
        }
        return AuraHelpers.mc.player.getDistance(entity) <= distance + preDistance;
    }

    public boolean attack(LivingEntity entity, MultiOption option, float distance, boolean criticals, boolean flags) {
        return this.canAttack(entity, option, distance, criticals, flags);
    }

    private boolean canAttack(LivingEntity entity, MultiOption option, float distance, boolean criticals, boolean flags) {
        boolean negative = AuraHelpers.mc.player.isPotionActive(Effects.LEVITATION) || AuraHelpers.mc.player.isPotionActive(Effects.SLOW_FALLING) || AuraHelpers.mc.player.isPotionActive(Effects.BLINDNESS) || AuraHelpers.mc.player.isPassenger() || AuraHelpers.mc.player.isOnLadder();
        Aura aura = Load.getInstance().getHooks().getModuleManagers().getAura();
        boolean waterCrits = AuraHelpers.mc.player.isInWater() && !AuraHelpers.mc.gameSettings.keyBindJump.isKeyDown();
        boolean ragelikbypas = aura.options.getSelected("Only Jump") && AuraHelpers.mc.player.isOnGround() && !AuraHelpers.mc.gameSettings.keyBindJump.isKeyDown();
        boolean flag = !criticals || AuraHelpers.mc.player.areEyesInFluid(FluidTags.WATER) || AuraHelpers.mc.player.isInLava() || waterCrits || ragelikbypas || negative || AuraHelpers.mc.player.getBlockState().isIn(Blocks.COBWEB) || AuraHelpers.mc.player.abilities.isFlying || AuraHelpers.mc.player.isRidingHorse() || AuraHelpers.mc.player.fallDistance > 0.0f && !AuraHelpers.mc.player.isInWater() && !AuraHelpers.mc.player.isOnGround() && !AuraHelpers.mc.player.isOnLadder() && !AuraHelpers.mc.player.isPassenger();
        boolean flag2 = AuraHelpers.mc.player.getDistance(entity) <= distance;
        boolean flag3 = !AuraHelpers.mc.player.isHandActive() || AuraHelpers.mc.player.isHandActive() && AuraHelpers.mc.player.getActiveHand() == Hand.OFF_HAND && AuraHelpers.mc.player.getHeldItemOffhand().getItem() instanceof ShieldItem || !option.getSelected("Unpress Shield");
        Vector2f selfRotation = Load.getInstance().getHooks().getModuleManagers().getAura().selfRotation;
        boolean flag4 = RayTrace.blockResult(distance, selfRotation.x, selfRotation.y, AuraHelpers.mc.player).getType() != RayTraceResult.Type.BLOCK || !option.getSelected("Dont Hit Walls");
        boolean flag5 = RayTrace.getMouseOver(entity, selfRotation.x, selfRotation.y, ((Float)Load.getInstance().getHooks().getModuleManagers().getAura().getDistance().getValue()).floatValue()) == entity || !Load.getInstance().getHooks().getModuleManagers().getAura().getMode().getSelected("AI");
        return (double)AuraHelpers.mc.player.getCooledAttackStrength(0.5f) > (double)0.9f + (flags ? Math.random() * 0.12 : 0.0) && flag && flag2 && flag3 && flag4 && flag5;
    }

    public void shieldBreaker(LivingEntity target) {
        if (target instanceof PlayerEntity) {
            int slot;
            PlayerEntity player = (PlayerEntity)target;
            if (!(!target.isActiveItemStackBlocking() || player.isSpectator() || player.isCreative() || target.getHeldItemOffhand().getItem() != Items.SHIELD && target.getHeldItemMainhand().getItem() != Items.SHIELD || (slot = this.findShield(player)) <= 8)) {
                AuraHelpers.mc.playerController.pickItem(slot);
            }
        }
    }

    private int findShield(LivingEntity target) {
        int hotBarSlot = this.getAxe(true);
        if (hotBarSlot != -1) {
            AuraHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
            AuraHelpers.mc.playerController.attackEntity(AuraHelpers.mc.player, target);
            AuraHelpers.mc.player.swingArm(Hand.MAIN_HAND);
            AuraHelpers.mc.player.connection.sendPacket(new CHeldItemChangePacket(AuraHelpers.mc.player.inventory.currentItem));
            return hotBarSlot;
        }
        int inventorySLot = this.getAxe(false);
        if (inventorySLot != -1) {
            AuraHelpers.mc.playerController.pickItem(inventorySLot);
            AuraHelpers.mc.playerController.attackEntity(AuraHelpers.mc.player, target);
            AuraHelpers.mc.player.swingArm(Hand.MAIN_HAND);
            return inventorySLot;
        }
        return -1;
    }

    private int getAxe(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = AuraHelpers.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof AxeItem)) continue;
            return i;
        }
        return -1;
    }

    public boolean sprint() {
        return this.canSprint();
    }

    private boolean canSprint() {
        boolean flag = AuraHelpers.mc.player.getBlockState().isIn(Blocks.COBWEB) || AuraHelpers.mc.player.abilities.isFlying || MoveUtils.isInLiquid() || AuraHelpers.mc.player.isRidingHorse() || AuraHelpers.mc.player.fallDistance <= 0.0f && AuraHelpers.mc.player.isOnGround();
        return flag;
    }

    public Vector2f applyRotation(Vector2f rotation) {
        return new Vector2f(GCDUtils.getFixedRotation(rotation.x), GCDUtils.getFixedRotation(rotation.y));
    }

    public Vector2f fastRotation(Vector2f selfRotation, Vector2f targetRotation) {
        float pitch = targetRotation.y;
        double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        double deltaPitch = pitch - selfRotation.y;
        float moveYaw = (float)Math.max(Math.min(deltaYaw, Math.abs(deltaYaw)), -Math.abs(deltaYaw));
        float movePitch = (float)Math.max(Math.min(deltaPitch, Math.abs(deltaPitch)), -Math.abs(deltaPitch));
        float yaw = selfRotation.x + moveYaw;
        pitch = selfRotation.y + movePitch;
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch + (!AuraHelpers.mc.player.isElytraFlying() ? this.getRandom(this.randomY.getCurrent()) : 0.0f)));
        return new Vector2f(yaw + (!AuraHelpers.mc.player.isElytraFlying() ? this.getRandom(this.randomX.getCurrent()) : 0.0f), pitch);
    }

    public Vector2f test(Vector2f selfRotation, Vector2f targetRotation) {
        float pitch = targetRotation.y;
        double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        double deltaPitch = pitch - selfRotation.y;
        float moveYaw = (float)Math.max(Math.min(deltaYaw, Math.abs(deltaYaw)), -Math.abs(deltaYaw));
        float movePitch = (float)Math.max(Math.min(deltaPitch, Math.abs(deltaPitch)), -Math.abs(deltaPitch));
        float yaw = selfRotation.x + moveYaw;
        pitch = selfRotation.y + movePitch;
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f fakeRotation(Vector2f selfRotation, Vector2f targetRotation) {
        float pitch = targetRotation.y;
        double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        double deltaPitch = pitch - selfRotation.y;
        float moveYaw = (float)Math.max(Math.min(deltaYaw, Math.abs(deltaYaw)), -Math.abs(deltaYaw));
        float movePitch = (float)Math.max(Math.min(deltaPitch, Math.abs(deltaPitch)), -Math.abs(deltaPitch));
        float yaw = selfRotation.x + moveYaw + 5.0f;
        pitch = selfRotation.y + movePitch + 5.0f;
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f snapRotation(Vector2f selfRotation, Vector2f targetRotation, int ticks) {
        float pitch = targetRotation.y;
        float lastYaw = selfRotation.x;
        float lastPitch = selfRotation.y;
        double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        double deltaPitch = pitch - lastPitch;
        float moveYaw = (float)Math.max(Math.min(deltaYaw, Math.abs(deltaYaw)), -Math.abs(deltaYaw));
        float movePitch = (float)Math.max(Math.min(deltaPitch, Math.abs(deltaPitch)), -Math.abs(deltaPitch));
        float yaw = lastYaw + moveYaw;
        pitch = lastPitch + movePitch;
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        if (AuraHelpers.mc.player.fallDistance > 0.0f && !AuraHelpers.mc.player.isOnGround()) {
            this.tick = ticks;
        }
        if (this.tick > 0) {
            --this.tick;
        }
        return this.tick > 0 ? new Vector2f(yaw, pitch) : new Vector2f(AuraHelpers.mc.player.rotationYaw + this.getRandom(25.0f), AuraHelpers.mc.player.rotationPitch);
    }

    public Vector2f snapSmoothRotation(Vector2f selfRotation, Vector2f targetRotation, int ticks) {
        float deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        float deltaPitch = MathHelper.wrapDegrees(targetRotation.y - selfRotation.y);
        float deltaTime = 0.8f;
        float yaw = selfRotation.x + deltaYaw * deltaTime;
        float pitch = selfRotation.y + deltaPitch * deltaTime;
        deltaTime -= 0.3f;
        if (AuraHelpers.mc.player.fallDistance > 0.0f && !AuraHelpers.mc.player.isOnGround()) {
            this.tick = ticks;
        }
        if (this.tick > 0) {
            --this.tick;
        } else {
            yaw = selfRotation.x + (AuraHelpers.mc.player.rotationYaw - selfRotation.x) * deltaTime;
            pitch = MathHelper.clamp(selfRotation.y + (AuraHelpers.mc.player.rotationPitch - selfRotation.y) * deltaTime, -90.0f, 90.0f);
        }
        int limit = 10;
        if (this.smoothedHead > (float)limit) {
            this.smoothness = true;
        }
        if (this.smoothedHead < (float)(-limit)) {
            this.smoothness = false;
        }
        this.smoothedHead = this.smoothness ? (this.smoothedHead -= 3.0f) : (this.smoothedHead += 3.0f);
        return new Vector2f(yaw + this.smoothedHead, pitch);
    }

    public Vector2f smoothRotation(Vector2f selfRotation, Vector2f targetRotation, float lerpSpeed) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        float lastYaw = selfRotation.x;
        float lastPitch = selfRotation.y;
        if (lerpSpeed != 0.0f) {
            double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
            double deltaPitch = pitch - lastPitch;
            double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            double distributionYaw = Math.abs(deltaYaw / distance);
            double distributionPitch = Math.abs(deltaPitch / distance);
            double maxYaw = (double)lerpSpeed * distributionYaw;
            double maxPitch = (double)lerpSpeed * distributionPitch;
            float moveYaw = (float)Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            float movePitch = (float)Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);
            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;
            for (int i = 1; i <= (int)((double)((float)Minecraft.getDebugFPS() / 20.0f) + Math.random() * 10.0); ++i) {
                if (Math.abs(moveYaw) + Math.abs(movePitch) > 1.0f) {
                    yaw += (float)((Math.random() - 0.5) / 1000.0);
                    pitch -= (float)(Math.random() / 200.0);
                }
                Vector2f fixedRotations = new Vector2f(yaw, pitch);
                yaw = fixedRotations.x;
                pitch = Math.max(-90.0f, Math.min(90.0f, fixedRotations.y));
            }
        }
        return new Vector2f(yaw, pitch);
    }

    public Vector2f legendsRotation(Vector2f selfRotation, Vector2f targetRotation) {
        float pitch = targetRotation.y;
        float lastYaw = selfRotation.x;
        float lastPitch = selfRotation.y;
        double deltaYaw = MathHelper.wrapDegrees(targetRotation.x - selfRotation.x);
        double deltaPitch = pitch - lastPitch;
        double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
        double distributionYaw = Math.abs(deltaYaw / distance);
        double distributionPitch = Math.abs(deltaPitch / distance);
        double maxYaw = (double)(160.0f + new Random().nextFloat(5.0f)) * distributionYaw;
        double maxPitch = (double)(160.0f + new Random().nextFloat(5.0f)) * distributionPitch;
        float moveYaw = (float)Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
        float movePitch = (float)Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);
        float yaw = lastYaw + moveYaw;
        pitch = lastPitch + movePitch;
        for (int i = 1; i <= (int)((double)((float)Minecraft.getDebugFPS() / 20.0f) + Math.random() * 20.0); ++i) {
            if (Math.abs(moveYaw) + Math.abs(movePitch) > 1.0f) {
                yaw += (float)((Math.random() - 0.5) / 900.0);
                pitch -= (float)(Math.random() / 100.0);
            }
            Vector2f fixedRotations = new Vector2f(yaw, pitch);
            yaw = fixedRotations.x;
            pitch = Math.max(-90.0f, Math.min(90.0f, fixedRotations.y));
        }
        return new Vector2f(yaw, pitch);
    }

    public Vector2f aimBotRotation(Vector2f selfRotation, Vector2f targetRotation) {
        float yawSpeed = 0.6f;
        float pitchSpeed = 0.5f;
        float smoothYaw = this.smoothRotation(selfRotation.x, targetRotation.x, yawSpeed);
        float smoothPitch = this.smoothRotation(selfRotation.y, targetRotation.y, pitchSpeed);
        return new Vector2f(smoothYaw, smoothPitch);
    }

    private float smoothRotation(float current, float target, float factor) {
        float delta = MathHelper.wrapDegrees(target - current);
        return current + delta * factor;
    }

    public Vector2f rotationAngles(LivingEntity target, boolean forward, ElytraTarget elytraTarget) {
        if (this.timer.hasTimeElapsed(200L)) {
            double dx = target.getPosX() - target.prevPosX;
            double dy = target.getPosY() - target.prevPosY;
            double dz = target.getPosZ() - target.prevPosZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            this.prevSpeed = this.speed;
            this.speed = distance * 20.0;
            this.timer.reset();
        }
        Vector3d playerEyes = AuraHelpers.mc.player.getEyePosition(1.0f);
        boolean anti = this.speed >= 20.0 || this.speed != this.prevSpeed && this.speed == 0.0;
        Vector3d closestPoint = this.pointBoundingBox(playerEyes, this.smoothedBoundingBox(target, mc.getRenderPartialTicks()));
        Vector3d entityPos = target.getPositionVec().add(0.0, (double)target.getHeight() / 2.0, 0.0);
        Vector3d direction = entityPos.subtract(playerEyes).normalize();
        float yaw = (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float)Math.toDegrees(Math.asin(direction.y));
        return new Vector2f(yaw, -pitch);
    }

    public Vector2f fakeRotationAngles(LivingEntity target) {
        Vector3d playerEyes = AuraHelpers.mc.player.getEyePosition(1.0f);
        Vector3d entityPos = target.getPositionVec().add(0.0, (double)target.getHeight() / 2.0, 0.0);
        Vector3d direction = entityPos.subtract(playerEyes).normalize();
        float yaw = (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float)Math.toDegrees(Math.asin(direction.y));
        return new Vector2f(yaw, -pitch);
    }

    private Vector3d pointBoundingBox(Vector3d point, AxisAlignedBB boundingBox) {
        double x = MathUtils.clamp(point.x, boundingBox.minX, boundingBox.maxX);
        double y = MathUtils.clamp(point.y, boundingBox.minY, boundingBox.maxY);
        double z = MathUtils.clamp(point.z, boundingBox.minZ, boundingBox.maxZ);
        return new Vector3d(x, y, z);
    }

    private AxisAlignedBB smoothedBoundingBox(LivingEntity player, float delta) {
        Vector3d lerpedPos = player.func_242282_l(delta);
        AxisAlignedBB originalBoundingBox = player.getBoundingBox();
        Vector3d offset = lerpedPos.subtract(player.getPosX(), player.getPosY(), player.getPosZ());
        return originalBoundingBox.offset(offset);
    }

    public void fixMovement(EventMoveInput event, float yaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = MathHelper.wrapDegrees(Math.toDegrees(this.direction(AuraHelpers.mc.player.isElytraFlying() ? yaw : AuraHelpers.mc.player.rotationYaw, forward, strafe)));
        if (forward == 0.0f && strafe == 0.0f) {
            return;
        }
        float closestForward = 0.0f;
        float closestStrafe = 0.0f;
        float closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
            for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                double predictedAngle;
                double difference;
                if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(this.direction(yaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                closestDifference = (float)difference;
                closestForward = predictedForward;
                closestStrafe = predictedStrafe;
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    private double direction(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    @Generated
    public static float getDistance() {
        return distance;
    }

    @Generated
    public static void setDistance(float distance) {
        AuraHelpers.distance = distance;
    }

    @Generated
    public TimerUtils getSnapBypass() {
        return this.snapBypass;
    }
}
