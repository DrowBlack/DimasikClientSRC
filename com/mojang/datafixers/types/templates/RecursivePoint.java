package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;

public final class RecursivePoint
implements TypeTemplate {
    private final int index;

    public RecursivePoint(int index) {
        this.index = index;
    }

    @Override
    public int size() {
        return this.index + 1;
    }

    @Override
    public TypeFamily apply(TypeFamily family) {
        final Type<?> result = family.apply(this.index);
        return new TypeFamily(){

            @Override
            public Type<?> apply(int index) {
                return result;
            }
        };
    }

    @Override
    public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> input, Type<A> aType, Type<B> bType) {
        return TypeFamily.familyOptic(i -> input.apply(this.index));
    }

    public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int index, @Nullable String name, Type<FT> type, Type<FR> resultType) {
        return Either.right(new Type.FieldNotFoundException("Recursion point"));
    }

    @Override
    public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily family, IntFunction<RewriteResult<?, ?>> function) {
        return i -> {
            RewriteResult result = (RewriteResult)function.apply(this.index);
            return this.cap(family, result);
        };
    }

    public <S, T> RewriteResult<S, T> cap(TypeFamily family, RewriteResult<S, T> result) {
        Type<?> sourceType = family.apply(this.index);
        if (!(sourceType instanceof RecursivePointType)) {
            throw new IllegalArgumentException("Type error: Recursive point template template got a non-recursice type as an input.");
        }
        if (!Objects.equals(result.view().type(), ((RecursivePointType)sourceType).unfold())) {
            throw new IllegalArgumentException("Type error: hmap function input type");
        }
        RecursivePointType sType = (RecursivePointType)sourceType;
        RecursivePointType<T> tType = sType.family().buildMuType(result.view().newType(), null);
        BitSet bitSet = ObjectUtils.clone(result.recData());
        bitSet.set(this.index);
        return RewriteResult.create(View.create(sType, tType, result.view().function()), bitSet);
    }

    public boolean equals(Object obj) {
        return obj instanceof RecursivePoint && this.index == ((RecursivePoint)obj).index;
    }

    public int hashCode() {
        return Objects.hash(this.index);
    }

    public String toString() {
        return "Id[" + this.index + "]";
    }

    public int index() {
        return this.index;
    }

    public static final class RecursivePointType<A>
    extends Type<A> {
        private final RecursiveTypeFamily family;
        private final int index;
        private final Supplier<Type<A>> delegate;
        @Nullable
        private volatile Type<A> type;

        public RecursivePointType(RecursiveTypeFamily family, int index, Supplier<Type<A>> delegate) {
            this.family = family;
            this.index = index;
            this.delegate = delegate;
        }

        public RecursiveTypeFamily family() {
            return this.family;
        }

        public int index() {
            return this.index;
        }

        public Type<A> unfold() {
            if (this.type == null) {
                this.type = this.delegate.get();
            }
            return this.type;
        }

        @Override
        protected Codec<A> buildCodec() {
            return new Codec<A>(){

                @Override
                public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                    return this.unfold().codec().decode(ops, input).setLifecycle(Lifecycle.experimental());
                }

                @Override
                public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                    return this.unfold().codec().encode(input, ops, prefix).setLifecycle(Lifecycle.experimental());
                }
            };
        }

        @Override
        public RewriteResult<A, ?> all(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
            return this.unfold().all(rule, recurse, checkIndex);
        }

        @Override
        public Optional<RewriteResult<A, ?>> one(TypeRewriteRule rule) {
            return this.unfold().one(rule);
        }

        @Override
        public Optional<RewriteResult<A, ?>> everywhere(TypeRewriteRule rule, PointFreeRule optimizationRule, boolean recurse, boolean checkIndex) {
            if (recurse) {
                return this.family.everywhere(this.index, rule, optimizationRule).map(view -> view);
            }
            return Optional.of(RewriteResult.nop(this));
        }

        @Override
        public Type<?> updateMu(RecursiveTypeFamily newFamily) {
            return newFamily.apply(this.index);
        }

        @Override
        public TypeTemplate buildTemplate() {
            return DSL.id(this.index);
        }

        @Override
        public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String name, int index) {
            return this.unfold().findChoiceType(name, this.index);
        }

        @Override
        public Optional<Type<?>> findCheckedType(int index) {
            return this.unfold().findCheckedType(this.index);
        }

        @Override
        public Optional<Type<?>> findFieldTypeOpt(String name) {
            return this.unfold().findFieldTypeOpt(name);
        }

        @Override
        public Optional<A> point(DynamicOps<?> ops) {
            return this.unfold().point(ops);
        }

        @Override
        public <FT, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> type, Type<FR> resultType, Type.TypeMatcher<FT, FR> matcher, boolean recurse) {
            return this.family.findType(this.index, type, resultType, matcher, recurse).mapLeft(o -> {
                if (!Objects.equals(this, o.sType())) {
                    throw new IllegalStateException(":/");
                }
                return o;
            });
        }

        private <B, FT, FR> TypedOptic<A, B, FT, FR> wrapOptic(TypedOptic<A, B, FT, FR> optic) {
            return new TypedOptic<A, B, FT, FR>(optic.bounds(), this, optic.tType(), optic.aType(), optic.bType(), optic.optic());
        }

        public String toString() {
            return "MuType[" + this.family.name() + "_" + this.index + "]";
        }

        @Override
        public boolean equals(Object obj, boolean ignoreRecursionPoints, boolean checkIndex) {
            if (!(obj instanceof RecursivePointType)) {
                return false;
            }
            RecursivePointType type = (RecursivePointType)obj;
            return (ignoreRecursionPoints || Objects.equals(this.family, type.family)) && this.index == type.index;
        }

        public int hashCode() {
            return Objects.hash(this.family, this.index);
        }

        public View<A, A> in() {
            return View.create(this.unfold(), this, Functions.in(this));
        }

        public View<A, A> out() {
            return View.create(this, this.unfold(), Functions.out(this));
        }
    }
}
