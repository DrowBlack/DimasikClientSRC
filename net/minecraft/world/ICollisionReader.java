package net.minecraft.world;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeSpliterator;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.border.WorldBorder;

public interface ICollisionReader
extends IBlockReader {
    public WorldBorder getWorldBorder();

    @Nullable
    public IBlockReader getBlockReader(int var1, int var2);

    default public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
        return true;
    }

    default public boolean placedBlockCollides(BlockState state, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = state.getCollisionShape(this, pos, context);
        return voxelshape.isEmpty() || this.checkNoEntityCollision(null, voxelshape.withOffset(pos.getX(), pos.getY(), pos.getZ()));
    }

    default public boolean checkNoEntityCollision(Entity entity) {
        return this.checkNoEntityCollision(entity, VoxelShapes.create(entity.getBoundingBox()));
    }

    default public boolean hasNoCollisions(AxisAlignedBB aabb) {
        return this.hasNoCollisions(null, aabb, entity -> true);
    }

    default public boolean hasNoCollisions(Entity entity) {
        return this.hasNoCollisions(entity, entity.getBoundingBox(), entity2 -> true);
    }

    default public boolean hasNoCollisions(Entity entity, AxisAlignedBB aabb) {
        return this.hasNoCollisions(entity, aabb, entity2 -> true);
    }

    default public boolean hasNoCollisions(@Nullable Entity entity, AxisAlignedBB aabb, Predicate<Entity> entityPredicate) {
        return this.func_234867_d_(entity, aabb, entityPredicate).allMatch(VoxelShape::isEmpty);
    }

    public Stream<VoxelShape> func_230318_c_(@Nullable Entity var1, AxisAlignedBB var2, Predicate<Entity> var3);

    default public Stream<VoxelShape> func_234867_d_(@Nullable Entity entity, AxisAlignedBB aabb, Predicate<Entity> entityPredicate) {
        return Stream.concat(this.getCollisionShapes(entity, aabb), this.func_230318_c_(entity, aabb, entityPredicate));
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, AxisAlignedBB aabb) {
        return StreamSupport.stream(new VoxelShapeSpliterator(this, entity, aabb), false);
    }

    default public boolean func_242405_a(@Nullable Entity p_242405_1_, AxisAlignedBB p_242405_2_, BiPredicate<BlockState, BlockPos> p_242405_3_) {
        return this.func_241457_a_(p_242405_1_, p_242405_2_, p_242405_3_).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> func_241457_a_(@Nullable Entity entity, AxisAlignedBB aabb, BiPredicate<BlockState, BlockPos> statePosPredicate) {
        return StreamSupport.stream(new VoxelShapeSpliterator(this, entity, aabb, statePosPredicate), false);
    }
}
