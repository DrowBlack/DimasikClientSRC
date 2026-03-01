package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.Manifest;

class TransformingClassLoaderBuilder
implements ITransformingClassLoaderBuilder {
    private List<Path> transformationPaths = new ArrayList<Path>();
    private Function<String, Optional<URL>> classBytesLocator;
    private Function<URLConnection, Optional<Manifest>> manifestLocator;

    TransformingClassLoaderBuilder() {
    }

    URL[] getSpecialJarsAsURLs() {
        return (URL[])this.transformationPaths.stream().map(LamdbaExceptionUtils.rethrowFunction(path -> path.toUri().toURL())).toArray(URL[]::new);
    }

    Function<String, Optional<URL>> getClassBytesLocator() {
        return this.classBytesLocator;
    }

    Function<URLConnection, Optional<Manifest>> getManifestLocator() {
        return this.manifestLocator;
    }

    @Override
    public void addTransformationPath(Path path) {
        this.transformationPaths.add(path);
    }

    @Override
    public void setClassBytesLocator(Function<String, Optional<URL>> additionalClassBytesLocator) {
        this.classBytesLocator = additionalClassBytesLocator;
    }

    @Override
    public void setManifestLocator(Function<URLConnection, Optional<Manifest>> manifestLocator) {
        this.manifestLocator = manifestLocator;
    }
}
