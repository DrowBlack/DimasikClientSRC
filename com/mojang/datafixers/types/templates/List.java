package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.ListTraversal;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class List
implements TypeTemplate {
    private final TypeTemplate element;

    public List(TypeTemplate element) {
        this.element = element;
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public TypeFamily apply(final TypeFamily family) {
        return new TypeFamily(){

            @Override
            public Type<?> apply(int index) {
                return DSL.list(List.this.element.apply(family).apply(index));
            }
        };
    }

    @Override
    public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> input, Type<A> aType, Type<B> bType) {
        return TypeFamily.familyOptic(i -> {
            OpticParts pair = this.element.applyO(input, aType, bType).apply(i);
            HashSet<TypeToken<? extends K1>> bounds = Sets.newHashSet(pair.bounds());
            bounds.add(TraversalP.Mu.TYPE_TOKEN);
            return new OpticParts(bounds, this.cap(pair.optic()));
        });
    }

    private <S, T, A, B> Optic<?, ?, ?, A, B> cap(Optic<?, S, T, A, B> concreteOptic) {
        return new ListTraversal<S, T>().composeUnchecked(concreteOptic);
    }

    public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int index, @Nullable String name, Type<FT> type, Type<FR> resultType) {
        return this.element.findFieldOrType(index, name, type, resultType).mapLeft(List::new);
    }

    @Override
    public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily family, IntFunction<RewriteResult<?, ?>> function) {
        return i -> {
            RewriteResult<?, ?> view = this.element.hmap(family, function).apply(i);
            return this.cap(this.apply(family).apply(i), view);
        };
    }

    private <E> RewriteResult<?, ?> cap(Type<?> type, RewriteResult<E, ?> view) {
        return ((ListType)type).fix(view);
    }

    public boolean equals(Object obj) {
        return obj instanceof List && Objects.equals(this.element, ((List)obj).element);
    }

    public int hashCode() {
        return Objects.hash(this.element);
    }

    public String toString() {
        return "List[" + this.element + "]";
    }

    public static final class ListType<A>
    extends Type<java.util.List<A>> {
        protected final Type<A> element;

        public ListType(Type<A> element) {
            this.element = element;
        }

        @Override
        public RewriteResult<java.util.List<A>, ?> all(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
            RewriteResult<A, ?> view = this.element.rewriteOrNop(rule);
            return this.fix(view);
        }

        @Override
        public Optional<RewriteResult<java.util.List<A>, ?>> one(TypeRewriteRule rule) {
            return rule.rewrite(this.element).map(this::fix);
        }

        @Override
        public Type<?> updateMu(RecursiveTypeFamily newFamily) {
            return DSL.list(this.element.updateMu(newFamily));
        }

        @Override
        public TypeTemplate buildTemplate() {
            return DSL.list(this.element.template());
        }

        @Override
        public Optional<java.util.List<A>> point(DynamicOps<?> ops) {
            return Optional.of(ImmutableList.of());
        }

        @Override
        public <FT, FR> Either<TypedOptic<java.util.List<A>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> type, Type<FR> resultType, Type.TypeMatcher<FT, FR> matcher, boolean recurse) {
            Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> firstFieldLens = this.element.findType(type, resultType, matcher, recurse);
            return firstFieldLens.mapLeft(this::capLeft);
        }

        private <FT, FR, B> TypedOptic<java.util.List<A>, ?, FT, FR> capLeft(TypedOptic<A, B, FT, FR> optic) {
            return TypedOptic.list(optic.sType(), optic.tType()).compose(optic);
        }

        public <B> RewriteResult<java.util.List<A>, ?> fix(RewriteResult<A, B> view) {
            return ListType.opticView(this, view, TypedOptic.list(this.element, view.view().newType()));
        }

        @Override
        public Codec<java.util.List<A>> buildCodec() {
            return Codec.list(this.element.codec());
        }

        public String toString() {
            return "List[" + this.element + "]";
        }

        @Override
        public boolean equals(Object obj, boolean ignoreRecursionPoints, boolean checkIndex) {
            return obj instanceof ListType && this.element.equals(((ListType)obj).element, ignoreRecursionPoints, checkIndex);
        }

        public int hashCode() {
            return this.element.hashCode();
        }

        public Type<A> getElement() {
            return this.element;
        }
    }
}
