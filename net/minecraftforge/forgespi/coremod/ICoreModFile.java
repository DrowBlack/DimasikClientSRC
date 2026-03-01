package net.minecraftforge.forgespi.coremod;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public interface ICoreModFile {
    public String getOwnerId();

    public Reader readCoreMod() throws IOException;

    public Path getPath();

    public Reader getAdditionalFile(String var1) throws IOException;
}
