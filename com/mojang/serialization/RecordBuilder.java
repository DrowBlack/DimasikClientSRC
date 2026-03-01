package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import java.util.function.UnaryOperator;

public interface RecordBuilder<T> {
    public DynamicOps<T> ops();

    public RecordBuilder<T> add(T var1, T var2);

    public RecordBuilder<T> add(T var1, DataResult<T> var2);

    public RecordBuilder<T> add(DataResult<T> var1, DataResult<T> var2);

    public RecordBuilder<T> withErrorsFrom(DataResult<?> var1);

    public RecordBuilder<T> setLifecycle(Lifecycle var1);

    public RecordBuilder<T> mapError(UnaryOperator<String> var1);

    public DataResult<T> build(T var1);

    default public DataResult<T> build(DataResult<T> prefix) {
        return prefix.flatMap(this::build);
    }

    default public RecordBuilder<T> add(String key, T value) {
        return this.add(this.ops().createString(key), value);
    }

    default public RecordBuilder<T> add(String key, DataResult<T> value) {
        return this.add(this.ops().createString(key), value);
    }

    default public <E> RecordBuilder<T> add(String key, E value, Encoder<E> encoder) {
        return this.add(key, encoder.encodeStart(this.ops(), value));
    }

    public static final class MapBuilder<T>
    extends AbstractUniversalBuilder<T, ImmutableMap.Builder<T, T>> {
        public MapBuilder(DynamicOps<T> ops) {
            super(ops);
        }

        @Override
        protected ImmutableMap.Builder<T, T> initBuilder() {
            return ImmutableMap.builder();
        }

        @Override
        protected ImmutableMap.Builder<T, T> append(T key, T value, ImmutableMap.Builder<T, T> builder) {
            return builder.put(key, value);
        }

        @Override
        protected DataResult<T> build(ImmutableMap.Builder<T, T> builder, T prefix) {
            return this.ops().mergeToMap(prefix, builder.build());
        }
    }

    public static abstract class AbstractUniversalBuilder<T, R>
    extends AbstractBuilder<T, R> {
        protected AbstractUniversalBuilder(DynamicOps<T> ops) {
            super(ops);
        }

        protected abstract R append(T var1, T var2, R var3);

        @Override
        public RecordBuilder<T> add(T key, T value) {
            this.builder = this.builder.map(b -> this.append(key, value, b));
            return this;
        }

        @Override
        public RecordBuilder<T> add(T key, DataResult<T> value) {
            this.builder = this.builder.apply2stable((b, v) -> this.append(key, v, b), value);
            return this;
        }

        @Override
        public RecordBuilder<T> add(DataResult<T> key, DataResult<T> value) {
            this.builder = this.builder.ap(key.apply2stable((k, v) -> b -> this.append(k, v, b), value));
            return this;
        }
    }

    public static abstract class AbstractStringBuilder<T, R>
    extends AbstractBuilder<T, R> {
        protected AbstractStringBuilder(DynamicOps<T> ops) {
            super(ops);
        }

        protected abstract R append(String var1, T var2, R var3);

        @Override
        public RecordBuilder<T> add(String key, T value) {
            this.builder = this.builder.map(b -> this.append(key, value, b));
            return this;
        }

        @Override
        public RecordBuilder<T> add(String key, DataResult<T> value) {
            this.builder = this.builder.apply2stable((b, v) -> this.append(key, v, b), value);
            return this;
        }

        @Override
        public RecordBuilder<T> add(T key, T value) {
            this.builder = this.ops().getStringValue(key).flatMap(k -> {
                this.add((T)k, value);
                return this.builder;
            });
            return this;
        }

        @Override
        public RecordBuilder<T> add(T key, DataResult<T> value) {
            this.builder = this.ops().getStringValue(key).flatMap(k -> {
                this.add((T)k, value);
                return this.builder;
            });
            return this;
        }

        @Override
        public RecordBuilder<T> add(DataResult<T> key, DataResult<T> value) {
            this.builder = key.flatMap(this.ops()::getStringValue).flatMap(k -> {
                this.add((T)k, value);
                return this.builder;
            });
            return this;
        }
    }

    public static abstract class AbstractBuilder<T, R>
    implements RecordBuilder<T> {
        private final DynamicOps<T> ops;
        protected DataResult<R> builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

        protected AbstractBuilder(DynamicOps<T> ops) {
            this.ops = ops;
        }

        @Override
        public DynamicOps<T> ops() {
            return this.ops;
        }

        protected abstract R initBuilder();

        protected abstract DataResult<T> build(R var1, T var2);

        @Override
        public DataResult<T> build(T prefix) {
            DataResult result = this.builder.flatMap(b -> this.build(b, prefix));
            this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
            return result;
        }

        @Override
        public RecordBuilder<T> withErrorsFrom(DataResult<?> result) {
            this.builder = this.builder.flatMap(v -> result.map(r -> v));
            return this;
        }

        @Override
        public RecordBuilder<T> setLifecycle(Lifecycle lifecycle) {
            this.builder = this.builder.setLifecycle(lifecycle);
            return this;
        }

        @Override
        public RecordBuilder<T> mapError(UnaryOperator<String> onError) {
            this.builder = this.builder.mapError(onError);
            return this;
        }
    }
}
