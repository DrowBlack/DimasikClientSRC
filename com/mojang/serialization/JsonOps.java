package com.mojang.serialization;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class JsonOps
implements DynamicOps<JsonElement> {
    public static final JsonOps INSTANCE = new JsonOps(false);
    public static final JsonOps COMPRESSED = new JsonOps(true);
    private final boolean compressed;

    protected JsonOps(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public JsonElement empty() {
        return JsonNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonElement input) {
        if (input instanceof JsonObject) {
            return this.convertMap(outOps, input);
        }
        if (input instanceof JsonArray) {
            return this.convertList(outOps, input);
        }
        if (input instanceof JsonNull) {
            return outOps.empty();
        }
        JsonPrimitive primitive = input.getAsJsonPrimitive();
        if (primitive.isString()) {
            return outOps.createString(primitive.getAsString());
        }
        if (primitive.isBoolean()) {
            return outOps.createBoolean(primitive.getAsBoolean());
        }
        BigDecimal value = primitive.getAsBigDecimal();
        try {
            long l = value.longValueExact();
            if ((long)((byte)l) == l) {
                return outOps.createByte((byte)l);
            }
            if ((long)((short)l) == l) {
                return outOps.createShort((short)l);
            }
            if ((long)((int)l) == l) {
                return outOps.createInt((int)l);
            }
            return outOps.createLong(l);
        }
        catch (ArithmeticException e) {
            double d = value.doubleValue();
            if ((double)((float)d) == d) {
                return outOps.createFloat((float)d);
            }
            return outOps.createDouble(d);
        }
    }

    @Override
    public DataResult<Number> getNumberValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            if (input.getAsJsonPrimitive().isNumber()) {
                return DataResult.success(input.getAsNumber());
            }
            if (input.getAsJsonPrimitive().isBoolean()) {
                return DataResult.success(input.getAsBoolean() ? 1 : 0);
            }
            if (this.compressed && input.getAsJsonPrimitive().isString()) {
                try {
                    return DataResult.success(Integer.parseInt(input.getAsString()));
                }
                catch (NumberFormatException e) {
                    return DataResult.error("Not a number: " + e + " " + input);
                }
            }
        }
        if (input instanceof JsonPrimitive && input.getAsJsonPrimitive().isBoolean()) {
            return DataResult.success(input.getAsJsonPrimitive().getAsBoolean() ? 1 : 0);
        }
        return DataResult.error("Not a number: " + input);
    }

    @Override
    public JsonElement createNumeric(Number i) {
        return new JsonPrimitive(i);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(JsonElement input) {
        if (input instanceof JsonPrimitive) {
            if (input.getAsJsonPrimitive().isBoolean()) {
                return DataResult.success(input.getAsBoolean());
            }
            if (input.getAsJsonPrimitive().isNumber()) {
                return DataResult.success(input.getAsNumber().byteValue() != 0);
            }
        }
        return DataResult.error("Not a boolean: " + input);
    }

    @Override
    public JsonElement createBoolean(boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<String> getStringValue(JsonElement input) {
        if (input instanceof JsonPrimitive && (input.getAsJsonPrimitive().isString() || input.getAsJsonPrimitive().isNumber() && this.compressed)) {
            return DataResult.success(input.getAsString());
        }
        return DataResult.error("Not a string: " + input);
    }

    @Override
    public JsonElement createString(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, JsonElement value) {
        if (!(list instanceof JsonArray) && list != this.empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }
        JsonArray result = new JsonArray();
        if (list != this.empty()) {
            result.addAll(list.getAsJsonArray());
        }
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<JsonElement> mergeToList(JsonElement list, List<JsonElement> values) {
        if (!(list instanceof JsonArray) && list != this.empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }
        JsonArray result = new JsonArray();
        if (list != this.empty()) {
            result.addAll(list.getAsJsonArray());
        }
        values.forEach(result::add);
        return DataResult.success(result);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, JsonElement key, JsonElement value) {
        if (!(map instanceof JsonObject) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        if (!(key instanceof JsonPrimitive) || !key.getAsJsonPrimitive().isString() && !this.compressed) {
            return DataResult.error("key is not a string: " + key, map);
        }
        JsonObject output = new JsonObject();
        if (map != this.empty()) {
            map.getAsJsonObject().entrySet().forEach(entry -> output.add((String)entry.getKey(), (JsonElement)entry.getValue()));
        }
        output.add(key.getAsString(), value);
        return DataResult.success(output);
    }

    @Override
    public DataResult<JsonElement> mergeToMap(JsonElement map, MapLike<JsonElement> values) {
        if (!(map instanceof JsonObject) && map != this.empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        JsonObject output = new JsonObject();
        if (map != this.empty()) {
            map.getAsJsonObject().entrySet().forEach(entry -> output.add((String)entry.getKey(), (JsonElement)entry.getValue()));
        }
        ArrayList missed = Lists.newArrayList();
        values.entries().forEach(entry -> {
            JsonElement key = (JsonElement)entry.getFirst();
            if (!(key instanceof JsonPrimitive) || !key.getAsJsonPrimitive().isString() && !this.compressed) {
                missed.add(key);
                return;
            }
            output.add(key.getAsString(), (JsonElement)entry.getSecond());
        });
        if (!missed.isEmpty()) {
            return DataResult.error("some keys are not strings: " + missed, output);
        }
        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<JsonElement, JsonElement>>> getMapValues(JsonElement input) {
        if (!(input instanceof JsonObject)) {
            return DataResult.error("Not a JSON object: " + input);
        }
        return DataResult.success(input.getAsJsonObject().entrySet().stream().map(entry -> Pair.of(new JsonPrimitive((String)entry.getKey()), entry.getValue() instanceof JsonNull ? null : (JsonElement)entry.getValue())));
    }

    @Override
    public DataResult<Consumer<BiConsumer<JsonElement, JsonElement>>> getMapEntries(JsonElement input) {
        if (!(input instanceof JsonObject)) {
            return DataResult.error("Not a JSON object: " + input);
        }
        return DataResult.success(c -> {
            for (Map.Entry<String, JsonElement> entry : input.getAsJsonObject().entrySet()) {
                c.accept(this.createString(entry.getKey()), entry.getValue() instanceof JsonNull ? null : entry.getValue());
            }
        });
    }

    @Override
    public DataResult<MapLike<JsonElement>> getMap(JsonElement input) {
        if (!(input instanceof JsonObject)) {
            return DataResult.error("Not a JSON object: " + input);
        }
        final JsonObject object = input.getAsJsonObject();
        return DataResult.success(new MapLike<JsonElement>(){

            @Override
            @Nullable
            public JsonElement get(JsonElement key) {
                JsonElement element = object.get(key.getAsString());
                if (element instanceof JsonNull) {
                    return null;
                }
                return element;
            }

            @Override
            @Nullable
            public JsonElement get(String key) {
                JsonElement element = object.get(key);
                if (element instanceof JsonNull) {
                    return null;
                }
                return element;
            }

            @Override
            public Stream<Pair<JsonElement, JsonElement>> entries() {
                return object.entrySet().stream().map(e -> Pair.of(new JsonPrimitive((String)e.getKey()), e.getValue()));
            }

            public String toString() {
                return "MapLike[" + object + "]";
            }
        });
    }

    @Override
    public JsonElement createMap(Stream<Pair<JsonElement, JsonElement>> map) {
        JsonObject result = new JsonObject();
        map.forEach(p -> result.add(((JsonElement)p.getFirst()).getAsString(), (JsonElement)p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<JsonElement>> getStream(JsonElement input) {
        if (input instanceof JsonArray) {
            return DataResult.success(StreamSupport.stream(input.getAsJsonArray().spliterator(), false).map(e -> e instanceof JsonNull ? null : e));
        }
        return DataResult.error("Not a json array: " + input);
    }

    @Override
    public DataResult<Consumer<Consumer<JsonElement>>> getList(JsonElement input) {
        if (input instanceof JsonArray) {
            return DataResult.success(c -> {
                for (JsonElement element : input.getAsJsonArray()) {
                    c.accept(element instanceof JsonNull ? null : element);
                }
            });
        }
        return DataResult.error("Not a json array: " + input);
    }

    @Override
    public JsonElement createList(Stream<JsonElement> input) {
        JsonArray result = new JsonArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public JsonElement remove(JsonElement input, String key) {
        if (input instanceof JsonObject) {
            JsonObject result = new JsonObject();
            input.getAsJsonObject().entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.add((String)entry.getKey(), (JsonElement)entry.getValue()));
            return result;
        }
        return input;
    }

    public String toString() {
        return "JSON";
    }

    @Override
    public ListBuilder<JsonElement> listBuilder() {
        return new ArrayBuilder();
    }

    @Override
    public boolean compressMaps() {
        return this.compressed;
    }

    @Override
    public RecordBuilder<JsonElement> mapBuilder() {
        return new JsonRecordBuilder();
    }

    private class JsonRecordBuilder
    extends RecordBuilder.AbstractStringBuilder<JsonElement, JsonObject> {
        protected JsonRecordBuilder() {
            super(JsonOps.this);
        }

        @Override
        protected JsonObject initBuilder() {
            return new JsonObject();
        }

        @Override
        protected JsonObject append(String key, JsonElement value, JsonObject builder) {
            builder.add(key, value);
            return builder;
        }

        @Override
        protected DataResult<JsonElement> build(JsonObject builder, JsonElement prefix) {
            if (prefix == null || prefix instanceof JsonNull) {
                return DataResult.success(builder);
            }
            if (prefix instanceof JsonObject) {
                JsonObject result = new JsonObject();
                for (Map.Entry<String, JsonElement> entry : prefix.getAsJsonObject().entrySet()) {
                    result.add(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<String, JsonElement> entry : builder.entrySet()) {
                    result.add(entry.getKey(), entry.getValue());
                }
                return DataResult.success(result);
            }
            return DataResult.error("mergeToMap called with not a map: " + prefix, prefix);
        }
    }

    private static final class ArrayBuilder
    implements ListBuilder<JsonElement> {
        private DataResult<JsonArray> builder = DataResult.success(new JsonArray(), Lifecycle.stable());

        private ArrayBuilder() {
        }

        @Override
        public DynamicOps<JsonElement> ops() {
            return INSTANCE;
        }

        @Override
        public ListBuilder<JsonElement> add(JsonElement value) {
            this.builder = this.builder.map(b -> {
                b.add(value);
                return b;
            });
            return this;
        }

        @Override
        public ListBuilder<JsonElement> add(DataResult<JsonElement> value) {
            this.builder = this.builder.apply2stable((b, element) -> {
                b.add((JsonElement)element);
                return b;
            }, value);
            return this;
        }

        @Override
        public ListBuilder<JsonElement> withErrorsFrom(DataResult<?> result) {
            this.builder = this.builder.flatMap(r -> result.map(v -> r));
            return this;
        }

        @Override
        public ListBuilder<JsonElement> mapError(UnaryOperator<String> onError) {
            this.builder = this.builder.mapError(onError);
            return this;
        }

        @Override
        public DataResult<JsonElement> build(JsonElement prefix) {
            DataResult<JsonElement> result = this.builder.flatMap(b -> {
                if (!(prefix instanceof JsonArray) && prefix != this.ops().empty()) {
                    return DataResult.error("Cannot append a list to not a list: " + prefix, prefix);
                }
                JsonArray array = new JsonArray();
                if (prefix != this.ops().empty()) {
                    array.addAll(prefix.getAsJsonArray());
                }
                array.addAll((JsonArray)b);
                return DataResult.success(array, Lifecycle.stable());
            });
            this.builder = DataResult.success(new JsonArray(), Lifecycle.stable());
            return result;
        }
    }
}
