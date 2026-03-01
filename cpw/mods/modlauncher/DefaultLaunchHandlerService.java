package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class DefaultLaunchHandlerService
implements ILaunchHandlerService {
    public static final String LAUNCH_PROPERTY = "minecraft.client.jar";
    public static final String LAUNCH_PATH_STRING = System.getProperty("minecraft.client.jar");

    @Override
    public String name() {
        return "minecraft";
    }

    @Override
    public void configureTransformationClassLoader(ITransformingClassLoaderBuilder builder) {
        if (LAUNCH_PATH_STRING == null) {
            throw new IllegalStateException("Missing minecraft.client.jar environment property. Update your launcher!");
        }
        builder.addTransformationPath(FileSystems.getDefault().getPath(LAUNCH_PATH_STRING, new String[0]));
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
        return () -> {
            Class<?> mcClass = Class.forName("net.minecraft.client.main.Main", true, launchClassLoader.getInstance());
            Method mcClassMethod = mcClass.getMethod("main", String[].class);
            mcClassMethod.invoke(null, new Object[]{arguments});
            return null;
        };
    }

    @Override
    public Path[] getPaths() {
        return new Path[]{FileSystems.getDefault().getPath(LAUNCH_PATH_STRING, new String[0])};
    }
}
