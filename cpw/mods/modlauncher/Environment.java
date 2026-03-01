package cpw.mods.modlauncher;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.TypesafeMap;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Environment
implements IEnvironment {
    private final TypesafeMap environment = new TypesafeMap(IEnvironment.class);
    private final Launcher launcher;

    Environment(Launcher launcher) {
        this.launcher = launcher;
    }

    @Override
    public final <T> Optional<T> getProperty(TypesafeMap.Key<T> key) {
        return this.environment.get(key);
    }

    @Override
    public Optional<ILaunchPluginService> findLaunchPlugin(String name) {
        return this.launcher.findLaunchPlugin(name);
    }

    @Override
    public Optional<ILaunchHandlerService> findLaunchHandler(String name) {
        return this.launcher.findLaunchHandler(name);
    }

    @Override
    public Optional<BiFunction<INameMappingService.Domain, String, String>> findNameMapping(String targetMapping) {
        return this.launcher.findNameMapping(targetMapping);
    }

    @Override
    public <T> T computePropertyIfAbsent(TypesafeMap.Key<T> key, Function<? super TypesafeMap.Key<T>, ? extends T> valueFunction) {
        return this.environment.computeIfAbsent(key, valueFunction);
    }
}
