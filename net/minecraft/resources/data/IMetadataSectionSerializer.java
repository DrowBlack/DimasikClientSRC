package net.minecraft.resources.data;

import com.google.gson.JsonObject;

public interface IMetadataSectionSerializer<T> {
    public String getSectionName();

    public T deserialize(JsonObject var1);
}
