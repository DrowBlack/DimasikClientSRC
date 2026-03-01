package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public interface ICriterionInstance {
    public ResourceLocation getId();

    public JsonObject serialize(ConditionArraySerializer var1);
}
