package dimasik.itemics.api.schematic;

import dimasik.itemics.api.schematic.AbstractSchematic;
import dimasik.itemics.api.utils.BlockOptionalMeta;
import java.util.List;
import net.minecraft.block.BlockState;

public class FillSchematic
extends AbstractSchematic {
    private final BlockOptionalMeta bom;

    public FillSchematic(int x, int y, int z, BlockOptionalMeta bom) {
        super(x, y, z);
        this.bom = bom;
    }

    public FillSchematic(int x, int y, int z, BlockState state) {
        this(x, y, z, new BlockOptionalMeta(state.getBlock()));
    }

    public BlockOptionalMeta getBom() {
        return this.bom;
    }

    @Override
    public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
        if (this.bom.matches(current)) {
            return current;
        }
        for (BlockState placeable : approxPlaceable) {
            if (!this.bom.matches(placeable)) continue;
            return placeable;
        }
        return this.bom.getAnyBlockState();
    }
}
