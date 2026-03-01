package dimasik.itemics.api.schematic.format;

import dimasik.itemics.api.schematic.IStaticSchematic;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ISchematicFormat {
    public IStaticSchematic parse(InputStream var1) throws IOException;

    public boolean isFileType(File var1);
}
