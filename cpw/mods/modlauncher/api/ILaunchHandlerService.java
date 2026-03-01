package cpw.mods.modlauncher.api;

import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public interface ILaunchHandlerService {
    public String name();

    public void configureTransformationClassLoader(ITransformingClassLoaderBuilder var1);

    public Callable<Void> launchService(String[] var1, ITransformingClassLoader var2);

    default public Path[] getPaths() {
        return new Path[0];
    }
}
