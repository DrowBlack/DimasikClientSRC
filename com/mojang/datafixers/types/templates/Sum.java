package com.mojang.datafixers.types.templates;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.OpticParts;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Traversal;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class Sum
implements TypeTemplate {
    private final TypeTemplate f;
    private final TypeTemplate g;

    public Sum(TypeTemplate f, TypeTemplate g) {
        this.f = f;
        this.g = g;
    }

    @Override
    public int size() {
        return Math.max(this.f.size(), this.g.size());
    }

    @Override
    public TypeFamily apply(final TypeFamily family) {
        return new TypeFamily(){

            @Override
            public Type<?> apply(int index) {
                return DSL.or(Sum.this.f.apply(family).apply(index), Sum.this.g.apply(family).apply(index));
            }
        };
    }

    @Override
    public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> input, Type<A> aType, Type<B> bType) {
        return TypeFamily.familyOptic(i -> this.cap(this.f.applyO(input, aType, bType), this.g.applyO(input, aType, bType), i));
    }

    private <A, B, LS, RS, LT, RT> OpticParts<A, B> cap(final FamilyOptic<A, B> lo, final FamilyOptic<A, B> ro, final int index) {
        final TypeToken<TraversalP.Mu> bound = TraversalP.Mu.TYPE_TOKEN;
        return new OpticParts(ImmutableSet.of(bound), new Traversal<Either<LS, RS>, Either<LT, RT>, A, B>(){

            @Override
            public <F extends K1> FunctionType<Either<LS, RS>, App<F, Either<LT, RT>>> wander(Applicative<F, ?> applicative, FunctionType<A, App<F, B>> input) {
                return e -> e.map(l -> {
                    OpticParts parts = lo.apply(index);
                    Traversal traversal = Optics.toTraversal(parts.optic().upCast(parts.bounds(), bound).orElseThrow(IllegalArgumentException::new));
                    return applicative.ap(Either::left, traversal.wander(applicative, input).apply(l));
                }, r -> {
                    OpticParts parts = ro.apply(index);
                    Traversal traversal = Optics.toTraversal(parts.optic().upCast(parts.bounds(), bound).orElseThrow(IllegalArgumentException::new));
                    return applicative.ap(Either::right, traversal.wander(applicative, input).apply(r));
                });
            }
        });
    }

    public <FT, FR> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int index, @Nullable String name, Type<FT> type, Type<FR> resultType) {
        Either<TypeTemplate, Type.FieldNotFoundException> either = this.f.findFieldOrType(index, name, type, resultType);
        return either.map(f2 -> Either.left(new Sum((TypeTemplate)f2, this.g)), r -> this.g.findFieldOrType(index, name, type, resultType).mapLeft(g2 -> new Sum(this.f, (TypeTemplate)g2)));
    }

    @Override
    public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily family, IntFunction<RewriteResult<?, ?>> function) {
        return i -> {
            RewriteResult<?, ?> f1 = this.f.hmap(family, function).apply(i);
            RewriteResult<?, ?> f2 = this.g.hmap(family, function).apply(i);
            return this.cap(this.apply(family).apply(i), f1, f2);
        };
    }

    private <L, R> RewriteResult<?, ?> cap(Type<?> type, RewriteResult<L, ?> f1, RewriteResult<R, ?> f2) {
        return ((SumType)type).mergeViews(f1, f2);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Sum)) {
            return false;
        }
        Sum that = (Sum)obj;
        return Objects.equals(this.f, that.f) && Objects.equals(this.g, that.g);
    }

    public int hashCode() {
        return Objects.hash(this.f, this.g);
    }

    public String toString() {
        return "(" + this.f + " | " + this.g + ")";
    }

    public static final class SumType<F, G>
    extends Type<Either<F, G>> {
        protected final Type<F> first;
        protected final Type<G> second;
        private int hashCode;

        public SumType(Type<F> first, Type<G> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public RewriteResult<Either<F, G>, ?> all(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
            return this.mergeViews(this.first.rewriteOrNop(rule), this.second.rewriteOrNop(rule));
        }

        public <F2, G2> RewriteResult<Either<F, G>, ?> mergeViews(RewriteResult<F, F2> leftView, RewriteResult<G, G2> rightView) {
            RewriteResult<Either<F, G>, Either<F2, G>> v1 = SumType.fixLeft(this, this.first, this.second, leftView);
            RewriteResult<Either<F, G>, Either<F, G2>> v2 = SumType.fixRight(v1.view().newType(), leftView.view().newType(), this.second, rightView);
            return v2.compose(v1);
        }

        @Override
        public Optional<RewriteResult<Either<F, G>, ?>> one(TypeRewriteRule rule) {
            return DataFixUtils.or(rule.rewrite(this.first).map(v -> SumType.fixLeft(this, this.first, this.second, v)), () -> rule.rewrite(this.second).map(v -> SumType.fixRight(this, this.first, this.second, v)));
        }

        private static <F, G, F2> RewriteResult<Either<F, G>, Either<F2, G>> fixLeft(Type<Either<F, G>> type, Type<F> first, Type<G> second, RewriteResult<F, F2> view) {
            return SumType.opticView(type, view, TypedOptic.inj1(first, second, view.view().newType()));
        }

        private static <F, G, G2> RewriteResult<Either<F, G>, Either<F, G2>> fixRight(Type<Either<F, G>> type, Type<F> first, Type<G> second, RewriteResult<G, G2> view) {
            return SumType.opticView(type, view, TypedOptic.inj2(first, second, view.view().newType()));
        }

        @Override
        public Type<?> updateMu(RecursiveTypeFamily newFamily) {
            return DSL.or(this.first.updateMu(newFamily), this.second.updateMu(newFamily));
        }

        @Override
        public TypeTemplate buildTemplate() {
            return DSL.or(this.first.template(), this.second.template());
        }

        @Override
        public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String name, int index) {
            return DataFixUtils.or(this.first.findChoiceType(name, index), () -> this.second.findChoiceType(name, index));
        }

        @Override
        public Optional<Type<?>> findCheckedType(int index) {
            return DataFixUtils.or(this.first.findCheckedType(index), () -> this.second.findCheckedType(index));
        }

        @Override
        protected Codec<Either<F, G>> buildCodec() {
            return Codec.either(this.first.codec(), this.second.codec());
        }

        public String toString() {
            return "(" + this.first + " | " + this.second + ")";
        }

        @Override
        public boolean equals(Object obj, boolean ignoreRecursionPoints, boolean checkIndex) {
            if (!(obj instanceof SumType)) {
                return false;
            }
            SumType that = (SumType)obj;
            return this.first.equals(that.first, ignoreRecursionPoints, checkIndex) && this.second.equals(that.second, ignoreRecursionPoints, checkIndex);
        }

        public int hashCode() {
            if (this.hashCode == 0) {
                this.hashCode = Objects.hash(this.first, this.second);
            }
            return this.hashCode;
        }

        @Override
        public Optional<Type<?>> findFieldTypeOpt(String name) {
            return DataFixUtils.or(this.first.findFieldTypeOpt(name), () -> this.second.findFieldTypeOpt(name));
        }

        @Override
        public Optional<Either<F, G>> point(DynamicOps<?> ops) {
            return DataFixUtils.or(this.second.point(ops).map(Either::right), () -> this.first.point(ops).map(Either::left));
        }

        private static <A, B, LS, RS, LT, RT> TypedOptic<Either<LS, RS>, Either<LT, RT>, A, B> mergeOptics(final TypedOptic<LS, LT, A, B> lo, final TypedOptic<RS, RT, A, B> ro) {
            final TypeToken<TraversalP.Mu> bound = TraversalP.Mu.TYPE_TOKEN;
            return new TypedOptic<Either<LS, RS>, Either<LT, RT>, A, B>(bound, DSL.or(lo.sType(), ro.sType()), DSL.or(lo.tType(), ro.tType()), lo.aType(), lo.bType(), new Traversal<Either<LS, RS>, Either<LT, RT>, A, B>(){

                @Override
                public <F extends K1> FunctionType<Either<LS, RS>, App<F, Either<LT, RT>>> wander(Applicative<F, ?> applicative, FunctionType<A, App<F, B>> input) {
                    return e -> e.map(l -> {
                        Traversal traversal = Optics.toTraversal(lo.optic().upCast(lo.bounds(), bound).orElseThrow(IllegalArgumentException::new));
                        return applicative.ap(Either::left, traversal.wander(applicative, input).apply(l));
                    }, r -> {
                        Traversal traversal = Optics.toTraversal(ro.optic().upCast(ro.bounds(), bound).orElseThrow(IllegalArgumentException::new));
                        return applicative.ap(Either::right, traversal.wander(applicative, input).apply(r));
                    });
                }
            });
        }

        @Override
        public <FT, FR> Either<TypedOptic<Either<F, G>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> type, Type<FR> resultType, Type.TypeMatcher<FT, FR> matcher, boolean recurse) {
            Either<TypedOptic<F, ?, FT, FR>, Type.FieldNotFoundException> firstOptic = this.first.findType(type, resultType, matcher, recurse);
            Either<TypedOptic<G, ?, FT, FR>, Type.FieldNotFoundException> secondOptic = this.second.findType(type, resultType, matcher, recurse);
            if (firstOptic.left().isPresent() && secondOptic.left().isPresent()) {
                return Either.left(SumType.mergeOptics(firstOptic.left().get(), secondOptic.left().get()));
            }
            if (firstOptic.left().isPresent()) {
                return firstOptic.mapLeft(this::capLeft);
            }
            return secondOptic.mapLeft(this::capRight);
        }

        private <FT, FR, F2> TypedOptic<Either<F, G>, ?, FT, FR> capLeft(TypedOptic<F, F2, FT, FR> optic) {
            return TypedOptic.inj1(optic.sType(), this.second, optic.tType()).compose(optic);
        }

        private <FT, FR, G2> TypedOptic<Either<F, G>, ?, FT, FR> capRight(TypedOptic<G, G2, FT, FR> optic) {
            return TypedOptic.inj2(this.first, optic.sType(), optic.tType()).compose(optic);
        }
    }
}
