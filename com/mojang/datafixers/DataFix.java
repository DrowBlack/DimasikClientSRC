package com.mojang.datafixers;

import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DataFix {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Schema outputSchema;
    private final boolean changesType;
    @Nullable
    private TypeRewriteRule rule;

    public DataFix(Schema outputSchema, boolean changesType) {
        this.outputSchema = outputSchema;
        this.changesType = changesType;
    }

    protected <A> TypeRewriteRule fixTypeEverywhere(String name, Type<A> type, Function<DynamicOps<?>, Function<A, A>> function) {
        return this.fixTypeEverywhere(name, type, type, function, new BitSet());
    }

    protected <A, B> TypeRewriteRule convertUnchecked(String name, Type<A> type, Type<B> newType) {
        return this.fixTypeEverywhere(name, type, newType, ops -> Function.identity(), new BitSet());
    }

    protected TypeRewriteRule writeAndRead(String name, Type<?> type, Type<?> newType) {
        return this.writeFixAndRead(name, type, newType, Function.identity());
    }

    protected <A, B> TypeRewriteRule writeFixAndRead(String name, Type<A> type, Type<B> newType, Function<Dynamic<?>, Dynamic<?>> fix) {
        return this.fixTypeEverywhere(name, type, newType, ops -> input -> {
            Optional written = type.writeDynamic(ops, (Object)input).resultOrPartial(LOGGER::error);
            if (!written.isPresent()) {
                throw new RuntimeException("Could not write the object in " + name);
            }
            Optional read = newType.readTyped((Dynamic)fix.apply(written.get())).resultOrPartial(LOGGER::error);
            if (!read.isPresent()) {
                throw new RuntimeException("Could not read the new object in " + name);
            }
            return read.get().getFirst().getValue();
        });
    }

    protected <A, B> TypeRewriteRule fixTypeEverywhere(String name, Type<A> type, Type<B> newType, Function<DynamicOps<?>, Function<A, B>> function) {
        return this.fixTypeEverywhere(name, type, newType, function, new BitSet());
    }

    protected <A, B> TypeRewriteRule fixTypeEverywhere(String name, Type<A> type, Type<B> newType, Function<DynamicOps<?>, Function<A, B>> function, BitSet bitSet) {
        return this.fixTypeEverywhere(type, RewriteResult.create(View.create(name, type, newType, new NamedFunctionWrapper<A, B>(name, function)), bitSet));
    }

    protected <A> TypeRewriteRule fixTypeEverywhereTyped(String name, Type<A> type, Function<Typed<?>, Typed<?>> function) {
        return this.fixTypeEverywhereTyped(name, type, function, new BitSet());
    }

    protected <A> TypeRewriteRule fixTypeEverywhereTyped(String name, Type<A> type, Function<Typed<?>, Typed<?>> function, BitSet bitSet) {
        return this.fixTypeEverywhereTyped(name, type, type, function, bitSet);
    }

    protected <A, B> TypeRewriteRule fixTypeEverywhereTyped(String name, Type<A> type, Type<B> newType, Function<Typed<?>, Typed<?>> function) {
        return this.fixTypeEverywhereTyped(name, type, newType, function, new BitSet());
    }

    protected <A, B> TypeRewriteRule fixTypeEverywhereTyped(String name, Type<A> type, Type<B> newType, Function<Typed<?>, Typed<?>> function, BitSet bitSet) {
        return this.fixTypeEverywhere(type, DataFix.checked(name, type, newType, function, bitSet));
    }

    public static <A, B> RewriteResult<A, B> checked(String name, Type<A> type, Type<B> newType, Function<Typed<?>, Typed<?>> function, BitSet bitSet) {
        return RewriteResult.create(View.create(name, type, newType, new NamedFunctionWrapper(name, ops -> a -> {
            Typed result = (Typed)function.apply(new Typed<Object>(type, (DynamicOps<?>)ops, a));
            if (!newType.equals(result.type, true, false)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", newType, result.type));
            }
            return result.value;
        })), bitSet);
    }

    protected <A, B> TypeRewriteRule fixTypeEverywhere(Type<A> type, RewriteResult<A, B> view) {
        return TypeRewriteRule.checkOnce(TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(type, view), DataFixerUpper.OPTIMIZATION_RULE, true, true), this::onFail);
    }

    protected void onFail(Type<?> type) {
        LOGGER.info("Not matched: " + this + " " + type);
    }

    public final int getVersionKey() {
        return this.getOutputSchema().getVersionKey();
    }

    public TypeRewriteRule getRule() {
        if (this.rule == null) {
            this.rule = this.makeRule();
        }
        return this.rule;
    }

    protected abstract TypeRewriteRule makeRule();

    protected Schema getInputSchema() {
        if (this.changesType) {
            return this.outputSchema.getParent();
        }
        return this.getOutputSchema();
    }

    protected Schema getOutputSchema() {
        return this.outputSchema;
    }

    private static final class NamedFunctionWrapper<A, B>
    implements Function<DynamicOps<?>, Function<A, B>> {
        private final String name;
        private final Function<DynamicOps<?>, Function<A, B>> delegate;

        public NamedFunctionWrapper(String name, Function<DynamicOps<?>, Function<A, B>> delegate) {
            this.name = name;
            this.delegate = delegate;
        }

        @Override
        public Function<A, B> apply(DynamicOps<?> ops) {
            return this.delegate.apply(ops);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NamedFunctionWrapper that = (NamedFunctionWrapper)o;
            return Objects.equals(this.name, that.name);
        }

        public int hashCode() {
            return Objects.hash(this.name);
        }
    }
}
