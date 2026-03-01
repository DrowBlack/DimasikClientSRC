package cpw.mods.modlauncher;

import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.LaunchServiceHandler;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.NameMappingServiceHandler;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformationServicesHandler;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.TransformingClassLoaderBuilder;
import cpw.mods.modlauncher.ValidateLibraries;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.TypesafeMap;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.logging.log4j.LogManager;

public class Launcher {
    public static Launcher INSTANCE;
    private final TypesafeMap blackboard;
    private final TransformationServicesHandler transformationServicesHandler;
    private final Environment environment;
    private final TransformStore transformStore;
    private final NameMappingServiceHandler nameMappingServiceHandler;
    private final ArgumentHandler argumentHandler;
    private final LaunchServiceHandler launchService;
    private final LaunchPluginHandler launchPlugins;
    private TransformingClassLoader classLoader;

    private Launcher() {
        INSTANCE = this;
        LogManager.getLogger().info(LogMarkers.MODLAUNCHER, "ModLauncher {} starting: java version {} by {}", () -> IEnvironment.class.getPackage().getImplementationVersion(), () -> System.getProperty("java.version"), () -> System.getProperty("java.vendor"));
        this.launchService = new LaunchServiceHandler();
        this.blackboard = new TypesafeMap();
        this.environment = new Environment(this);
        this.environment.computePropertyIfAbsent(IEnvironment.Keys.MLSPEC_VERSION.get(), s -> IEnvironment.class.getPackage().getSpecificationVersion());
        this.environment.computePropertyIfAbsent(IEnvironment.Keys.MLIMPL_VERSION.get(), s -> IEnvironment.class.getPackage().getImplementationVersion());
        this.environment.computePropertyIfAbsent(IEnvironment.Keys.MODLIST.get(), s -> new ArrayList());
        this.transformStore = new TransformStore();
        this.transformationServicesHandler = new TransformationServicesHandler(this.transformStore);
        this.argumentHandler = new ArgumentHandler();
        this.nameMappingServiceHandler = new NameMappingServiceHandler();
        this.launchPlugins = new LaunchPluginHandler();
    }

    public static void main(String ... args) {
        ValidateLibraries.validate();
        LogManager.getLogger().info(LogMarkers.MODLAUNCHER, "ModLauncher running: args {}", () -> LaunchServiceHandler.hideAccessToken(args));
        new Launcher().run(args);
    }

    public final TypesafeMap blackboard() {
        return this.blackboard;
    }

    private void run(String ... args) {
        Path gameDir = this.argumentHandler.setArgs(args);
        this.transformationServicesHandler.discoverServices(gameDir);
        List<Map.Entry<String, Path>> scanResults = this.transformationServicesHandler.initializeTransformationServices(this.argumentHandler, this.environment, this.nameMappingServiceHandler);
        this.launchPlugins.offerScanResultsToPlugins(scanResults);
        this.launchService.validateLaunchTarget(this.argumentHandler);
        TransformingClassLoaderBuilder classLoaderBuilder = this.launchService.identifyTransformationTargets(this.argumentHandler);
        this.classLoader = this.transformationServicesHandler.buildTransformingClassLoader(this.launchPlugins, classLoaderBuilder, this.environment);
        Thread.currentThread().setContextClassLoader(this.classLoader);
        this.launchService.launch(this.argumentHandler, this.classLoader, this.launchPlugins);
    }

    public Environment environment() {
        return this.environment;
    }

    Optional<ILaunchPluginService> findLaunchPlugin(String name) {
        return this.launchPlugins.get(name);
    }

    Optional<ILaunchHandlerService> findLaunchHandler(String name) {
        return this.launchService.findLaunchHandler(name);
    }

    Optional<BiFunction<INameMappingService.Domain, String, String>> findNameMapping(String targetMapping) {
        return this.nameMappingServiceHandler.findNameTranslator(targetMapping);
    }
}
