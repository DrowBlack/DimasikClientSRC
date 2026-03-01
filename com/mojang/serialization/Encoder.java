package com.mojang.serialization;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.FieldEncoder;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Encoder<A> {
    public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3);

    default public <T> DataResult<T> encodeStart(DynamicOps<T> ops, A input) {
        return this.encode(input, ops, ops.empty());
    }

    default public MapEncoder<A> fieldOf(String name) {
        return new FieldEncoder(name, this);
    }

    default public <B> Encoder<B> comap(final Function<? super B, ? extends A> function) {
        return new Encoder<B>(){

            @Override
            public <T> DataResult<T> encode(B input, DynamicOps<T> ops, T prefix) {
                return Encoder.this.encode(function.apply(input), ops, prefix);
            }

            public String toString() {
                return Encoder.this.toString() + "[comapped]";
            }
        };
    }

    default public <B> Encoder<B> flatComap(final Function<? super B, ? extends DataResult<? extends A>> function) {
        return new Encoder<B>(){

            @Override
            public <T> DataResult<T> encode(B input, DynamicOps<T> ops, T prefix) {
                return ((DataResult)function.apply(input)).flatMap(a -> Encoder.this.encode(a, ops, prefix));
            }

            public String toString() {
                return Encoder.this.toString() + "[flatComapped]";
            }
        };
    }

    default public Encoder<A> withLifecycle(final Lifecycle lifecycle) {
        return new Encoder<A>(){

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return Encoder.this.encode(input, ops, prefix).setLifecycle(lifecycle);
            }

            public String toString() {
                return Encoder.this.toString();
            }
        };
    }

    public static <A> MapEncoder<A> empty() {
        return new MapEncoder.Implementation<A>(){

            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix;
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            public String toString() {
                return "EmptyEncoder";
            }
        };
    }

    public static <A> Encoder<A> error(final String error) {
        return new Encoder<A>(){

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return DataResult.error(error + " " + input);
            }

            public String toString() {
                return "ErrorEncoder[" + error + "]";
            }
        };
    }
}
