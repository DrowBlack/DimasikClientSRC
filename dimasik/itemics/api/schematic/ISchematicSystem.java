package dimasik.itemics.api.schematic;

import dimasik.itemics.api.command.registry.Registry;
import dimasik.itemics.api.schematic.format.ISchematicFormat;
import java.io.File;
import java.util.Optional;

public interface ISchematicSystem {
    public Registry<ISchematicFormat> getRegistry();

    public Optional<ISchematicFormat> getByFile(File var1);
}
