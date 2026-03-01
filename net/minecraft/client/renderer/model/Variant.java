package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;

public class Variant
implements IModelTransform {
    private final ResourceLocation modelLocation;
    private final TransformationMatrix rotation;
    private final boolean uvLock;
    private final int weight;

    public Variant(ResourceLocation modelLocationIn, TransformationMatrix rotationIn, boolean uvLockIn, int weightIn) {
        this.modelLocation = modelLocationIn;
        this.rotation = rotationIn;
        this.uvLock = uvLockIn;
        this.weight = weightIn;
    }

    public ResourceLocation getModelLocation() {
        return this.modelLocation;
    }

    @Override
    public TransformationMatrix getRotation() {
        return this.rotation;
    }

    @Override
    public boolean isUvLock() {
        return this.uvLock;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + String.valueOf(this.modelLocation) + ", rotation=" + String.valueOf(this.rotation) + ", uvLock=" + this.uvLock + ", weight=" + this.weight + "}";
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof Variant)) {
            return false;
        }
        Variant variant = (Variant)p_equals_1_;
        return this.modelLocation.equals(variant.modelLocation) && Objects.equals(this.rotation, variant.rotation) && this.uvLock == variant.uvLock && this.weight == variant.weight;
    }

    public int hashCode() {
        int i = this.modelLocation.hashCode();
        i = 31 * i + this.rotation.hashCode();
        i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
        return 31 * i + this.weight;
    }

    public static class Deserializer
    implements JsonDeserializer<Variant> {
        @Override
        public Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            ResourceLocation resourcelocation = this.getStringModel(jsonobject);
            ModelRotation modelrotation = this.parseModelRotation(jsonobject);
            boolean flag = this.parseUvLock(jsonobject);
            int i = this.parseWeight(jsonobject);
            return new Variant(resourcelocation, modelrotation.getRotation(), flag, i);
        }

        private boolean parseUvLock(JsonObject json) {
            return JSONUtils.getBoolean(json, "uvlock", false);
        }

        protected ModelRotation parseModelRotation(JsonObject json) {
            int j;
            int i = JSONUtils.getInt(json, "x", 0);
            ModelRotation modelrotation = ModelRotation.getModelRotation(i, j = JSONUtils.getInt(json, "y", 0));
            if (modelrotation == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
            }
            return modelrotation;
        }

        protected ResourceLocation getStringModel(JsonObject json) {
            return new ResourceLocation(JSONUtils.getString(json, "model"));
        }

        protected int parseWeight(JsonObject json) {
            int i = JSONUtils.getInt(json, "weight", 1);
            if (i < 1) {
                throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
            }
            return i;
        }
    }
}
