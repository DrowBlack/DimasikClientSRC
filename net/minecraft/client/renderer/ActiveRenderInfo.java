package net.minecraft.client.renderer;

import dimasik.Load;
import dimasik.events.main.visual.EventCamera;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.optifine.reflect.Reflector;

public class ActiveRenderInfo {
    private boolean valid;
    private IBlockReader world;
    private Entity renderViewEntity;
    private Vector3d pos = Vector3d.ZERO;
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private final Vector3f look = new Vector3f(0.0f, 0.0f, 1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f left = new Vector3f(1.0f, 0.0f, 0.0f);
    private float pitch;
    private float yaw;
    private final Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private boolean thirdPerson;
    private boolean thirdPersonReverse;
    private float height;
    private float previousHeight;
    private final EventCamera event = new EventCamera(0.0f, 0.0f, 0.0f);

    public void update(IBlockReader worldIn, Entity renderViewEntity, boolean thirdPersonIn, boolean thirdPersonReverseIn, float partialTicks) {
        this.valid = true;
        this.world = worldIn;
        this.renderViewEntity = renderViewEntity;
        this.thirdPerson = thirdPersonIn;
        this.thirdPersonReverse = thirdPersonReverseIn;
        this.setDirection(renderViewEntity.getYaw(partialTicks), renderViewEntity.getPitch(partialTicks));
        this.setPosition(MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosX, renderViewEntity.getPosX()), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosY, renderViewEntity.getPosY()) + (double)MathHelper.lerp(partialTicks, this.previousHeight, this.height), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosZ, renderViewEntity.getPosZ()));
        this.event.partialTicks = partialTicks;
        this.event.yaw = renderViewEntity.getYaw(partialTicks);
        this.event.pitch = renderViewEntity.getPitch(partialTicks);
        Load.getInstance().getEvents().call(this.event);
        this.setDirection(this.event.getYaw(), this.event.getPitch());
        this.setPosition(MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosX, renderViewEntity.getPosX()), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosY, renderViewEntity.getPosY()) + (double)MathHelper.lerp(partialTicks, this.previousHeight, this.height), MathHelper.lerp((double)partialTicks, renderViewEntity.prevPosZ, renderViewEntity.getPosZ()));
        if (thirdPersonIn) {
            if (thirdPersonReverseIn) {
                this.setDirection(this.event.getYaw() + 180.0f, -this.event.getPitch());
            }
            this.movePosition(-this.calcCameraDistance(4.0), 0.0, 0.0);
        } else if (renderViewEntity instanceof LivingEntity && ((LivingEntity)renderViewEntity).isSleeping()) {
            Direction direction = ((LivingEntity)renderViewEntity).getBedDirection();
            this.setDirection(direction != null ? direction.getHorizontalAngle() - 180.0f : 0.0f, 0.0f);
            this.movePosition(0.0, 0.3, 0.0);
        }
    }

    public void interpolateHeight() {
        if (this.renderViewEntity != null) {
            this.previousHeight = this.height;
            this.height += (this.renderViewEntity.getEyeHeight() - this.height) * 0.5f;
        }
    }

    private double calcCameraDistance(double startingDistance) {
        EventCamera eventCamera = new EventCamera();
        Load.getInstance().getEvents().call(eventCamera);
        if (!eventCamera.isCancelled()) {
            for (int i = 0; i < 8; ++i) {
                double d0;
                Vector3d vector3d1;
                BlockRayTraceResult raytraceresult;
                float f = (i & 1) * 2 - 1;
                float f1 = (i >> 1 & 1) * 2 - 1;
                float f2 = (i >> 2 & 1) * 2 - 1;
                Vector3d vector3d = this.pos.add(f *= 0.1f, f1 *= 0.1f, f2 *= 0.1f);
                if (((RayTraceResult)(raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1 = new Vector3d(this.pos.x - (double)this.look.getX() * startingDistance + (double)f + (double)f2, this.pos.y - (double)this.look.getY() * startingDistance + (double)f1, this.pos.z - (double)this.look.getZ() * startingDistance + (double)f2), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, this.renderViewEntity)))).getType() == RayTraceResult.Type.MISS || !((d0 = raytraceresult.getHitVec().distanceTo(this.pos)) < startingDistance)) continue;
                startingDistance = d0;
            }
        }
        return startingDistance;
    }

    protected void movePosition(double distanceOffset, double verticalOffset, double horizontalOffset) {
        double d0 = (double)this.look.getX() * distanceOffset + (double)this.up.getX() * verticalOffset + (double)this.left.getX() * horizontalOffset;
        double d1 = (double)this.look.getY() * distanceOffset + (double)this.up.getY() * verticalOffset + (double)this.left.getY() * horizontalOffset;
        double d2 = (double)this.look.getZ() * distanceOffset + (double)this.up.getZ() * verticalOffset + (double)this.left.getZ() * horizontalOffset;
        this.setPosition(new Vector3d(this.pos.x + d0, this.pos.y + d1, this.pos.z + d2));
    }

    protected void setDirection(float pitchIn, float yawIn) {
        this.pitch = yawIn;
        this.yaw = pitchIn;
        this.rotation.set(0.0f, 0.0f, 0.0f, 1.0f);
        this.rotation.multiply(Vector3f.YP.rotationDegrees(-pitchIn));
        this.rotation.multiply(Vector3f.XP.rotationDegrees(yawIn));
        this.look.set(0.0f, 0.0f, 1.0f);
        this.look.transform(this.rotation);
        this.up.set(0.0f, 1.0f, 0.0f);
        this.up.transform(this.rotation);
        this.left.set(1.0f, 0.0f, 0.0f);
        this.left.transform(this.rotation);
    }

    protected void setPosition(double x, double y, double z) {
        this.setPosition(new Vector3d(x, y, z));
    }

    protected void setPosition(Vector3d posIn) {
        this.pos = posIn;
        this.blockPos.setPos(posIn.x, posIn.y, posIn.z);
    }

    public Vector3d getProjectedView() {
        return this.pos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Quaternion getRotation() {
        return this.rotation;
    }

    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public FluidState getFluidState() {
        if (!this.valid) {
            return Fluids.EMPTY.getDefaultState();
        }
        FluidState fluidstate = this.world.getFluidState(this.blockPos);
        return !fluidstate.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + fluidstate.getActualHeight(this.world, this.blockPos)) ? Fluids.EMPTY.getDefaultState() : fluidstate;
    }

    public BlockState getBlockState() {
        return !this.valid ? Blocks.AIR.getDefaultState() : this.world.getBlockState(this.blockPos);
    }

    public void setAnglesInternal(float p_setAnglesInternal_1_, float p_setAnglesInternal_2_) {
        this.yaw = p_setAnglesInternal_1_;
        this.pitch = p_setAnglesInternal_2_;
    }

    public BlockState getBlockAtCamera() {
        if (!this.valid) {
            return Blocks.AIR.getDefaultState();
        }
        BlockState blockstate = this.world.getBlockState(this.blockPos);
        if (Reflector.IForgeBlockState_getStateAtViewpoint.exists()) {
            blockstate = (BlockState)Reflector.call(blockstate, Reflector.IForgeBlockState_getStateAtViewpoint, this.world, this.blockPos, this.pos);
        }
        return blockstate;
    }

    public final Vector3f getViewVector() {
        return this.look;
    }

    public final Vector3f getUpVector() {
        return this.up;
    }

    public void clear() {
    }

    @Generated
    public IBlockReader getWorld() {
        return this.world;
    }

    @Generated
    public Vector3d getPos() {
        return this.pos;
    }

    @Generated
    public Vector3f getLook() {
        return this.look;
    }

    @Generated
    public Vector3f getUp() {
        return this.up;
    }

    @Generated
    public Vector3f getLeft() {
        return this.left;
    }

    @Generated
    public boolean isThirdPersonReverse() {
        return this.thirdPersonReverse;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public float getPreviousHeight() {
        return this.previousHeight;
    }

    @Generated
    public EventCamera getEvent() {
        return this.event;
    }
}
