package net.minecraft.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.loot.BinomialRange;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RandomRanges {
    private static final Map<ResourceLocation, Class<? extends IRandomRange>> GENERATOR_MAP = Maps.newHashMap();

    public static IRandomRange deserialize(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return (IRandomRange)context.deserialize(json, (Type)((Object)ConstantRange.class));
        }
        JsonObject jsonobject = json.getAsJsonObject();
        String s = JSONUtils.getString(jsonobject, "type", IRandomRange.UNIFORM.toString());
        Class<? extends IRandomRange> oclass = GENERATOR_MAP.get(new ResourceLocation(s));
        if (oclass == null) {
            throw new JsonParseException("Unknown generator: " + s);
        }
        return (IRandomRange)context.deserialize(jsonobject, oclass);
    }

    public static JsonElement serialize(IRandomRange randomRange, JsonSerializationContext context) {
        JsonElement jsonelement = context.serialize(randomRange);
        if (jsonelement.isJsonObject()) {
            jsonelement.getAsJsonObject().addProperty("type", randomRange.getType().toString());
        }
        return jsonelement;
    }

    static {
        GENERATOR_MAP.put(IRandomRange.UNIFORM, RandomValueRange.class);
        GENERATOR_MAP.put(IRandomRange.BINOMIAL, BinomialRange.class);
        GENERATOR_MAP.put(IRandomRange.CONSTANT, ConstantRange.class);
    }
}
