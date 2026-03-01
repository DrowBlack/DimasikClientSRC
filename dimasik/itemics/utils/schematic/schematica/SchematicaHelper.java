package dimasik.itemics.utils.schematic.schematica;

import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import dimasik.itemics.api.schematic.IStaticSchematic;
import dimasik.itemics.utils.schematic.schematica.SchematicAdapter;
import java.util.Optional;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

public final class SchematicaHelper
extends Enum<SchematicaHelper> {
    private static final /* synthetic */ SchematicaHelper[] $VALUES;

    public static SchematicaHelper[] values() {
        return (SchematicaHelper[])$VALUES.clone();
    }

    public static SchematicaHelper valueOf(String name) {
        return Enum.valueOf(SchematicaHelper.class, name);
    }

    public static boolean isSchematicaPresent() {
        try {
            Class.forName(Schematica.class.getName());
            return true;
        }
        catch (ClassNotFoundException | NoClassDefFoundError ex) {
            return false;
        }
    }

    public static Optional<Tuple<IStaticSchematic, BlockPos>> getOpenSchematic() {
        return Optional.ofNullable(ClientProxy.schematic).map(world -> new Tuple<SchematicAdapter, MBlockPos>(new SchematicAdapter((SchematicWorld)world), world.position));
    }

    private static /* synthetic */ SchematicaHelper[] $values() {
        return new SchematicaHelper[0];
    }

    static {
        $VALUES = SchematicaHelper.$values();
    }
}
