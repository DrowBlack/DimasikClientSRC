package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface DynamicOps<T> {
    public T empty();

    default public T emptyMap() {
        return (T)this.createMap(ImmutableMap.of());
    }

    default public T emptyList() {
        return this.createList(Stream.empty());
    }

    public <U> U convertTo(DynamicOps<U> var1, T var2);

    public DataResult<Number> getNumberValue(T var1);

    default public Number getNumberValue(T input, Number defaultValue) {
        return this.getNumberValue(input).result().orElse(defaultValue);
    }

    public T createNumeric(Number var1);

    default public T createByte(byte value) {
        return this.createNumeric(value);
    }

    default public T createShort(short value) {
        return this.createNumeric(value);
    }

    default public T createInt(int value) {
        return this.createNumeric(value);
    }

    default public T createLong(long value) {
        return this.createNumeric(value);
    }

    default public T createFloat(float value) {
        return this.createNumeric(Float.valueOf(value));
    }

    default public T createDouble(double value) {
        return this.createNumeric(value);
    }

    default public DataResult<Boolean> getBooleanValue(T input) {
        return this.getNumberValue(input).map(number -> number.byteValue() != 0);
    }

    default public T createBoolean(boolean value) {
        return this.createByte((byte)(value ? 1 : 0));
    }

    public DataResult<String> getStringValue(T var1);

    public T createString(String var1);

    public DataResult<T> mergeToList(T var1, T var2);

    default public DataResult<T> mergeToList(T list, List<T> values) {
        DataResult<Object> result = DataResult.success(list);
        for (T value : values) {
            result = result.flatMap(r -> this.mergeToList(r, value));
        }
        return result;
    }

    public DataResult<T> mergeToMap(T var1, T var2, T var3);

    default public DataResult<T> mergeToMap(T map, Map<T, T> values) {
        return this.mergeToMap(map, MapLike.forMap(values, this));
    }

    default public DataResult<T> mergeToMap(T map, MapLike<T> values) {
        MutableObject<DataResult<T>> result = new MutableObject<DataResult<T>>(DataResult.success(map));
        values.entries().forEach(entry -> result.setValue(((DataResult)result.getValue()).flatMap(r -> this.mergeToMap(r, entry.getFirst(), entry.getSecond()))));
        return result.getValue();
    }

    default public DataResult<T> mergeToPrimitive(T prefix, T value) {
        if (!Objects.equals(prefix, this.empty())) {
            return DataResult.error("Do not know how to append a primitive value " + value + " to " + prefix, value);
        }
        return DataResult.success(value);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T var1);

    default public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T input) {
        return this.getMapValues(input).map(s -> c -> s.forEach(p -> c.accept(p.getFirst(), p.getSecond())));
    }

    public T createMap(Stream<Pair<T, T>> var1);

    default public DataResult<MapLike<T>> getMap(T input) {
        return this.getMapValues(input).flatMap(s -> {
            try {
                return DataResult.success(MapLike.forMap(s.collect(Pair.toMap()), this));
            }
            catch (IllegalStateException e) {
                return DataResult.error("Error while building map: " + e.getMessage());
            }
        });
    }

    default public T createMap(Map<T, T> map) {
        return this.createMap(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
    }

    public DataResult<Stream<T>> getStream(T var1);

    default public DataResult<Consumer<Consumer<T>>> getList(T input) {
        return this.getStream(input).map(s -> s::forEach);
    }

    public T createList(Stream<T> var1);

    default public DataResult<ByteBuffer> getByteBuffer(T input) {
        return this.getStream(input).flatMap(stream -> {
            List list = stream.collect(Collectors.toList());
            if (list.stream().allMatch(element -> this.getNumberValue(element).result().isPresent())) {
                ByteBuffer buffer = ByteBuffer.wrap(new byte[list.size()]);
                for (int i = 0; i < list.size(); ++i) {
                    buffer.put(i, this.getNumberValue(list.get(i)).result().get().byteValue());
                }
                return DataResult.success(buffer);
            }
            return DataResult.error("Some elements are not bytes: " + input);
        });
    }

    default public T createByteList(ByteBuffer input) {
        return (T)this.createList(IntStream.range(0, input.capacity()).mapToObj(i -> this.createByte(input.get(i))));
    }

    default public DataResult<IntStream> getIntStream(T input) {
        return this.getStream(input).flatMap(stream -> {
            List list = stream.collect(Collectors.toList());
            if (list.stream().allMatch(element -> this.getNumberValue(element).result().isPresent())) {
                return DataResult.success(list.stream().mapToInt(element -> this.getNumberValue(element).result().get().intValue()));
            }
            return DataResult.error("Some elements are not ints: " + input);
        });
    }

    default public T createIntList(IntStream input) {
        return (T)this.createList(input.mapToObj(this::createInt));
    }

    default public DataResult<LongStream> getLongStream(T input) {
        return this.getStream(input).flatMap(stream -> {
            List list = stream.collect(Collectors.toList());
            if (list.stream().allMatch(element -> this.getNumberValue(element).result().isPresent())) {
                return DataResult.success(list.stream().mapToLong(element -> this.getNumberValue(element).result().get().longValue()));
            }
            return DataResult.error("Some elements are not longs: " + input);
        });
    }

    default public T createLongList(LongStream input) {
        return (T)this.createList(input.mapToObj(this::createLong));
    }

    public T remove(T var1, String var2);

    default public boolean compressMaps() {
        return false;
    }

    default public DataResult<T> get(T input, String key) {
        return this.getGeneric(input, this.createString(key));
    }

    default public DataResult<T> getGeneric(T input, T key) {
        return this.getMap(input).flatMap(map -> Optional.ofNullable(map.get(key)).map(DataResult::success).orElseGet(() -> DataResult.error("No element " + key + " in the map " + input)));
    }

    default public T set(T input, String key, T value) {
        return this.mergeToMap(input, this.createString(key), value).result().orElse(input);
    }

    default public T update(T input, String key, Function<T, T> function) {
        return (T)this.get(input, key).map(value -> this.set(input, key, function.apply(value))).result().orElse(input);
    }

    default public T updateGeneric(T input, T key, Function<T, T> function) {
        return (T)this.getGeneric(input, key).flatMap(value -> this.mergeToMap(input, key, function.apply(value))).result().orElse(input);
    }

    default public ListBuilder<T> listBuilder() {
        return new ListBuilder.Builder(this);
    }

    default public RecordBuilder<T> mapBuilder() {
        return new RecordBuilder.MapBuilder(this);
    }

    default public <E> Function<E, DataResult<T>> withEncoder(Encoder<E> encoder) {
        return e -> encoder.encodeStart(this, e);
    }

    default public <E> Function<T, DataResult<Pair<E, T>>> withDecoder(Decoder<E> decoder) {
        return t -> decoder.decode(this, t);
    }

    default public <E> Function<T, DataResult<E>> withParser(Decoder<E> decoder) {
        return t -> decoder.parse(this, t);
    }

    default public <U> U convertList(DynamicOps<U> outOps, T input) {
        return (U)outOps.createList(this.getStream(input).result().orElse(Stream.empty()).map(e -> this.convertTo(outOps, e)));
    }

    default public <U> U convertMap(DynamicOps<U> outOps, T input) {
        return outOps.createMap(this.getMapValues(input).result().orElse(Stream.empty()).map(e -> Pair.of(this.convertTo(outOps, e.getFirst()), this.convertTo(outOps, e.getSecond()))));
    }
}
