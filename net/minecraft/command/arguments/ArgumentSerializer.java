package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class ArgumentSerializer<T extends ArgumentType<?>>
implements IArgumentSerializer<T> {
    private final Supplier<T> factory;

    public ArgumentSerializer(Supplier<T> factory) {
        this.factory = factory;
    }

    @Override
    public void write(T argument, PacketBuffer buffer) {
    }

    @Override
    public T read(PacketBuffer buffer) {
        return (T)((ArgumentType)this.factory.get());
    }

    @Override
    public void write(T p_212244_1_, JsonObject p_212244_2_) {
    }
}
