package dimasik.itemics.utils.schematic.format;

import dimasik.itemics.api.schematic.IStaticSchematic;
import dimasik.itemics.api.schematic.format.ISchematicFormat;
import dimasik.itemics.utils.schematic.format.defaults.LitematicaSchematic;
import dimasik.itemics.utils.schematic.format.defaults.MCEditSchematic;
import dimasik.itemics.utils.schematic.format.defaults.SpongeSchematic;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.commons.io.FilenameUtils;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum DefaultSchematicFormats implements ISchematicFormat
{
    MCEDIT("schematic"){

        @Override
        public IStaticSchematic parse(InputStream input) throws IOException {
            return new MCEditSchematic(CompressedStreamTools.readCompressed(input));
        }
    }
    ,
    SPONGE("schem"){

        @Override
        public IStaticSchematic parse(InputStream input) throws IOException {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(input);
            int version = nbt.getInt("Version");
            switch (version) {
                case 1: 
                case 2: {
                    return new SpongeSchematic(nbt);
                }
            }
            throw new UnsupportedOperationException("Unsupported Version of a Sponge Schematic");
        }
    }
    ,
    LITEMATICA("litematic"){

        @Override
        public IStaticSchematic parse(InputStream input) throws IOException {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(input);
            int version = nbt.getInt("Version");
            switch (version) {
                case 4: {
                    throw new UnsupportedOperationException("This litematic Version is too old.");
                }
                case 5: {
                    return new LitematicaSchematic(nbt, false);
                }
                case 6: {
                    throw new UnsupportedOperationException("This litematic Version is too new.");
                }
            }
            throw new UnsupportedOperationException("Unsuported Version of a Litematica Schematic");
        }
    };

    private final String extension;

    private DefaultSchematicFormats(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean isFileType(File file) {
        return this.extension.equalsIgnoreCase(FilenameUtils.getExtension(file.getAbsolutePath()));
    }
}
