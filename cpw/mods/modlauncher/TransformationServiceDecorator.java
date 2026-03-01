package cpw.mods.modlauncher;

import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.LogMarkers;
import cpw.mods.modlauncher.TransformStore;
import cpw.mods.modlauncher.TransformTargetLabel;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class TransformationServiceDecorator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ITransformationService service;
    private boolean isValid;
    private static Set<String> classPrefixes = new HashSet<String>();
    private static Set<String> resourceNames = new HashSet<String>();

    TransformationServiceDecorator(ITransformationService service) {
        this.service = service;
    }

    void onLoad(IEnvironment env, Set<String> otherServices) {
        try {
            Supplier[] supplierArray = new Supplier[1];
            supplierArray[0] = this.service::name;
            LOGGER.debug(LogMarkers.MODLAUNCHER, "Loading service {}", supplierArray);
            this.service.onLoad(env, otherServices);
            this.isValid = true;
            Supplier[] supplierArray2 = new Supplier[1];
            supplierArray2[0] = this.service::name;
            LOGGER.debug(LogMarkers.MODLAUNCHER, "Loaded service {}", supplierArray2);
        }
        catch (IncompatibleEnvironmentException e) {
            LOGGER.error(LogMarkers.MODLAUNCHER, "Service failed to load {}", (Object)this.service.name(), (Object)e);
            this.isValid = false;
        }
    }

    boolean isValid() {
        return this.isValid;
    }

    void onInitialize(IEnvironment environment) {
        Supplier[] supplierArray = new Supplier[1];
        supplierArray[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Initializing transformation service {}", supplierArray);
        this.service.initialize(environment);
        Supplier[] supplierArray2 = new Supplier[1];
        supplierArray2[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Initialized transformation service {}", supplierArray2);
    }

    public void gatherTransformers(TransformStore transformStore) {
        Supplier[] supplierArray = new Supplier[1];
        supplierArray[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Initializing transformers for transformation service {}", supplierArray);
        List<ITransformer> transformers = this.service.transformers();
        Objects.requireNonNull(transformers, "The transformers list should not be null");
        Map<Type, List<ITransformer>> transformersByType = transformers.stream().collect(Collectors.groupingBy(t -> {
            Type[] genericInterfaces;
            for (Type typ : genericInterfaces = t.getClass().getGenericInterfaces()) {
                ParameterizedType pt = (ParameterizedType)typ;
                if (!pt.getRawType().equals(ITransformer.class)) continue;
                return pt.getActualTypeArguments()[0];
            }
            throw new RuntimeException("How did a non-transformer get here????");
        }));
        for (Type type : transformersByType.keySet()) {
            TransformTargetLabel.LabelType labelType = TransformTargetLabel.LabelType.getTypeFor(type).orElseThrow(() -> new IllegalArgumentException("Invalid transformer type found"));
            for (ITransformer xform : transformersByType.get(type)) {
                Set<ITransformer.Target> targets = xform.targets();
                if (targets.isEmpty()) continue;
                Map<TransformTargetLabel.LabelType, List<TransformTargetLabel>> labelTypeListMap = targets.stream().map(TransformTargetLabel::new).collect(Collectors.groupingBy(TransformTargetLabel::getLabelType));
                if (labelTypeListMap.keySet().size() > 1 || !labelTypeListMap.keySet().contains((Object)labelType)) {
                    LOGGER.error(LogMarkers.MODLAUNCHER, "Invalid target {} for transformer {}", (Object)labelType, (Object)xform);
                    throw new IllegalArgumentException("The transformer contains invalid targets");
                }
                labelTypeListMap.values().stream().flatMap(Collection::stream).forEach(target -> transformStore.addTransformer((TransformTargetLabel)target, xform, this.service));
            }
        }
        Supplier[] supplierArray2 = new Supplier[1];
        supplierArray2[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Initialized transformers for transformation service {}", supplierArray2);
    }

    ITransformationService getService() {
        return this.service;
    }

    List<Map.Entry<String, Path>> runScan(Environment environment) {
        Supplier[] supplierArray = new Supplier[1];
        supplierArray[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "Beginning scan trigger - transformation service {}", supplierArray);
        List<Map.Entry<String, Path>> scanResults = this.service.runScan(environment);
        Supplier[] supplierArray2 = new Supplier[1];
        supplierArray2[0] = this.service::name;
        LOGGER.debug(LogMarkers.MODLAUNCHER, "End scan trigger - transformation service {}", supplierArray2);
        return scanResults;
    }

    Function<String, Optional<URL>> getClassLoader() {
        Map.Entry<Set<String>, java.util.function.Supplier<Function<String, Optional<URL>>>> resourcesLocator;
        Map.Entry<Set<String>, java.util.function.Supplier<Function<String, Optional<URL>>>> classesLocator = this.service.additionalClassesLocator();
        if (classesLocator != null) {
            HashSet packagePrefixes = new HashSet(classesLocator.getKey());
            Set<String> badPrefixes = packagePrefixes.stream().filter(s -> s.startsWith("net.minecraft.") || s.startsWith("net.minecraftforge.") || classPrefixes.contains(s) || !s.endsWith(".") || s.indexOf(46) <= 0).collect(Collectors.toSet());
            if (!badPrefixes.isEmpty()) {
                badPrefixes.forEach(s -> LOGGER.error("Illegal prefix specified for {} : {}", (Object)this.service.name(), s));
                throw new IllegalArgumentException("Bad prefixes specified");
            }
            classPrefixes.addAll((Collection<String>)classesLocator.getKey());
        }
        if ((resourcesLocator = this.service.additionalResourcesLocator()) != null) {
            HashSet resNames = new HashSet(resourcesLocator.getKey());
            Set<String> badResourceNames = resNames.stream().filter(s -> !s.endsWith(".class") || resourceNames.contains(s)).collect(Collectors.toSet());
            if (!badResourceNames.isEmpty()) {
                badResourceNames.forEach(s -> LOGGER.error("Illegal resource name specified for {} : {}", (Object)this.service.name(), s));
                throw new IllegalArgumentException("Bad resources specified");
            }
            resourceNames.addAll((Collection<String>)resourcesLocator.getKey());
        }
        return s -> this.getOptionalURL(classesLocator, resourcesLocator, (String)s);
    }

    private Optional<URL> getOptionalURL(@Nullable Map.Entry<Set<String>, java.util.function.Supplier<Function<String, Optional<URL>>>> classes, @Nullable Map.Entry<Set<String>, java.util.function.Supplier<Function<String, Optional<URL>>>> resources, String name) {
        block3: {
            block2: {
                if (classes == null || !name.endsWith(".class")) break block2;
                for (String pfx : classes.getKey()) {
                    if (!name.startsWith(pfx.replace('.', '/'))) continue;
                    return classes.getValue().get().apply(name);
                }
                break block3;
            }
            if (resources == null || name.endsWith(".class")) break block3;
            for (String pfx : resources.getKey()) {
                if (!Objects.equals(name, pfx)) continue;
                return resources.getValue().get().apply(name);
            }
        }
        return Optional.empty();
    }
}
