package cpw.mods.modlauncher;

import cpw.mods.gross.Java9ClassLoaderUtil;
import cpw.mods.modlauncher.ArgumentHandler;
import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.InvalidLauncherSetupException;
import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.NameMappingServiceHandler;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformationServiceDecorator;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.TransformingClassLoaderBuilder;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import cpw.mods.modlauncher.serviceapi.ITransformerDiscoveryService;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TransformationServicesHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private ServiceLoader<ITransformationService> transformationServices;
    private Map<String, TransformationServiceDecorator> serviceLookup;
    private final TransformStore transformStore;

    TransformationServicesHandler(TransformStore transformStore) {
        this.transformStore = transformStore;
    }

    private static <I, R> Function<I, Optional<R>> alternate(@Nullable Function<I, Optional<R>> first, @Nullable Function<I, Optional<R>> second) {
        if (second == null) {
            return first;
        }
        if (first == null) {
            return second;
        }
        return input -> Optional.ofNullable(((Optional)first.apply(input)).orElseGet(() -> ((Optional)second.apply(input)).orElse(null)));
    }

    List<Map.Entry<String, Path>> initializeTransformationServices(ArgumentHandler argumentHandler, Environment environment, NameMappingServiceHandler nameMappingServiceHandler) {
        this.loadTransformationServices(environment);
        this.validateTransformationServices();
        this.processArguments(argumentHandler, environment);
        this.initialiseTransformationServices(environment);
        environment.computePropertyIfAbsent(IEnvironment.Keys.NAMING.get(), a -> "mojang");
        nameMappingServiceHandler.bindNamingServices(environment.getProperty(IEnvironment.Keys.NAMING.get()).orElse("mojang"));
        List<Map.Entry<String, Path>> scanResults = this.runScanningTransformationServices(environment);
        this.initialiseServiceTransformers();
        return scanResults;
    }

    TransformingClassLoader buildTransformingClassLoader(LaunchPluginHandler pluginHandler, TransformingClassLoaderBuilder builder, Environment environment) {
        List classLocatorList = this.serviceLookup.values().stream().map(TransformationServiceDecorator::getClassLoader).filter(Objects::nonNull).collect(Collectors.toList());
        Function<String, Optional<URL>> classBytesLocator = builder.getClassBytesLocator();
        for (Function transformerClassLocator : classLocatorList) {
            classBytesLocator = TransformationServicesHandler.alternate(classBytesLocator, transformerClassLocator);
        }
        builder.setClassBytesLocator(classBytesLocator);
        return new TransformingClassLoader(this.transformStore, pluginHandler, builder, environment);
    }

    private void processArguments(ArgumentHandler argumentHandler, Environment environment) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Configuring option handling for services");
        argumentHandler.processArguments(environment, this::computeArgumentsForServices, this::offerArgumentResultsToServices);
    }

    private void computeArgumentsForServices(OptionParser parser) {
        ServiceLoaderStreamUtils.parallelForEach(this.transformationServices, service -> service.arguments((a, b) -> parser.accepts(service.name() + "." + a, (String)b)));
    }

    private void offerArgumentResultsToServices(OptionSet optionSet, BiFunction<String, OptionSet, ITransformationService.OptionResult> resultHandler) {
        ServiceLoaderStreamUtils.parallelForEach(this.transformationServices, service -> service.argumentValues((ITransformationService.OptionResult)resultHandler.apply(service.name(), optionSet)));
    }

    private void initialiseServiceTransformers() {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Transformation services loading transformers");
        this.serviceLookup.values().forEach(s -> s.gatherTransformers(this.transformStore));
    }

    private void initialiseTransformationServices(Environment environment) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Transformation services initializing");
        this.serviceLookup.values().forEach(s -> s.onInitialize(environment));
    }

    private List<Map.Entry<String, Path>> runScanningTransformationServices(Environment environment) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Transformation services begin scanning");
        return this.serviceLookup.values().stream().flatMap(s -> s.runScan(environment).stream()).collect(Collectors.toList());
    }

    private void validateTransformationServices() {
        if (this.serviceLookup.values().stream().filter(d -> !d.isValid()).count() > 0L) {
            List services = this.serviceLookup.values().stream().filter(d -> !d.isValid()).map(TransformationServiceDecorator::getService).collect(Collectors.toList());
            String names = services.stream().map(ITransformationService::name).collect(Collectors.joining(","));
            LOGGER.error(LogMarkers.MODLAUNCHER, "Found {} services that failed to load : [{}]", (Object)services.size(), (Object)names);
            throw new InvalidLauncherSetupException("Invalid Services found " + names);
        }
    }

    private void loadTransformationServices(Environment environment) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Transformation services loading");
        this.serviceLookup.values().forEach(s -> s.onLoad(environment, this.serviceLookup.keySet()));
    }

    void discoverServices(Path gameDir) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Discovering transformation services");
        ServiceLoader<ITransformerDiscoveryService> discoveryServices = ServiceLoaderStreamUtils.errorHandlingServiceLoader(ITransformerDiscoveryService.class, serviceConfigurationError -> LOGGER.fatal(LogMarkers.MODLAUNCHER, "Encountered serious error loading transformation discoverer, expect problems", (Throwable)serviceConfigurationError));
        List additionalPaths = ServiceLoaderStreamUtils.map(discoveryServices, s -> s.candidates(gameDir)).flatMap(Collection::stream).collect(Collectors.toList());
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Found additional transformation services from discovery services: {}", (Object)additionalPaths);
        TransformerClassLoader cl = new TransformerClassLoader(Java9ClassLoaderUtil.getSystemClassPathURLs());
        additionalPaths.stream().map(LamdbaExceptionUtils.rethrowFunction(p -> p.toUri().toURL())).forEach(cl::addURL);
        this.transformationServices = ServiceLoaderStreamUtils.errorHandlingServiceLoader(ITransformationService.class, cl, serviceConfigurationError -> LOGGER.fatal(LogMarkers.MODLAUNCHER, "Encountered serious error loading transformation service, expect problems", (Throwable)serviceConfigurationError));
        this.serviceLookup = ServiceLoaderStreamUtils.toMap(this.transformationServices, ITransformationService::name, TransformationServiceDecorator::new);
        List<Map<String, String>> modlist = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MODLIST.get()).orElseThrow(() -> new RuntimeException("The MODLIST isn't set, huh?"));
        this.serviceLookup.forEach((name, deco) -> {
            HashMap<String, String> mod = new HashMap<String, String>();
            mod.put("name", (String)name);
            mod.put("type", "TRANSFORMATIONSERVICE");
            String fName = deco.getService().getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            mod.put("file", fName.substring(fName.lastIndexOf("/")));
            modlist.add(mod);
        });
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Found transformer services : [{}]", () -> String.join((CharSequence)",", this.serviceLookup.keySet()));
    }

    private static class TransformerClassLoader
    extends URLClassLoader {
        TransformerClassLoader(URL[] urls) {
            super(urls);
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }
    }
}
