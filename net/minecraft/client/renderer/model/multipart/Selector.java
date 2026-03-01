package net.minecraft.client.renderer.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.multipart.AndCondition;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.client.renderer.model.multipart.OrCondition;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;

public class Selector {
    private final ICondition condition;
    private final VariantList variantList;

    public Selector(ICondition conditionIn, VariantList variantListIn) {
        if (conditionIn == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if (variantListIn == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.condition = conditionIn;
        this.variantList = variantListIn;
    }

    public VariantList getVariantList() {
        return this.variantList;
    }

    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> state) {
        return this.condition.getPredicate(state);
    }

    public boolean equals(Object p_equals_1_) {
        return this == p_equals_1_;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public static class Deserializer
    implements JsonDeserializer<Selector> {
        @Override
        public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            return new Selector(this.getWhenCondition(jsonobject), (VariantList)p_deserialize_3_.deserialize(jsonobject.get("apply"), (Type)((Object)VariantList.class)));
        }

        private ICondition getWhenCondition(JsonObject json) {
            return json.has("when") ? Deserializer.getOrAndCondition(JSONUtils.getJsonObject(json, "when")) : ICondition.TRUE;
        }

        @VisibleForTesting
        static ICondition getOrAndCondition(JsonObject json) {
            Set<Map.Entry<String, JsonElement>> set = json.entrySet();
            if (set.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if (set.size() == 1) {
                if (json.has("OR")) {
                    List list1 = Streams.stream(JSONUtils.getJsonArray(json, "OR")).map(json1 -> Deserializer.getOrAndCondition(json1.getAsJsonObject())).collect(Collectors.toList());
                    return new OrCondition(list1);
                }
                if (json.has("AND")) {
                    List list = Streams.stream(JSONUtils.getJsonArray(json, "AND")).map(json1 -> Deserializer.getOrAndCondition(json1.getAsJsonObject())).collect(Collectors.toList());
                    return new AndCondition(list);
                }
                return Deserializer.makePropertyValue(set.iterator().next());
            }
            return new AndCondition(set.stream().map(Deserializer::makePropertyValue).collect(Collectors.toList()));
        }

        private static ICondition makePropertyValue(Map.Entry<String, JsonElement> entry) {
            return new PropertyValueCondition(entry.getKey(), entry.getValue().getAsString());
        }
    }
}
