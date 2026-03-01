package com.mojang.serialization;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.CompoundListCodec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.EitherMapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.PairCodec;
import com.mojang.serialization.codecs.PairMapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface Codec<A>
extends Encoder<A>,
Decoder<A> {
    public static final PrimitiveCodec<Boolean> BOOL = new PrimitiveCodec<Boolean>(){

        @Override
        public <T> DataResult<Boolean> read(DynamicOps<T> ops, T input) {
            return ops.getBooleanValue(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Boolean value) {
            return ops.createBoolean(value);
        }

        public String toString() {
            return "Bool";
        }
    };
    public static final PrimitiveCodec<Byte> BYTE = new PrimitiveCodec<Byte>(){

        @Override
        public <T> DataResult<Byte> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::byteValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Byte value) {
            return ops.createByte(value);
        }

        public String toString() {
            return "Byte";
        }
    };
    public static final PrimitiveCodec<Short> SHORT = new PrimitiveCodec<Short>(){

        @Override
        public <T> DataResult<Short> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::shortValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Short value) {
            return ops.createShort(value);
        }

        public String toString() {
            return "Short";
        }
    };
    public static final PrimitiveCodec<Integer> INT = new PrimitiveCodec<Integer>(){

        @Override
        public <T> DataResult<Integer> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::intValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Integer value) {
            return ops.createInt(value);
        }

        public String toString() {
            return "Int";
        }
    };
    public static final PrimitiveCodec<Long> LONG = new PrimitiveCodec<Long>(){

        @Override
        public <T> DataResult<Long> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::longValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Long value) {
            return ops.createLong(value);
        }

        public String toString() {
            return "Long";
        }
    };
    public static final PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<Float>(){

        @Override
        public <T> DataResult<Float> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::floatValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Float value) {
            return ops.createFloat(value.floatValue());
        }

        public String toString() {
            return "Float";
        }
    };
    public static final PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<Double>(){

        @Override
        public <T> DataResult<Double> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(Number::doubleValue);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Double value) {
            return ops.createDouble(value);
        }

        public String toString() {
            return "Double";
        }
    };
    public static final PrimitiveCodec<String> STRING = new PrimitiveCodec<String>(){

        @Override
        public <T> DataResult<String> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, String value) {
            return ops.createString(value);
        }

        public String toString() {
            return "String";
        }
    };
    public static final PrimitiveCodec<ByteBuffer> BYTE_BUFFER = new PrimitiveCodec<ByteBuffer>(){

        @Override
        public <T> DataResult<ByteBuffer> read(DynamicOps<T> ops, T input) {
            return ops.getByteBuffer(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, ByteBuffer value) {
            return ops.createByteList(value);
        }

        public String toString() {
            return "ByteBuffer";
        }
    };
    public static final PrimitiveCodec<IntStream> INT_STREAM = new PrimitiveCodec<IntStream>(){

        @Override
        public <T> DataResult<IntStream> read(DynamicOps<T> ops, T input) {
            return ops.getIntStream(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, IntStream value) {
            return ops.createIntList(value);
        }

        public String toString() {
            return "IntStream";
        }
    };
    public static final PrimitiveCodec<LongStream> LONG_STREAM = new PrimitiveCodec<LongStream>(){

        @Override
        public <T> DataResult<LongStream> read(DynamicOps<T> ops, T input) {
            return ops.getLongStream(input);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, LongStream value) {
            return ops.createLongList(value);
        }

        public String toString() {
            return "LongStream";
        }
    };
    public static final Codec<Dynamic<?>> PASSTHROUGH = new Codec<Dynamic<?>>(){

        @Override
        public <T> DataResult<Pair<Dynamic<?>, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(Pair.of(new Dynamic<T>(ops, input), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(Dynamic<?> input, DynamicOps<T> ops, T prefix) {
            if (input.getValue() == input.getOps().empty()) {
                return DataResult.success(prefix, Lifecycle.experimental());
            }
            Object casted = input.convert(ops).getValue();
            if (prefix == ops.empty()) {
                return DataResult.success(casted, Lifecycle.experimental());
            }
            DataResult toMap = ops.getMap(casted).flatMap((? super R map) -> ops.mergeToMap(prefix, (MapLike)map));
            return toMap.result().map(DataResult::success).orElseGet(() -> {
                DataResult toList = ops.getStream(casted).flatMap((? super R stream) -> ops.mergeToList(prefix, stream.collect(Collectors.toList())));
                return toList.result().map(DataResult::success).orElseGet(() -> DataResult.error("Don't know how to merge " + prefix + " and " + casted, prefix, Lifecycle.experimental()));
            });
        }

        public String toString() {
            return "passthrough";
        }
    };
    public static final MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE));

    @Override
    default public Codec<A> withLifecycle(final Lifecycle lifecycle) {
        return new Codec<A>(){

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return Codec.this.encode(input, ops, prefix).setLifecycle(lifecycle);
            }

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return Codec.this.decode(ops, input).setLifecycle(lifecycle);
            }

            public String toString() {
                return Codec.this.toString();
            }
        };
    }

    default public Codec<A> stable() {
        return this.withLifecycle(Lifecycle.stable());
    }

    default public Codec<A> deprecated(int since) {
        return this.withLifecycle(Lifecycle.deprecated(since));
    }

    public static <A> Codec<A> of(Encoder<A> encoder, Decoder<A> decoder) {
        return Codec.of(encoder, decoder, "Codec[" + encoder + " " + decoder + "]");
    }

    public static <A> Codec<A> of(final Encoder<A> encoder, final Decoder<A> decoder, final String name) {
        return new Codec<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return encoder.encode(input, ops, prefix);
            }

            public String toString() {
                return name;
            }
        };
    }

    public static <A> MapCodec<A> of(MapEncoder<A> encoder, MapDecoder<A> decoder) {
        return Codec.of(encoder, decoder, () -> "MapCodec[" + encoder + " " + decoder + "]");
    }

    public static <A> MapCodec<A> of(final MapEncoder<A> encoder, final MapDecoder<A> decoder, final Supplier<String> name) {
        return new MapCodec<A>(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(encoder.keys(ops), decoder.keys(ops));
            }

            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return encoder.encode(input, ops, prefix);
            }

            public String toString() {
                return (String)name.get();
            }
        };
    }

    public static <F, S> Codec<Pair<F, S>> pair(Codec<F> first, Codec<S> second) {
        return new PairCodec<F, S>(first, second);
    }

    public static <F, S> Codec<Either<F, S>> either(Codec<F> first, Codec<S> second) {
        return new EitherCodec<F, S>(first, second);
    }

    public static <F, S> MapCodec<Pair<F, S>> mapPair(MapCodec<F> first, MapCodec<S> second) {
        return new PairMapCodec<F, S>(first, second);
    }

    public static <F, S> MapCodec<Either<F, S>> mapEither(MapCodec<F> first, MapCodec<S> second) {
        return new EitherMapCodec<F, S>(first, second);
    }

    public static <E> Codec<List<E>> list(Codec<E> elementCodec) {
        return new ListCodec<E>(elementCodec);
    }

    public static <K, V> Codec<List<Pair<K, V>>> compoundList(Codec<K> keyCodec, Codec<V> elementCodec) {
        return new CompoundListCodec<K, V>(keyCodec, elementCodec);
    }

    public static <K, V> SimpleMapCodec<K, V> simpleMap(Codec<K> keyCodec, Codec<V> elementCodec, Keyable keys) {
        return new SimpleMapCodec<K, V>(keyCodec, elementCodec, keys);
    }

    public static <K, V> UnboundedMapCodec<K, V> unboundedMap(Codec<K> keyCodec, Codec<V> elementCodec) {
        return new UnboundedMapCodec<K, V>(keyCodec, elementCodec);
    }

    public static <F> MapCodec<Optional<F>> optionalField(String name, Codec<F> elementCodec) {
        return new OptionalFieldCodec<F>(name, elementCodec);
    }

    default public Codec<List<A>> listOf() {
        return Codec.list(this);
    }

    default public <S> Codec<S> xmap(Function<? super A, ? extends S> to, Function<? super S, ? extends A> from) {
        return Codec.of(this.comap(from), this.map(to), this.toString() + "[xmapped]");
    }

    default public <S> Codec<S> comapFlatMap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends A> from) {
        return Codec.of(this.comap(from), this.flatMap(to), this.toString() + "[comapFlatMapped]");
    }

    default public <S> Codec<S> flatComapMap(Function<? super A, ? extends S> to, Function<? super S, ? extends DataResult<? extends A>> from) {
        return Codec.of(this.flatComap(from), this.map(to), this.toString() + "[flatComapMapped]");
    }

    default public <S> Codec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> to, Function<? super S, ? extends DataResult<? extends A>> from) {
        return Codec.of(this.flatComap(from), this.flatMap(to), this.toString() + "[flatXmapped]");
    }

    @Override
    default public MapCodec<A> fieldOf(String name) {
        return MapCodec.of(Encoder.super.fieldOf(name), Decoder.super.fieldOf(name), () -> "Field[" + name + ": " + this.toString() + "]");
    }

    default public MapCodec<Optional<A>> optionalFieldOf(String name) {
        return Codec.optionalField(name, this);
    }

    default public MapCodec<A> optionalFieldOf(String name, A defaultValue) {
        return Codec.optionalField(name, this).xmap(o -> o.orElse(defaultValue), a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a));
    }

    default public MapCodec<A> optionalFieldOf(String name, A defaultValue, Lifecycle lifecycleOfDefault) {
        return this.optionalFieldOf(name, Lifecycle.experimental(), defaultValue, lifecycleOfDefault);
    }

    default public MapCodec<A> optionalFieldOf(String name, Lifecycle fieldLifecycle, A defaultValue, Lifecycle lifecycleOfDefault) {
        return Codec.optionalField(name, this).stable().flatXmap(o -> o.map((? super T v) -> DataResult.success(v, fieldLifecycle)).orElse(DataResult.success(defaultValue, lifecycleOfDefault)), a -> Objects.equals(a, defaultValue) ? DataResult.success(Optional.empty(), lifecycleOfDefault) : DataResult.success(Optional.of(a), fieldLifecycle));
    }

    default public Codec<A> mapResult(final ResultFunction<A> function) {
        return new Codec<A>(){

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return function.coApply(ops, input, Codec.this.encode(input, ops, prefix));
            }

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return function.apply(ops, input, Codec.this.decode(ops, input));
            }

            public String toString() {
                return Codec.this + "[mapResult " + function + "]";
            }
        };
    }

    default public Codec<A> orElse(Consumer<String> onError, A value) {
        return this.orElse(DataFixUtils.consumerToFunction(onError), value);
    }

    default public Codec<A> orElse(final UnaryOperator<String> onError, final A value) {
        return this.mapResult(new ResultFunction<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> a) {
                return DataResult.success(a.mapError(onError).result().orElseGet(() -> Pair.of(value, input)));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> t) {
                return t.mapError(onError);
            }

            public String toString() {
                return "OrElse[" + onError + " " + value + "]";
            }
        });
    }

    default public Codec<A> orElseGet(Consumer<String> onError, Supplier<? extends A> value) {
        return this.orElseGet(DataFixUtils.consumerToFunction(onError), value);
    }

    default public Codec<A> orElseGet(final UnaryOperator<String> onError, final Supplier<? extends A> value) {
        return this.mapResult(new ResultFunction<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> a) {
                return DataResult.success(a.mapError(onError).result().orElseGet(() -> 6.lambda$apply$0((Supplier)value, input)));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> t) {
                return t.mapError(onError);
            }

            public String toString() {
                return "OrElseGet[" + onError + " " + value.get() + "]";
            }

            private static /* synthetic */ Pair lambda$apply$0(Supplier value2, Object input) {
                return Pair.of(value2.get(), input);
            }
        });
    }

    default public Codec<A> orElse(final A value) {
        return this.mapResult(new ResultFunction<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> a) {
                return DataResult.success(a.result().orElseGet(() -> Pair.of(value, input)));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> t) {
                return t;
            }

            public String toString() {
                return "OrElse[" + value + "]";
            }
        });
    }

    default public Codec<A> orElseGet(final Supplier<? extends A> value) {
        return this.mapResult(new ResultFunction<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> a) {
                return DataResult.success(a.result().orElseGet(() -> 8.lambda$apply$0((Supplier)value, input)));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> t) {
                return t;
            }

            public String toString() {
                return "OrElseGet[" + value.get() + "]";
            }

            private static /* synthetic */ Pair lambda$apply$0(Supplier value2, Object input) {
                return Pair.of(value2.get(), input);
            }
        });
    }

    @Override
    default public Codec<A> promotePartial(Consumer<String> onError) {
        return Codec.of(this, Decoder.super.promotePartial(onError));
    }

    public static <A> Codec<A> unit(A defaultValue) {
        return Codec.unit(() -> defaultValue);
    }

    public static <A> Codec<A> unit(Supplier<A> defaultValue) {
        return MapCodec.unit(defaultValue).codec();
    }

    default public <E> Codec<E> dispatch(Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
        return this.dispatch("type", type, codec);
    }

    default public <E> Codec<E> dispatch(String typeKey, Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
        return this.partialDispatch(typeKey, type.andThen(DataResult::success), codec.andThen(DataResult::success));
    }

    default public <E> Codec<E> dispatchStable(Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
        return this.partialDispatch("type", e -> DataResult.success(type.apply((Object)e), Lifecycle.stable()), a -> DataResult.success(codec.apply((Object)a), Lifecycle.stable()));
    }

    default public <E> Codec<E> partialDispatch(String typeKey, Function<? super E, ? extends DataResult<? extends A>> type, Function<? super A, ? extends DataResult<? extends Codec<? extends E>>> codec) {
        return new KeyDispatchCodec<A, E>(typeKey, this, type, codec).codec();
    }

    default public <E> MapCodec<E> dispatchMap(Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
        return this.dispatchMap("type", type, codec);
    }

    default public <E> MapCodec<E> dispatchMap(String typeKey, Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
        return new KeyDispatchCodec<A, E>(typeKey, this, type.andThen(DataResult::success), codec.andThen(DataResult::success));
    }

    public static <N extends Number> Function<N, DataResult<N>> checkRange(N minInclusive, N maxInclusive) {
        return value -> {
            if (((Comparable)((Object)value)).compareTo(minInclusive) >= 0 && ((Comparable)((Object)value)).compareTo(maxInclusive) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error("Value " + value + " outside of range [" + minInclusive + ":" + maxInclusive + "]", value);
        };
    }

    public static Codec<Integer> intRange(int minInclusive, int maxInclusive) {
        Function<Integer, DataResult<Integer>> checker = Codec.checkRange(minInclusive, maxInclusive);
        return INT.flatXmap(checker, checker);
    }

    public static Codec<Float> floatRange(float minInclusive, float maxInclusive) {
        Function<Float, DataResult<Float>> checker = Codec.checkRange(Float.valueOf(minInclusive), Float.valueOf(maxInclusive));
        return FLOAT.flatXmap(checker, checker);
    }

    public static Codec<Double> doubleRange(double minInclusive, double maxInclusive) {
        Function<Double, DataResult<Double>> checker = Codec.checkRange(minInclusive, maxInclusive);
        return DOUBLE.flatXmap(checker, checker);
    }

    public static interface ResultFunction<A> {
        public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3);

        public <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3);
    }
}
