package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.profunctors.Cartesian;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Named
implements TypeTemplate {
    private final String name;
    private final TypeTemplate element;

    public Named(String name, TypeTemplate element) {
        this.name = name;
        this.element = element;
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public TypeFamily apply(TypeFamily family) {
        return index -> DSL.named(this.name, this.element.apply(family).apply(index));
    }

    @Override
    public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> input, Type<A> aType, Type<B> bType) {
        return TypeFamily.familyOptic(i -> this.element.applyO(input, aType, bType).apply(i));
    }

    public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int index, @Nullable String name, Type<FT> type, Type<FR> resultType) {
        return this.element.findFieldOrType(index, name, type, resultType);
    }

    @Override
    public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily family, IntFunction<RewriteResult<?, ?>> function) {
        return index -> {
            RewriteResult<?, ?> elementResult = this.element.hmap(family, function).apply(index);
            return this.cap(family, index, elementResult);
        };
    }

    private <A> RewriteResult<Pair<String, A>, ?> cap(TypeFamily family, int index, RewriteResult<A, ?> elementResult) {
        return NamedType.fix((NamedType)this.apply(family).apply(index), elementResult);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Named)) {
            return false;
        }
        Named that = (Named)obj;
        return Objects.equals(this.name, that.name) && Objects.equals(this.element, that.element);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.element);
    }

    public String toString() {
        return "NamedTypeTag[" + this.name + ": " + this.element + "]";
    }

    public static final class NamedType<A>
    extends Type<Pair<String, A>> {
        protected final String name;
        protected final Type<A> element;

        public NamedType(String name, Type<A> element) {
            this.name = name;
            this.element = element;
        }

        public static <A, B> RewriteResult<Pair<String, A>, ?> fix(NamedType<A> type, RewriteResult<A, B> instance) {
            if (Objects.equals(instance.view().function(), Functions.id())) {
                return RewriteResult.nop(type);
            }
            return NamedType.opticView(type, instance, NamedType.wrapOptic(type.name, TypedOptic.adapter(instance.view().type(), instance.view().newType())));
        }

        @Override
        public RewriteResult<Pair<String, A>, ?> all(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
            RewriteResult<A, ?> elementView = this.element.rewriteOrNop(rule);
            return NamedType.fix(this, elementView);
        }

        @Override
        public Optional<RewriteResult<Pair<String, A>, ?>> one(TypeRewriteRule rule) {
            Optional<RewriteResult<A, ?>> view = rule.rewrite(this.element);
            return view.map(instance -> NamedType.fix(this, instance));
        }

        @Override
        public Type<?> updateMu(RecursiveTypeFamily newFamily) {
            return DSL.named(this.name, this.element.updateMu(newFamily));
        }

        @Override
        public TypeTemplate buildTemplate() {
            return DSL.named(this.name, this.element.template());
        }

        @Override
        public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String name, int index) {
            return this.element.findChoiceType(name, index);
        }

        @Override
        public Optional<Type<?>> findCheckedType(int index) {
            return this.element.findCheckedType(index);
        }

        @Override
        protected Codec<Pair<String, A>> buildCodec() {
            return new Codec<Pair<String, A>>(){

                @Override
                public <T> DataResult<Pair<Pair<String, A>, T>> decode(DynamicOps<T> ops, T input) {
                    return element.codec().decode(ops, input).map((? super R vo) -> vo.mapFirst(v -> Pair.of(name, v))).setLifecycle(Lifecycle.experimental());
                }

                @Override
                public <T> DataResult<T> encode(Pair<String, A> input, DynamicOps<T> ops, T prefix) {
                    if (!Objects.equals(input.getFirst(), name)) {
                        return DataResult.error("Named type name doesn't match: expected: " + name + ", got: " + input.getFirst(), prefix);
                    }
                    return element.codec().encode(input.getSecond(), ops, prefix).setLifecycle(Lifecycle.experimental());
                }
            };
        }

        public String toString() {
            return "NamedType[\"" + this.name + "\", " + this.element + "]";
        }

        public String name() {
            return this.name;
        }

        public Type<A> element() {
            return this.element;
        }

        @Override
        public boolean equals(Object obj, boolean ignoreRecursionPoints, boolean checkIndex) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof NamedType)) {
                return false;
            }
            NamedType other = (NamedType)obj;
            return Objects.equals(this.name, other.name) && this.element.equals(other.element, ignoreRecursionPoints, checkIndex);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.element);
        }

        @Override
        public Optional<Type<?>> findFieldTypeOpt(String name) {
            return this.element.findFieldTypeOpt(name);
        }

        @Override
        public Optional<Pair<String, A>> point(DynamicOps<?> ops) {
            return this.element.point(ops).map(value -> Pair.of(this.name, value));
        }

        @Override
        public <FT, FR> Either<TypedOptic<Pair<String, A>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> type, Type<FR> resultType, Type.TypeMatcher<FT, FR> matcher, boolean recurse) {
            return this.element.findType(type, resultType, matcher, recurse).mapLeft(o -> NamedType.wrapOptic(this.name, o));
        }

        protected static <A, B, FT, FR> TypedOptic<Pair<String, A>, Pair<String, B>, FT, FR> wrapOptic(String name, TypedOptic<A, B, FT, FR> optic) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            builder.addAll(optic.bounds());
            builder.add(Cartesian.Mu.TYPE_TOKEN);
            return new TypedOptic<Pair<String, A>, Pair<String, B>, FT, FR>((Set<TypeToken<? extends K1>>)((Object)builder.build()), DSL.named(name, optic.sType()), DSL.named(name, optic.tType()), optic.aType(), optic.bType(), Optics.proj2().composeUnchecked(optic.optic()));
        }
    }
}
