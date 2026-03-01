package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface ILootSerializer<T> {
    public void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

    public T deserialize(JsonObject var1, JsonDeserializationContext var2);
}
