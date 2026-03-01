package dimasik.itemics.utils.schematic;

import dimasik.itemics.api.schematic.AbstractSchematic;
import dimasik.itemics.api.schematic.IStaticSchematic;
import java.util.List;
import net.minecraft.block.BlockState;

public class StaticSchematic
extends AbstractSchematic
implements IStaticSchematic {
    protected BlockState[][][] states;

    @Override
    public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
        return this.states[x][z][y];
    }

    @Override
    public BlockState getDirect(int x, int y, int z) {
        return this.states[x][z][y];
    }

    @Override
    public BlockState[] getColumn(int x, int z) {
        return this.states[x][z];
    }
}
