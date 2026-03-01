package dimasik.itemics.utils.schematic;

import dimasik.itemics.api.command.registry.Registry;
import dimasik.itemics.api.schematic.ISchematicSystem;
import dimasik.itemics.api.schematic.format.ISchematicFormat;
import dimasik.itemics.utils.schematic.format.DefaultSchematicFormats;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public enum SchematicSystem implements ISchematicSystem
{
    INSTANCE;

    private final Registry<ISchematicFormat> registry = new Registry();

    private SchematicSystem() {
        Arrays.stream(DefaultSchematicFormats.values()).forEach(this.registry::register);
    }

    @Override
    public Registry<ISchematicFormat> getRegistry() {
        return this.registry;
    }

    @Override
    public Optional<ISchematicFormat> getByFile(File file) {
        return this.registry.stream().filter(format -> format.isFileType(file)).findFirst();
    }
}
