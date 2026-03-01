package dimasik.itemics.pathing.movement.movements;

import com.google.common.collect.ImmutableSet;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.pathing.movement.MovementStatus;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.pathing.movement.Movement;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.pathing.movement.MovementState;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class MovementDownward
extends Movement {
    private int numTicks = 0;

    public MovementDownward(IItemics itemics, BetterBlockPos start, BetterBlockPos end) {
        super(itemics, start, end, new BetterBlockPos[]{end});
    }

    @Override
    public void reset() {
        super.reset();
        this.numTicks = 0;
    }

    @Override
    public double calculateCost(CalculationContext context) {
        return MovementDownward.cost(context, this.src.x, this.src.y, this.src.z);
    }

    @Override
    protected Set<BetterBlockPos> calculateValidPositions() {
        return ImmutableSet.of(this.src, this.dest);
    }

    public static double cost(CalculationContext context, int x, int y, int z) {
        if (!context.allowDownward) {
            return 1000000.0;
        }
        if (!MovementHelper.canWalkOn(context, x, y - 2, z)) {
            return 1000000.0;
        }
        BlockState down = context.get(x, y - 1, z);
        Block downBlock = down.getBlock();
        if (downBlock == Blocks.LADDER || downBlock == Blocks.VINE) {
            return 6.666666666666667;
        }
        return FALL_N_BLOCKS_COST[1] + MovementHelper.getMiningDurationTicks(context, x, y - 1, z, down, false);
    }

    @Override
    public MovementState updateState(MovementState state) {
        super.updateState(state);
        if (state.getStatus() != MovementStatus.RUNNING) {
            return state;
        }
        if (this.ctx.playerFeet().equals(this.dest)) {
            return state.setStatus(MovementStatus.SUCCESS);
        }
        if (!this.playerInValidPosition()) {
            return state.setStatus(MovementStatus.UNREACHABLE);
        }
        double diffX = this.ctx.player().getPositionVec().x - ((double)this.dest.getX() + 0.5);
        double diffZ = this.ctx.player().getPositionVec().z - ((double)this.dest.getZ() + 0.5);
        double ab = Math.sqrt(diffX * diffX + diffZ * diffZ);
        if (this.numTicks++ < 10 && ab < 0.2) {
            return state;
        }
        MovementHelper.moveTowards(this.ctx, state, this.positionsToBreak[0]);
        return state;
    }
}
