package dimasik.itemics.utils.schematic.schematica;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import dimasik.itemics.api.schematic.IStaticSchematic;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public final class SchematicAdapter
implements IStaticSchematic {
    private final SchematicWorld schematic;

    public SchematicAdapter(SchematicWorld schematicWorld) {
        this.schematic = schematicWorld;
    }

    @Override
    public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
        return this.getDirect(x, y, z);
    }

    @Override
    public BlockState getDirect(int x, int y, int z) {
        return this.schematic.getSchematic().getBlockState(new BlockPos(x, y, z));
    }

    @Override
    public int widthX() {
        return this.schematic.getSchematic().getWidth();
    }

    @Override
    public int heightY() {
        return this.schematic.getSchematic().getHeight();
    }

    @Override
    public int lengthZ() {
        return this.schematic.getSchematic().getLength();
    }
}
