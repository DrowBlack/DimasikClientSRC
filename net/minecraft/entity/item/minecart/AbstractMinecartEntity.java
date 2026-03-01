package net.minecraft.entity.item.minecart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity
extends Entity {
    private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.BOOLEAN);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> field_234627_am_ = ImmutableMap.of(Pose.STANDING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), Pose.CROUCHING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), Pose.SWIMMING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1)));
    private boolean isInReverse;
    private static final Map<RailShape, Pair<Vector3i, Vector3i>> MATRIX = Util.make(Maps.newEnumMap(RailShape.class), shapeVectorMap -> {
        Vector3i vector3i = Direction.WEST.getDirectionVec();
        Vector3i vector3i1 = Direction.EAST.getDirectionVec();
        Vector3i vector3i2 = Direction.NORTH.getDirectionVec();
        Vector3i vector3i3 = Direction.SOUTH.getDirectionVec();
        Vector3i vector3i4 = vector3i.down();
        Vector3i vector3i5 = vector3i1.down();
        Vector3i vector3i6 = vector3i2.down();
        Vector3i vector3i7 = vector3i3.down();
        shapeVectorMap.put(RailShape.NORTH_SOUTH, Pair.of(vector3i2, vector3i3));
        shapeVectorMap.put(RailShape.EAST_WEST, Pair.of(vector3i, vector3i1));
        shapeVectorMap.put(RailShape.ASCENDING_EAST, Pair.of(vector3i4, vector3i1));
        shapeVectorMap.put(RailShape.ASCENDING_WEST, Pair.of(vector3i, vector3i5));
        shapeVectorMap.put(RailShape.ASCENDING_NORTH, Pair.of(vector3i2, vector3i7));
        shapeVectorMap.put(RailShape.ASCENDING_SOUTH, Pair.of(vector3i6, vector3i3));
        shapeVectorMap.put(RailShape.SOUTH_EAST, Pair.of(vector3i3, vector3i1));
        shapeVectorMap.put(RailShape.SOUTH_WEST, Pair.of(vector3i3, vector3i));
        shapeVectorMap.put(RailShape.NORTH_WEST, Pair.of(vector3i2, vector3i));
        shapeVectorMap.put(RailShape.NORTH_EAST, Pair.of(vector3i2, vector3i1));
    });
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    protected AbstractMinecartEntity(EntityType<?> type, World worldIn) {
        super(type, worldIn);
        this.preventEntitySpawning = true;
    }

    protected AbstractMinecartEntity(EntityType<?> type, World worldIn, double posX, double posY, double posZ) {
        this(type, worldIn);
        this.setPosition(posX, posY, posZ);
        this.setMotion(Vector3d.ZERO);
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
    }

    public static AbstractMinecartEntity create(World worldIn, double x, double y, double z, Type typeIn) {
        if (typeIn == Type.CHEST) {
            return new ChestMinecartEntity(worldIn, x, y, z);
        }
        if (typeIn == Type.FURNACE) {
            return new FurnaceMinecartEntity(worldIn, x, y, z);
        }
        if (typeIn == Type.TNT) {
            return new TNTMinecartEntity(worldIn, x, y, z);
        }
        if (typeIn == Type.SPAWNER) {
            return new SpawnerMinecartEntity(worldIn, x, y, z);
        }
        if (typeIn == Type.HOPPER) {
            return new HopperMinecartEntity(worldIn, x, y, z);
        }
        return typeIn == Type.COMMAND_BLOCK ? new CommandBlockMinecartEntity(worldIn, x, y, z) : new MinecartEntity(worldIn, x, y, z);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void registerData() {
        this.dataManager.register(ROLLING_AMPLITUDE, 0);
        this.dataManager.register(ROLLING_DIRECTION, 1);
        this.dataManager.register(DAMAGE, Float.valueOf(0.0f));
        this.dataManager.register(DISPLAY_TILE, Block.getStateId(Blocks.AIR.getDefaultState()));
        this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
        this.dataManager.register(SHOW_BLOCK, false);
    }

    @Override
    public boolean canCollide(Entity entity) {
        return BoatEntity.func_242378_a(this, entity);
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
        return 0.0;
    }

    @Override
    public Vector3d func_230268_c_(LivingEntity livingEntity) {
        Direction direction = this.getAdjustedHorizontalFacing();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.func_230268_c_(livingEntity);
        }
        int[][] aint = TransportationHelper.func_234632_a_(direction);
        BlockPos blockpos = this.getPosition();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        ImmutableList<Pose> immutablelist = livingEntity.getAvailablePoses();
        for (Pose pose : immutablelist) {
            EntitySize entitysize = livingEntity.getSize(pose);
            float f = Math.min(entitysize.width, 1.0f) / 2.0f;
            Iterator iterator = field_234627_am_.get((Object)pose).iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                for (int[] aint1 : aint) {
                    Vector3d vector3d;
                    AxisAlignedBB axisalignedbb;
                    blockpos$mutable.setPos(blockpos.getX() + aint1[0], blockpos.getY() + i, blockpos.getZ() + aint1[1]);
                    double d0 = this.world.func_242402_a(TransportationHelper.func_242380_a(this.world, blockpos$mutable), () -> TransportationHelper.func_242380_a(this.world, (BlockPos)blockpos$mutable.down()));
                    if (!TransportationHelper.func_234630_a_(d0) || !TransportationHelper.func_234631_a_(this.world, livingEntity, (axisalignedbb = new AxisAlignedBB(-f, 0.0, -f, f, entitysize.height, f)).offset(vector3d = Vector3d.copyCenteredWithVerticalOffset(blockpos$mutable, d0)))) continue;
                    livingEntity.setPose(pose);
                    return vector3d;
                }
            }
        }
        double d1 = this.getBoundingBox().maxY;
        blockpos$mutable.setPos((double)blockpos.getX(), d1, (double)blockpos.getZ());
        for (Pose pose1 : immutablelist) {
            double d2 = livingEntity.getSize((Pose)pose1).height;
            int j = MathHelper.ceil(d1 - (double)blockpos$mutable.getY() + d2);
            double d3 = TransportationHelper.func_242383_a(blockpos$mutable, j, pos -> this.world.getBlockState((BlockPos)pos).getCollisionShape(this.world, (BlockPos)pos));
            if (!(d1 + d2 <= d3)) continue;
            livingEntity.setPose(pose1);
            break;
        }
        return super.func_230268_c_(livingEntity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote && !this.removed) {
            boolean flag;
            if (this.isInvulnerableTo(source)) {
                return false;
            }
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.markVelocityChanged();
            this.setDamage(this.getDamage() + amount * 10.0f);
            boolean bl = flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0f) {
                this.removePassengers();
                if (flag && !this.hasCustomName()) {
                    this.remove();
                } else {
                    this.killMinecart(source);
                }
            }
            return true;
        }
        return true;
    }

    @Override
    protected float getSpeedFactor() {
        BlockState blockstate = this.world.getBlockState(this.getPosition());
        return blockstate.isIn(BlockTags.RAILS) ? 1.0f : super.getSpeedFactor();
    }

    public void killMinecart(DamageSource source) {
        this.remove();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemstack = new ItemStack(Items.MINECART);
            if (this.hasCustomName()) {
                itemstack.setDisplayName(this.getCustomName());
            }
            this.entityDropItem(itemstack);
        }
    }

    @Override
    public void performHurtAnimation() {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0f);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.removed;
    }

    private static Pair<Vector3i, Vector3i> getMovementMatrixForShape(RailShape shape) {
        return MATRIX.get(shape);
    }

    @Override
    public Direction getAdjustedHorizontalFacing() {
        return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
    }

    @Override
    public void tick() {
        if (this.getRollingAmplitude() > 0) {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }
        if (this.getDamage() > 0.0f) {
            this.setDamage(this.getDamage() - 1.0f);
        }
        if (this.getPosY() < -64.0) {
            this.outOfWorld();
        }
        this.updatePortal();
        if (this.world.isRemote) {
            if (this.turnProgress > 0) {
                double d4 = this.getPosX() + (this.minecartX - this.getPosX()) / (double)this.turnProgress;
                double d5 = this.getPosY() + (this.minecartY - this.getPosY()) / (double)this.turnProgress;
                double d6 = this.getPosZ() + (this.minecartZ - this.getPosZ()) / (double)this.turnProgress;
                double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(d4, d5, d6);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                this.recenterBoundingBox();
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {
            double d3;
            BlockPos blockpos;
            BlockState blockstate;
            int k;
            int j;
            int i;
            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0, -0.04, 0.0));
            }
            if (this.world.getBlockState(new BlockPos(i = MathHelper.floor(this.getPosX()), (j = MathHelper.floor(this.getPosY())) - 1, k = MathHelper.floor(this.getPosZ()))).isIn(BlockTags.RAILS)) {
                --j;
            }
            if (AbstractRailBlock.isRail(blockstate = this.world.getBlockState(blockpos = new BlockPos(i, j, k)))) {
                this.moveAlongTrack(blockpos, blockstate);
                if (blockstate.isIn(Blocks.ACTIVATOR_RAIL)) {
                    this.onActivatorRailPass(i, j, k, blockstate.get(PoweredRailBlock.POWERED));
                }
            } else {
                this.moveDerailedMinecart();
            }
            this.doBlockCollisions();
            this.rotationPitch = 0.0f;
            double d0 = this.prevPosX - this.getPosX();
            double d2 = this.prevPosZ - this.getPosZ();
            if (d0 * d0 + d2 * d2 > 0.001) {
                this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0 / Math.PI);
                if (this.isInReverse) {
                    this.rotationYaw += 180.0f;
                }
            }
            if ((d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw)) < -170.0 || d3 >= 170.0) {
                this.rotationYaw += 180.0f;
                this.isInReverse = !this.isInReverse;
            }
            this.setRotation(this.rotationYaw, this.rotationPitch);
            if (this.getMinecartType() == Type.RIDEABLE && AbstractMinecartEntity.horizontalMag(this.getMotion()) > 0.01) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(0.2f, 0.0, 0.2f), EntityPredicates.pushableBy(this));
                if (!list.isEmpty()) {
                    for (int l = 0; l < list.size(); ++l) {
                        Entity entity1 = list.get(l);
                        if (!(entity1 instanceof PlayerEntity || entity1 instanceof IronGolemEntity || entity1 instanceof AbstractMinecartEntity || this.isBeingRidden() || entity1.isPassenger())) {
                            entity1.startRiding(this);
                            continue;
                        }
                        entity1.applyEntityCollision(this);
                    }
                }
            } else {
                for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(0.2f, 0.0, 0.2f))) {
                    if (this.isPassenger(entity) || !entity.canBePushed() || !(entity instanceof AbstractMinecartEntity)) continue;
                    entity.applyEntityCollision(this);
                }
            }
            this.func_233566_aG_();
            if (this.isInLava()) {
                this.setOnFireFromLava();
                this.fallDistance *= 0.5f;
            }
            this.firstUpdate = false;
        }
    }

    protected double getMaximumSpeed() {
        return 0.4;
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
    }

    protected void moveDerailedMinecart() {
        double d0 = this.getMaximumSpeed();
        Vector3d vector3d = this.getMotion();
        this.setMotion(MathHelper.clamp(vector3d.x, -d0, d0), vector3d.y, MathHelper.clamp(vector3d.z, -d0, d0));
        if (this.onGround) {
            this.setMotion(this.getMotion().scale(0.5));
        }
        this.move(MoverType.SELF, this.getMotion());
        if (!this.onGround) {
            this.setMotion(this.getMotion().scale(0.95));
        }
    }

    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        double d14;
        Entity entity;
        this.fallDistance = 0.0f;
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        Vector3d vector3d = this.getPos(d0, d1, d2);
        d1 = pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        AbstractRailBlock abstractrailblock = (AbstractRailBlock)state.getBlock();
        if (abstractrailblock == Blocks.POWERED_RAIL) {
            flag = state.get(PoweredRailBlock.POWERED);
            flag1 = !flag;
        }
        double d3 = 0.0078125;
        Vector3d vector3d1 = this.getMotion();
        RailShape railshape = state.get(abstractrailblock.getShapeProperty());
        switch (railshape) {
            case ASCENDING_EAST: {
                this.setMotion(vector3d1.add(-0.0078125, 0.0, 0.0));
                d1 += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.setMotion(vector3d1.add(0.0078125, 0.0, 0.0));
                d1 += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.setMotion(vector3d1.add(0.0, 0.0, 0.0078125));
                d1 += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setMotion(vector3d1.add(0.0, 0.0, -0.0078125));
                d1 += 1.0;
            }
        }
        vector3d1 = this.getMotion();
        Pair<Vector3i, Vector3i> pair = AbstractMinecartEntity.getMovementMatrixForShape(railshape);
        Vector3i vector3i = pair.getFirst();
        Vector3i vector3i1 = pair.getSecond();
        double d4 = vector3i1.getX() - vector3i.getX();
        double d5 = vector3i1.getZ() - vector3i.getZ();
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
        double d7 = vector3d1.x * d4 + vector3d1.z * d5;
        if (d7 < 0.0) {
            d4 = -d4;
            d5 = -d5;
        }
        double d8 = Math.min(2.0, Math.sqrt(AbstractMinecartEntity.horizontalMag(vector3d1)));
        vector3d1 = new Vector3d(d8 * d4 / d6, vector3d1.y, d8 * d5 / d6);
        this.setMotion(vector3d1);
        Entity entity2 = entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        if (entity instanceof PlayerEntity) {
            Vector3d vector3d2 = entity.getMotion();
            double d9 = AbstractMinecartEntity.horizontalMag(vector3d2);
            double d11 = AbstractMinecartEntity.horizontalMag(this.getMotion());
            if (d9 > 1.0E-4 && d11 < 0.01) {
                this.setMotion(this.getMotion().add(vector3d2.x * 0.1, 0.0, vector3d2.z * 0.1));
                flag1 = false;
            }
        }
        if (flag1) {
            double d22 = Math.sqrt(AbstractMinecartEntity.horizontalMag(this.getMotion()));
            if (d22 < 0.03) {
                this.setMotion(Vector3d.ZERO);
            } else {
                this.setMotion(this.getMotion().mul(0.5, 0.0, 0.5));
            }
        }
        double d23 = (double)pos.getX() + 0.5 + (double)vector3i.getX() * 0.5;
        double d10 = (double)pos.getZ() + 0.5 + (double)vector3i.getZ() * 0.5;
        double d12 = (double)pos.getX() + 0.5 + (double)vector3i1.getX() * 0.5;
        double d13 = (double)pos.getZ() + 0.5 + (double)vector3i1.getZ() * 0.5;
        d4 = d12 - d23;
        d5 = d13 - d10;
        if (d4 == 0.0) {
            d14 = d2 - (double)pos.getZ();
        } else if (d5 == 0.0) {
            d14 = d0 - (double)pos.getX();
        } else {
            double d15 = d0 - d23;
            double d16 = d2 - d10;
            d14 = (d15 * d4 + d16 * d5) * 2.0;
        }
        d0 = d23 + d4 * d14;
        d2 = d10 + d5 * d14;
        this.setPosition(d0, d1, d2);
        double d24 = this.isBeingRidden() ? 0.75 : 1.0;
        double d25 = this.getMaximumSpeed();
        vector3d1 = this.getMotion();
        this.move(MoverType.SELF, new Vector3d(MathHelper.clamp(d24 * vector3d1.x, -d25, d25), 0.0, MathHelper.clamp(d24 * vector3d1.z, -d25, d25)));
        if (vector3i.getY() != 0 && MathHelper.floor(this.getPosX()) - pos.getX() == vector3i.getX() && MathHelper.floor(this.getPosZ()) - pos.getZ() == vector3i.getZ()) {
            this.setPosition(this.getPosX(), this.getPosY() + (double)vector3i.getY(), this.getPosZ());
        } else if (vector3i1.getY() != 0 && MathHelper.floor(this.getPosX()) - pos.getX() == vector3i1.getX() && MathHelper.floor(this.getPosZ()) - pos.getZ() == vector3i1.getZ()) {
            this.setPosition(this.getPosX(), this.getPosY() + (double)vector3i1.getY(), this.getPosZ());
        }
        this.applyDrag();
        Vector3d vector3d3 = this.getPos(this.getPosX(), this.getPosY(), this.getPosZ());
        if (vector3d3 != null && vector3d != null) {
            double d17 = (vector3d.y - vector3d3.y) * 0.05;
            Vector3d vector3d4 = this.getMotion();
            double d18 = Math.sqrt(AbstractMinecartEntity.horizontalMag(vector3d4));
            if (d18 > 0.0) {
                this.setMotion(vector3d4.mul((d18 + d17) / d18, 1.0, (d18 + d17) / d18));
            }
            this.setPosition(this.getPosX(), vector3d3.y, this.getPosZ());
        }
        int j = MathHelper.floor(this.getPosX());
        int i = MathHelper.floor(this.getPosZ());
        if (j != pos.getX() || i != pos.getZ()) {
            Vector3d vector3d5 = this.getMotion();
            double d26 = Math.sqrt(AbstractMinecartEntity.horizontalMag(vector3d5));
            this.setMotion(d26 * (double)(j - pos.getX()), vector3d5.y, d26 * (double)(i - pos.getZ()));
        }
        if (flag) {
            Vector3d vector3d6 = this.getMotion();
            double d27 = Math.sqrt(AbstractMinecartEntity.horizontalMag(vector3d6));
            if (d27 > 0.01) {
                double d19 = 0.06;
                this.setMotion(vector3d6.add(vector3d6.x / d27 * 0.06, 0.0, vector3d6.z / d27 * 0.06));
            } else {
                Vector3d vector3d7 = this.getMotion();
                double d20 = vector3d7.x;
                double d21 = vector3d7.z;
                if (railshape == RailShape.EAST_WEST) {
                    if (this.isNormalCube(pos.west())) {
                        d20 = 0.02;
                    } else if (this.isNormalCube(pos.east())) {
                        d20 = -0.02;
                    }
                } else {
                    if (railshape != RailShape.NORTH_SOUTH) {
                        return;
                    }
                    if (this.isNormalCube(pos.north())) {
                        d21 = 0.02;
                    } else if (this.isNormalCube(pos.south())) {
                        d21 = -0.02;
                    }
                }
                this.setMotion(d20, vector3d7.y, d21);
            }
        }
    }

    private boolean isNormalCube(BlockPos pos) {
        return this.world.getBlockState(pos).isNormalCube(this.world, pos);
    }

    protected void applyDrag() {
        double d0 = this.isBeingRidden() ? 0.997 : 0.96;
        this.setMotion(this.getMotion().mul(d0, 0.0, d0));
    }

    @Nullable
    public Vector3d getPosOffset(double x, double y, double z, double offset) {
        BlockState blockstate;
        int k;
        int j;
        int i = MathHelper.floor(x);
        if (this.world.getBlockState(new BlockPos(i, (j = MathHelper.floor(y)) - 1, k = MathHelper.floor(z))).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(blockstate = this.world.getBlockState(new BlockPos(i, j, k)))) {
            RailShape railshape = blockstate.get(((AbstractRailBlock)blockstate.getBlock()).getShapeProperty());
            y = j;
            if (railshape.isAscending()) {
                y = j + 1;
            }
            Pair<Vector3i, Vector3i> pair = AbstractMinecartEntity.getMovementMatrixForShape(railshape);
            Vector3i vector3i = pair.getFirst();
            Vector3i vector3i1 = pair.getSecond();
            double d0 = vector3i1.getX() - vector3i.getX();
            double d1 = vector3i1.getZ() - vector3i.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            if (vector3i.getY() != 0 && MathHelper.floor(x += (d0 /= d2) * offset) - i == vector3i.getX() && MathHelper.floor(z += (d1 /= d2) * offset) - k == vector3i.getZ()) {
                y += (double)vector3i.getY();
            } else if (vector3i1.getY() != 0 && MathHelper.floor(x) - i == vector3i1.getX() && MathHelper.floor(z) - k == vector3i1.getZ()) {
                y += (double)vector3i1.getY();
            }
            return this.getPos(x, y, z);
        }
        return null;
    }

    @Nullable
    public Vector3d getPos(double x, double y, double z) {
        BlockState blockstate;
        int k;
        int j;
        int i = MathHelper.floor(x);
        if (this.world.getBlockState(new BlockPos(i, (j = MathHelper.floor(y)) - 1, k = MathHelper.floor(z))).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(blockstate = this.world.getBlockState(new BlockPos(i, j, k)))) {
            double d9;
            RailShape railshape = blockstate.get(((AbstractRailBlock)blockstate.getBlock()).getShapeProperty());
            Pair<Vector3i, Vector3i> pair = AbstractMinecartEntity.getMovementMatrixForShape(railshape);
            Vector3i vector3i = pair.getFirst();
            Vector3i vector3i1 = pair.getSecond();
            double d0 = (double)i + 0.5 + (double)vector3i.getX() * 0.5;
            double d1 = (double)j + 0.0625 + (double)vector3i.getY() * 0.5;
            double d2 = (double)k + 0.5 + (double)vector3i.getZ() * 0.5;
            double d3 = (double)i + 0.5 + (double)vector3i1.getX() * 0.5;
            double d4 = (double)j + 0.0625 + (double)vector3i1.getY() * 0.5;
            double d5 = (double)k + 0.5 + (double)vector3i1.getZ() * 0.5;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0;
            double d8 = d5 - d2;
            if (d6 == 0.0) {
                d9 = z - (double)k;
            } else if (d8 == 0.0) {
                d9 = x - (double)i;
            } else {
                double d10 = x - d0;
                double d11 = z - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0;
            }
            x = d0 + d6 * d9;
            y = d1 + d7 * d9;
            z = d2 + d8 * d9;
            if (d7 < 0.0) {
                y += 1.0;
            } else if (d7 > 0.0) {
                y += 0.5;
            }
            return new Vector3d(x, y, z);
        }
        return null;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0) : axisalignedbb;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.getBoolean("CustomDisplayTile")) {
            this.setDisplayTile(NBTUtil.readBlockState(compound.getCompound("DisplayState")));
            this.setDisplayTileOffset(compound.getInt("DisplayOffset"));
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.hasDisplayTile()) {
            compound.putBoolean("CustomDisplayTile", true);
            compound.put("DisplayState", NBTUtil.writeBlockState(this.getDisplayTile()));
            compound.putInt("DisplayOffset", this.getDisplayTileOffset());
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn) {
        double d1;
        double d0;
        double d2;
        if (!(this.world.isRemote || entityIn.noClip || this.noClip || this.isPassenger(entityIn) || !((d2 = (d0 = entityIn.getPosX() - this.getPosX()) * d0 + (d1 = entityIn.getPosZ() - this.getPosZ()) * d1) >= (double)1.0E-4f))) {
            d2 = MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
                d3 = 1.0;
            }
            d0 *= d3;
            d1 *= d3;
            d0 *= (double)0.1f;
            d1 *= (double)0.1f;
            d0 *= (double)(1.0f - this.entityCollisionReduction);
            d1 *= (double)(1.0f - this.entityCollisionReduction);
            d0 *= 0.5;
            d1 *= 0.5;
            if (entityIn instanceof AbstractMinecartEntity) {
                Vector3d vector3d1;
                double d5;
                double d4 = entityIn.getPosX() - this.getPosX();
                Vector3d vector3d = new Vector3d(d4, 0.0, d5 = entityIn.getPosZ() - this.getPosZ()).normalize();
                double d6 = Math.abs(vector3d.dotProduct(vector3d1 = new Vector3d(MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180)), 0.0, MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180))).normalize()));
                if (d6 < (double)0.8f) {
                    return;
                }
                Vector3d vector3d2 = this.getMotion();
                Vector3d vector3d3 = entityIn.getMotion();
                if (((AbstractMinecartEntity)entityIn).getMinecartType() == Type.FURNACE && this.getMinecartType() != Type.FURNACE) {
                    this.setMotion(vector3d2.mul(0.2, 1.0, 0.2));
                    this.addVelocity(vector3d3.x - d0, 0.0, vector3d3.z - d1);
                    entityIn.setMotion(vector3d3.mul(0.95, 1.0, 0.95));
                } else if (((AbstractMinecartEntity)entityIn).getMinecartType() != Type.FURNACE && this.getMinecartType() == Type.FURNACE) {
                    entityIn.setMotion(vector3d3.mul(0.2, 1.0, 0.2));
                    entityIn.addVelocity(vector3d2.x + d0, 0.0, vector3d2.z + d1);
                    this.setMotion(vector3d2.mul(0.95, 1.0, 0.95));
                } else {
                    double d7 = (vector3d3.x + vector3d2.x) / 2.0;
                    double d8 = (vector3d3.z + vector3d2.z) / 2.0;
                    this.setMotion(vector3d2.mul(0.2, 1.0, 0.2));
                    this.addVelocity(d7 - d0, 0.0, d8 - d1);
                    entityIn.setMotion(vector3d3.mul(0.2, 1.0, 0.2));
                    entityIn.addVelocity(d7 + d0, 0.0, d8 + d1);
                }
            } else {
                this.addVelocity(-d0, 0.0, -d1);
                entityIn.addVelocity(d0 / 4.0, 0.0, d1 / 4.0);
            }
        }
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.minecartX = x;
        this.minecartY = y;
        this.minecartZ = z;
        this.minecartYaw = yaw;
        this.minecartPitch = pitch;
        this.turnProgress = posRotationIncrements + 2;
        this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
    }

    public void setDamage(float damage) {
        this.dataManager.set(DAMAGE, Float.valueOf(damage));
    }

    public float getDamage() {
        return this.dataManager.get(DAMAGE).floatValue();
    }

    public void setRollingAmplitude(int rollingAmplitude) {
        this.dataManager.set(ROLLING_AMPLITUDE, rollingAmplitude);
    }

    public int getRollingAmplitude() {
        return this.dataManager.get(ROLLING_AMPLITUDE);
    }

    public void setRollingDirection(int rollingDirection) {
        this.dataManager.set(ROLLING_DIRECTION, rollingDirection);
    }

    public int getRollingDirection() {
        return this.dataManager.get(ROLLING_DIRECTION);
    }

    public abstract Type getMinecartType();

    public BlockState getDisplayTile() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataManager().get(DISPLAY_TILE));
    }

    public BlockState getDefaultDisplayTile() {
        return Blocks.AIR.getDefaultState();
    }

    public int getDisplayTileOffset() {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataManager().get(DISPLAY_TILE_OFFSET).intValue();
    }

    public int getDefaultDisplayTileOffset() {
        return 6;
    }

    public void setDisplayTile(BlockState displayTile) {
        this.getDataManager().set(DISPLAY_TILE, Block.getStateId(displayTile));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int displayTileOffset) {
        this.getDataManager().set(DISPLAY_TILE_OFFSET, displayTileOffset);
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile() {
        return this.getDataManager().get(SHOW_BLOCK);
    }

    public void setHasDisplayTile(boolean showBlock) {
        this.getDataManager().set(SHOW_BLOCK, showBlock);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    public static enum Type {
        RIDEABLE,
        CHEST,
        FURNACE,
        TNT,
        SPAWNER,
        HOPPER,
        COMMAND_BLOCK;

    }
}
