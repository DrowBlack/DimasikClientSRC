package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BoatEntity
extends Entity {
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(BoatEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> BOAT_TYPE = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> LEFT_PADDLE = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RIGHT_PADDLE = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ROCKING_TICKS = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
    private final float[] paddlePositions = new float[2];
    private float momentum;
    private float outOfControlTicks;
    private float deltaRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    private boolean leftInputDown;
    private boolean rightInputDown;
    private boolean forwardInputDown;
    private boolean backInputDown;
    private double waterLevel;
    private float boatGlide;
    private Status status;
    private Status previousStatus;
    private double lastYd;
    private boolean rocking;
    private boolean downwards;
    private float rockingIntensity;
    private float rockingAngle;
    private float prevRockingAngle;

    public BoatEntity(EntityType<? extends BoatEntity> type, World world) {
        super(type, world);
        this.preventEntitySpawning = true;
    }

    public BoatEntity(World worldIn, double x, double y, double z) {
        this((EntityType<? extends BoatEntity>)EntityType.BOAT, worldIn);
        this.setPosition(x, y, z);
        this.setMotion(Vector3d.ZERO);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(TIME_SINCE_HIT, 0);
        this.dataManager.register(FORWARD_DIRECTION, 1);
        this.dataManager.register(DAMAGE_TAKEN, Float.valueOf(0.0f));
        this.dataManager.register(BOAT_TYPE, Type.OAK.ordinal());
        this.dataManager.register(LEFT_PADDLE, false);
        this.dataManager.register(RIGHT_PADDLE, false);
        this.dataManager.register(ROCKING_TICKS, 0);
    }

    @Override
    public boolean canCollide(Entity entity) {
        return BoatEntity.func_242378_a(this, entity);
    }

    public static boolean func_242378_a(Entity p_242378_0_, Entity entity) {
        return (entity.func_241845_aY() || entity.canBePushed()) && !p_242378_0_.isRidingSameEntity(entity);
    }

    @Override
    public boolean func_241845_aY() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    protected Vector3d func_241839_a(Direction.Axis axis, TeleportationRepositioner.Result result) {
        return LivingEntity.func_242288_h(super.func_241839_a(axis, result));
    }

    @Override
    public double getMountedYOffset() {
        return -0.1;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isRemote && !this.removed) {
            boolean flag;
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0f);
            this.markVelocityChanged();
            boolean bl = flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamageTaken() > 40.0f) {
                if (!flag && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.entityDropItem(this.getItemBoat());
                }
                this.remove();
            }
            return true;
        }
        return true;
    }

    @Override
    public void onEnterBubbleColumnWithAirAbove(boolean downwards) {
        if (!this.world.isRemote) {
            this.rocking = true;
            this.downwards = downwards;
            if (this.getRockingTicks() == 0) {
                this.setRockingTicks(60);
            }
        }
        this.world.addParticle(ParticleTypes.SPLASH, this.getPosX() + (double)this.rand.nextFloat(), this.getPosY() + 0.7, this.getPosZ() + (double)this.rand.nextFloat(), 0.0, 0.0, 0.0);
        if (this.rand.nextInt(20) == 0) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.rand.nextFloat(), false);
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        if (entityIn instanceof BoatEntity) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.applyEntityCollision(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.applyEntityCollision(entityIn);
        }
    }

    public Item getItemBoat() {
        switch (this.getBoatType()) {
            default: {
                return Items.OAK_BOAT;
            }
            case SPRUCE: {
                return Items.SPRUCE_BOAT;
            }
            case BIRCH: {
                return Items.BIRCH_BOAT;
            }
            case JUNGLE: {
                return Items.JUNGLE_BOAT;
            }
            case ACACIA: {
                return Items.ACACIA_BOAT;
            }
            case DARK_OAK: 
        }
        return Items.DARK_OAK_BOAT;
    }

    @Override
    public void performHurtAnimation() {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0f);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.removed;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = 10;
    }

    @Override
    public Direction getAdjustedHorizontalFacing() {
        return this.getHorizontalFacing().rotateY();
    }

    @Override
    public void tick() {
        this.previousStatus = this.status;
        this.status = this.getBoatStatus();
        this.outOfControlTicks = this.status != Status.UNDER_WATER && this.status != Status.UNDER_FLOWING_WATER ? 0.0f : (this.outOfControlTicks += 1.0f);
        if (!this.world.isRemote && this.outOfControlTicks >= 60.0f) {
            this.removePassengers();
        }
        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }
        if (this.getDamageTaken() > 0.0f) {
            this.setDamageTaken(this.getDamageTaken() - 1.0f);
        }
        super.tick();
        this.tickLerp();
        if (this.canPassengerSteer()) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof PlayerEntity)) {
                this.setPaddleState(false, false);
            }
            this.updateMotion();
            if (this.world.isRemote) {
                this.controlBoat();
                this.world.sendPacketToServer(new CSteerBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
            this.move(MoverType.SELF, this.getMotion());
        } else {
            this.setMotion(Vector3d.ZERO);
        }
        this.updateRocking();
        for (int i = 0; i <= 1; ++i) {
            if (this.getPaddleState(i)) {
                SoundEvent soundevent;
                if (!this.isSilent() && (double)(this.paddlePositions[i] % ((float)Math.PI * 2)) <= 0.7853981852531433 && ((double)this.paddlePositions[i] + (double)0.3926991f) % 6.2831854820251465 >= 0.7853981852531433 && (soundevent = this.getPaddleSound()) != null) {
                    Vector3d vector3d = this.getLook(1.0f);
                    double d0 = i == 1 ? -vector3d.z : vector3d.z;
                    double d1 = i == 1 ? vector3d.x : -vector3d.x;
                    this.world.playSound(null, this.getPosX() + d0, this.getPosY(), this.getPosZ() + d1, soundevent, this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.rand.nextFloat());
                }
                this.paddlePositions[i] = (float)((double)this.paddlePositions[i] + (double)0.3926991f);
                continue;
            }
            this.paddlePositions[i] = 0.0f;
        }
        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0.2f, -0.01f, 0.2f), EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof PlayerEntity);
            for (int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                if (entity.isPassenger(this)) continue;
                if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterMobEntity) && !(entity instanceof PlayerEntity)) {
                    entity.startRiding(this);
                    continue;
                }
                this.applyEntityCollision(entity);
            }
        }
    }

    private void updateRocking() {
        if (this.world.isRemote) {
            int i = this.getRockingTicks();
            this.rockingIntensity = i > 0 ? (this.rockingIntensity += 0.05f) : (this.rockingIntensity -= 0.1f);
            this.rockingIntensity = MathHelper.clamp(this.rockingIntensity, 0.0f, 1.0f);
            this.prevRockingAngle = this.rockingAngle;
            this.rockingAngle = 10.0f * (float)Math.sin(0.5f * (float)this.world.getGameTime()) * this.rockingIntensity;
        } else {
            int k;
            if (!this.rocking) {
                this.setRockingTicks(0);
            }
            if ((k = this.getRockingTicks()) > 0) {
                this.setRockingTicks(--k);
                int j = 60 - k - 1;
                if (j > 0 && k == 0) {
                    this.setRockingTicks(0);
                    Vector3d vector3d = this.getMotion();
                    if (this.downwards) {
                        this.setMotion(vector3d.add(0.0, -0.7, 0.0));
                        this.removePassengers();
                    } else {
                        this.setMotion(vector3d.x, this.isPassenger(PlayerEntity.class) ? 2.7 : 0.6, vector3d.z);
                    }
                }
                this.rocking = false;
            }
        }
    }

    @Nullable
    protected SoundEvent getPaddleSound() {
        switch (this.getBoatStatus()) {
            case IN_WATER: 
            case UNDER_WATER: 
            case UNDER_FLOWING_WATER: {
                return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
            }
            case ON_LAND: {
                return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
            }
        }
        return null;
    }

    private void tickLerp() {
        if (this.canPassengerSteer()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getPosX(), this.getPosY(), this.getPosZ());
        }
        if (this.lerpSteps > 0) {
            double d0 = this.getPosX() + (this.lerpX - this.getPosX()) / (double)this.lerpSteps;
            double d1 = this.getPosY() + (this.lerpY - this.getPosY()) / (double)this.lerpSteps;
            double d2 = this.getPosZ() + (this.lerpZ - this.getPosZ()) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    public void setPaddleState(boolean left, boolean right) {
        this.dataManager.set(LEFT_PADDLE, left);
        this.dataManager.set(RIGHT_PADDLE, right);
    }

    public float getRowingTime(int side, float limbSwing) {
        return this.getPaddleState(side) ? (float)MathHelper.clampedLerp((double)this.paddlePositions[side] - (double)0.3926991f, this.paddlePositions[side], limbSwing) : 0.0f;
    }

    private Status getBoatStatus() {
        Status boatentity$status = this.getUnderwaterStatus();
        if (boatentity$status != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return boatentity$status;
        }
        if (this.checkInWater()) {
            return Status.IN_WATER;
        }
        float f = this.getBoatGlide();
        if (f > 0.0f) {
            this.boatGlide = f;
            return Status.ON_LAND;
        }
        return Status.IN_AIR;
    }

    public float getWaterLevelAbove() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        block0: for (int k1 = k; k1 < l; ++k1) {
            float f = 0.0f;
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.setPos(l1, k1, i2);
                    FluidState fluidstate = this.world.getFluidState(blockpos$mutable);
                    if (fluidstate.isTagged(FluidTags.WATER)) {
                        f = Math.max(f, fluidstate.getActualHeight(this.world, blockpos$mutable));
                    }
                    if (f >= 1.0f) continue block0;
                }
            }
            if (!(f < 1.0f)) continue;
            return (float)blockpos$mutable.getY() + f;
        }
        return l + 1;
    }

    public float getBoatGlide() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = VoxelShapes.create(axisalignedbb1);
        float f = 0.0f;
        int k1 = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
                if (j2 == 2) continue;
                for (int k2 = k; k2 < l; ++k2) {
                    if (j2 > 0 && (k2 == k || k2 == l - 1)) continue;
                    blockpos$mutable.setPos(l1, k2, i2);
                    BlockState blockstate = this.world.getBlockState(blockpos$mutable);
                    if (blockstate.getBlock() instanceof LilyPadBlock || !VoxelShapes.compare(blockstate.getCollisionShape(this.world, blockpos$mutable).withOffset(l1, k2, i2), voxelshape, IBooleanFunction.AND)) continue;
                    f += blockstate.getBlock().getSlipperiness();
                    ++k1;
                }
            }
        }
        return f / (float)k1;
    }

    private boolean checkInWater() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.minY + 0.001);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.setPos(k1, l1, i2);
                    FluidState fluidstate = this.world.getFluidState(blockpos$mutable);
                    if (!fluidstate.isTagged(FluidTags.WATER)) continue;
                    float f = (float)l1 + fluidstate.getActualHeight(this.world, blockpos$mutable);
                    this.waterLevel = Math.max((double)f, this.waterLevel);
                    flag |= axisalignedbb.minY < (double)f;
                }
            }
        }
        return flag;
    }

    @Nullable
    private Status getUnderwaterStatus() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockpos$mutable.setPos(k1, l1, i2);
                    FluidState fluidstate = this.world.getFluidState(blockpos$mutable);
                    if (!fluidstate.isTagged(FluidTags.WATER) || !(d0 < (double)((float)blockpos$mutable.getY() + fluidstate.getActualHeight(this.world, blockpos$mutable)))) continue;
                    if (!fluidstate.isSource()) {
                        return Status.UNDER_FLOWING_WATER;
                    }
                    flag = true;
                }
            }
        }
        return flag ? Status.UNDER_WATER : null;
    }

    private void updateMotion() {
        double d0 = -0.04f;
        double d1 = this.hasNoGravity() ? 0.0 : (double)-0.04f;
        double d2 = 0.0;
        this.momentum = 0.05f;
        if (this.previousStatus == Status.IN_AIR && this.status != Status.IN_AIR && this.status != Status.ON_LAND) {
            this.waterLevel = this.getPosYHeight(1.0);
            this.setPosition(this.getPosX(), (double)(this.getWaterLevelAbove() - this.getHeight()) + 0.101, this.getPosZ());
            this.setMotion(this.getMotion().mul(1.0, 0.0, 1.0));
            this.lastYd = 0.0;
            this.status = Status.IN_WATER;
        } else {
            if (this.status == Status.IN_WATER) {
                d2 = (this.waterLevel - this.getPosY()) / (double)this.getHeight();
                this.momentum = 0.9f;
            } else if (this.status == Status.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4;
                this.momentum = 0.9f;
            } else if (this.status == Status.UNDER_WATER) {
                d2 = 0.01f;
                this.momentum = 0.45f;
            } else if (this.status == Status.IN_AIR) {
                this.momentum = 0.9f;
            } else if (this.status == Status.ON_LAND) {
                this.momentum = this.boatGlide;
                if (this.getControllingPassenger() instanceof PlayerEntity) {
                    this.boatGlide /= 2.0f;
                }
            }
            Vector3d vector3d = this.getMotion();
            this.setMotion(vector3d.x * (double)this.momentum, vector3d.y + d1, vector3d.z * (double)this.momentum);
            this.deltaRotation *= this.momentum;
            if (d2 > 0.0) {
                Vector3d vector3d1 = this.getMotion();
                this.setMotion(vector3d1.x, (vector3d1.y + d2 * 0.06153846016296973) * 0.75, vector3d1.z);
            }
        }
    }

    private void controlBoat() {
        if (this.isBeingRidden()) {
            float f = 0.0f;
            if (this.leftInputDown) {
                this.deltaRotation -= 1.0f;
            }
            if (this.rightInputDown) {
                this.deltaRotation += 1.0f;
            }
            if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
                f += 0.005f;
            }
            this.rotationYaw += this.deltaRotation;
            if (this.forwardInputDown) {
                f += 0.04f;
            }
            if (this.backInputDown) {
                f -= 0.005f;
            }
            this.setMotion(this.getMotion().add(MathHelper.sin(-this.rotationYaw * ((float)Math.PI / 180)) * f, 0.0, MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)) * f));
            this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
        }
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float f = 0.0f;
            float f1 = (float)((this.removed ? (double)0.01f : this.getMountedYOffset()) + passenger.getYOffset());
            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);
                f = i == 0 ? 0.2f : -0.6f;
                if (passenger instanceof AnimalEntity) {
                    f = (float)((double)f + 0.2);
                }
            }
            Vector3d vector3d = new Vector3d(f, 0.0, 0.0).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180) - 1.5707964f);
            passenger.setPosition(this.getPosX() + vector3d.x, this.getPosY() + (double)f1, this.getPosZ() + vector3d.z);
            passenger.rotationYaw += this.deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
            this.applyYawToEntity(passenger);
            if (passenger instanceof AnimalEntity && this.getPassengers().size() > 1) {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((AnimalEntity)passenger).renderYawOffset + (float)j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + (float)j);
            }
        }
    }

    @Override
    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        double d1;
        Vector3d vector3d = BoatEntity.func_233559_a_(this.getWidth() * MathHelper.SQRT_2, livingEntity.getWidth(), this.rotationYaw);
        double d0 = this.getPosX() + vector3d.x;
        BlockPos blockpos = new BlockPos(d0, this.getBoundingBox().maxY, d1 = this.getPosZ() + vector3d.z);
        BlockPos blockpos1 = blockpos.down();
        if (!this.world.hasWater(blockpos1)) {
            double d2 = (double)blockpos.getY() + this.world.func_242403_h(blockpos);
            double d3 = (double)blockpos.getY() + this.world.func_242403_h(blockpos1);
            for (Pose pose : livingEntity.getAvailablePoses()) {
                Vector3d vector3d1 = TransportationHelper.func_242381_a(this.world, d0, d2, d1, livingEntity, pose);
                if (vector3d1 != null) {
                    livingEntity.setPose(pose);
                    return vector3d1;
                }
                Vector3d vector3d2 = TransportationHelper.func_242381_a(this.world, d0, d3, d1, livingEntity, pose);
                if (vector3d2 == null) continue;
                livingEntity.setPose(pose);
                return vector3d2;
            }
        }
        return super.func_230268_c_(livingEntity);
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0f, 105.0f);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putString("Type", this.getBoatType().getName());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("Type", 8)) {
            this.setBoatType(Type.getTypeFromString(compound.getString("Type")));
        }
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (player.isSecondaryUseActive()) {
            return ActionResultType.PASS;
        }
        if (this.outOfControlTicks < 60.0f) {
            if (!this.world.isRemote) {
                return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        this.lastYd = this.getMotion().y;
        if (!this.isPassenger()) {
            if (onGroundIn) {
                if (this.fallDistance > 3.0f) {
                    if (this.status != Status.ON_LAND) {
                        this.fallDistance = 0.0f;
                        return;
                    }
                    this.onLivingFall(this.fallDistance, 1.0f);
                    if (!this.world.isRemote && !this.removed) {
                        this.remove();
                        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            for (int i = 0; i < 3; ++i) {
                                this.entityDropItem(this.getBoatType().asPlank());
                            }
                            for (int j = 0; j < 2; ++j) {
                                this.entityDropItem(Items.STICK);
                            }
                        }
                    }
                }
                this.fallDistance = 0.0f;
            } else if (!this.world.getFluidState(this.getPosition().down()).isTagged(FluidTags.WATER) && y < 0.0) {
                this.fallDistance = (float)((double)this.fallDistance - y);
            }
        }
    }

    public boolean getPaddleState(int side) {
        return this.dataManager.get(side == 0 ? LEFT_PADDLE : RIGHT_PADDLE) != false && this.getControllingPassenger() != null;
    }

    public void setDamageTaken(float damageTaken) {
        this.dataManager.set(DAMAGE_TAKEN, Float.valueOf(damageTaken));
    }

    public float getDamageTaken() {
        return this.dataManager.get(DAMAGE_TAKEN).floatValue();
    }

    public void setTimeSinceHit(int timeSinceHit) {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    public int getTimeSinceHit() {
        return this.dataManager.get(TIME_SINCE_HIT);
    }

    private void setRockingTicks(int ticks) {
        this.dataManager.set(ROCKING_TICKS, ticks);
    }

    private int getRockingTicks() {
        return this.dataManager.get(ROCKING_TICKS);
    }

    public float getRockingAngle(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
    }

    public void setForwardDirection(int forwardDirection) {
        this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
    }

    public int getForwardDirection() {
        return this.dataManager.get(FORWARD_DIRECTION);
    }

    public void setBoatType(Type boatType) {
        this.dataManager.set(BOAT_TYPE, boatType.ordinal());
    }

    public Type getBoatType() {
        return Type.byId(this.dataManager.get(BOAT_TYPE));
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 2 && !this.areEyesInFluid(FluidTags.WATER);
    }

    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    public void updateInputs(boolean leftInputDown, boolean rightInputDown, boolean forwardInputDown, boolean backInputDown) {
        this.leftInputDown = leftInputDown;
        this.rightInputDown = rightInputDown;
        this.forwardInputDown = forwardInputDown;
        this.backInputDown = backInputDown;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    public boolean canSwim() {
        return this.status == Status.UNDER_WATER || this.status == Status.UNDER_FLOWING_WATER;
    }

    public static enum Type {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String name;
        private final Block block;

        private Type(Block block, String name) {
            this.name = name;
            this.block = block;
        }

        public String getName() {
            return this.name;
        }

        public Block asPlank() {
            return this.block;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int id) {
            Type[] aboatentity$type = Type.values();
            if (id < 0 || id >= aboatentity$type.length) {
                id = 0;
            }
            return aboatentity$type[id];
        }

        public static Type getTypeFromString(String nameIn) {
            Type[] aboatentity$type = Type.values();
            for (int i = 0; i < aboatentity$type.length; ++i) {
                if (!aboatentity$type[i].getName().equals(nameIn)) continue;
                return aboatentity$type[i];
            }
            return aboatentity$type[0];
        }
    }

    public static enum Status {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }
}
