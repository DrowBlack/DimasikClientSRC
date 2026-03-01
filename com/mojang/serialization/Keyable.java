package com.mojang.serialization;

import com.mojang.serialization.DynamicOps;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Keyable {
    public <T> Stream<T> keys(DynamicOps<T> var1);

    public static Keyable forStrings(final Supplier<Stream<String>> keys) {
        return new Keyable(){

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return ((Stream)keys.get()).map(ops::createString);
            }
        };
    }
}
