package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventTick;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class PearlTarget
extends Module {
    private final Minecraft mc = Minecraft.getInstance();
    private Vector3d targetPosition = null;
    private long aimStartTime = 0L;
    private final int AIM_DELAY = 50;
    private float targetYaw;
    private float targetPitch;
    private final Set<Integer> trackedPearls = new HashSet<Integer>();
    private int pearlsToThrow = 0;
    private int pearlsThrown = 0;
    private final double MIN_DISTANCE = 5.0;
    public boolean isThrowing = false;
    private int lastPearlThrowTick = 0;
    private final EventListener<EventTick> tick = this::tick;

    public PearlTarget() {
        super("Pearl Target", Category.PLAYER);
    }

    public void tick(EventTick e) {
        EnderPearlEntity pearl;
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (this.isOwnPearlActive()) {
            this.resetState();
            return;
        }
        if (!this.hasPearlsInHotbar()) {
            this.resetState();
            return;
        }
        int newPearls = this.countNewPearls();
        if (newPearls > 0) {
            this.pearlsToThrow += newPearls;
        }
        if (this.pearlsToThrow > this.pearlsThrown && !this.isThrowing && this.targetPosition == null && !this.isOwnPearlActive() && (pearl = this.findValidPearl()) != null) {
            this.prepareThrow(pearl);
        }
        if (this.targetPosition != null && System.currentTimeMillis() - this.aimStartTime >= 50L) {
            if (this.throwPearl()) {
                ++this.pearlsThrown;
                this.lastPearlThrowTick = this.mc.player.ticksExisted;
            }
            this.resetAim();
        }
    }

    private boolean isOwnPearlActive() {
        for (Entity entity : this.mc.world.getAllEntities()) {
            EnderPearlEntity pearl;
            if (!(entity instanceof EnderPearlEntity) || (pearl = (EnderPearlEntity)entity).getThrowerId() == null || !pearl.getThrowerId().equals(this.mc.player.getUniqueID()) || this.mc.player.ticksExisted - this.lastPearlThrowTick >= 20) continue;
            return true;
        }
        return false;
    }

    private boolean hasPearlsInHotbar() {
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() != Items.ENDER_PEARL) continue;
            return true;
        }
        return false;
    }

    private int countNewPearls() {
        int count = 0;
        for (Entity entity : this.mc.world.getAllEntities()) {
            double distance;
            EnderPearlEntity pearl;
            if (!(entity instanceof EnderPearlEntity) || this.trackedPearls.contains((pearl = (EnderPearlEntity)entity).getEntityId()) || !((distance = this.mc.player.getDistanceSq(pearl)) >= 25.0)) continue;
            ++count;
            this.trackedPearls.add(pearl.getEntityId());
        }
        return count;
    }

    private EnderPearlEntity findValidPearl() {
        EnderPearlEntity closestPearl = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity entity : this.mc.world.getAllEntities()) {
            double distance;
            if (!(entity instanceof EnderPearlEntity) || !((distance = this.mc.player.getDistanceSq(entity)) >= 25.0) || !(distance < closestDistance)) continue;
            closestDistance = distance;
            closestPearl = (EnderPearlEntity)entity;
        }
        return closestPearl;
    }

    private void prepareThrow(EnderPearlEntity pearl) {
        if (!this.hasPearlsInHotbar()) {
            return;
        }
        Vector3d targetPos = this.calculatePearlLanding(pearl);
        double hitDistance = this.mc.player.getDistanceSq(targetPos.x, targetPos.y, targetPos.z);
        if (hitDistance >= 25.0) {
            this.aimAt(targetPos);
            this.targetPosition = targetPos;
            this.aimStartTime = System.currentTimeMillis();
            this.isThrowing = true;
        } else {
            ++this.pearlsThrown;
        }
    }

    private Vector3d calculatePearlLanding(EnderPearlEntity pearl) {
        Vector3d position = pearl.getPositionVec();
        Vector3d motion = pearl.getMotion();
        for (int i = 0; i < 300; ++i) {
            Vector3d lastPosition = position;
            position = position.add(motion);
            motion = this.updatePearlMotion(pearl, motion);
            if (!this.checkCollision(lastPosition, position) && !(position.y <= 0.0)) continue;
            return position;
        }
        return position;
    }

    private void aimAt(Vector3d targetPos) {
        Vector3d playerPos = this.mc.player.getPositionVec().add(0.0, this.mc.player.getEyeHeight(), 0.0);
        Vector3d direction = targetPos.subtract(playerPos);
        double xzDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double yOffset = direction.y;
        double velocity = 1.5;
        double gravity = 0.03;
        double pitch = this.calculateThrowAngle(xzDistance, yOffset, velocity, gravity);
        float yaw = (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
        this.targetPitch = (float)(-pitch);
        this.targetYaw = yaw;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean throwPearl() {
        int pearlSlot = this.findPearlSlot();
        if (pearlSlot == -1) {
            return false;
        }
        Hand hand = this.mc.player.getHeldItemOffhand().getItem() instanceof EnderPearlItem ? Hand.OFF_HAND : Hand.MAIN_HAND;
        int oldSlot = this.mc.player.inventory.currentItem;
        try {
            if (hand != Hand.OFF_HAND) {
                this.mc.player.connection.sendPacket(new CHeldItemChangePacket(pearlSlot));
                this.mc.player.inventory.currentItem = pearlSlot;
            }
            this.mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(this.targetYaw, this.targetPitch, this.mc.player.onGround));
            this.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
            this.mc.player.swingArm(hand);
            boolean bl = true;
            return bl;
        }
        finally {
            if (hand != Hand.OFF_HAND) {
                this.mc.player.connection.sendPacket(new CHeldItemChangePacket(oldSlot));
                this.mc.player.inventory.currentItem = oldSlot;
            }
            this.isThrowing = false;
        }
    }

    private int findPearlSlot() {
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.inventory.getStackInSlot(i).getItem() != Items.ENDER_PEARL) continue;
            return i;
        }
        return -1;
    }

    private void resetAim() {
        this.targetPosition = null;
        this.targetYaw = 0.0f;
        this.targetPitch = 0.0f;
    }

    private void resetState() {
        this.resetAim();
        this.pearlsToThrow = 0;
        this.pearlsThrown = 0;
        this.trackedPearls.clear();
        this.isThrowing = false;
    }

    private double calculateThrowAngle(double xzDistance, double yOffset, double velocity, double gravity) {
        double velocitySquared = velocity * velocity;
        double underRoot = velocitySquared * velocitySquared - gravity * (gravity * xzDistance * xzDistance + 2.0 * yOffset * velocitySquared);
        if (underRoot < 0.0) {
            return 45.0;
        }
        double root = Math.sqrt(underRoot);
        double angle1 = Math.toDegrees(Math.atan((velocitySquared + root) / (gravity * xzDistance)));
        double angle2 = Math.toDegrees(Math.atan((velocitySquared - root) / (gravity * xzDistance)));
        return Math.min(angle1, angle2);
    }

    private Vector3d updatePearlMotion(EnderPearlEntity pearl, Vector3d originalPearlMotion) {
        Vector3d pearlMotion = originalPearlMotion.scale(pearl.isInWater() ? (double)0.8f : (double)0.99f);
        if (!pearl.hasNoGravity()) {
            pearlMotion = new Vector3d(pearlMotion.x, pearlMotion.y - (double)pearl.getGravityVelocity(), pearlMotion.z);
        }
        return pearlMotion;
    }

    private boolean checkCollision(Vector3d start, Vector3d end) {
        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.mc.player);
        BlockRayTraceResult result = this.mc.world.rayTraceBlocks(context);
        return result.getType() != RayTraceResult.Type.MISS;
    }

    @Generated
    public Vector3d getTargetPosition() {
        return this.targetPosition;
    }
}
