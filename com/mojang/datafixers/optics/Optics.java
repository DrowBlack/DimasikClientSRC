package com.mojang.datafixers.optics;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Adapter;
import com.mojang.datafixers.optics.Affine;
import com.mojang.datafixers.optics.Forget;
import com.mojang.datafixers.optics.ForgetE;
import com.mojang.datafixers.optics.ForgetOpt;
import com.mojang.datafixers.optics.Getter;
import com.mojang.datafixers.optics.Grate;
import com.mojang.datafixers.optics.IdAdapter;
import com.mojang.datafixers.optics.Inj1;
import com.mojang.datafixers.optics.Inj2;
import com.mojang.datafixers.optics.Lens;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.PStore;
import com.mojang.datafixers.optics.Prism;
import com.mojang.datafixers.optics.Proj1;
import com.mojang.datafixers.optics.Proj2;
import com.mojang.datafixers.optics.ReForget;
import com.mojang.datafixers.optics.ReForgetC;
import com.mojang.datafixers.optics.ReForgetE;
import com.mojang.datafixers.optics.ReForgetEP;
import com.mojang.datafixers.optics.ReForgetP;
import com.mojang.datafixers.optics.Traversal;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.Cocartesian;
import com.mojang.datafixers.optics.profunctors.GetterP;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Optics {
    public static <S, T, A, B> Adapter<S, T, A, B> toAdapter(Optic<? super Profunctor.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Adapter.Instance());
        return Adapter.unbox(eval.apply(Optics.adapter(Function.identity(), Function.identity())));
    }

    public static <S, T, A, B> Lens<S, T, A, B> toLens(Optic<? super Cartesian.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Lens.Instance());
        return Lens.unbox(eval.apply(Optics.lens(Function.identity(), (b, a) -> b)));
    }

    public static <S, T, A, B> Prism<S, T, A, B> toPrism(Optic<? super Cocartesian.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Prism.Instance());
        return Prism.unbox(eval.apply(Optics.prism(Either::right, Function.identity())));
    }

    public static <S, T, A, B> Affine<S, T, A, B> toAffine(Optic<? super AffineP.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Affine.Instance());
        return Affine.unbox(eval.apply(Optics.affine(Either::right, (b, a) -> b)));
    }

    public static <S, T, A, B> Getter<S, T, A, B> toGetter(Optic<? super GetterP.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Getter.Instance());
        return Getter.unbox(eval.apply(Optics.getter(Function.identity())));
    }

    public static <S, T, A, B> Traversal<S, T, A, B> toTraversal(Optic<? super TraversalP.Mu, S, T, A, B> optic) {
        Function eval = optic.eval(new Traversal.Instance());
        return Traversal.unbox(eval.apply(new Traversal<A, B, A, B>(){

            @Override
            public <F extends K1> FunctionType<A, App<F, B>> wander(Applicative<F, ?> applicative, FunctionType<A, App<F, B>> input) {
                return input;
            }
        }));
    }

    static <S, T, A, B, F> Lens<S, T, Pair<F, A>, B> merge(Lens<S, ?, F, ?> getter, Lens<S, T, A, B> lens) {
        return Optics.lens(s -> Pair.of(getter.view(s), lens.view(s)), lens::update);
    }

    public static <S, T> Adapter<S, T, S, T> id() {
        return new IdAdapter();
    }

    public static <S, T, A, B> Adapter<S, T, A, B> adapter(final Function<S, A> from, final Function<B, T> to) {
        return new Adapter<S, T, A, B>(){

            @Override
            public A from(S s) {
                return from.apply(s);
            }

            @Override
            public T to(B b) {
                return to.apply(b);
            }
        };
    }

    public static <S, T, A, B> Lens<S, T, A, B> lens(final Function<S, A> view, final BiFunction<B, S, T> update) {
        return new Lens<S, T, A, B>(){

            @Override
            public A view(S s) {
                return view.apply(s);
            }

            @Override
            public T update(B b, S s) {
                return update.apply(b, s);
            }
        };
    }

    public static <S, T, A, B> Prism<S, T, A, B> prism(final Function<S, Either<T, A>> match, final Function<B, T> build) {
        return new Prism<S, T, A, B>(){

            @Override
            public Either<T, A> match(S s) {
                return (Either)match.apply(s);
            }

            @Override
            public T build(B b) {
                return build.apply(b);
            }
        };
    }

    public static <S, T, A, B> Affine<S, T, A, B> affine(final Function<S, Either<T, A>> preview, final BiFunction<B, S, T> build) {
        return new Affine<S, T, A, B>(){

            @Override
            public Either<T, A> preview(S s) {
                return (Either)preview.apply(s);
            }

            @Override
            public T set(B b, S s) {
                return build.apply(b, s);
            }
        };
    }

    public static <S, T, A, B> Getter<S, T, A, B> getter(Function<S, A> get) {
        return get::apply;
    }

    public static <R, A, B> Forget<R, A, B> forget(Function<A, R> function) {
        return function::apply;
    }

    public static <R, A, B> ForgetOpt<R, A, B> forgetOpt(Function<A, Optional<R>> function) {
        return function::apply;
    }

    public static <R, A, B> ForgetE<R, A, B> forgetE(Function<A, Either<B, R>> function) {
        return function::apply;
    }

    public static <R, A, B> ReForget<R, A, B> reForget(Function<R, B> function) {
        return function::apply;
    }

    public static <S, T, A, B> Grate<S, T, A, B> grate(FunctionType<FunctionType<FunctionType<S, A>, B>, T> grate) {
        return grate::apply;
    }

    public static <R, A, B> ReForgetEP<R, A, B> reForgetEP(final String name, final Function<Either<A, Pair<A, R>>, B> function) {
        return new ReForgetEP<R, A, B>(){

            @Override
            public B run(Either<A, Pair<A, R>> e) {
                return function.apply(e);
            }

            public String toString() {
                return "ReForgetEP_" + name;
            }
        };
    }

    public static <R, A, B> ReForgetE<R, A, B> reForgetE(final String name, final Function<Either<A, R>, B> function) {
        return new ReForgetE<R, A, B>(){

            @Override
            public B run(Either<A, R> t) {
                return function.apply(t);
            }

            public String toString() {
                return "ReForgetE_" + name;
            }
        };
    }

    public static <R, A, B> ReForgetP<R, A, B> reForgetP(final String name, final BiFunction<A, R, B> function) {
        return new ReForgetP<R, A, B>(){

            @Override
            public B run(A a, R r) {
                return function.apply(a, r);
            }

            public String toString() {
                return "ReForgetP_" + name;
            }
        };
    }

    public static <R, A, B> ReForgetC<R, A, B> reForgetC(final String name, final Either<Function<R, B>, BiFunction<A, R, B>> either) {
        return new ReForgetC<R, A, B>(){

            @Override
            public Either<Function<R, B>, BiFunction<A, R, B>> impl() {
                return either;
            }

            public String toString() {
                return "ReForgetC_" + name;
            }
        };
    }

    public static <I, J, X> PStore<I, J, X> pStore(final Function<J, X> peek, final Supplier<I> pos) {
        return new PStore<I, J, X>(){

            @Override
            public X peek(J j) {
                return peek.apply(j);
            }

            @Override
            public I pos() {
                return pos.get();
            }
        };
    }

    public static <A, B> Function<A, B> getFunc(App2<FunctionType.Mu, A, B> box) {
        return FunctionType.unbox(box);
    }

    public static <F, G, F2> Proj1<F, G, F2> proj1() {
        return new Proj1();
    }

    public static <F, G, G2> Proj2<F, G, G2> proj2() {
        return new Proj2();
    }

    public static <F, G, F2> Inj1<F, G, F2> inj1() {
        return new Inj1();
    }

    public static <F, G, G2> Inj2<F, G, G2> inj2() {
        return new Inj2();
    }

    public static <F, G, F2, G2, A, B> Lens<Either<F, G>, Either<F2, G2>, A, B> eitherLens(Lens<F, F2, A, B> fLens, Lens<G, G2, A, B> gLens) {
        return Optics.lens(either -> either.map(fLens::view, gLens::view), (b, either) -> either.mapBoth(f -> fLens.update(b, f), g -> gLens.update(b, g)));
    }

    public static <F, G, F2, G2, A, B> Affine<Either<F, G>, Either<F2, G2>, A, B> eitherAffine(Affine<F, F2, A, B> fAffine, Affine<G, G2, A, B> gAffine) {
        return Optics.affine(either -> either.map(f -> fAffine.preview(f).mapLeft(Either::left), g -> gAffine.preview(g).mapLeft(Either::right)), (b, either) -> either.mapBoth(f -> fAffine.set(b, f), g -> gAffine.set(b, g)));
    }

    public static <F, G, F2, G2, A, B> Traversal<Either<F, G>, Either<F2, G2>, A, B> eitherTraversal(final Traversal<F, F2, A, B> fOptic, final Traversal<G, G2, A, B> gOptic) {
        return new Traversal<Either<F, G>, Either<F2, G2>, A, B>(){

            @Override
            public <FT extends K1> FunctionType<Either<F, G>, App<FT, Either<F2, G2>>> wander(Applicative<FT, ?> applicative, FunctionType<A, App<FT, B>> input) {
                return e -> e.map(l -> applicative.ap(Either::left, fOptic.wander(applicative, input).apply(l)), r -> applicative.ap(Either::right, gOptic.wander(applicative, input).apply(r)));
            }
        };
    }
}
