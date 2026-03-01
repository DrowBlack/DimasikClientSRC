package net.minecraft.util.registry;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DelegatingDynamicOps;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenSettingsExport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSettingsImport<T>
extends DelegatingDynamicOps<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IResourceAccess resourceAccess;
    private final DynamicRegistries.Impl dynamicRegistries;
    private final Map<RegistryKey<? extends Registry<?>>, ResultMap<?>> registryToResultMap;
    private final WorldSettingsImport<JsonElement> jsonOps;

    public static <T> WorldSettingsImport<T> create(DynamicOps<T> ops, IResourceManager resourceManager, DynamicRegistries.Impl dynamicRegistries) {
        return WorldSettingsImport.create(ops, IResourceAccess.create(resourceManager), dynamicRegistries);
    }

    public static <T> WorldSettingsImport<T> create(DynamicOps<T> ops, IResourceAccess resourceAccess, DynamicRegistries.Impl dynamicRegistries) {
        WorldSettingsImport<T> worldsettingsimport = new WorldSettingsImport<T>(ops, resourceAccess, dynamicRegistries, Maps.newIdentityHashMap());
        DynamicRegistries.loadRegistryData(dynamicRegistries, worldsettingsimport);
        return worldsettingsimport;
    }

    private WorldSettingsImport(DynamicOps<T> ops, IResourceAccess resourceAccess, DynamicRegistries.Impl dynamicRegistries, IdentityHashMap<RegistryKey<? extends Registry<?>>, ResultMap<?>> registryToResultMap) {
        super(ops);
        this.resourceAccess = resourceAccess;
        this.dynamicRegistries = dynamicRegistries;
        this.registryToResultMap = registryToResultMap;
        this.jsonOps = ops == JsonOps.INSTANCE ? this : new WorldSettingsImport<JsonElement>(JsonOps.INSTANCE, resourceAccess, dynamicRegistries, registryToResultMap);
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> decode(T input, RegistryKey<? extends Registry<E>> registryKey, Codec<E> mapCodec, boolean allowInlineDefinitions) {
        Optional optional = this.dynamicRegistries.func_230521_a_(registryKey);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + String.valueOf(registryKey));
        }
        MutableRegistry mutableregistry = optional.get();
        DataResult dataresult = ResourceLocation.CODEC.decode(this.ops, input);
        if (!dataresult.result().isPresent()) {
            return !allowInlineDefinitions ? DataResult.error("Inline definitions not allowed here") : mapCodec.decode(this, input).map(instanceInputPair -> instanceInputPair.mapFirst(instance -> () -> instance));
        }
        Pair pair = dataresult.result().get();
        ResourceLocation resourcelocation = (ResourceLocation)pair.getFirst();
        return this.createRegistry(registryKey, mutableregistry, mapCodec, resourcelocation).map(instanceSupplier -> Pair.of(instanceSupplier, pair.getSecond()));
    }

    public <E> DataResult<SimpleRegistry<E>> decode(SimpleRegistry<E> simpleRegistry, RegistryKey<? extends Registry<E>> registryKey, Codec<E> mapCodec) {
        Collection<ResourceLocation> collection = this.resourceAccess.getRegistryObjects(registryKey);
        DataResult<SimpleRegistry<Object>> dataresult = DataResult.success(simpleRegistry, Lifecycle.stable());
        String s = registryKey.getLocation().getPath() + "/";
        for (ResourceLocation resourcelocation : collection) {
            String s1 = resourcelocation.getPath();
            if (!s1.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)resourcelocation);
                continue;
            }
            if (!s1.startsWith(s)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)resourcelocation);
                continue;
            }
            String s2 = s1.substring(s.length(), s1.length() - ".json".length());
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s2);
            dataresult = dataresult.flatMap(registry -> this.createRegistry(registryKey, (MutableRegistry)registry, mapCodec, resourcelocation1).map(instanceSupplier -> registry));
        }
        return dataresult.setPartial(simpleRegistry);
    }

    private <E> DataResult<Supplier<E>> createRegistry(RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> mapCodec, ResourceLocation id) {
        RegistryKey registrykey = RegistryKey.getOrCreateKey(registryKey, id);
        ResultMap<E> resultmap = this.getResultMap(registryKey);
        DataResult dataresult = resultmap.resultMap.get(registrykey);
        if (dataresult != null) {
            return dataresult;
        }
        com.google.common.base.Supplier<Object> supplier = Suppliers.memoize(() -> {
            Object e = mutableRegistry.getValueForKey(registrykey);
            if (e == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + String.valueOf(registrykey));
            }
            return e;
        });
        resultmap.resultMap.put(registrykey, DataResult.success(supplier));
        DataResult<Pair<Supplier, OptionalInt>> dataresult1 = this.resourceAccess.decode(this.jsonOps, registryKey, registrykey, mapCodec);
        Optional optional = dataresult1.result();
        if (optional.isPresent()) {
            Pair pair = optional.get();
            mutableRegistry.validateAndRegister(pair.getSecond(), registrykey, pair.getFirst(), dataresult1.lifecycle());
        }
        DataResult<Supplier<Object>> dataresult2 = !optional.isPresent() && mutableRegistry.getValueForKey(registrykey) != null ? DataResult.success(() -> mutableRegistry.getValueForKey(registrykey), Lifecycle.stable()) : dataresult1.map(instanceIndexPair -> () -> mutableRegistry.getValueForKey(registrykey));
        resultmap.resultMap.put(registrykey, dataresult2);
        return dataresult2;
    }

    private <E> ResultMap<E> getResultMap(RegistryKey<? extends Registry<E>> key) {
        return this.registryToResultMap.computeIfAbsent(key, registryKey -> new ResultMap());
    }

    protected <E> DataResult<Registry<E>> getRegistryByKey(RegistryKey<? extends Registry<E>> registryKey) {
        return this.dynamicRegistries.func_230521_a_(registryKey).map(mutableRegistry -> DataResult.success(mutableRegistry, mutableRegistry.getLifecycle())).orElseGet(() -> DataResult.error("Unknown registry: " + String.valueOf(registryKey)));
    }

    public static interface IResourceAccess {
        public Collection<ResourceLocation> getRegistryObjects(RegistryKey<? extends Registry<?>> var1);

        public <E> DataResult<Pair<E, OptionalInt>> decode(DynamicOps<JsonElement> var1, RegistryKey<? extends Registry<E>> var2, RegistryKey<E> var3, Decoder<E> var4);

        public static IResourceAccess create(final IResourceManager manager) {
            return new IResourceAccess(){

                @Override
                public Collection<ResourceLocation> getRegistryObjects(RegistryKey<? extends Registry<?>> registryKey) {
                    return manager.getAllResourceLocations(registryKey.getLocation().getPath(), fileName -> fileName.endsWith(".json"));
                }

                /*
                 * Enabled aggressive exception aggregation
                 */
                @Override
                public <E> DataResult<Pair<E, OptionalInt>> decode(DynamicOps<JsonElement> jsonOps, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> objectKey, Decoder<E> decoder) {
                    ResourceLocation resourcelocation = objectKey.getLocation();
                    ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), registryKey.getLocation().getPath() + "/" + resourcelocation.getPath() + ".json");
                    try (IResource iresource = manager.getResource(resourcelocation1);){
                        DataResult<Pair<E, OptionalInt>> dataResult;
                        try (InputStreamReader reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);){
                            JsonParser jsonparser = new JsonParser();
                            JsonElement jsonelement = jsonparser.parse(reader);
                            dataResult = decoder.parse(jsonOps, jsonelement).map(instance -> Pair.of(instance, OptionalInt.empty()));
                        }
                        return dataResult;
                    }
                    catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                        return DataResult.error("Failed to parse " + String.valueOf(resourcelocation1) + " file: " + ioexception.getMessage());
                    }
                }

                public String toString() {
                    return "ResourceAccess[" + String.valueOf(manager) + "]";
                }
            };
        }

        public static final class RegistryAccess
        implements IResourceAccess {
            private final Map<RegistryKey<?>, JsonElement> keyToElementMap = Maps.newIdentityHashMap();
            private final Object2IntMap<RegistryKey<?>> keyToIDMap = new Object2IntOpenCustomHashMap(Util.identityHashStrategy());
            private final Map<RegistryKey<?>, Lifecycle> keyToLifecycleMap = Maps.newIdentityHashMap();

            public <E> void encode(DynamicRegistries.Impl dynamicRegistries, RegistryKey<E> key, Encoder<E> encoder, int id, E instance, Lifecycle lifecycle) {
                DataResult<JsonElement> dataresult = encoder.encodeStart(WorldGenSettingsExport.create(JsonOps.INSTANCE, dynamicRegistries), instance);
                Optional<DataResult.PartialResult<JsonElement>> optional = dataresult.error();
                if (optional.isPresent()) {
                    LOGGER.error("Error adding element: {}", (Object)optional.get().message());
                } else {
                    this.keyToElementMap.put(key, dataresult.result().get());
                    this.keyToIDMap.put((RegistryKey<?>)key, id);
                    this.keyToLifecycleMap.put(key, lifecycle);
                }
            }

            @Override
            public Collection<ResourceLocation> getRegistryObjects(RegistryKey<? extends Registry<?>> registryKey) {
                return this.keyToElementMap.keySet().stream().filter(key -> key.isParent(registryKey)).map(key -> new ResourceLocation(key.getLocation().getNamespace(), registryKey.getLocation().getPath() + "/" + key.getLocation().getPath() + ".json")).collect(Collectors.toList());
            }

            @Override
            public <E> DataResult<Pair<E, OptionalInt>> decode(DynamicOps<JsonElement> jsonOps, RegistryKey<? extends Registry<E>> registryKey, RegistryKey<E> objectKey, Decoder<E> decoder) {
                JsonElement jsonelement = this.keyToElementMap.get(objectKey);
                return jsonelement == null ? DataResult.error("Unknown element: " + String.valueOf(objectKey)) : decoder.parse(jsonOps, jsonelement).setLifecycle(this.keyToLifecycleMap.get(objectKey)).map(instance -> Pair.of(instance, OptionalInt.of(this.keyToIDMap.getInt(objectKey))));
            }
        }
    }

    static final class ResultMap<E> {
        private final Map<RegistryKey<E>, DataResult<Supplier<E>>> resultMap = Maps.newIdentityHashMap();

        private ResultMap() {
        }
    }
}
