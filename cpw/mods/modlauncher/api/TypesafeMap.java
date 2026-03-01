package cpw.mods.modlauncher.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TypesafeMap {
    private static final ConcurrentHashMap<Class<?>, TypesafeMap> maps = new ConcurrentHashMap();
    private final ConcurrentHashMap<Key<Object>, Object> map = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, Key<Object>> keys = new ConcurrentHashMap();

    public TypesafeMap() {
    }

    public TypesafeMap(Class<?> owner) {
        KeyBuilder.keyBuilders.getOrDefault(owner, Collections.emptyList()).forEach(kb -> kb.buildKey(this));
        maps.put(owner, this);
    }

    public <V> Optional<V> get(Key<V> key) {
        return Optional.ofNullable(((Key)key).clz.cast(this.map.get(key)));
    }

    public <V> V computeIfAbsent(Key<V> key, Function<? super Key<V>, ? extends V> valueFunction) {
        return this.computeIfAbsent(this.map, key, valueFunction);
    }

    private <C1, C2, V> V computeIfAbsent(ConcurrentHashMap<C1, C2> map, Key<V> key, Function<? super Key<V>, ? extends V> valueFunction) {
        return map.computeIfAbsent(key, valueFunction);
    }

    private ConcurrentHashMap<String, Key<Object>> getKeyIdentifiers() {
        return this.keys;
    }

    public static final class KeyBuilder<T>
    implements Supplier<Key<T>> {
        private static final Map<Class<?>, List<KeyBuilder<?>>> keyBuilders = new HashMap();
        private final Class<?> owner;
        private final String name;
        private final Class<? super T> clazz;
        private Key<T> key;

        public KeyBuilder(String name, Class<? super T> clazz, Class<?> owner) {
            this.name = name;
            this.clazz = clazz;
            this.owner = owner;
            keyBuilders.computeIfAbsent(owner, k -> new ArrayList()).add(this);
        }

        final void buildKey(TypesafeMap map) {
            this.key = Key.getOrCreate(map, this.name, this.clazz);
        }

        @Override
        public Key<T> get() {
            if (this.key == null && maps.containsKey(this.owner)) {
                this.buildKey((TypesafeMap)maps.get(this.owner));
            }
            if (this.key == null) {
                throw new NullPointerException("Missing map");
            }
            return this.key;
        }
    }

    public static final class Key<T>
    implements Comparable<Key<T>> {
        private static final AtomicLong idGenerator = new AtomicLong();
        private final String name;
        private final long uniqueId;
        private final Class<T> clz;

        private Key(String name, Class<T> clz) {
            this.clz = clz;
            this.name = name;
            this.uniqueId = idGenerator.getAndIncrement();
        }

        public static <V> Key<V> getOrCreate(TypesafeMap owner, String name, Class<? super V> clazz) {
            Key result = owner.getKeyIdentifiers().computeIfAbsent(name, n -> new Key((String)n, clazz));
            if (result.clz != clazz) {
                throw new IllegalArgumentException("Invalid type");
            }
            return result;
        }

        public static <V> Supplier<Key<V>> getOrCreate(Supplier<TypesafeMap> owner, String name, Class<V> clazz) {
            return () -> Key.getOrCreate((TypesafeMap)owner.get(), name, clazz);
        }

        public final String name() {
            return this.name;
        }

        public int hashCode() {
            return (int)(this.uniqueId ^ this.uniqueId >>> 32);
        }

        public boolean equals(Object obj) {
            try {
                return this.uniqueId == ((Key)obj).uniqueId;
            }
            catch (ClassCastException cc) {
                return false;
            }
        }

        @Override
        public int compareTo(Key o) {
            if (this == o) {
                return 0;
            }
            if (this.uniqueId < o.uniqueId) {
                return -1;
            }
            if (this.uniqueId > o.uniqueId) {
                return 1;
            }
            throw new RuntimeException("Huh?");
        }
    }
}
