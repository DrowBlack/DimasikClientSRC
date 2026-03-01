package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.AabbHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class PistonTileEntity
extends TileEntity
implements ITickableTileEntity {
    private BlockState pistonState;
    private Direction pistonFacing;
    private boolean extending;
    private boolean shouldHeadBeRendered;
    private static final ThreadLocal<Direction> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long lastTicked;
    private int field_242697_l;

    public PistonTileEntity() {
        super(TileEntityType.PISTON);
    }

    public PistonTileEntity(BlockState pistonStateIn, Direction pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
        this();
        this.pistonState = pistonStateIn;
        this.pistonFacing = pistonFacingIn;
        this.extending = extendingIn;
        this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Direction getFacing() {
        return this.pistonFacing;
    }

    public boolean shouldPistonHeadBeRendered() {
        return this.shouldHeadBeRendered;
    }

    public float getProgress(float ticks) {
        if (ticks > 1.0f) {
            ticks = 1.0f;
        }
        return MathHelper.lerp(ticks, this.lastProgress, this.progress);
    }

    public float getOffsetX(float ticks) {
        return (float)this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    public float getOffsetY(float ticks) {
        return (float)this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    public float getOffsetZ(float ticks) {
        return (float)this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    private float getExtendedProgress(float p_184320_1_) {
        return this.extending ? p_184320_1_ - 1.0f : 1.0f - p_184320_1_;
    }

    private BlockState getCollisionRelatedBlockState() {
        return !this.isExtending() && this.shouldPistonHeadBeRendered() && this.pistonState.getBlock() instanceof PistonBlock ? (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.SHORT, this.progress > 0.25f)).with(PistonHeadBlock.TYPE, this.pistonState.isIn(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).with(PistonHeadBlock.FACING, this.pistonState.get(PistonBlock.FACING)) : this.pistonState;
    }

    private void moveCollidedEntities(float p_184322_1_) {
        AxisAlignedBB axisalignedbb;
        List<Entity> list;
        Direction direction = this.getMotionDirection();
        double d0 = p_184322_1_ - this.progress;
        VoxelShape voxelshape = this.getCollisionRelatedBlockState().getCollisionShape(this.world, this.getPos());
        if (!voxelshape.isEmpty() && !(list = this.world.getEntitiesWithinAABBExcludingEntity(null, AabbHelper.func_227019_a_(axisalignedbb = this.moveByPositionAndProgress(voxelshape.getBoundingBox()), direction, d0).union(axisalignedbb))).isEmpty()) {
            List<AxisAlignedBB> list1 = voxelshape.toBoundingBoxList();
            boolean flag = this.pistonState.isIn(Blocks.SLIME_BLOCK);
            Iterator<Entity> iterator = list.iterator();
            while (true) {
                AxisAlignedBB axisalignedbb3;
                AxisAlignedBB axisalignedbb2;
                AxisAlignedBB axisalignedbb1;
                if (!iterator.hasNext()) {
                    return;
                }
                Entity entity = iterator.next();
                if (entity.getPushReaction() == PushReaction.IGNORE) continue;
                if (flag) {
                    if (entity instanceof ServerPlayerEntity) continue;
                    Vector3d vector3d = entity.getMotion();
                    double d1 = vector3d.x;
                    double d2 = vector3d.y;
                    double d3 = vector3d.z;
                    switch (direction.getAxis()) {
                        case X: {
                            d1 = direction.getXOffset();
                            break;
                        }
                        case Y: {
                            d2 = direction.getYOffset();
                            break;
                        }
                        case Z: {
                            d3 = direction.getZOffset();
                        }
                    }
                    entity.setMotion(d1, d2, d3);
                }
                double d4 = 0.0;
                Iterator<AxisAlignedBB> iterator2 = list1.iterator();
                while (!(!iterator2.hasNext() || (axisalignedbb1 = AabbHelper.func_227019_a_(this.moveByPositionAndProgress(axisalignedbb2 = iterator2.next()), direction, d0)).intersects(axisalignedbb3 = entity.getBoundingBox()) && (d4 = Math.max(d4, PistonTileEntity.getMovement(axisalignedbb1, direction, axisalignedbb3))) >= d0)) {
                }
                if (d4 <= 0.0) continue;
                d4 = Math.min(d4, d0) + 0.01;
                PistonTileEntity.func_227022_a_(direction, entity, d4, direction);
                if (this.extending || !this.shouldHeadBeRendered) continue;
                this.fixEntityWithinPistonBase(entity, direction, d0);
            }
        }
    }

    private static void func_227022_a_(Direction p_227022_0_, Entity p_227022_1_, double p_227022_2_, Direction p_227022_4_) {
        MOVING_ENTITY.set(p_227022_0_);
        p_227022_1_.move(MoverType.PISTON, new Vector3d(p_227022_2_ * (double)p_227022_4_.getXOffset(), p_227022_2_ * (double)p_227022_4_.getYOffset(), p_227022_2_ * (double)p_227022_4_.getZOffset()));
        MOVING_ENTITY.set(null);
    }

    private void func_227024_g_(float p_227024_1_) {
        Direction direction;
        if (this.func_227025_y_() && (direction = this.getMotionDirection()).getAxis().isHorizontal()) {
            double d0 = this.pistonState.getCollisionShape(this.world, this.pos).getEnd(Direction.Axis.Y);
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(new AxisAlignedBB(0.0, d0, 0.0, 1.0, 1.5000000999999998, 1.0));
            double d1 = p_227024_1_ - this.progress;
            for (Entity entity : this.world.getEntitiesInAABBexcluding(null, axisalignedbb, p_227023_1_ -> PistonTileEntity.func_227021_a_(axisalignedbb, p_227023_1_))) {
                PistonTileEntity.func_227022_a_(direction, entity, d1, direction);
            }
        }
    }

    private static boolean func_227021_a_(AxisAlignedBB p_227021_0_, Entity p_227021_1_) {
        return p_227021_1_.getPushReaction() == PushReaction.NORMAL && p_227021_1_.isOnGround() && p_227021_1_.getPosX() >= p_227021_0_.minX && p_227021_1_.getPosX() <= p_227021_0_.maxX && p_227021_1_.getPosZ() >= p_227021_0_.minZ && p_227021_1_.getPosZ() <= p_227021_0_.maxZ;
    }

    private boolean func_227025_y_() {
        return this.pistonState.isIn(Blocks.HONEY_BLOCK);
    }

    public Direction getMotionDirection() {
        return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
    }

    private static double getMovement(AxisAlignedBB p_190612_0_, Direction p_190612_1_, AxisAlignedBB facing) {
        switch (p_190612_1_) {
            case EAST: {
                return p_190612_0_.maxX - facing.minX;
            }
            case WEST: {
                return facing.maxX - p_190612_0_.minX;
            }
            default: {
                return p_190612_0_.maxY - facing.minY;
            }
            case DOWN: {
                return facing.maxY - p_190612_0_.minY;
            }
            case SOUTH: {
                return p_190612_0_.maxZ - facing.minZ;
            }
            case NORTH: 
        }
        return facing.maxZ - p_190612_0_.minZ;
    }

    private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_) {
        double d0 = this.getExtendedProgress(this.progress);
        return p_190607_1_.offset((double)this.pos.getX() + d0 * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + d0 * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + d0 * (double)this.pistonFacing.getZOffset());
    }

    private void fixEntityWithinPistonBase(Entity p_190605_1_, Direction p_190605_2_, double p_190605_3_) {
        double d1;
        Direction direction;
        double d0;
        AxisAlignedBB axisalignedbb1;
        AxisAlignedBB axisalignedbb = p_190605_1_.getBoundingBox();
        if (axisalignedbb.intersects(axisalignedbb1 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos)) && Math.abs((d0 = PistonTileEntity.getMovement(axisalignedbb1, direction = p_190605_2_.getOpposite(), axisalignedbb) + 0.01) - (d1 = PistonTileEntity.getMovement(axisalignedbb1, direction, axisalignedbb.intersect(axisalignedbb1)) + 0.01)) < 0.01) {
            d0 = Math.min(d0, p_190605_3_) + 0.01;
            PistonTileEntity.func_227022_a_(p_190605_2_, p_190605_1_, d0, direction);
        }
    }

    public BlockState getPistonState() {
        return this.pistonState;
    }

    public void clearPistonTileEntity() {
        if (this.world != null && (this.lastProgress < 1.0f || this.world.isRemote)) {
            this.lastProgress = this.progress = 1.0f;
            this.world.removeTileEntity(this.pos);
            this.remove();
            if (this.world.getBlockState(this.pos).isIn(Blocks.MOVING_PISTON)) {
                BlockState blockstate = this.shouldHeadBeRendered ? Blocks.AIR.getDefaultState() : Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
                this.world.setBlockState(this.pos, blockstate, 3);
                this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);
            }
        }
    }

    @Override
    public void tick() {
        this.lastTicked = this.world.getGameTime();
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0f) {
            if (this.world.isRemote && this.field_242697_l < 5) {
                ++this.field_242697_l;
            } else {
                this.world.removeTileEntity(this.pos);
                this.remove();
                if (this.pistonState != null && this.world.getBlockState(this.pos).isIn(Blocks.MOVING_PISTON)) {
                    BlockState blockstate = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
                    if (blockstate.isAir()) {
                        this.world.setBlockState(this.pos, this.pistonState, 84);
                        Block.replaceBlock(this.pistonState, blockstate, this.world, this.pos, 3);
                    } else {
                        if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.get(BlockStateProperties.WATERLOGGED).booleanValue()) {
                            blockstate = (BlockState)blockstate.with(BlockStateProperties.WATERLOGGED, false);
                        }
                        this.world.setBlockState(this.pos, blockstate, 67);
                        this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);
                    }
                }
            }
        } else {
            float f = this.progress + 0.5f;
            this.moveCollidedEntities(f);
            this.func_227024_g_(f);
            this.progress = f;
            if (this.progress >= 1.0f) {
                this.progress = 1.0f;
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.pistonState = NBTUtil.readBlockState(nbt.getCompound("blockState"));
        this.pistonFacing = Direction.byIndex(nbt.getInt("facing"));
        this.lastProgress = this.progress = nbt.getFloat("progress");
        this.extending = nbt.getBoolean("extending");
        this.shouldHeadBeRendered = nbt.getBoolean("source");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put("blockState", NBTUtil.writeBlockState(this.pistonState));
        compound.putInt("facing", this.pistonFacing.getIndex());
        compound.putFloat("progress", this.lastProgress);
        compound.putBoolean("extending", this.extending);
        compound.putBoolean("source", this.shouldHeadBeRendered);
        return compound;
    }

    public VoxelShape getCollisionShape(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
        VoxelShape voxelshape = !this.extending && this.shouldHeadBeRendered ? ((BlockState)this.pistonState.with(PistonBlock.EXTENDED, true)).getCollisionShape(p_195508_1_, p_195508_2_) : VoxelShapes.empty();
        Direction direction = MOVING_ENTITY.get();
        if ((double)this.progress < 1.0 && direction == this.getMotionDirection()) {
            return voxelshape;
        }
        BlockState blockstate = this.shouldPistonHeadBeRendered() ? (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.pistonFacing)).with(PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 0.25f) : this.pistonState;
        float f = this.getExtendedProgress(this.progress);
        double d0 = (float)this.pistonFacing.getXOffset() * f;
        double d1 = (float)this.pistonFacing.getYOffset() * f;
        double d2 = (float)this.pistonFacing.getZOffset() * f;
        return VoxelShapes.or(voxelshape, blockstate.getCollisionShape(p_195508_1_, p_195508_2_).withOffset(d0, d1, d2));
    }

    public long getLastTicked() {
        return this.lastTicked;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 68.0;
    }
}
