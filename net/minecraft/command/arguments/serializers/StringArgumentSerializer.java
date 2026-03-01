package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class StringArgumentSerializer
implements IArgumentSerializer<StringArgumentType> {
    @Override
    public void write(StringArgumentType argument, PacketBuffer buffer) {
        buffer.writeEnumValue(argument.getType());
    }

    @Override
    public StringArgumentType read(PacketBuffer buffer) {
        StringArgumentType.StringType stringtype = buffer.readEnumValue(StringArgumentType.StringType.class);
        switch (stringtype) {
            case SINGLE_WORD: {
                return StringArgumentType.word();
            }
            case QUOTABLE_PHRASE: {
                return StringArgumentType.string();
            }
        }
        return StringArgumentType.greedyString();
    }

    @Override
    public void write(StringArgumentType p_212244_1_, JsonObject p_212244_2_) {
        switch (p_212244_1_.getType()) {
            case SINGLE_WORD: {
                p_212244_2_.addProperty("type", "word");
                break;
            }
            case QUOTABLE_PHRASE: {
                p_212244_2_.addProperty("type", "phrase");
                break;
            }
            default: {
                p_212244_2_.addProperty("type", "greedy");
            }
        }
    }
}
