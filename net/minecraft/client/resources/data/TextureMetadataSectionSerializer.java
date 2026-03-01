package net.minecraft.client.resources.data;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;

public class TextureMetadataSectionSerializer
implements IMetadataSectionSerializer<TextureMetadataSection> {
    @Override
    public TextureMetadataSection deserialize(JsonObject json) {
        boolean flag = JSONUtils.getBoolean(json, "blur", false);
        boolean flag1 = JSONUtils.getBoolean(json, "clamp", false);
        return new TextureMetadataSection(flag, flag1);
    }

    @Override
    public String getSectionName() {
        return "texture";
    }
}
