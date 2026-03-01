package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SetStewEffect
extends LootFunction {
    private final Map<Effect, RandomValueRange> field_215950_a;

    private SetStewEffect(ILootCondition[] p_i51215_1_, Map<Effect, RandomValueRange> p_i51215_2_) {
        super(p_i51215_1_);
        this.field_215950_a = ImmutableMap.copyOf(p_i51215_2_);
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootFunctionManager.SET_STEW_EFFECT;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        if (stack.getItem() == Items.SUSPICIOUS_STEW && !this.field_215950_a.isEmpty()) {
            Random random = context.getRandom();
            int i = random.nextInt(this.field_215950_a.size());
            Map.Entry<Effect, RandomValueRange> entry = Iterables.get(this.field_215950_a.entrySet(), i);
            Effect effect = entry.getKey();
            int j = entry.getValue().generateInt(random);
            if (!effect.isInstant()) {
                j *= 20;
            }
            SuspiciousStewItem.addEffect(stack, effect, j);
            return stack;
        }
        return stack;
    }

    public static Builder func_215948_b() {
        return new Builder();
    }

    public static class Builder
    extends LootFunction.Builder<Builder> {
        private final Map<Effect, RandomValueRange> field_216078_a = Maps.newHashMap();

        @Override
        protected Builder doCast() {
            return this;
        }

        public Builder func_216077_a(Effect p_216077_1_, RandomValueRange p_216077_2_) {
            this.field_216078_a.put(p_216077_1_, p_216077_2_);
            return this;
        }

        @Override
        public ILootFunction build() {
            return new SetStewEffect(this.getConditions(), this.field_216078_a);
        }
    }

    public static class Serializer
    extends LootFunction.Serializer<SetStewEffect> {
        @Override
        public void serialize(JsonObject p_230424_1_, SetStewEffect p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            if (!p_230424_2_.field_215950_a.isEmpty()) {
                JsonArray jsonarray = new JsonArray();
                for (Effect effect : p_230424_2_.field_215950_a.keySet()) {
                    JsonObject jsonobject = new JsonObject();
                    ResourceLocation resourcelocation = Registry.EFFECTS.getKey(effect);
                    if (resourcelocation == null) {
                        throw new IllegalArgumentException("Don't know how to serialize mob effect " + String.valueOf(effect));
                    }
                    jsonobject.add("type", new JsonPrimitive(resourcelocation.toString()));
                    jsonobject.add("duration", p_230424_3_.serialize(p_230424_2_.field_215950_a.get(effect)));
                    jsonarray.add(jsonobject);
                }
                p_230424_1_.add("effects", jsonarray);
            }
        }

        @Override
        public SetStewEffect deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            HashMap<Effect, RandomValueRange> map = Maps.newHashMap();
            if (object.has("effects")) {
                for (JsonElement jsonelement : JSONUtils.getJsonArray(object, "effects")) {
                    String s = JSONUtils.getString(jsonelement.getAsJsonObject(), "type");
                    Effect effect = Registry.EFFECTS.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + s + "'"));
                    RandomValueRange randomvaluerange = JSONUtils.deserializeClass(jsonelement.getAsJsonObject(), "duration", deserializationContext, RandomValueRange.class);
                    map.put(effect, randomvaluerange);
                }
            }
            return new SetStewEffect(conditionsIn, map);
        }
    }
}
