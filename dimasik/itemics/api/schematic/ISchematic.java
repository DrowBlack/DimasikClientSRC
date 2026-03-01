package dimasik.itemics.api.schematic;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public interface ISchematic {
    default public boolean inSchematic(int x, int y, int z, BlockState currentState) {
        return x >= 0 && x < this.widthX() && y >= 0 && y < this.heightY() && z >= 0 && z < this.lengthZ();
    }

    default public int size(Direction.Axis axis) {
        switch (axis) {
            case X: {
                return this.widthX();
            }
            case Y: {
                return this.heightY();
            }
            case Z: {
                return this.lengthZ();
            }
        }
        throw new UnsupportedOperationException(String.valueOf(axis));
    }

    public BlockState desiredState(int var1, int var2, int var3, BlockState var4, List<BlockState> var5);

    default public void reset() {
    }

    public int widthX();

    public int heightY();

    public int lengthZ();
}
