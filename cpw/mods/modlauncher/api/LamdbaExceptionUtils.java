package cpw.mods.modlauncher.api;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LamdbaExceptionUtils {
    public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            }
            catch (Exception exception) {
                LamdbaExceptionUtils.throwAsUnchecked(exception);
            }
        };
    }

    public static <T, U, E extends Exception> BiConsumer<T, U> rethrowBiConsumer(BiConsumer_WithExceptions<T, U, E> biConsumer) {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            }
            catch (Exception exception) {
                LamdbaExceptionUtils.throwAsUnchecked(exception);
            }
        };
    }

    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            }
            catch (Exception exception) {
                LamdbaExceptionUtils.throwAsUnchecked(exception);
                return null;
            }
        };
    }

    public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function) {
        return () -> {
            try {
                return function.get();
            }
            catch (Exception exception) {
                LamdbaExceptionUtils.throwAsUnchecked(exception);
                return null;
            }
        };
    }

    public static void uncheck(Runnable_WithExceptions t) {
        try {
            t.run();
        }
        catch (Exception exception) {
            LamdbaExceptionUtils.throwAsUnchecked(exception);
        }
    }

    public static <R, E extends Exception> R uncheck(Supplier_WithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception exception) {
            LamdbaExceptionUtils.throwAsUnchecked(exception);
            return null;
        }
    }

    public static <T, R, E extends Exception> R uncheck(Function_WithExceptions<T, R, E> function, T t) {
        try {
            return function.apply(t);
        }
        catch (Exception exception) {
            LamdbaExceptionUtils.throwAsUnchecked(exception);
            return null;
        }
    }

    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw exception;
    }

    @FunctionalInterface
    public static interface Runnable_WithExceptions<E extends Exception> {
        public void run() throws E;
    }

    @FunctionalInterface
    public static interface Supplier_WithExceptions<T, E extends Exception> {
        public T get() throws E;
    }

    @FunctionalInterface
    public static interface Function_WithExceptions<T, R, E extends Exception> {
        public R apply(T var1) throws E;
    }

    public static interface BiConsumer_WithExceptions<T, U, E extends Exception> {
        public void accept(T var1, U var2) throws E;
    }

    @FunctionalInterface
    public static interface Consumer_WithExceptions<T, E extends Exception> {
        public void accept(T var1) throws E;
    }
}
