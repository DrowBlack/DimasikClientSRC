package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class VariantList
implements IUnbakedModel {
    private final List<Variant> variantList;

    public VariantList(List<Variant> variantListIn) {
        this.variantList = variantListIn;
    }

    public List<Variant> getVariantList() {
        return this.variantList;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof VariantList) {
            VariantList variantlist = (VariantList)p_equals_1_;
            return this.variantList.equals(variantlist.variantList);
        }
        return false;
    }

    public int hashCode() {
        return this.variantList.hashCode();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.getVariantList().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
    }

    @Override
    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return this.getVariantList().stream().map(Variant::getModelLocation).distinct().flatMap(variantModel -> ((IUnbakedModel)modelGetter.apply((ResourceLocation)variantModel)).getTextures(modelGetter, missingTextureErrors).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
        if (this.getVariantList().isEmpty()) {
            return null;
        }
        WeightedBakedModel.Builder weightedbakedmodel$builder = new WeightedBakedModel.Builder();
        for (Variant variant : this.getVariantList()) {
            IBakedModel ibakedmodel = modelBakeryIn.bake(variant.getModelLocation(), variant);
            weightedbakedmodel$builder.add(ibakedmodel, variant.getWeight());
        }
        return weightedbakedmodel$builder.build();
    }

    public static class Deserializer
    implements JsonDeserializer<VariantList> {
        @Override
        public VariantList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            ArrayList<Variant> list = Lists.newArrayList();
            if (p_deserialize_1_.isJsonArray()) {
                JsonArray jsonarray = p_deserialize_1_.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement jsonelement : jsonarray) {
                    list.add((Variant)p_deserialize_3_.deserialize(jsonelement, (Type)((Object)Variant.class)));
                }
            } else {
                list.add((Variant)p_deserialize_3_.deserialize(p_deserialize_1_, (Type)((Object)Variant.class)));
            }
            return new VariantList(list);
        }
    }
}
