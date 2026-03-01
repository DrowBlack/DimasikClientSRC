package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockStructureHelper {
    private final World world;
    private final BlockPos pistonPos;
    private final boolean extending;
    private final BlockPos blockToMove;
    private final Direction moveDirection;
    private final List<BlockPos> toMove = Lists.newArrayList();
    private final List<BlockPos> toDestroy = Lists.newArrayList();
    private final Direction facing;

    public PistonBlockStructureHelper(World worldIn, BlockPos posIn, Direction pistonFacing, boolean extending) {
        this.world = worldIn;
        this.pistonPos = posIn;
        this.facing = pistonFacing;
        this.extending = extending;
        if (extending) {
            this.moveDirection = pistonFacing;
            this.blockToMove = posIn.offset(pistonFacing);
        } else {
            this.moveDirection = pistonFacing.getOpposite();
            this.blockToMove = posIn.offset(pistonFacing, 2);
        }
    }

    public boolean canMove() {
        this.toMove.clear();
        this.toDestroy.clear();
        BlockState blockstate = this.world.getBlockState(this.blockToMove);
        if (!PistonBlock.canPush(blockstate, this.world, this.blockToMove, this.moveDirection, false, this.facing)) {
            if (this.extending && blockstate.getPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(this.blockToMove);
                return true;
            }
            return false;
        }
        if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
            return false;
        }
        for (int i = 0; i < this.toMove.size(); ++i) {
            BlockPos blockpos = this.toMove.get(i);
            if (!PistonBlockStructureHelper.func_227029_a_(this.world.getBlockState(blockpos).getBlock()) || this.addBranchingBlocks(blockpos)) continue;
            return false;
        }
        return true;
    }

    private static boolean func_227029_a_(Block p_227029_0_) {
        return p_227029_0_ == Blocks.SLIME_BLOCK || p_227029_0_ == Blocks.HONEY_BLOCK;
    }

    private static boolean func_227030_a_(Block p_227030_0_, Block p_227030_1_) {
        if (p_227030_0_ == Blocks.HONEY_BLOCK && p_227030_1_ == Blocks.SLIME_BLOCK) {
            return false;
        }
        if (p_227030_0_ == Blocks.SLIME_BLOCK && p_227030_1_ == Blocks.HONEY_BLOCK) {
            return false;
        }
        return PistonBlockStructureHelper.func_227029_a_(p_227030_0_) || PistonBlockStructureHelper.func_227029_a_(p_227030_1_);
    }

    private boolean addBlockLine(BlockPos origin, Direction facingIn) {
        BlockState blockstate = this.world.getBlockState(origin);
        Block block = blockstate.getBlock();
        if (blockstate.isAir()) {
            return true;
        }
        if (!PistonBlock.canPush(blockstate, this.world, origin, this.moveDirection, false, facingIn)) {
            return true;
        }
        if (origin.equals(this.pistonPos)) {
            return true;
        }
        if (this.toMove.contains(origin)) {
            return true;
        }
        int i = 1;
        if (i + this.toMove.size() > 12) {
            return false;
        }
        while (PistonBlockStructureHelper.func_227029_a_(block)) {
            BlockPos blockpos = origin.offset(this.moveDirection.getOpposite(), i);
            Block block1 = block;
            blockstate = this.world.getBlockState(blockpos);
            block = blockstate.getBlock();
            if (blockstate.isAir() || !PistonBlockStructureHelper.func_227030_a_(block1, block) || !PistonBlock.canPush(blockstate, this.world, blockpos, this.moveDirection, false, this.moveDirection.getOpposite()) || blockpos.equals(this.pistonPos)) break;
            if (++i + this.toMove.size() <= 12) continue;
            return false;
        }
        int l = 0;
        for (int i1 = i - 1; i1 >= 0; --i1) {
            this.toMove.add(origin.offset(this.moveDirection.getOpposite(), i1));
            ++l;
        }
        int j1 = 1;
        while (true) {
            BlockPos blockpos1;
            int j;
            if ((j = this.toMove.indexOf(blockpos1 = origin.offset(this.moveDirection, j1))) > -1) {
                this.reorderListAtCollision(l, j);
                for (int k = 0; k <= j + l; ++k) {
                    BlockPos blockpos2 = this.toMove.get(k);
                    if (!PistonBlockStructureHelper.func_227029_a_(this.world.getBlockState(blockpos2).getBlock()) || this.addBranchingBlocks(blockpos2)) continue;
                    return false;
                }
                return true;
            }
            blockstate = this.world.getBlockState(blockpos1);
            if (blockstate.isAir()) {
                return true;
            }
            if (!PistonBlock.canPush(blockstate, this.world, blockpos1, this.moveDirection, true, this.moveDirection) || blockpos1.equals(this.pistonPos)) {
                return false;
            }
            if (blockstate.getPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(blockpos1);
                return true;
            }
            if (this.toMove.size() >= 12) {
                return false;
            }
            this.toMove.add(blockpos1);
            ++l;
            ++j1;
        }
    }

    private void reorderListAtCollision(int offsets, int index) {
        ArrayList<BlockPos> list = Lists.newArrayList();
        ArrayList<BlockPos> list1 = Lists.newArrayList();
        ArrayList<BlockPos> list2 = Lists.newArrayList();
        list.addAll(this.toMove.subList(0, index));
        list1.addAll(this.toMove.subList(this.toMove.size() - offsets, this.toMove.size()));
        list2.addAll(this.toMove.subList(index, this.toMove.size() - offsets));
        this.toMove.clear();
        this.toMove.addAll(list);
        this.toMove.addAll(list1);
        this.toMove.addAll(list2);
    }

    private boolean addBranchingBlocks(BlockPos fromPos) {
        BlockState blockstate = this.world.getBlockState(fromPos);
        for (Direction direction : Direction.values()) {
            BlockPos blockpos;
            BlockState blockstate1;
            if (direction.getAxis() == this.moveDirection.getAxis() || !PistonBlockStructureHelper.func_227030_a_((blockstate1 = this.world.getBlockState(blockpos = fromPos.offset(direction))).getBlock(), blockstate.getBlock()) || this.addBlockLine(blockpos, direction)) continue;
            return false;
        }
        return true;
    }

    public List<BlockPos> getBlocksToMove() {
        return this.toMove;
    }

    public List<BlockPos> getBlocksToDestroy() {
        return this.toDestroy;
    }
}
