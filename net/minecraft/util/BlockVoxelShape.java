package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum BlockVoxelShape {
    FULL{

        @Override
        public boolean func_241854_a(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
            return Block.doesSideFillSquare(p_241854_1_.getRenderShape(p_241854_2_, p_241854_3_), p_241854_4_);
        }
    }
    ,
    CENTER{
        private final int field_242680_d = 1;
        private final VoxelShape field_242681_e = Block.makeCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);

        @Override
        public boolean func_241854_a(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
            return !VoxelShapes.compare(p_241854_1_.getRenderShape(p_241854_2_, p_241854_3_).project(p_241854_4_), this.field_242681_e, IBooleanFunction.ONLY_SECOND);
        }
    }
    ,
    RIGID{
        private final int field_242682_d = 2;
        private final VoxelShape field_242683_e = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.makeCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), IBooleanFunction.ONLY_FIRST);

        @Override
        public boolean func_241854_a(BlockState p_241854_1_, IBlockReader p_241854_2_, BlockPos p_241854_3_, Direction p_241854_4_) {
            return !VoxelShapes.compare(p_241854_1_.getRenderShape(p_241854_2_, p_241854_3_).project(p_241854_4_), this.field_242683_e, IBooleanFunction.ONLY_SECOND);
        }
    };


    public abstract boolean func_241854_a(BlockState var1, IBlockReader var2, BlockPos var3, Direction var4);
}
