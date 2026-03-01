package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonReloadListener
extends ReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Gson gson;
    private final String folder;

    public JsonReloadListener(Gson p_i51536_1_, String p_i51536_2_) {
        this.gson = p_i51536_1_;
        this.folder = p_i51536_2_;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
        HashMap<ResourceLocation, JsonElement> map = Maps.newHashMap();
        int i = this.folder.length() + 1;
        for (ResourceLocation resourcelocation : resourceManagerIn.getAllResourceLocations(this.folder, p_223379_0_ -> p_223379_0_.endsWith(".json"))) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - JSON_EXTENSION_LENGTH));
            try {
                IResource iresource = resourceManagerIn.getResource(resourcelocation);
                try {
                    InputStream inputstream = iresource.getInputStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));){
                        JsonElement jsonelement = JSONUtils.fromJson(this.gson, reader, JsonElement.class);
                        if (jsonelement != null) {
                            JsonElement jsonelement1 = map.put(resourcelocation1, jsonelement);
                            if (jsonelement1 == null) continue;
                            throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(resourcelocation1));
                        }
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", (Object)resourcelocation1, (Object)resourcelocation);
                    }
                    finally {
                        if (inputstream == null) continue;
                        inputstream.close();
                    }
                }
                finally {
                    if (iresource == null) continue;
                    iresource.close();
                }
            }
            catch (JsonParseException | IOException | IllegalArgumentException jsonparseexception) {
                LOGGER.error("Couldn't parse data file {} from {}", (Object)resourcelocation1, (Object)resourcelocation, (Object)jsonparseexception);
            }
        }
        return map;
    }
}
