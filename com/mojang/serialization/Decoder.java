package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.FieldDecoder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Decoder<A> {
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2);

    default public <T> DataResult<A> parse(DynamicOps<T> ops, T input) {
        return this.decode(ops, input).map(Pair::getFirst);
    }

    default public <T> DataResult<Pair<A, T>> decode(Dynamic<T> input) {
        return this.decode(input.getOps(), input.getValue());
    }

    default public <T> DataResult<A> parse(Dynamic<T> input) {
        return this.decode(input).map(Pair::getFirst);
    }

    default public Terminal<A> terminal() {
        return this::parse;
    }

    default public Boxed<A> boxed() {
        return this::decode;
    }

    default public Simple<A> simple() {
        return this::parse;
    }

    default public MapDecoder<A> fieldOf(String name) {
        return new FieldDecoder(name, this);
    }

    default public <B> Decoder<B> flatMap(final Function<? super A, ? extends DataResult<? extends B>> function) {
        return new Decoder<B>(){

            @Override
            public <T> DataResult<Pair<B, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).flatMap((? super R p) -> ((DataResult)function.apply(p.getFirst())).map((? super R r) -> Pair.of(r, p.getSecond())));
            }

            public String toString() {
                return Decoder.this.toString() + "[flatMapped]";
            }
        };
    }

    default public <B> Decoder<B> map(final Function<? super A, ? extends B> function) {
        return new Decoder<B>(){

            @Override
            public <T> DataResult<Pair<B, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).map((? super R p) -> p.mapFirst(function));
            }

            public String toString() {
                return Decoder.this.toString() + "[mapped]";
            }
        };
    }

    default public Decoder<A> promotePartial(final Consumer<String> onError) {
        return new Decoder<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).promotePartial(onError);
            }

            public String toString() {
                return Decoder.this.toString() + "[promotePartial]";
            }
        };
    }

    default public Decoder<A> withLifecycle(final Lifecycle lifecycle) {
        return new Decoder<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return Decoder.this.decode(ops, input).setLifecycle(lifecycle);
            }

            public String toString() {
                return Decoder.this.toString();
            }
        };
    }

    public static <A> Decoder<A> ofTerminal(Terminal<? extends A> terminal) {
        return terminal.decoder().map(Function.identity());
    }

    public static <A> Decoder<A> ofBoxed(Boxed<? extends A> boxed) {
        return boxed.decoder().map(Function.identity());
    }

    public static <A> Decoder<A> ofSimple(Simple<? extends A> simple) {
        return simple.decoder().map(Function.identity());
    }

    public static <A> MapDecoder<A> unit(A instance) {
        return Decoder.unit(() -> instance);
    }

    public static <A> MapDecoder<A> unit(final Supplier<A> instance) {
        return new MapDecoder.Implementation<A>(){

            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                return DataResult.success(instance.get());
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            public String toString() {
                return "UnitDecoder[" + instance.get() + "]";
            }
        };
    }

    public static <A> Decoder<A> error(final String error) {
        return new Decoder<A>(){

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return DataResult.error(error);
            }

            public String toString() {
                return "ErrorDecoder[" + error + ']';
            }
        };
    }

    public static interface Simple<A> {
        public <T> DataResult<A> decode(Dynamic<T> var1);

        default public Decoder<A> decoder() {
            return new Decoder<A>(){

                @Override
                public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                    return this.decode(new Dynamic<T>(ops, input)).map((? super R a) -> Pair.of(a, ops.empty()));
                }

                public String toString() {
                    return "SimpleDecoder[" + this + "]";
                }
            };
        }
    }

    public static interface Boxed<A> {
        public <T> DataResult<Pair<A, T>> decode(Dynamic<T> var1);

        default public Decoder<A> decoder() {
            return new Decoder<A>(){

                @Override
                public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                    return this.decode(new Dynamic<T>(ops, input));
                }

                public String toString() {
                    return "BoxedDecoder[" + this + "]";
                }
            };
        }
    }

    public static interface Terminal<A> {
        public <T> DataResult<A> decode(DynamicOps<T> var1, T var2);

        default public Decoder<A> decoder() {
            return new Decoder<A>(){

                @Override
                public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                    return this.decode(ops, input).map((? super R a) -> Pair.of(a, ops.empty()));
                }

                public String toString() {
                    return "TerminalDecoder[" + this + "]";
                }
            };
        }
    }
}
