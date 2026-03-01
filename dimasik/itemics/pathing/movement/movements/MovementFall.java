package dimasik.itemics.pathing.movement.movements;

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
import dimasik.itemics.pathing.movement.movements.MovementDescend;
import dimasik.itemics.utils.pathing.MutableMoveResult;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class MovementFall
extends Movement {
    private static final ItemStack STACK_BUCKET_WATER = new ItemStack(Items.WATER_BUCKET);
    private static final ItemStack STACK_BUCKET_EMPTY = new ItemStack(Items.BUCKET);

    public MovementFall(IItemics itemics, BetterBlockPos src, BetterBlockPos dest) {
        super(itemics, src, dest, MovementFall.buildPositionsToBreak(src, dest));
    }

    @Override
    public double calculateCost(CalculationContext context) {
        MutableMoveResult result = new MutableMoveResult();
        MovementDescend.cost(context, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, result);
        if (result.y != this.dest.y) {
            return 1000000.0;
        }
        return result.cost;
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        HashSet<BetterBlockPos> set = new HashSet<BetterBlockPos>();
        set.add(this.src);
        for (int y = this.src.y - this.dest.y; y >= 0; --y) {
            set.add(this.dest.up(y));
        }
        return set;
    }

    private boolean willPlaceBucket() {
        CalculationContext context = new CalculationContext(this.itemics);
        MutableMoveResult result = new MutableMoveResult();
        return MovementDescend.dynamicFallCost(context, this.src.x, this.src.y, this.src.z, this.dest.x, this.dest.z, 0.0, context.get(this.dest.x, this.src.y - 2, this.dest.z), result);
    }

    @Override
    public MovementState updateState(MovementState state) {
        Vector3i avoid;
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        BetterBlockPos playerFeet = this.ctx.playerFeet();
        Rotation toDest = RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), VecUtils.getBlockPosCenter(this.dest), this.ctx.playerRotations());
        Rotation targetRotation = null;
        BlockState destState = this.ctx.world().getBlockState(this.dest);
        Block destBlock = destState.getBlock();
        boolean isWater = destState.getFluidState().getFluid() instanceof WaterFluid;
        if (!isWater && this.willPlaceBucket() && !((Vector3i)playerFeet).equals(this.dest)) {
            if (!PlayerInventory.isHotbar(this.ctx.player().inventory.getSlotFor(STACK_BUCKET_WATER)) || this.ctx.world().getDimensionKey() == World.THE_NETHER) {
                return state.setStatus(MovementStatus.UNREACHABLE);
            }
            if (this.ctx.player().getPositionVec().y - (double)this.dest.getY() < this.ctx.playerController().getBlockReachDistance() && !this.ctx.player().isOnGround()) {
                this.ctx.player().inventory.currentItem = this.ctx.player().inventory.getSlotFor(STACK_BUCKET_WATER);
                targetRotation = new Rotation(toDest.getYaw(), 90.0f);
                if (this.ctx.isLookingAt(this.dest) || this.ctx.isLookingAt(this.dest.down())) {
                    state.setInput(Input.CLICK_RIGHT, true);
                }
            }
        }
        if (targetRotation != null) {
            state.setTarget(new MovementState.MovementTarget(targetRotation, true));
        } else {
            state.setTarget(new MovementState.MovementTarget(toDest, false));
        }
        if (((Vector3i)playerFeet).equals(this.dest) && (this.ctx.player().getPositionVec().y - (double)playerFeet.getY() < 0.094 || isWater)) {
            if (isWater) {
                if (PlayerInventory.isHotbar(this.ctx.player().inventory.getSlotFor(STACK_BUCKET_EMPTY))) {
                    this.ctx.player().inventory.currentItem = this.ctx.player().inventory.getSlotFor(STACK_BUCKET_EMPTY);
                    if (this.ctx.player().getMotion().y >= 0.0) {
                        return state.setInput(Input.CLICK_RIGHT, true);
                    }
                    return state;
                }
                if (this.ctx.player().getMotion().y >= 0.0) {
                    return state.setStatus(MovementStatus.SUCCESS);
                }
            } else {
                return state.setStatus(MovementStatus.SUCCESS);
            }
        }
        Vector3d destCenter = VecUtils.getBlockPosCenter(this.dest);
        if (Math.abs(this.ctx.player().getPositionVec().x + this.ctx.player().getMotion().x - destCenter.x) > 0.1 || Math.abs(this.ctx.player().getPositionVec().z + this.ctx.player().getMotion().z - destCenter.z) > 0.1) {
            if (!this.ctx.player().isOnGround() && Math.abs(this.ctx.player().getMotion().y) > 0.4) {
                state.setInput(Input.SNEAK, true);
            }
            state.setInput(Input.MOVE_FORWARD, true);
        }
        if ((avoid = (Vector3i)Optional.ofNullable(this.avoid()).map(Direction::getDirectionVec).orElse(null)) == null) {
            avoid = this.src.subtract(this.dest);
        } else {
            double dist = Math.abs((double)avoid.getX() * (destCenter.x - (double)avoid.getX() / 2.0 - this.ctx.player().getPositionVec().x)) + Math.abs((double)avoid.getZ() * (destCenter.z - (double)avoid.getZ() / 2.0 - this.ctx.player().getPositionVec().z));
            if (dist < 0.6) {
                state.setInput(Input.MOVE_FORWARD, true);
            } else if (!this.ctx.player().isOnGround()) {
                state.setInput(Input.SNEAK, false);
            }
        }
        if (targetRotation == null) {
            Vector3d destCenterOffset = new Vector3d(destCenter.x + 0.125 * (double)avoid.getX(), destCenter.y, destCenter.z + 0.125 * (double)avoid.getZ());
            state.setTarget(new MovementState.MovementTarget(RotationUtils.calcRotationFromVec3d(this.ctx.playerHead(), destCenterOffset, this.ctx.playerRotations()), false));
        }
        return state;
    }

    private Direction avoid() {
        for (int i = 0; i < 15; ++i) {
            BlockState state = this.ctx.world().getBlockState(this.ctx.playerFeet().down(i));
            if (state.getBlock() != Blocks.LADDER) continue;
            return state.get(LadderBlock.FACING);
        }
        return null;
    }

    @Override
    public boolean safeToCancel(MovementState state) {
        return this.ctx.playerFeet().equals(this.src) || state.getStatus() != MovementStatus.RUNNING;
    }

    private static BetterBlockPos[] buildPositionsToBreak(BetterBlockPos src, BetterBlockPos dest) {
        int diffX = src.getX() - dest.getX();
        int diffZ = src.getZ() - dest.getZ();
        int diffY = src.getY() - dest.getY();
        BetterBlockPos[] toBreak = new BetterBlockPos[diffY + 2];
        for (int i = 0; i < toBreak.length; ++i) {
            toBreak[i] = new BetterBlockPos(src.getX() - diffX, src.getY() + 1 - i, src.getZ() - diffZ);
        }
        return toBreak;
    }

    @Override
    protected boolean prepared(MovementState state) {
        if (state.getStatus() == MovementStatus.WAITING) {
            return true;
        }
        for (int i = 0; i < 4 && i < this.positionsToBreak.length; ++i) {
            if (MovementHelper.canWalkThrough(this.ctx, this.positionsToBreak[i])) continue;
            return super.prepared(state);
        }
        return true;
    }
}
