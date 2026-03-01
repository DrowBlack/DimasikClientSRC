package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundList;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer
implements JsonDeserializer<SoundList> {
    @Override
    public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
        JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "entry");
        boolean flag = JSONUtils.getBoolean(jsonobject, "replace", false);
        String s = JSONUtils.getString(jsonobject, "subtitle", null);
        List<Sound> list = this.deserializeSounds(jsonobject);
        return new SoundList(list, flag, s);
    }

    private List<Sound> deserializeSounds(JsonObject object) {
        ArrayList<Sound> list = Lists.newArrayList();
        if (object.has("sounds")) {
            JsonArray jsonarray = JSONUtils.getJsonArray(object, "sounds");
            for (int i = 0; i < jsonarray.size(); ++i) {
                JsonElement jsonelement = jsonarray.get(i);
                if (JSONUtils.isString(jsonelement)) {
                    String s = JSONUtils.getString(jsonelement, "sound");
                    list.add(new Sound(s, 1.0f, 1.0f, 1, Sound.Type.FILE, false, false, 16));
                    continue;
                }
                list.add(this.deserializeSound(JSONUtils.getJsonObject(jsonelement, "sound")));
            }
        }
        return list;
    }

    private Sound deserializeSound(JsonObject object) {
        String s = JSONUtils.getString(object, "name");
        Sound.Type sound$type = this.deserializeType(object, Sound.Type.FILE);
        float f = JSONUtils.getFloat(object, "volume", 1.0f);
        Validate.isTrue(f > 0.0f, "Invalid volume", new Object[0]);
        float f1 = JSONUtils.getFloat(object, "pitch", 1.0f);
        Validate.isTrue(f1 > 0.0f, "Invalid pitch", new Object[0]);
        int i = JSONUtils.getInt(object, "weight", 1);
        Validate.isTrue(i > 0, "Invalid weight", new Object[0]);
        boolean flag = JSONUtils.getBoolean(object, "preload", false);
        boolean flag1 = JSONUtils.getBoolean(object, "stream", false);
        int j = JSONUtils.getInt(object, "attenuation_distance", 16);
        return new Sound(s, f, f1, i, sound$type, flag1, flag, j);
    }

    private Sound.Type deserializeType(JsonObject object, Sound.Type defaultValue) {
        Sound.Type sound$type = defaultValue;
        if (object.has("type")) {
            sound$type = Sound.Type.getByName(JSONUtils.getString(object, "type"));
            Validate.notNull(sound$type, "Invalid type", new Object[0]);
        }
        return sound$type;
    }
}
