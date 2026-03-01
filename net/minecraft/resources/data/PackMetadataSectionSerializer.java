package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer
implements IMetadataSectionSerializer<PackMetadataSection> {
    @Override
    public PackMetadataSection deserialize(JsonObject json) {
        IFormattableTextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(json.get("description"));
        if (itextcomponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int i = JSONUtils.getInt(json, "pack_format");
        return new PackMetadataSection(itextcomponent, i);
    }

    @Override
    public String getSectionName() {
        return "pack";
    }
}
