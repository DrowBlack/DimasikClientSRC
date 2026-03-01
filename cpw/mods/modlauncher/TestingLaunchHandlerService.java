package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformingClassLoaderBuilder;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class TestingLaunchHandlerService
implements ILaunchHandlerService {
    @Override
    public String name() {
        return "testharness";
    }

    @Override
    public void configureTransformationClassLoader(ITransformingClassLoaderBuilder builder) {
        Arrays.stream(System.getProperty("test.harness").split(",")).map(arg_0 -> TestingLaunchHandlerService.lambda$configureTransformationClassLoader$0(FileSystems.getDefault(), arg_0)).forEach(builder::addTransformationPath);
    }

    @Override
    public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
        try {
            Class<?> callableLaunch = Class.forName(System.getProperty("test.harness.callable"));
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(lookup, "get", MethodType.methodType(Supplier.class), MethodType.methodType(Object.class), lookup.findStatic(callableLaunch, "supplier", MethodType.methodType(Callable.class)), MethodType.methodType(Supplier.class));
            Supplier supplier = site.getTarget().invoke();
            return (Callable)supplier.get();
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | LambdaConversionException e) {
            throw new RuntimeException(e);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return () -> null;
        }
    }

    private static /* synthetic */ Path lambda$configureTransformationClassLoader$0(FileSystem rec$, String x$0) {
        return rec$.getPath(x$0, new String[0]);
    }
}
