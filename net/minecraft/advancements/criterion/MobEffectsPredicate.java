package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class MobEffectsPredicate {
    public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
    private final Map<Effect, InstancePredicate> effects;

    public MobEffectsPredicate(Map<Effect, InstancePredicate> effects) {
        this.effects = effects;
    }

    public static MobEffectsPredicate any() {
        return new MobEffectsPredicate(Maps.newLinkedHashMap());
    }

    public MobEffectsPredicate addEffect(Effect effect) {
        this.effects.put(effect, new InstancePredicate());
        return this;
    }

    public boolean test(Entity entityIn) {
        if (this == ANY) {
            return true;
        }
        return entityIn instanceof LivingEntity ? this.test(((LivingEntity)entityIn).getActivePotionMap()) : false;
    }

    public boolean test(LivingEntity entityIn) {
        return this == ANY ? true : this.test(entityIn.getActivePotionMap());
    }

    public boolean test(Map<Effect, EffectInstance> potions) {
        if (this == ANY) {
            return true;
        }
        for (Map.Entry<Effect, InstancePredicate> entry : this.effects.entrySet()) {
            EffectInstance effectinstance = potions.get(entry.getKey());
            if (entry.getValue().test(effectinstance)) continue;
            return false;
        }
        return true;
    }

    public static MobEffectsPredicate deserialize(@Nullable JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "effects");
            LinkedHashMap<Effect, InstancePredicate> map = Maps.newLinkedHashMap();
            for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
                Effect effect = Registry.EFFECTS.getOptional(resourcelocation).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + String.valueOf(resourcelocation) + "'"));
                InstancePredicate mobeffectspredicate$instancepredicate = InstancePredicate.deserialize(JSONUtils.getJsonObject(entry.getValue(), entry.getKey()));
                map.put(effect, mobeffectspredicate$instancepredicate);
            }
            return new MobEffectsPredicate(map);
        }
        return ANY;
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonobject = new JsonObject();
        for (Map.Entry<Effect, InstancePredicate> entry : this.effects.entrySet()) {
            jsonobject.add(Registry.EFFECTS.getKey(entry.getKey()).toString(), entry.getValue().serialize());
        }
        return jsonobject;
    }

    public static class InstancePredicate {
        private final MinMaxBounds.IntBound amplifier;
        private final MinMaxBounds.IntBound duration;
        @Nullable
        private final Boolean ambient;
        @Nullable
        private final Boolean visible;

        public InstancePredicate(MinMaxBounds.IntBound amplifier, MinMaxBounds.IntBound duration, @Nullable Boolean ambient, @Nullable Boolean visible) {
            this.amplifier = amplifier;
            this.duration = duration;
            this.ambient = ambient;
            this.visible = visible;
        }

        public InstancePredicate() {
            this(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, null, null);
        }

        public boolean test(@Nullable EffectInstance effect) {
            if (effect == null) {
                return false;
            }
            if (!this.amplifier.test(effect.getAmplifier())) {
                return false;
            }
            if (!this.duration.test(effect.getDuration())) {
                return false;
            }
            if (this.ambient != null && this.ambient.booleanValue() != effect.isAmbient()) {
                return false;
            }
            return this.visible == null || this.visible.booleanValue() == effect.doesShowParticles();
        }

        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("amplifier", this.amplifier.serialize());
            jsonobject.add("duration", this.duration.serialize());
            jsonobject.addProperty("ambient", this.ambient);
            jsonobject.addProperty("visible", this.visible);
            return jsonobject;
        }

        public static InstancePredicate deserialize(JsonObject object) {
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(object.get("amplifier"));
            MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(object.get("duration"));
            Boolean obool = object.has("ambient") ? Boolean.valueOf(JSONUtils.getBoolean(object, "ambient")) : null;
            Boolean obool1 = object.has("visible") ? Boolean.valueOf(JSONUtils.getBoolean(object, "visible")) : null;
            return new InstancePredicate(minmaxbounds$intbound, minmaxbounds$intbound1, obool, obool1);
        }
    }
}
