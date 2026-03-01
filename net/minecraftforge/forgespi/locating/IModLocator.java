package net.minecraftforge.forgespi.locating;

import java.nio.file.Path;
import java.security.CodeSigner;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.Manifest;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.commons.lang3.tuple.Pair;

public interface IModLocator {
    public List<IModFile> scanMods();

    public String name();

    public Path findPath(IModFile var1, String ... var2);

    public void scanFile(IModFile var1, Consumer<Path> var2);

    public Optional<Manifest> findManifest(Path var1);

    default public Pair<Optional<Manifest>, Optional<CodeSigner[]>> findManifestAndSigners(Path file) {
        return Pair.of(this.findManifest(file), Optional.empty());
    }

    public void initArguments(Map<String, ?> var1);

    public boolean isValid(IModFile var1);
}
