package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class MinMaxBounds<T extends Number> {
    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.swapped"));
    protected final T min;
    protected final T max;

    protected MinMaxBounds(@Nullable T min, @Nullable T max) {
        this.min = min;
        this.max = max;
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    public boolean isUnbounded() {
        return this.min == null && this.max == null;
    }

    public JsonElement serialize() {
        if (this.isUnbounded()) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive((Number)this.min);
        }
        JsonObject jsonobject = new JsonObject();
        if (this.min != null) {
            jsonobject.addProperty("min", (Number)this.min);
        }
        if (this.max != null) {
            jsonobject.addProperty("max", (Number)this.max);
        }
        return jsonobject;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement element, R defaultIn, BiFunction<JsonElement, String, T> biFunction, IBoundFactory<T, R> boundedFactory) {
        if (element != null && !element.isJsonNull()) {
            if (JSONUtils.isNumber(element)) {
                Number t2 = (Number)biFunction.apply(element, "value");
                return boundedFactory.create(t2, t2);
            }
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "value");
            Number t = jsonobject.has("min") ? (Number)((Number)biFunction.apply(jsonobject.get("min"), "min")) : (Number)null;
            Number t1 = jsonobject.has("max") ? (Number)((Number)biFunction.apply(jsonobject.get("max"), "max")) : (Number)null;
            return boundedFactory.create(t, t1);
        }
        return defaultIn;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader reader, IBoundReader<T, R> minMaxReader, Function<String, T> valueFunction, Supplier<DynamicCommandExceptionType> commandExceptionSupplier, Function<T, T> function) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_EMPTY.createWithContext(reader);
        }
        int i = reader.getCursor();
        try {
            Number t1;
            Number t = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(reader, valueFunction, commandExceptionSupplier), function);
            if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
                reader.skip();
                reader.skip();
                t1 = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(reader, valueFunction, commandExceptionSupplier), function);
                if (t == null && t1 == null) {
                    throw ERROR_EMPTY.createWithContext(reader);
                }
            } else {
                t1 = t;
            }
            if (t == null && t1 == null) {
                throw ERROR_EMPTY.createWithContext(reader);
            }
            return minMaxReader.create(reader, t, t1);
        }
        catch (CommandSyntaxException commandsyntaxexception) {
            reader.setCursor(i);
            throw new CommandSyntaxException(commandsyntaxexception.getType(), commandsyntaxexception.getRawMessage(), commandsyntaxexception.getInput(), i);
        }
    }

    @Nullable
    private static <T extends Number> T readNumber(StringReader reader, Function<String, T> stringToValueFunction, Supplier<DynamicCommandExceptionType> commandExceptionSupplier) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && MinMaxBounds.isAllowedInputChat(reader)) {
            reader.skip();
        }
        String s = reader.getString().substring(i, reader.getCursor());
        if (s.isEmpty()) {
            return (T)((Number)null);
        }
        try {
            return (T)((Number)stringToValueFunction.apply(s));
        }
        catch (NumberFormatException numberformatexception) {
            throw commandExceptionSupplier.get().createWithContext(reader, s);
        }
    }

    private static boolean isAllowedInputChat(StringReader reader) {
        char c0 = reader.peek();
        if ((c0 < '0' || c0 > '9') && c0 != '-') {
            if (c0 != '.') {
                return false;
            }
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return true;
    }

    @Nullable
    private static <T> T optionallyFormat(@Nullable T value, Function<T, T> formatterFunction) {
        return value == null ? null : (T)formatterFunction.apply(value);
    }

    @FunctionalInterface
    public static interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(@Nullable T var1, @Nullable T var2);
    }

    @FunctionalInterface
    public static interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {
        public R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
    }

    public static class IntBound
    extends MinMaxBounds<Integer> {
        public static final IntBound UNBOUNDED = new IntBound((Integer)null, (Integer)null);
        private final Long minSquared;
        private final Long maxSquared;

        private static IntBound create(StringReader reader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw ERROR_SWAPPED.createWithContext(reader);
            }
            return new IntBound(min, max);
        }

        @Nullable
        private static Long square(@Nullable Integer value) {
            return value == null ? null : Long.valueOf(value.longValue() * value.longValue());
        }

        private IntBound(@Nullable Integer min, @Nullable Integer max) {
            super(min, max);
            this.minSquared = IntBound.square(min);
            this.maxSquared = IntBound.square(max);
        }

        public static IntBound exactly(int value) {
            return new IntBound(value, value);
        }

        public static IntBound atLeast(int value) {
            return new IntBound(value, (Integer)null);
        }

        public boolean test(int value) {
            if (this.min != null && (Integer)this.min > value) {
                return false;
            }
            return this.max == null || (Integer)this.max >= value;
        }

        public static IntBound fromJson(@Nullable JsonElement element) {
            return IntBound.fromJson(element, UNBOUNDED, JSONUtils::getInt, IntBound::new);
        }

        public static IntBound fromReader(StringReader reader) throws CommandSyntaxException {
            return IntBound.fromReader(reader, integer -> integer);
        }

        public static IntBound fromReader(StringReader reader, Function<Integer, Integer> valueFunction) throws CommandSyntaxException {
            return IntBound.fromReader(reader, IntBound::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, valueFunction);
        }
    }

    public static class FloatBound
    extends MinMaxBounds<Float> {
        public static final FloatBound UNBOUNDED = new FloatBound((Float)null, (Float)null);
        private final Double minSquared;
        private final Double maxSquared;

        private static FloatBound create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException {
            if (min != null && max != null && min.floatValue() > max.floatValue()) {
                throw ERROR_SWAPPED.createWithContext(reader);
            }
            return new FloatBound(min, max);
        }

        @Nullable
        private static Double square(@Nullable Float value) {
            return value == null ? null : Double.valueOf(value.doubleValue() * value.doubleValue());
        }

        private FloatBound(@Nullable Float min, @Nullable Float max) {
            super(min, max);
            this.minSquared = FloatBound.square(min);
            this.maxSquared = FloatBound.square(max);
        }

        public static FloatBound atLeast(float value) {
            return new FloatBound(Float.valueOf(value), (Float)null);
        }

        public boolean test(float value) {
            if (this.min != null && ((Float)this.min).floatValue() > value) {
                return false;
            }
            return this.max == null || !(((Float)this.max).floatValue() < value);
        }

        public boolean testSquared(double value) {
            if (this.minSquared != null && this.minSquared > value) {
                return false;
            }
            return this.maxSquared == null || !(this.maxSquared < value);
        }

        public static FloatBound fromJson(@Nullable JsonElement element) {
            return FloatBound.fromJson(element, UNBOUNDED, JSONUtils::getFloat, FloatBound::new);
        }

        public static FloatBound fromReader(StringReader reader) throws CommandSyntaxException {
            return FloatBound.fromReader(reader, floatValue -> floatValue);
        }

        public static FloatBound fromReader(StringReader reader, Function<Float, Float> valueFunction) throws CommandSyntaxException {
            return FloatBound.fromReader(reader, FloatBound::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, valueFunction);
        }
    }
}
