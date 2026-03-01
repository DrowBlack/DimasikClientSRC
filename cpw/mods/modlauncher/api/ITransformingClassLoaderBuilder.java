package cpw.mods.modlauncher.api;

import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.Manifest;

public interface ITransformingClassLoaderBuilder {
    public void addTransformationPath(Path var1);

    public void setClassBytesLocator(Function<String, Optional<URL>> var1);

    public void setManifestLocator(Function<URLConnection, Optional<Manifest>> var1);
}
