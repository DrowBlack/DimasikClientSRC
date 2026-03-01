package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.loot.LootType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootTypesManager {
    public static <E, T extends LootType<E>> LootTypeRegistryWrapper<E, T> getLootTypeRegistryWrapper(Registry<T> registry, String id, String name, Function<E, T> typeFunction) {
        return new LootTypeRegistryWrapper<E, T>(registry, id, name, typeFunction);
    }

    public static class LootTypeRegistryWrapper<E, T extends LootType<E>> {
        private final Registry<T> registry;
        private final String id;
        private final String name;
        private final Function<E, T> typeFunction;
        @Nullable
        private Pair<T, ISerializer<? extends E>> typeSerializer;

        private LootTypeRegistryWrapper(Registry<T> registry, String id, String name, Function<E, T> typeFunction) {
            this.registry = registry;
            this.id = id;
            this.name = name;
            this.typeFunction = typeFunction;
        }

        public Object getSerializer() {
            return new Serializer<E, T>(this.registry, this.id, this.name, this.typeFunction, this.typeSerializer);
        }
    }

    static class Serializer<E, T extends LootType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String id;
        private final String name;
        private final Function<E, T> typeFunction;
        @Nullable
        private final Pair<T, ISerializer<? extends E>> typeSerializer;

        private Serializer(Registry<T> registry, String id, String name, Function<E, T> typeFunction, @Nullable Pair<T, ISerializer<? extends E>> typeSerializer) {
            this.registry = registry;
            this.id = id;
            this.name = name;
            this.typeFunction = typeFunction;
            this.typeSerializer = typeSerializer;
        }

        @Override
        public E deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            if (p_deserialize_1_.isJsonObject()) {
                JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, this.id);
                ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, this.name));
                LootType t = (LootType)this.registry.getOrDefault(resourcelocation);
                if (t == null) {
                    throw new JsonSyntaxException("Unknown type '" + String.valueOf(resourcelocation) + "'");
                }
                return (E)t.getSerializer().deserialize(jsonobject, p_deserialize_3_);
            }
            if (this.typeSerializer == null) {
                throw new UnsupportedOperationException("Object " + String.valueOf(p_deserialize_1_) + " can't be deserialized");
            }
            return this.typeSerializer.getSecond().deserialize(p_deserialize_1_, p_deserialize_3_);
        }

        @Override
        public JsonElement serialize(E p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            LootType t = (LootType)this.typeFunction.apply(p_serialize_1_);
            if (this.typeSerializer != null && this.typeSerializer.getFirst() == t) {
                return this.typeSerializer.getSecond().serializer(p_serialize_1_, p_serialize_3_);
            }
            if (t == null) {
                throw new JsonSyntaxException("Unknown type: " + String.valueOf(p_serialize_1_));
            }
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(this.name, this.registry.getKey(t).toString());
            t.getSerializer().serialize(jsonobject, p_serialize_1_, p_serialize_3_);
            return jsonobject;
        }
    }

    public static interface ISerializer<T> {
        public JsonElement serializer(T var1, JsonSerializationContext var2);

        public T deserialize(JsonElement var1, JsonDeserializationContext var2);
    }
}
