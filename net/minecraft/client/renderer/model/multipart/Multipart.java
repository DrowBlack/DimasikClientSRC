package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BlockModelDefinition;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;

public class Multipart
implements IUnbakedModel {
    private final StateContainer<Block, BlockState> stateContainer;
    private final List<Selector> selectors;

    public Multipart(StateContainer<Block, BlockState> stateContainerIn, List<Selector> selectorsIn) {
        this.stateContainer = stateContainerIn;
        this.selectors = selectorsIn;
    }

    public List<Selector> getSelectors() {
        return this.selectors;
    }

    public Set<VariantList> getVariants() {
        HashSet<VariantList> set = Sets.newHashSet();
        for (Selector selector : this.selectors) {
            set.add(selector.getVariantList());
        }
        return set;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof Multipart)) {
            return false;
        }
        Multipart multipart = (Multipart)p_equals_1_;
        return Objects.equals(this.stateContainer, multipart.stateContainer) && Objects.equals(this.selectors, multipart.selectors);
    }

    public int hashCode() {
        return Objects.hash(this.stateContainer, this.selectors);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.getSelectors().stream().flatMap(selector -> selector.getVariantList().getDependencies().stream()).collect(Collectors.toSet());
    }

    @Override
    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return this.getSelectors().stream().flatMap(selector -> selector.getVariantList().getTextures(modelGetter, missingTextureErrors).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn) {
        MultipartBakedModel.Builder multipartbakedmodel$builder = new MultipartBakedModel.Builder();
        for (Selector selector : this.getSelectors()) {
            IBakedModel ibakedmodel = selector.getVariantList().bakeModel(modelBakeryIn, spriteGetterIn, transformIn, locationIn);
            if (ibakedmodel == null) continue;
            multipartbakedmodel$builder.putModel(selector.getPredicate(this.stateContainer), ibakedmodel);
        }
        return multipartbakedmodel$builder.build();
    }

    public static class Deserializer
    implements JsonDeserializer<Multipart> {
        private final BlockModelDefinition.ContainerHolder containerHolder;

        public Deserializer(BlockModelDefinition.ContainerHolder containerHolderIn) {
            this.containerHolder = containerHolderIn;
        }

        @Override
        public Multipart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            return new Multipart(this.containerHolder.getStateContainer(), this.getSelectors(p_deserialize_3_, p_deserialize_1_.getAsJsonArray()));
        }

        private List<Selector> getSelectors(JsonDeserializationContext context, JsonArray elements) {
            ArrayList<Selector> list = Lists.newArrayList();
            for (JsonElement jsonelement : elements) {
                list.add((Selector)context.deserialize(jsonelement, (Type)((Object)Selector.class)));
            }
            return list;
        }
    }
}
