package com.mojang.serialization;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ListBuilder<T> {
    public DynamicOps<T> ops();

    public DataResult<T> build(T var1);

    public ListBuilder<T> add(T var1);

    public ListBuilder<T> add(DataResult<T> var1);

    public ListBuilder<T> withErrorsFrom(DataResult<?> var1);

    public ListBuilder<T> mapError(UnaryOperator<String> var1);

    default public DataResult<T> build(DataResult<T> prefix) {
        return prefix.flatMap(this::build);
    }

    default public <E> ListBuilder<T> add(E value, Encoder<E> encoder) {
        return this.add(encoder.encodeStart(this.ops(), value));
    }

    default public <E> ListBuilder<T> addAll(Iterable<E> values, Encoder<E> encoder) {
        values.forEach(v -> encoder.encode(v, this.ops(), this.ops().empty()));
        return this;
    }

    public static final class Builder<T>
    implements ListBuilder<T> {
        private final DynamicOps<T> ops;
        private DataResult<ImmutableList.Builder<T>> builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());

        public Builder(DynamicOps<T> ops) {
            this.ops = ops;
        }

        @Override
        public DynamicOps<T> ops() {
            return this.ops;
        }

        @Override
        public ListBuilder<T> add(T value) {
            this.builder = this.builder.map(b -> b.add(value));
            return this;
        }

        @Override
        public ListBuilder<T> add(DataResult<T> value) {
            this.builder = this.builder.apply2stable(ImmutableList.Builder::add, value);
            return this;
        }

        @Override
        public ListBuilder<T> withErrorsFrom(DataResult<?> result) {
            this.builder = this.builder.flatMap(r -> result.map(v -> r));
            return this;
        }

        @Override
        public ListBuilder<T> mapError(UnaryOperator<String> onError) {
            this.builder = this.builder.mapError(onError);
            return this;
        }

        @Override
        public DataResult<T> build(T prefix) {
            DataResult result = this.builder.flatMap(b -> this.ops.mergeToList(prefix, (List<Object>)((Object)b.build())));
            this.builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());
            return result;
        }
    }
}
