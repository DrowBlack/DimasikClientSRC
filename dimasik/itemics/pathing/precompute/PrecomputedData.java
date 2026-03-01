package dimasik.itemics.pathing.precompute;

import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.pathing.precompute.Ternary;
import dimasik.itemics.utils.BlockStateInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class PrecomputedData {
    private final int[] data = new int[Block.BLOCK_STATE_IDS.size()];
    private static final int COMPLETED_MASK = 1;
    private static final int CAN_WALK_ON_MASK = 2;
    private static final int CAN_WALK_ON_SPECIAL_MASK = 4;
    private static final int CAN_WALK_THROUGH_MASK = 8;
    private static final int CAN_WALK_THROUGH_SPECIAL_MASK = 16;
    private static final int FULLY_PASSABLE_MASK = 32;
    private static final int FULLY_PASSABLE_SPECIAL_MASK = 64;

    private int fillData(int id, BlockState state) {
        Ternary fullyPassableState;
        Ternary canWalkThroughState;
        int blockData = 0;
        Ternary canWalkOnState = MovementHelper.canWalkOnBlockState(state);
        if (canWalkOnState == Ternary.YES) {
            blockData |= 2;
        }
        if (canWalkOnState == Ternary.MAYBE) {
            blockData |= 4;
        }
        if ((canWalkThroughState = MovementHelper.canWalkThroughBlockState(state)) == Ternary.YES) {
            blockData |= 8;
        }
        if (canWalkThroughState == Ternary.MAYBE) {
            blockData |= 0x10;
        }
        if ((fullyPassableState = MovementHelper.fullyPassableBlockState(state)) == Ternary.YES) {
            blockData |= 0x20;
        }
        if (fullyPassableState == Ternary.MAYBE) {
            blockData |= 0x40;
        }
        this.data[id] = blockData |= 1;
        return blockData;
    }

    public boolean canWalkOn(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        int id = Block.BLOCK_STATE_IDS.getId(state);
        int blockData = this.data[id];
        if ((blockData & 1) == 0) {
            blockData = this.fillData(id, state);
        }
        if ((blockData & 4) != 0) {
            return MovementHelper.canWalkOnPosition(bsi, x, y, z, state);
        }
        return (blockData & 2) != 0;
    }

    public boolean canWalkThrough(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        int id = Block.BLOCK_STATE_IDS.getId(state);
        int blockData = this.data[id];
        if ((blockData & 1) == 0) {
            blockData = this.fillData(id, state);
        }
        if ((blockData & 0x10) != 0) {
            return MovementHelper.canWalkThroughPosition(bsi, x, y, z, state);
        }
        return (blockData & 8) != 0;
    }

    public boolean fullyPassable(BlockStateInterface bsi, int x, int y, int z, BlockState state) {
        int id = Block.BLOCK_STATE_IDS.getId(state);
        int blockData = this.data[id];
        if ((blockData & 1) == 0) {
            blockData = this.fillData(id, state);
        }
        if ((blockData & 0x40) != 0) {
            return MovementHelper.fullyPassablePosition(bsi, x, y, z, state);
        }
        return (blockData & 0x20) != 0;
    }
}
