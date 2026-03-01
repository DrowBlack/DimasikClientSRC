package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;

public class BlockModelDefinition {
    private final Map<String, VariantList> mapVariants = Maps.newLinkedHashMap();
    private Multipart multipart;

    public static BlockModelDefinition fromJson(ContainerHolder containerHolderIn, Reader readerIn) {
        return JSONUtils.fromJson(containerHolderIn.gson, readerIn, BlockModelDefinition.class);
    }

    public BlockModelDefinition(Map<String, VariantList> variants, Multipart multipartIn) {
        this.multipart = multipartIn;
        this.mapVariants.putAll(variants);
    }

    public BlockModelDefinition(List<BlockModelDefinition> definitions) {
        BlockModelDefinition blockmodeldefinition = null;
        for (BlockModelDefinition blockmodeldefinition1 : definitions) {
            if (blockmodeldefinition1.hasMultipartData()) {
                this.mapVariants.clear();
                blockmodeldefinition = blockmodeldefinition1;
            }
            this.mapVariants.putAll(blockmodeldefinition1.mapVariants);
        }
        if (blockmodeldefinition != null) {
            this.multipart = blockmodeldefinition.multipart;
        }
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof BlockModelDefinition) {
            BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)p_equals_1_;
            if (this.mapVariants.equals(blockmodeldefinition.mapVariants)) {
                return this.hasMultipartData() ? this.multipart.equals(blockmodeldefinition.multipart) : !blockmodeldefinition.hasMultipartData();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.mapVariants.hashCode() + (this.hasMultipartData() ? this.multipart.hashCode() : 0);
    }

    public Map<String, VariantList> getVariants() {
        return this.mapVariants;
    }

    public boolean hasMultipartData() {
        return this.multipart != null;
    }

    public Multipart getMultipartData() {
        return this.multipart;
    }

    public static final class ContainerHolder {
        protected final Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)BlockModelDefinition.class), new Deserializer()).registerTypeAdapter((Type)((Object)Variant.class), new Variant.Deserializer()).registerTypeAdapter((Type)((Object)VariantList.class), new VariantList.Deserializer()).registerTypeAdapter((Type)((Object)Multipart.class), new Multipart.Deserializer(this)).registerTypeAdapter((Type)((Object)Selector.class), new Selector.Deserializer()).create();
        private StateContainer<Block, BlockState> stateContainer;

        public StateContainer<Block, BlockState> getStateContainer() {
            return this.stateContainer;
        }

        public void setStateContainer(StateContainer<Block, BlockState> stateContainerIn) {
            this.stateContainer = stateContainerIn;
        }
    }

    public static class Deserializer
    implements JsonDeserializer<BlockModelDefinition> {
        @Override
        public BlockModelDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            Map<String, VariantList> map = this.parseMapVariants(p_deserialize_3_, jsonobject);
            Multipart multipart = this.parseMultipart(p_deserialize_3_, jsonobject);
            if (!map.isEmpty() || multipart != null && !multipart.getVariants().isEmpty()) {
                return new BlockModelDefinition(map, multipart);
            }
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
        }

        protected Map<String, VariantList> parseMapVariants(JsonDeserializationContext deserializationContext, JsonObject object) {
            HashMap<String, VariantList> map = Maps.newHashMap();
            if (object.has("variants")) {
                JsonObject jsonobject = JSONUtils.getJsonObject(object, "variants");
                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), (VariantList)deserializationContext.deserialize(entry.getValue(), (Type)((Object)VariantList.class)));
                }
            }
            return map;
        }

        @Nullable
        protected Multipart parseMultipart(JsonDeserializationContext deserializationContext, JsonObject object) {
            if (!object.has("multipart")) {
                return null;
            }
            JsonArray jsonarray = JSONUtils.getJsonArray(object, "multipart");
            return (Multipart)deserializationContext.deserialize(jsonarray, (Type)((Object)Multipart.class));
        }
    }
}
