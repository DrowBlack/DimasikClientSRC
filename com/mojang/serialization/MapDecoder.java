package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.CompressorHolder;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.KeyCompressor;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface MapDecoder<A>
extends Keyable {
    public <T> DataResult<A> decode(DynamicOps<T> var1, MapLike<T> var2);

    default public <T> DataResult<A> compressedDecode(DynamicOps<T> ops, T input) {
        if (ops.compressMaps()) {
            Optional<Consumer<Consumer<T>>> inputList = ops.getList(input).result();
            if (!inputList.isPresent()) {
                return DataResult.error("Input is not a list");
            }
            final KeyCompressor<T> compressor = this.compressor(ops);
            final ArrayList entries = new ArrayList();
            inputList.get().accept(entries::add);
            MapLike map2 = new MapLike<T>(){

                @Override
                @Nullable
                public T get(T key) {
                    return entries.get(compressor.compress(key));
                }

                @Override
                @Nullable
                public T get(String key) {
                    return entries.get(compressor.compress(key));
                }

                @Override
                public Stream<Pair<T, T>> entries() {
                    return IntStream.range(0, entries.size()).mapToObj(i -> Pair.of(compressor.decompress(i), entries.get(i))).filter(p -> p.getSecond() != null);
                }
            };
            return this.decode(ops, map2);
        }
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap((? super R map) -> this.decode(ops, (MapLike)map));
    }

    public <T> KeyCompressor<T> compressor(DynamicOps<T> var1);

    default public Decoder<A> decoder() {
        return new Decoder<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return MapDecoder.this.compressedDecode(ops, input).map((? super R r) -> Pair.of(r, input));
            }

            public String toString() {
                return MapDecoder.this.toString();
            }
        };
    }

    default public <B> MapDecoder<B> flatMap(final Function<? super A, ? extends DataResult<? extends B>> function) {
        return new Implementation<B>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapDecoder.this.keys(ops);
            }

            @Override
            public <T> DataResult<B> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).flatMap((? super R b) -> ((DataResult)function.apply(b)).map(Function.identity()));
            }

            public String toString() {
                return MapDecoder.this.toString() + "[flatMapped]";
            }
        };
    }

    default public <B> MapDecoder<B> map(final Function<? super A, ? extends B> function) {
        return new Implementation<B>(){

            @Override
            public <T> DataResult<B> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).map(function);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapDecoder.this.keys(ops);
            }

            public String toString() {
                return MapDecoder.this.toString() + "[mapped]";
            }
        };
    }

    default public <E> MapDecoder<E> ap(final MapDecoder<Function<? super A, ? extends E>> decoder) {
        return new Implementation<E>(){

            @Override
            public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).flatMap((? super R f) -> decoder.decode(ops, input).map((? super R e) -> e.apply(f)));
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(MapDecoder.this.keys(ops), decoder.keys(ops));
            }

            public String toString() {
                return decoder.toString() + " * " + MapDecoder.this.toString();
            }
        };
    }

    default public MapDecoder<A> withLifecycle(final Lifecycle lifecycle) {
        return new Implementation<A>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return MapDecoder.this.keys(ops);
            }

            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                return MapDecoder.this.decode(ops, input).setLifecycle(lifecycle);
            }

            public String toString() {
                return MapDecoder.this.toString();
            }
        };
    }

    public static abstract class Implementation<A>
    extends CompressorHolder
    implements MapDecoder<A> {
    }
}
