package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventMoveInput;
import dimasik.events.main.player.EventSync;
import dimasik.helpers.module.aura.AuraHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.time.TimerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class CreeperFarm
extends Module {
    private Entity target;
    private final TimerUtils timerUtil = new TimerUtils();
    private final TimerUtils hubTimer = new TimerUtils();
    private Vector2f rotateVector = new Vector2f(0.0f, 0.0f);
    private boolean needRotate = false;
    private boolean isRunningAway = false;
    private Vector3d runAwayPosition = null;
    private Vector3d lastPosition;
    private long lastMoveTime;
    private boolean isStuck = false;
    private boolean strafingRight = false;
    private boolean hasExploded = false;
    private final AuraHelpers auraHelpers = new AuraHelpers();
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventMoveInput> input = this::input;
    private final EventListener<EventSync> sync = this::sync;
    private boolean isChangingItem;
    private int originalSlot = -1;

    public CreeperFarm() {
        super("CreeperFarm", Category.MISC);
    }

    public void input(EventMoveInput eventMoveInput) {
        if (this.target != null && CreeperFarm.mc.player != null) {
            this.auraHelpers.fixMovement(eventMoveInput, this.rotateVector.x);
        }
    }

    public void update(EventUpdate eventUpdate) {
        if (this.isRunningAway) {
            this.runFromCreeper();
        } else {
            this.updateTarget();
            if (this.target != null) {
                this.processRotationLogic();
                this.moveToTarget();
            } else {
                this.timerUtil.setLastMS(0L);
                this.reset();
            }
        }
        if (this.target instanceof CreeperEntity && !this.isRunningAway && !this.hasExploded && (double)CreeperFarm.mc.player.getDistance(this.target) <= 3.0) {
            this.attack((CreeperEntity)this.target);
        }
    }

    public void sync(EventSync eventSync) {
        if (this.target == null && !this.isRunningAway) {
            return;
        }
        this.setPlayerRotation(eventSync);
    }

    private void moveToTarget() {
        if (this.target == null || CreeperFarm.mc.player == null) {
            return;
        }
        double deltaX = this.target.getPosX() - CreeperFarm.mc.player.getPosX();
        double deltaZ = this.target.getPosZ() - CreeperFarm.mc.player.getPosZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (this.target instanceof ItemEntity) {
            if (distance <= 1.5) {
                this.moveTowards(distance);
            } else {
                this.moveTowards(distance);
            }
            return;
        }
        Entity entity = this.target;
        if (entity instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity)entity;
            if (creeper.getCreeperState() > 2) {
                this.isRunningAway = true;
                this.runAwayPosition = this.getRunAwayPosition(creeper);
                return;
            }
            if (distance <= 6.0) {
                this.attack(creeper);
            }
        }
        this.moveTowards(distance);
    }

    private void moveTowards(double distance) {
        if (distance > 1.5) {
            this.checkStuck();
            if (!CreeperFarm.mc.player.isSprinting()) {
                CreeperFarm.mc.player.setSprinting(true);
            }
            CreeperFarm.mc.gameSettings.keyBindForward.setPressed(true);
            CreeperFarm.mc.gameSettings.keyBindJump.setPressed(true);
            if (this.isStuck) {
                if (this.strafingRight) {
                    CreeperFarm.mc.gameSettings.keyBindRight.setPressed(true);
                    CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(false);
                    this.rotateVector = new Vector2f(CreeperFarm.mc.player.rotationYaw - 45.0f, this.rotateVector.y);
                } else {
                    CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(true);
                    CreeperFarm.mc.gameSettings.keyBindRight.setPressed(false);
                    this.rotateVector = new Vector2f(CreeperFarm.mc.player.rotationYaw + 45.0f, this.rotateVector.y);
                }
                this.needRotate = true;
            } else {
                CreeperFarm.mc.gameSettings.keyBindRight.setPressed(false);
                CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(false);
            }
        } else {
            this.resetKeys();
        }
    }

    private void runFromCreeper() {
        if (this.runAwayPosition == null || CreeperFarm.mc.player == null) {
            this.isRunningAway = false;
            return;
        }
        double deltaX = this.runAwayPosition.x - CreeperFarm.mc.player.getPosX();
        double deltaZ = this.runAwayPosition.z - CreeperFarm.mc.player.getPosZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
        this.rotateVector = new Vector2f(this.smoothRotation(CreeperFarm.mc.player.rotationYaw, yaw, 150.0f), CreeperFarm.mc.player.rotationPitch);
        this.needRotate = true;
        CreeperFarm.mc.player.setSprinting(true);
        CreeperFarm.mc.gameSettings.keyBindJump.setPressed(true);
        if (this.target instanceof CreeperEntity && (double)CreeperFarm.mc.player.getDistance(this.target) <= 3.0) {
            this.attack((CreeperEntity)this.target);
        }
        if (distance < 6.0) {
            this.isRunningAway = false;
            this.runAwayPosition = null;
            this.reset();
            return;
        }
        this.checkStuck();
        if (this.isStuck) {
            if (this.strafingRight) {
                CreeperFarm.mc.gameSettings.keyBindRight.setPressed(true);
                CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(false);
            } else {
                CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(true);
                CreeperFarm.mc.gameSettings.keyBindRight.setPressed(false);
            }
        } else {
            CreeperFarm.mc.gameSettings.keyBindRight.setPressed(false);
            CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(false);
        }
        CreeperFarm.mc.gameSettings.keyBindForward.setPressed(true);
    }

    private void checkStuck() {
        if (CreeperFarm.mc.player == null) {
            return;
        }
        Vector3d currentPos = CreeperFarm.mc.player.getPositionVec();
        long currentTime = System.currentTimeMillis();
        if (this.lastPosition != null) {
            double deltaX = Math.abs(currentPos.x - this.lastPosition.x);
            double deltaZ = Math.abs(currentPos.z - this.lastPosition.z);
            if (currentTime - this.lastMoveTime <= 10L) {
                if (deltaX < 0.01 && deltaZ < 0.01) {
                    this.rotateVector = this.strafingRight ? new Vector2f(CreeperFarm.mc.player.rotationYaw - 45.0f, this.rotateVector.y) : new Vector2f(CreeperFarm.mc.player.rotationYaw + 45.0f, this.rotateVector.y);
                    this.strafingRight = !this.strafingRight;
                }
            } else {
                this.lastMoveTime = currentTime;
                this.lastPosition = currentPos;
            }
        } else {
            this.lastPosition = currentPos;
            this.lastMoveTime = currentTime;
        }
    }

    private Vector3d getRunAwayPosition(CreeperEntity creeper) {
        double deltaZ;
        double deltaX = CreeperFarm.mc.player.getPosX() - creeper.getPosX();
        double length = Math.sqrt(deltaX * deltaX + (deltaZ = CreeperFarm.mc.player.getPosZ() - creeper.getPosZ()) * deltaZ);
        if (length > 0.0) {
            deltaX /= length;
            deltaZ /= length;
        }
        double targetX = creeper.getPosX() + deltaX * 15.0;
        double targetZ = creeper.getPosZ() + deltaZ * 15.0;
        return new Vector3d(targetX, CreeperFarm.mc.player.getPosY(), targetZ);
    }

    private void updateTarget() {
        if (this.isRunningAway) {
            return;
        }
        Entity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;
        for (Entity entity : CreeperFarm.mc.world.getAllEntities()) {
            CreeperEntity creeper;
            double heightDifference;
            if (!(entity instanceof CreeperEntity) && !(entity instanceof ItemEntity) || entity instanceof ItemEntity && !((ItemEntity)entity).getItem().getItem().equals(Items.GUNPOWDER)) continue;
            double distance = CreeperFarm.mc.player.getDistance(entity);
            if (entity instanceof CreeperEntity && ((heightDifference = (creeper = (CreeperEntity)entity).getPosY() - CreeperFarm.mc.player.getPosY()) < -3.0 || heightDifference > 3.0 || !this.isPathClear(creeper)) || !(distance < bestDistance)) continue;
            bestDistance = distance;
            bestTarget = entity;
        }
        this.target = bestTarget;
    }

    private boolean isPathClear(Entity target) {
        Vector3d playerPos = new Vector3d(CreeperFarm.mc.player.getPosX(), CreeperFarm.mc.player.getPosY() + (double)CreeperFarm.mc.player.getEyeHeight(), CreeperFarm.mc.player.getPosZ());
        Vector3d targetPos = new Vector3d(target.getPosX(), target.getPosY() + (double)target.getHeight() * 0.5, target.getPosZ());
        Vector3d direction = targetPos.subtract(playerPos);
        double distance = direction.length();
        direction = direction.normalize();
        BlockRayTraceResult result = CreeperFarm.mc.world.rayTraceBlocks(new RayTraceContext(playerPos, targetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, CreeperFarm.mc.player));
        return result == null || ((RayTraceResult)result).getType() == RayTraceResult.Type.MISS;
    }

    private void processRotationLogic() {
        if (this.target == null || CreeperFarm.mc.player == null) {
            return;
        }
        Vector3d vec = this.target.getPositionVec().add(0.0, (double)this.target.getHeight() * 0.5, 0.0).subtract(CreeperFarm.mc.player.getEyePosition(mc.getRenderPartialTicks()));
        double distance = vec.length();
        vec = vec.normalize();
        float targetYaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float targetPitch = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))), -90.0, 90.0);
        this.rotateVector = new Vector2f(this.smoothRotation(CreeperFarm.mc.player.rotationYaw, targetYaw, 150.0f), this.smoothRotation(CreeperFarm.mc.player.rotationPitch, targetPitch, 150.0f));
        this.needRotate = true;
        if (this.target instanceof CreeperEntity && distance < 3.0) {
            this.attack((CreeperEntity)this.target);
        }
    }

    private void setPlayerRotation(EventSync event) {
        if (!this.needRotate) {
            return;
        }
        event.setYaw(this.rotateVector.x);
        event.setPitch(this.rotateVector.y);
        CreeperFarm.mc.player.rotationYaw = this.rotateVector.x;
        CreeperFarm.mc.player.rotationPitch = this.rotateVector.y;
        CreeperFarm.mc.player.rotationYawHead = this.rotateVector.x;
        CreeperFarm.mc.player.renderYawOffset = this.rotateVector.x;
        CreeperFarm.mc.player.rotationPitchHead = this.rotateVector.y;
    }

    private float smoothRotation(float current, float target, float maxSpeed) {
        float speed = Math.min(maxSpeed, Math.abs(target - current) * 2.0f);
        float delta = MathHelper.wrapDegrees(target - current);
        return MathHelper.wrapDegrees(current + MathHelper.clamp(delta, -speed, speed));
    }

    private void attack(CreeperEntity creeper) {
        if (creeper.getCreeperState() > 0) {
            this.isRunningAway = true;
            this.runAwayPosition = this.getRunAwayPosition(creeper);
            return;
        }
        if (this.timerUtil.hasTimeElapsed()) {
            CreeperFarm.mc.playerController.attackEntity(CreeperFarm.mc.player, creeper);
            CreeperFarm.mc.player.swingArm(Hand.MAIN_HAND);
            this.timerUtil.setLastMS(505L);
        }
    }

    private void resetKeys() {
        CreeperFarm.mc.gameSettings.keyBindForward.setPressed(false);
        CreeperFarm.mc.gameSettings.keyBindRight.setPressed(false);
        CreeperFarm.mc.gameSettings.keyBindLeft.setPressed(false);
        CreeperFarm.mc.gameSettings.keyBindJump.setPressed(false);
        CreeperFarm.mc.player.setSprinting(false);
    }

    private void reset() {
        if (CreeperFarm.mc.player != null) {
            this.resetKeys();
        }
        this.needRotate = false;
        this.rotateVector = new Vector2f(CreeperFarm.mc.player != null ? CreeperFarm.mc.player.rotationYaw : 0.0f, CreeperFarm.mc.player != null ? CreeperFarm.mc.player.rotationPitch : 0.0f);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        if (CreeperFarm.mc.player != null) {
            this.reset();
            this.target = null;
            this.isRunningAway = false;
            this.runAwayPosition = null;
            this.lastPosition = null;
            this.isStuck = false;
        }
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.reset();
        this.timerUtil.setLastMS(0L);
        this.target = null;
        this.isRunningAway = false;
        this.runAwayPosition = null;
        this.lastPosition = null;
        this.isStuck = false;
    }
}
