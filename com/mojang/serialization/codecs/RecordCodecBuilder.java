package com.mojang.serialization.codecs;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RecordCodecBuilder<O, F>
implements App<Mu<O>, F> {
    private final Function<O, F> getter;
    private final Function<O, MapEncoder<F>> encoder;
    private final MapDecoder<F> decoder;

    public static <O, F> RecordCodecBuilder<O, F> unbox(App<Mu<O>, F> box) {
        return (RecordCodecBuilder)box;
    }

    private RecordCodecBuilder(Function<O, F> getter, Function<O, MapEncoder<F>> encoder, MapDecoder<F> decoder) {
        this.getter = getter;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public static <O> Instance<O> instance() {
        return new Instance();
    }

    public static <O, F> RecordCodecBuilder<O, F> of(Function<O, F> getter, String name, Codec<F> fieldCodec) {
        return RecordCodecBuilder.of(getter, fieldCodec.fieldOf(name));
    }

    public static <O, F> RecordCodecBuilder<O, F> of(Function<O, F> getter, MapCodec<F> codec) {
        return new RecordCodecBuilder<Object, F>(getter, o -> codec, codec);
    }

    public static <O, F> RecordCodecBuilder<O, F> point(F instance) {
        return new RecordCodecBuilder<Object, Object>(o -> instance, o -> Encoder.empty(), Decoder.unit(instance));
    }

    public static <O, F> RecordCodecBuilder<O, F> stable(F instance) {
        return RecordCodecBuilder.point(instance, Lifecycle.stable());
    }

    public static <O, F> RecordCodecBuilder<O, F> deprecated(F instance, int since) {
        return RecordCodecBuilder.point(instance, Lifecycle.deprecated(since));
    }

    public static <O, F> RecordCodecBuilder<O, F> point(F instance, Lifecycle lifecycle) {
        return new RecordCodecBuilder<Object, Object>(o -> instance, o -> Encoder.empty().withLifecycle(lifecycle), Decoder.unit(instance).withLifecycle(lifecycle));
    }

    public static <O> Codec<O> create(Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return RecordCodecBuilder.build(builder.apply(RecordCodecBuilder.instance())).codec();
    }

    public static <O> MapCodec<O> mapCodec(Function<Instance<O>, ? extends App<Mu<O>, O>> builder) {
        return RecordCodecBuilder.build(builder.apply(RecordCodecBuilder.instance()));
    }

    public <E> RecordCodecBuilder<O, E> dependent(Function<O, E> getter, final MapEncoder<E> encoder, final Function<? super F, ? extends MapDecoder<E>> decoderGetter) {
        return new RecordCodecBuilder<Object, E>(getter, o -> encoder, new MapDecoder.Implementation<E>(){

            @Override
            public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
                return RecordCodecBuilder.this.decoder.decode(ops, input).map(decoderGetter).flatMap((? super R decoder1) -> decoder1.decode(ops, input).map(Function.identity()));
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return encoder.keys(ops);
            }

            public String toString() {
                return "Dependent[" + encoder + "]";
            }
        });
    }

    public static <O> MapCodec<O> build(App<Mu<O>, O> builderBox) {
        final RecordCodecBuilder<O, O> builder = RecordCodecBuilder.unbox(builderBox);
        return new MapCodec<O>(){

            @Override
            public <T> DataResult<O> decode(DynamicOps<T> ops, MapLike<T> input) {
                return builder.decoder.decode(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(O input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return ((MapEncoder)builder.encoder.apply(input)).encode(input, ops, prefix);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return builder.decoder.keys(ops);
            }

            public String toString() {
                return "RecordCodec[" + builder.decoder + "]";
            }
        };
    }

    public static final class Instance<O>
    implements Applicative<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, Mu<O>> {
        public <A> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A> stable(A a) {
            return RecordCodecBuilder.stable(a);
        }

        public <A> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A> deprecated(A a, int since) {
            return RecordCodecBuilder.deprecated(a, since);
        }

        public <A> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A> point(A a, Lifecycle lifecycle) {
            return RecordCodecBuilder.point(a, lifecycle);
        }

        @Override
        public <A> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A> point(A a) {
            return RecordCodecBuilder.point(a);
        }

        @Override
        public <A, R> Function<App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A>, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, R>> lift1(App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, Function<A, R>> function) {
            return fa -> {
                final RecordCodecBuilder f = RecordCodecBuilder.unbox(function);
                final RecordCodecBuilder a = RecordCodecBuilder.unbox(fa);
                return new RecordCodecBuilder(o -> ((Function)f.getter.apply(o)).apply(a.getter.apply(o)), o -> {
                    final MapEncoder fEnc = (MapEncoder)f.encoder.apply(o);
                    final MapEncoder aEnc = (MapEncoder)a.encoder.apply(o);
                    final Object aFromO = a.getter.apply(o);
                    return new MapEncoder.Implementation<R>(){

                        @Override
                        public <T> RecordBuilder<T> encode(R input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                            aEnc.encode(aFromO, ops, prefix);
                            fEnc.encode(a1 -> input, ops, prefix);
                            return prefix;
                        }

                        @Override
                        public <T> Stream<T> keys(DynamicOps<T> ops) {
                            return Stream.concat(aEnc.keys(ops), fEnc.keys(ops));
                        }

                        public String toString() {
                            return fEnc + " * " + aEnc;
                        }
                    };
                }, new MapDecoder.Implementation<R>(){

                    @Override
                    public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                        return a.decoder.decode(ops, input).flatMap((? super R ar) -> f.decoder.decode(ops, input).map((? super R fr) -> fr.apply(ar)));
                    }

                    @Override
                    public <T> Stream<T> keys(DynamicOps<T> ops) {
                        return Stream.concat(a.decoder.keys(ops), f.decoder.keys(ops));
                    }

                    public String toString() {
                        return f.decoder + " * " + a.decoder;
                    }
                });
            };
        }

        @Override
        public <A, B, R> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, R> ap2(App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, BiFunction<A, B, R>> func, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, A> a, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, B> b) {
            final RecordCodecBuilder function = RecordCodecBuilder.unbox(func);
            final RecordCodecBuilder fa = RecordCodecBuilder.unbox(a);
            final RecordCodecBuilder fb = RecordCodecBuilder.unbox(b);
            return new RecordCodecBuilder(o -> ((BiFunction)function.getter.apply(o)).apply(fa.getter.apply(o), fb.getter.apply(o)), o -> {
                final MapEncoder fEncoder = (MapEncoder)function.encoder.apply(o);
                final MapEncoder aEncoder = (MapEncoder)fa.encoder.apply(o);
                final Object aFromO = fa.getter.apply(o);
                final MapEncoder bEncoder = (MapEncoder)fb.encoder.apply(o);
                final Object bFromO = fb.getter.apply(o);
                return new MapEncoder.Implementation<R>(){

                    @Override
                    public <T> RecordBuilder<T> encode(R input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                        aEncoder.encode(aFromO, ops, prefix);
                        bEncoder.encode(bFromO, ops, prefix);
                        fEncoder.encode((a1, b1) -> input, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(DynamicOps<T> ops) {
                        return Stream.of(fEncoder.keys(ops), aEncoder.keys(ops), bEncoder.keys(ops)).flatMap(Function.identity());
                    }

                    public String toString() {
                        return fEncoder + " * " + aEncoder + " * " + bEncoder;
                    }
                };
            }, new MapDecoder.Implementation<R>(){

                @Override
                public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return DataResult.unbox(DataResult.instance().ap2(function.decoder.decode(ops, input), fa.decoder.decode(ops, input), fb.decoder.decode(ops, input)));
                }

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.of(function.decoder.keys(ops), fa.decoder.keys(ops), fb.decoder.keys(ops)).flatMap(Function.identity());
                }

                public String toString() {
                    return function.decoder + " * " + fa.decoder + " * " + fb.decoder;
                }
            });
        }

        @Override
        public <T1, T2, T3, R> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, R> ap3(App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, Function3<T1, T2, T3, R>> func, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T1> t1, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T2> t2, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T3> t3) {
            final RecordCodecBuilder function = RecordCodecBuilder.unbox(func);
            final RecordCodecBuilder f1 = RecordCodecBuilder.unbox(t1);
            final RecordCodecBuilder f2 = RecordCodecBuilder.unbox(t2);
            final RecordCodecBuilder f3 = RecordCodecBuilder.unbox(t3);
            return new RecordCodecBuilder(o -> ((Function3)function.getter.apply(o)).apply(f1.getter.apply(o), f2.getter.apply(o), f3.getter.apply(o)), o -> {
                final MapEncoder fEncoder = (MapEncoder)function.encoder.apply(o);
                final MapEncoder e1 = (MapEncoder)f1.encoder.apply(o);
                final Object v1 = f1.getter.apply(o);
                final MapEncoder e2 = (MapEncoder)f2.encoder.apply(o);
                final Object v2 = f2.getter.apply(o);
                final MapEncoder e3 = (MapEncoder)f3.encoder.apply(o);
                final Object v3 = f3.getter.apply(o);
                return new MapEncoder.Implementation<R>(){

                    @Override
                    public <T> RecordBuilder<T> encode(R input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                        e1.encode(v1, ops, prefix);
                        e2.encode(v2, ops, prefix);
                        e3.encode(v3, ops, prefix);
                        fEncoder.encode((t1, t2, t3) -> input, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(DynamicOps<T> ops) {
                        return Stream.of(fEncoder.keys(ops), e1.keys(ops), e2.keys(ops), e3.keys(ops)).flatMap(Function.identity());
                    }

                    public String toString() {
                        return fEncoder + " * " + e1 + " * " + e2 + " * " + e3;
                    }
                };
            }, new MapDecoder.Implementation<R>(){

                @Override
                public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return DataResult.unbox(DataResult.instance().ap3(function.decoder.decode(ops, input), f1.decoder.decode(ops, input), f2.decoder.decode(ops, input), f3.decoder.decode(ops, input)));
                }

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.of(function.decoder.keys(ops), f1.decoder.keys(ops), f2.decoder.keys(ops), f3.decoder.keys(ops)).flatMap(Function.identity());
                }

                public String toString() {
                    return function.decoder + " * " + f1.decoder + " * " + f2.decoder + " * " + f3.decoder;
                }
            });
        }

        @Override
        public <T1, T2, T3, T4, R> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, R> ap4(App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, Function4<T1, T2, T3, T4, R>> func, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T1> t1, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T2> t2, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T3> t3, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T4> t4) {
            final RecordCodecBuilder function = RecordCodecBuilder.unbox(func);
            final RecordCodecBuilder f1 = RecordCodecBuilder.unbox(t1);
            final RecordCodecBuilder f2 = RecordCodecBuilder.unbox(t2);
            final RecordCodecBuilder f3 = RecordCodecBuilder.unbox(t3);
            final RecordCodecBuilder f4 = RecordCodecBuilder.unbox(t4);
            return new RecordCodecBuilder(o -> ((Function4)function.getter.apply(o)).apply(f1.getter.apply(o), f2.getter.apply(o), f3.getter.apply(o), f4.getter.apply(o)), o -> {
                final MapEncoder fEncoder = (MapEncoder)function.encoder.apply(o);
                final MapEncoder e1 = (MapEncoder)f1.encoder.apply(o);
                final Object v1 = f1.getter.apply(o);
                final MapEncoder e2 = (MapEncoder)f2.encoder.apply(o);
                final Object v2 = f2.getter.apply(o);
                final MapEncoder e3 = (MapEncoder)f3.encoder.apply(o);
                final Object v3 = f3.getter.apply(o);
                final MapEncoder e4 = (MapEncoder)f4.encoder.apply(o);
                final Object v4 = f4.getter.apply(o);
                return new MapEncoder.Implementation<R>(){

                    @Override
                    public <T> RecordBuilder<T> encode(R input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                        e1.encode(v1, ops, prefix);
                        e2.encode(v2, ops, prefix);
                        e3.encode(v3, ops, prefix);
                        e4.encode(v4, ops, prefix);
                        fEncoder.encode((t1, t2, t3, t4) -> input, ops, prefix);
                        return prefix;
                    }

                    @Override
                    public <T> Stream<T> keys(DynamicOps<T> ops) {
                        return Stream.of(fEncoder.keys(ops), e1.keys(ops), e2.keys(ops), e3.keys(ops), e4.keys(ops)).flatMap(Function.identity());
                    }

                    public String toString() {
                        return fEncoder + " * " + e1 + " * " + e2 + " * " + e3 + " * " + e4;
                    }
                };
            }, new MapDecoder.Implementation<R>(){

                @Override
                public <T> DataResult<R> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return DataResult.unbox(DataResult.instance().ap4(function.decoder.decode(ops, input), f1.decoder.decode(ops, input), f2.decoder.decode(ops, input), f3.decoder.decode(ops, input), f4.decoder.decode(ops, input)));
                }

                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.of(function.decoder.keys(ops), f1.decoder.keys(ops), f2.decoder.keys(ops), f3.decoder.keys(ops), f4.decoder.keys(ops)).flatMap(Function.identity());
                }

                public String toString() {
                    return function.decoder + " * " + f1.decoder + " * " + f2.decoder + " * " + f3.decoder + " * " + f4.decoder;
                }
            });
        }

        @Override
        public <T, R> App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, R> map(Function<? super T, ? extends R> func, App<com.mojang.serialization.codecs.RecordCodecBuilder$Mu<O>, T> ts) {
            final RecordCodecBuilder unbox = RecordCodecBuilder.unbox(ts);
            final Function getter = unbox.getter;
            return new RecordCodecBuilder(getter.andThen(func), o -> new MapEncoder.Implementation<R>(){
                private final MapEncoder encoder;
                {
                    this.encoder = (MapEncoder)unbox.encoder.apply(o);
                }

                @Override
                public <U> RecordBuilder<U> encode(R input, DynamicOps<U> ops, RecordBuilder<U> prefix) {
                    return this.encoder.encode(getter.apply(o), ops, prefix);
                }

                public <U> Stream<U> keys(DynamicOps<U> ops) {
                    return this.encoder.keys(ops);
                }

                public String toString() {
                    return this.encoder + "[mapped]";
                }
            }, unbox.decoder.map(func));
        }

        private static final class Mu<O>
        implements Applicative.Mu {
            private Mu() {
            }
        }
    }

    public static final class Mu<O>
    implements K1 {
    }
}
