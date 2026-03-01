package cpw.mods.modlauncher.api;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.ITransformerAuditTrail;
import cpw.mods.modlauncher.api.TypesafeMap;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IEnvironment {
    public <T> Optional<T> getProperty(TypesafeMap.Key<T> var1);

    public <T> T computePropertyIfAbsent(TypesafeMap.Key<T> var1, Function<? super TypesafeMap.Key<T>, ? extends T> var2);

    public Optional<ILaunchPluginService> findLaunchPlugin(String var1);

    public Optional<ILaunchHandlerService> findLaunchHandler(String var1);

    public Optional<BiFunction<INameMappingService.Domain, String, String>> findNameMapping(String var1);

    public static <T> Supplier<TypesafeMap.Key<T>> buildKey(String name, Class<? super T> clazz) {
        return new TypesafeMap.KeyBuilder<T>(name, clazz, IEnvironment.class);
    }

    public static final class Keys {
        public static final Supplier<TypesafeMap.Key<String>> VERSION = IEnvironment.buildKey("version", String.class);
        public static final Supplier<TypesafeMap.Key<Path>> GAMEDIR = IEnvironment.buildKey("gamedir", Path.class);
        public static final Supplier<TypesafeMap.Key<Path>> ASSETSDIR = IEnvironment.buildKey("assetsdir", Path.class);
        public static final Supplier<TypesafeMap.Key<String>> UUID = IEnvironment.buildKey("uuid", String.class);
        public static final Supplier<TypesafeMap.Key<String>> LAUNCHTARGET = IEnvironment.buildKey("launchtarget", String.class);
        public static final Supplier<TypesafeMap.Key<String>> NAMING = IEnvironment.buildKey("naming", String.class);
        public static final Supplier<TypesafeMap.Key<ITransformerAuditTrail>> AUDITTRAIL = IEnvironment.buildKey("audittrail", ITransformerAuditTrail.class);
        public static final Supplier<TypesafeMap.Key<List<Map<String, String>>>> MODLIST = IEnvironment.buildKey("modlist", List.class);
        public static final Supplier<TypesafeMap.Key<String>> MLSPEC_VERSION = IEnvironment.buildKey("mlspecVersion", String.class);
        public static final Supplier<TypesafeMap.Key<String>> MLIMPL_VERSION = IEnvironment.buildKey("mlimplVersion", String.class);
    }
}
