package net.minecraftforge.forgespi.locating;

import java.nio.file.Path;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

public interface ModFileFactory {
    public static final ModFileFactory FACTORY = Environment.get().getModFileFactory();

    public IModFile build(Path var1, IModLocator var2, ModFileInfoParser var3);

    public static interface ModFileInfoParser {
        public IModFileInfo build(IModFile var1);
    }
}
