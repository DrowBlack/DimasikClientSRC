package fi.dy.masa.litematica.schematic.placement;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class SchematicPlacementUnloaded {
    protected String name = "?";
    @Nullable
    protected File schematicFile;
    protected BlockPos origin = BlockPos.ZERO;

    public String getName() {
        return this.name;
    }

    @Nullable
    public File getSchematicFile() {
        return this.schematicFile;
    }

    public BlockPos getOrigin() {
        return this.origin;
    }
}
