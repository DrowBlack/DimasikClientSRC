package com.mojang.serialization;

import com.mojang.serialization.CompressorHolder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.KeyCompressor;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface MapEncoder<A>
extends Keyable {
    public <T> RecordBuilder<T> encode(A var1, DynamicOps<T> var2, RecordBuilder<T> var3);

    default public <T> RecordBuilder<T> compressedBuilder(DynamicOps<T> ops) {
        if (ops.compressMaps()) {
            return MapEncoder.makeCompressedBuilder(ops, this.compressor(ops));
        }
        return ops.mapBuilder();
    }

    public <T> KeyCompressor<T> compressor(DynamicOps<T> var1);

    default public <B> MapEncoder<B> comap(final Function<? super B, ? extends A> function) {
        return new Implementation<B>(){

            @Override
            public <T> RecordBuilder<T> encode(B input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return MapEncoder.this.encode(function.apply(input), ops, prefix);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapEncoder.this.keys(ops);
            }

            public String toString() {
                return MapEncoder.this.toString() + "[comapped]";
            }
        };
    }

    default public <B> MapEncoder<B> flatComap(final Function<? super B, ? extends DataResult<? extends A>> function) {
        return new Implementation<B>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapEncoder.this.keys(ops);
            }

            @Override
            public <T> RecordBuilder<T> encode(B input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                DataResult aResult = (DataResult)function.apply(input);
                RecordBuilder builder = prefix.withErrorsFrom(aResult);
                return aResult.map(r -> MapEncoder.this.encode(r, ops, builder)).result().orElse(builder);
            }

            public String toString() {
                return MapEncoder.this.toString() + "[flatComapped]";
            }
        };
    }

    default public Encoder<A> encoder() {
        return new Encoder<A>(){

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return MapEncoder.this.encode(input, ops, MapEncoder.this.compressedBuilder(ops)).build(prefix);
            }

            public String toString() {
                return MapEncoder.this.toString();
            }
        };
    }

    default public MapEncoder<A> withLifecycle(final Lifecycle lifecycle) {
        return new Implementation<A>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapEncoder.this.keys(ops);
            }

            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return MapEncoder.this.encode(input, ops, prefix).setLifecycle(lifecycle);
            }

            public String toString() {
                return MapEncoder.this.toString();
            }
        };
    }

    public static <T> RecordBuilder<T> makeCompressedBuilder(final DynamicOps<T> ops, final KeyCompressor<T> compressor) {
        class CompressedRecordBuilder
        extends RecordBuilder.AbstractUniversalBuilder<T, List<T>> {
            CompressedRecordBuilder() {
                super(dynamicOps);
            }

            @Override
            protected List<T> initBuilder() {
                ArrayList<Object> list = new ArrayList<Object>(compressor.size());
                for (int i = 0; i < compressor.size(); ++i) {
                    list.add(null);
                }
                return list;
            }

            @Override
            protected List<T> append(T key, T value, List<T> builder) {
                builder.set(compressor.compress(key), value);
                return builder;
            }

            @Override
            protected DataResult<T> build(List<T> builder, T prefix) {
                return this.ops().mergeToList(prefix, builder);
            }
        }
        return new CompressedRecordBuilder();
    }

    public static abstract class Implementation<A>
    extends CompressorHolder
    implements MapEncoder<A> {
    }
}
