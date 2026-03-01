package cpw.mods.modlauncher;

import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.NameMappingServiceDecorator;
import cpw.mods.modlauncher.ServiceLoaderStreamUtils;
import cpw.mods.modlauncher.api.INameMappingService;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NameMappingServiceHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServiceLoader<INameMappingService> namingServices = ServiceLoaderStreamUtils.errorHandlingServiceLoader(INameMappingService.class, serviceConfigurationError -> LOGGER.fatal("Encountered serious error loading naming service, expect problems", (Throwable)serviceConfigurationError));
    private final Map<String, NameMappingServiceDecorator> namingTable = ServiceLoaderStreamUtils.toMap(this.namingServices, INameMappingService::mappingName, NameMappingServiceDecorator::new);
    private Map<String, NameMappingServiceDecorator> nameBindings;

    public NameMappingServiceHandler() {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Found naming services : [{}]", () -> String.join((CharSequence)",", this.namingTable.keySet()));
    }

    public Optional<BiFunction<INameMappingService.Domain, String, String>> findNameTranslator(String targetNaming) {
        return Optional.ofNullable(this.nameBindings.get(targetNaming)).map(NameMappingServiceDecorator::function);
    }

    public void bindNamingServices(String currentNaming) {
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Current naming domain is '{}'", (Object)currentNaming);
        this.nameBindings = this.namingTable.values().stream().filter(nameMappingServiceDecorator -> nameMappingServiceDecorator.validTarget(currentNaming)).collect(Collectors.toMap(NameMappingServiceDecorator::understands, Function.identity()));
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Identified name mapping providers {}", (Object)this.nameBindings);
    }
}
