package dimasik.itemics.pathing.movement.movements;

import com.google.common.collect.ImmutableSet;
import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.pathing.movement.MovementStatus;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Rotation;
import dimasik.itemics.api.utils.RotationUtils;
import dimasik.itemics.api.utils.VecUtils;
import dimasik.itemics.api.utils.input.Input;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.pathing.movement.Movement;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.pathing.movement.MovementState;
import dimasik.itemics.utils.BlockStateInterface;
import java.util.Set;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class MovementPillar
extends Movement {
    public MovementPillar(IItemics itemics, BetterBlockPos start, BetterBlockPos end) {
        super(itemics, start, end, new BetterBlockPos[]{start.up(2)}, start);
    }

    @Override
    public double calculateCost(CalculationContext context) {
        return MovementPillar.cost(context, this.src.x, this.src.y, this.src.z);
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        return ImmutableSet.of(this.src, this.dest);
    }

    public static double cost(CalculationContext context, int x, int y, int z) {
        BlockState fromState = context.get(x, y, z);
        Block from = fromState.getBlock();
        boolean ladder = from == Blocks.LADDER || from == Blocks.VINE;
        BlockState fromDown = context.get(x, y - 1, z);
        if (!ladder) {
            if (fromDown.getBlock() == Blocks.LADDER || fromDown.getBlock() == Blocks.VINE) {
                return 1000000.0;
            }
            if (fromDown.getBlock() instanceof SlabBlock && fromDown.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                return 1000000.0;
            }
        }
        if (from == Blocks.VINE && !MovementPillar.hasAgainst(context, x, y, z)) {
            return 1000000.0;
        }
        BlockState toBreak = context.get(x, y + 2, z);
        Block toBreakBlock = toBreak.getBlock();
        if (toBreakBlock instanceof FenceGateBlock) {
            return 1000000.0;
        }
        BlockState srcUp = null;
        if (MovementHelper.isWater(toBreak) && MovementHelper.isWater(fromState) && MovementHelper.isWater(srcUp = context.get(x, y + 1, z))) {
            return 8.51063829787234;
        }
        double placeCost = 0.0;
        if (!ladder) {
            placeCost = context.costOfPlacingAt(x, y, z, fromState);
            if (placeCost >= 1000000.0) {
                return 1000000.0;
            }
            if (fromDown.getBlock() instanceof AirBlock) {
                placeCost += 0.1;
            }
        }
        if (MovementHelper.isLiquid(fromState) && !MovementHelper.canPlaceAgainst(context.bsi, x, y - 1, z, fromDown) || MovementHelper.isLiquid(fromDown) && context.assumeWalkOnWater) {
            return 1000000.0;
        }
        if ((from == Blocks.LILY_PAD || from instanceof CarpetBlock) && !fromDown.getFluidState().isEmpty()) {
            return 1000000.0;
        }
        double hardness = MovementHelper.getMiningDurationTicks(context, x, y + 2, z, toBreak, true);
        if (hardness >= 1000000.0) {
            return 1000000.0;
        }
        if (hardness != 0.0) {
            if (toBreakBlock == Blocks.LADDER || toBreakBlock == Blocks.VINE) {
                hardness = 0.0;
            } else {
                BlockState check = context.get(x, y + 3, z);
                if (check.getBlock() instanceof FallingBlock) {
                    if (srcUp == null) {
                        srcUp = context.get(x, y + 1, z);
                    }
                    if (!(toBreakBlock instanceof FallingBlock) || !(srcUp.getBlock() instanceof FallingBlock)) {
                        return 1000000.0;
                    }
                }
            }
        }
        if (ladder) {
            return 8.51063829787234 + hardness * 5.0;
        }
        return JUMP_ONE_BLOCK_COST + placeCost + context.jumpPenalty + hardness;
    }

    public static boolean hasAgainst(CalculationContext context, int x, int y, int z) {
        return MovementHelper.isBlockNormalCube(context.get(x + 1, y, z)) || MovementHelper.isBlockNormalCube(context.get(x - 1, y, z)) || MovementHelper.isBlockNormalCube(context.get(x, y, z + 1)) || MovementHelper.isBlockNormalCube(context.get(x, y, z - 1));
    }

    public static BlockPos getAgainst(CalculationContext context, BetterBlockPos vine) {
        if (MovementHelper.isBlockNormalCube(context.get(vine.north()))) {
            return vine.north();
        }
        if (MovementHelper.isBlockNormalCube(context.get(vine.south()))) {
            return vine.south();
        }
        if (MovementHelper.isBlockNormalCube(context.get(vine.east()))) {
            return vine.east();
        }
        if (MovementHelper.isBlockNormalCube(context.get(vine.west()))) {
            return vine.west();
        }
        return null;
    }

    @Override
    public MovementState updateState(MovementState state) {
        boolean blockIsThere;
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        if (this.ctx.playerFeet().y < this.src.y) {
            return state.setStatus(MovementStatus.UNREACHABLE);
        }
        BlockState fromDown = BlockStateInterface.get(this.ctx, this.src);
        if (MovementHelper.isWater(fromDown) && MovementHelper.isWater(this.ctx, this.dest)) {
            state.setTarget(new MovementState.MovementTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.dest), this.ctx.playerRotations()), false));
            Vector3d destCenter = VecUtils.getBlockPosCenter(this.dest);
            if (Math.abs(this.ctx.player().getPositionVec().x - destCenter.x) > 0.2 || Math.abs(this.ctx.player().getPositionVec().z - destCenter.z) > 0.2) {
                state.setInput(Input.MOVE_FORWARD, true);
            }
            if (this.ctx.playerFeet().equals(this.dest)) {
                return state.setStatus(MovementStatus.SUCCESS);
            }
            return state;
        }
        boolean ladder = fromDown.getBlock() == Blocks.LADDER || fromDown.getBlock() == Blocks.VINE;
        boolean vine = fromDown.getBlock() == Blocks.VINE;
        Rotation rotation = RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.positionToPlace), new Rotation(this.ctx.player().packetYaw, this.ctx.player().packetPitch));
        if (!ladder) {
            state.setTarget(new MovementState.MovementTarget(new Rotation(this.ctx.player().packetYaw, rotation.getPitch()), true));
        }
        boolean bl = blockIsThere = MovementHelper.canWalkOn(this.ctx, this.src) || ladder;
        if (ladder) {
            BlockPos against;
            BlockPos blockPos = against = vine ? MovementPillar.getAgainst(new CalculationContext(this.itemics), this.src) : this.src.offset(fromDown.get(LadderBlock.FACING).getOpposite());
            if (against == null) {
                this.logDirect("Unable to climb vines. Consider disabling allowVines.");
                return state.setStatus(MovementStatus.UNREACHABLE);
            }
            if (this.ctx.playerFeet().equals(against.up()) || this.ctx.playerFeet().equals(this.dest)) {
                return state.setStatus(MovementStatus.SUCCESS);
            }
            if (MovementHelper.isBottomSlab(BlockStateInterface.get(this.ctx, this.src.down()))) {
                state.setInput(Input.JUMP, true);
            }
            MovementHelper.moveTowards(this.ctx, state, against);
            return state;
        }
        if (!((Itemics)this.itemics).getInventoryBehavior().selectThrowawayForLocation(true, this.src.x, this.src.y, this.src.z)) {
            return state.setStatus(MovementStatus.UNREACHABLE);
        }
        state.setInput(Input.SNEAK, this.ctx.player().getPositionVec().y > (double)this.dest.getY() || this.ctx.player().getPositionVec().y < (double)this.src.getY() + 0.2);
        double diffX = this.ctx.player().getPositionVec().x - ((double)this.dest.getX() + 0.5);
        double diffZ = this.ctx.player().getPositionVec().z - ((double)this.dest.getZ() + 0.5);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double flatMotion = Math.sqrt(this.ctx.player().getMotion().x * this.ctx.player().getMotion().x + this.ctx.player().getMotion().z * this.ctx.player().getMotion().z);
        if (dist > 0.17) {
            state.setInput(Input.MOVE_FORWARD, true);
            state.setTarget(new MovementState.MovementTarget(rotation, true));
        } else if (flatMotion < 0.05) {
            state.setInput(Input.JUMP, this.ctx.player().getPositionVec().y < (double)this.dest.getY());
        }
        if (!blockIsThere) {
            BlockState frState = BlockStateInterface.get(this.ctx, this.src);
            Block fr = frState.getBlock();
            if (!(fr instanceof AirBlock) && !frState.getMaterial().isReplaceable()) {
                RotationUtils.reachable(this.ctx.player(), (BlockPos)this.src, this.ctx.playerController().getBlockReachDistance()).map(rot -> new MovementState.MovementTarget((Rotation)rot, true)).ifPresent(state::setTarget);
                state.setInput(Input.JUMP, false);
                state.setInput(Input.CLICK_LEFT, true);
                blockIsThere = false;
            } else if (this.ctx.player().isCrouching() && (this.ctx.isLookingAt(this.src.down()) || this.ctx.isLookingAt(this.src)) && this.ctx.player().getPositionVec().y > (double)this.dest.getY() + 0.1) {
                state.setInput(Input.CLICK_RIGHT, true);
            }
        }
        if (this.ctx.playerFeet().equals(this.dest) && blockIsThere) {
            return state.setStatus(MovementStatus.SUCCESS);
        }
        return state;
    }

    @Override
    protected boolean prepared(MovementState state) {
        Block block;
        if ((this.ctx.playerFeet().equals(this.src) || this.ctx.playerFeet().equals(this.src.down())) && ((block = BlockStateInterface.getBlock(this.ctx, this.src.down())) == Blocks.LADDER || block == Blocks.VINE)) {
            state.setInput(Input.SNEAK, true);
        }
        if (MovementHelper.isWater(this.ctx, this.dest.up())) {
            return true;
        }
        return super.prepared(state);
    }
}
