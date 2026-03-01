package net.minecraft.entity.item;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.Validate;

public abstract class HangingEntity
extends Entity {
    protected static final Predicate<Entity> IS_HANGING_ENTITY = entity -> entity instanceof HangingEntity;
    private int tickCounter1;
    protected BlockPos hangingPosition;
    protected Direction facingDirection = Direction.SOUTH;

    protected HangingEntity(EntityType<? extends HangingEntity> type, World p_i48561_2_) {
        super(type, p_i48561_2_);
    }

    protected HangingEntity(EntityType<? extends HangingEntity> type, World world, BlockPos hangingPos) {
        this(type, world);
        this.hangingPosition = hangingPos;
    }

    @Override
    protected void registerData() {
    }

    protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
        this.facingDirection = facingDirectionIn;
        this.prevRotationYaw = this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
        this.updateBoundingBox();
    }

    protected void updateBoundingBox() {
        if (this.facingDirection != null) {
            double d0 = (double)this.hangingPosition.getX() + 0.5;
            double d1 = (double)this.hangingPosition.getY() + 0.5;
            double d2 = (double)this.hangingPosition.getZ() + 0.5;
            double d3 = 0.46875;
            double d4 = this.offs(this.getWidthPixels());
            double d5 = this.offs(this.getHeightPixels());
            d0 -= (double)this.facingDirection.getXOffset() * 0.46875;
            d2 -= (double)this.facingDirection.getZOffset() * 0.46875;
            Direction direction = this.facingDirection.rotateYCCW();
            this.setRawPosition(d0 += d4 * (double)direction.getXOffset(), d1 += d5, d2 += d4 * (double)direction.getZOffset());
            double d6 = this.getWidthPixels();
            double d7 = this.getHeightPixels();
            double d8 = this.getWidthPixels();
            if (this.facingDirection.getAxis() == Direction.Axis.Z) {
                d8 = 1.0;
            } else {
                d6 = 1.0;
            }
            this.setBoundingBox(new AxisAlignedBB(d0 - (d6 /= 32.0), d1 - (d7 /= 32.0), d2 - (d8 /= 32.0), d0 + d6, d1 + d7, d2 + d8));
        }
    }

    private double offs(int p_190202_1_) {
        return p_190202_1_ % 32 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote) {
            if (this.getPosY() < -64.0) {
                this.outOfWorld();
            }
            if (this.tickCounter1++ == 100) {
                this.tickCounter1 = 0;
                if (!this.removed && !this.onValidSurface()) {
                    this.remove();
                    this.onBroken(null);
                }
            }
        }
    }

    public boolean onValidSurface() {
        if (!this.world.hasNoCollisions(this)) {
            return false;
        }
        int i = Math.max(1, this.getWidthPixels() / 16);
        int j = Math.max(1, this.getHeightPixels() / 16);
        BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
        Direction direction = this.facingDirection.rotateYCCW();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int k = 0; k < i; ++k) {
            for (int l = 0; l < j; ++l) {
                int i1 = (i - 1) / -2;
                int j1 = (j - 1) / -2;
                blockpos$mutable.setPos(blockpos).move(direction, k + i1).move(Direction.UP, l + j1);
                BlockState blockstate = this.world.getBlockState(blockpos$mutable);
                if (blockstate.getMaterial().isSolid() || RedstoneDiodeBlock.isDiode(blockstate)) continue;
                return false;
            }
        }
        return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        if (entityIn instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entityIn;
            return !this.world.isBlockModifiable(playerentity, this.hangingPosition) ? true : this.attackEntityFrom(DamageSource.causePlayerDamage(playerentity), 0.0f);
        }
        return false;
    }

    @Override
    public Direction getHorizontalFacing() {
        return this.facingDirection;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.removed && !this.world.isRemote) {
            this.remove();
            this.markVelocityChanged();
            this.onBroken(source.getTrueSource());
        }
        return true;
    }

    @Override
    public void move(MoverType typeIn, Vector3d pos) {
        if (!this.world.isRemote && !this.removed && pos.lengthSquared() > 0.0) {
            this.remove();
            this.onBroken(null);
        }
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (!this.world.isRemote && !this.removed && x * x + y * y + z * z > 0.0) {
            this.remove();
            this.onBroken(null);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        BlockPos blockpos = this.getHangingPosition();
        compound.putInt("TileX", blockpos.getX());
        compound.putInt("TileY", blockpos.getY());
        compound.putInt("TileZ", blockpos.getZ());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.hangingPosition = new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    public abstract void onBroken(@Nullable Entity var1);

    public abstract void playPlaceSound();

    @Override
    public ItemEntity entityDropItem(ItemStack stack, float offsetY) {
        ItemEntity itementity = new ItemEntity(this.world, this.getPosX() + (double)((float)this.facingDirection.getXOffset() * 0.15f), this.getPosY() + (double)offsetY, this.getPosZ() + (double)((float)this.facingDirection.getZOffset() * 0.15f), stack);
        itementity.setDefaultPickupDelay();
        this.world.addEntity(itementity);
        return itementity;
    }

    @Override
    protected boolean shouldSetPosAfterLoading() {
        return false;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.hangingPosition = new BlockPos(x, y, z);
        this.updateBoundingBox();
        this.isAirBorne = true;
    }

    public BlockPos getHangingPosition() {
        return this.hangingPosition;
    }

    @Override
    public float getRotatedYaw(Rotation transformRotation) {
        if (this.facingDirection.getAxis() != Direction.Axis.Y) {
            switch (transformRotation) {
                case CLOCKWISE_180: {
                    this.facingDirection = this.facingDirection.getOpposite();
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    this.facingDirection = this.facingDirection.rotateYCCW();
                    break;
                }
                case CLOCKWISE_90: {
                    this.facingDirection = this.facingDirection.rotateY();
                }
            }
        }
        float f = MathHelper.wrapDegrees(this.rotationYaw);
        switch (transformRotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 90.0f;
            }
            case CLOCKWISE_90: {
                return f + 270.0f;
            }
        }
        return f;
    }

    @Override
    public float getMirroredYaw(Mirror transformMirror) {
        return this.getRotatedYaw(transformMirror.toRotation(this.facingDirection));
    }

    @Override
    public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
    }

    @Override
    public void recalculateSize() {
    }
}
