package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class Memory<T> {
    private final T value;
    private long timeToLive;

    public Memory(T value, long timeToLive) {
        this.value = value;
        this.timeToLive = timeToLive;
    }

    public void tick() {
        if (this.isForgettable()) {
            --this.timeToLive;
        }
    }

    public static <T> Memory<T> create(T value) {
        return new Memory<T>(value, Long.MAX_VALUE);
    }

    public static <T> Memory<T> create(T value, long timeToLive) {
        return new Memory<T>(value, timeToLive);
    }

    public T getValue() {
        return this.value;
    }

    public boolean isForgotten() {
        return this.timeToLive <= 0L;
    }

    public String toString() {
        return this.value.toString() + (String)(this.isForgettable() ? " (ttl: " + this.timeToLive + ")" : "");
    }

    public boolean isForgettable() {
        return this.timeToLive != Long.MAX_VALUE;
    }

    public static <T> Codec<Memory<T>> createCodec(Codec<T> valueCodec) {
        return RecordCodecBuilder.create(builder -> builder.group(((MapCodec)valueCodec.fieldOf("value")).forGetter(memory -> memory.value), Codec.LONG.optionalFieldOf("ttl").forGetter(memory -> memory.isForgettable() ? Optional.of(memory.timeToLive) : Optional.empty())).apply((Applicative<Memory, ?>)builder, (value, timeToLive) -> new Memory<Object>(value, timeToLive.orElse(Long.MAX_VALUE))));
    }
}
