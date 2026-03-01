package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer
implements IMetadataSectionSerializer<AnimationMetadataSection> {
    @Override
    public AnimationMetadataSection deserialize(JsonObject json) {
        ArrayList<AnimationFrame> list = Lists.newArrayList();
        int i = JSONUtils.getInt(json, "frametime", 1);
        if (i != 1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, i, "Invalid default frame time");
        }
        if (json.has("frames")) {
            try {
                JsonArray jsonarray = JSONUtils.getJsonArray(json, "frames");
                for (int j = 0; j < jsonarray.size(); ++j) {
                    JsonElement jsonelement = jsonarray.get(j);
                    AnimationFrame animationframe = this.parseAnimationFrame(j, jsonelement);
                    if (animationframe == null) continue;
                    list.add(animationframe);
                }
            }
            catch (ClassCastException classcastexception) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + String.valueOf(json.get("frames")), classcastexception);
            }
        }
        int k = JSONUtils.getInt(json, "width", -1);
        int l = JSONUtils.getInt(json, "height", -1);
        if (k != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, k, "Invalid width");
        }
        if (l != -1) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, l, "Invalid height");
        }
        boolean flag = JSONUtils.getBoolean(json, "interpolate", false);
        return new AnimationMetadataSection(list, k, l, i, flag);
    }

    private AnimationFrame parseAnimationFrame(int frame, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return new AnimationFrame(JSONUtils.getInt(element, "frames[" + frame + "]"));
        }
        if (element.isJsonObject()) {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "frames[" + frame + "]");
            int i = JSONUtils.getInt(jsonobject, "time", -1);
            if (jsonobject.has("time")) {
                Validate.inclusiveBetween(1L, Integer.MAX_VALUE, i, "Invalid frame time");
            }
            int j = JSONUtils.getInt(jsonobject, "index");
            Validate.inclusiveBetween(0L, Integer.MAX_VALUE, j, "Invalid frame index");
            return new AnimationFrame(j, i);
        }
        return null;
    }

    @Override
    public String getSectionName() {
        return "animation";
    }
}
