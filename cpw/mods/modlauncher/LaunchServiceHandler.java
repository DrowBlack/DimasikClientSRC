package cpw.mods.modlauncher;

import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.LaunchServiceHandlerDecorator;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.TransformingClassLoaderBuilder;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class LaunchServiceHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<ILaunchHandlerService> launchHandlerServices = ServiceLoaderStreamUtils.errorHandlingServiceLoader(ILaunchHandlerService.class, serviceConfigurationError -> LOGGER.fatal("Encountered serious error loading transformation service, expect problems", (Throwable)serviceConfigurationError));
    private final Map<String, LaunchServiceHandlerDecorator> launchHandlerLookup = ServiceLoaderStreamUtils.toMap(this.launchHandlerServices, ILaunchHandlerService::name, LaunchServiceHandlerDecorator::new);

    public LaunchServiceHandler() {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Found launch services [{}]", () -> String.join((CharSequence)",", this.launchHandlerLookup.keySet()));
    }

    public Optional<ILaunchHandlerService> findLaunchHandler(String name) {
        return Optional.ofNullable(this.launchHandlerLookup.getOrDefault(name, null)).map(LaunchServiceHandlerDecorator::getService);
    }

    private void launch(String target, String[] arguments, TransformingClassLoader classLoader, LaunchPluginHandler launchPluginHandler) {
        LaunchServiceHandlerDecorator launchServiceHandlerDecorator = this.launchHandlerLookup.get(target);
        Path[] paths = launchServiceHandlerDecorator.getService().getPaths();
        launchPluginHandler.announceLaunch(classLoader, paths);
        LOGGER.info(LogMarkers.MODLAUNCHER, "Launching target '{}' with arguments {}", (Object)target, (Object)LaunchServiceHandler.hideAccessToken(arguments));
        launchServiceHandlerDecorator.launch(arguments, classLoader);
    }

    static List<String> hideAccessToken(String[] arguments) {
        ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < arguments.length; ++i) {
            if (i > 0 && Objects.equals(arguments[i - 1], "--accessToken")) {
                output.add("\u2744\u2744\u2744\u2744\u2744\u2744\u2744\u2744");
                continue;
            }
            output.add(arguments[i]);
        }
        return output;
    }

    public void launch(ArgumentHandler argumentHandler, TransformingClassLoader classLoader, LaunchPluginHandler launchPluginHandler) {
        String launchTarget = argumentHandler.getLaunchTarget();
        String[] args = argumentHandler.buildArgumentList();
        this.launch(launchTarget, args, classLoader, launchPluginHandler);
    }

    TransformingClassLoaderBuilder identifyTransformationTargets(ArgumentHandler argumentHandler) {
        String launchTarget = argumentHandler.getLaunchTarget();
        TransformingClassLoaderBuilder builder = new TransformingClassLoaderBuilder();
        Arrays.stream(argumentHandler.getSpecialJars()).forEach(builder::addTransformationPath);
        this.launchHandlerLookup.get(launchTarget).configureTransformationClassLoaderBuilder(builder);
        return builder;
    }

    void validateLaunchTarget(ArgumentHandler argumentHandler) {
        if (!this.launchHandlerLookup.containsKey(argumentHandler.getLaunchTarget())) {
            LOGGER.error(LogMarkers.MODLAUNCHER, "Cannot find launch target {}, unable to launch", (Object)argumentHandler.getLaunchTarget());
            throw new RuntimeException("Cannot find launch target");
        }
    }
}
