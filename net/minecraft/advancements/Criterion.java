package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion {
    private final ICriterionInstance criterionInstance;

    public Criterion(ICriterionInstance criterionInstance) {
        this.criterionInstance = criterionInstance;
    }

    public Criterion() {
        this.criterionInstance = null;
    }

    public void serializeToNetwork(PacketBuffer buffer) {
    }

    public static Criterion deserializeCriterion(JsonObject json, ConditionArrayParser conditionParser) {
        ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "trigger"));
        ICriterionTrigger icriteriontrigger = CriteriaTriggers.get(resourcelocation);
        if (icriteriontrigger == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + String.valueOf(resourcelocation));
        }
        Object icriterioninstance = icriteriontrigger.deserialize(JSONUtils.getJsonObject(json, "conditions", new JsonObject()), conditionParser);
        return new Criterion((ICriterionInstance)icriterioninstance);
    }

    public static Criterion criterionFromNetwork(PacketBuffer buffer) {
        return new Criterion();
    }

    public static Map<String, Criterion> deserializeAll(JsonObject json, ConditionArrayParser conditionParser) {
        HashMap<String, Criterion> map = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), Criterion.deserializeCriterion(JSONUtils.getJsonObject(entry.getValue(), "criterion"), conditionParser));
        }
        return map;
    }

    public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer bus) {
        HashMap<String, Criterion> map = Maps.newHashMap();
        int i = bus.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(bus.readString(Short.MAX_VALUE), Criterion.criterionFromNetwork(bus));
        }
        return map;
    }

    public static void serializeToNetwork(Map<String, Criterion> criteria, PacketBuffer buf) {
        buf.writeVarInt(criteria.size());
        for (Map.Entry<String, Criterion> entry : criteria.entrySet()) {
            buf.writeString(entry.getKey());
            entry.getValue().serializeToNetwork(buf);
        }
    }

    @Nullable
    public ICriterionInstance getCriterionInstance() {
        return this.criterionInstance;
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("trigger", this.criterionInstance.getId().toString());
        JsonObject jsonobject1 = this.criterionInstance.serialize(ConditionArraySerializer.field_235679_a_);
        if (jsonobject1.size() != 0) {
            jsonobject.add("conditions", jsonobject1);
        }
        return jsonobject;
    }
}
