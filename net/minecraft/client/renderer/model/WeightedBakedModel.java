package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel
implements IBakedModel {
    private final int totalWeight;
    private final List<WeightedModel> models;
    private final IBakedModel baseModel;

    public WeightedBakedModel(List<WeightedModel> modelsIn) {
        this.models = modelsIn;
        this.totalWeight = WeightedRandom.getTotalWeight(modelsIn);
        this.baseModel = modelsIn.get((int)0).model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return WeightedRandom.getRandomItem(this.models, (int)(Math.abs((int)((int)rand.nextLong())) % this.totalWeight)).model.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return this.baseModel.isSideLit();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.baseModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.baseModel.getOverrides();
    }

    static class WeightedModel
    extends WeightedRandom.Item {
        protected final IBakedModel model;

        public WeightedModel(IBakedModel modelIn, int itemWeightIn) {
            super(itemWeightIn);
            this.model = modelIn;
        }
    }

    public static class Builder {
        private final List<WeightedModel> listItems = Lists.newArrayList();

        public Builder add(@Nullable IBakedModel model, int weight) {
            if (model != null) {
                this.listItems.add(new WeightedModel(model, weight));
            }
            return this;
        }

        @Nullable
        public IBakedModel build() {
            if (this.listItems.isEmpty()) {
                return null;
            }
            return this.listItems.size() == 1 ? this.listItems.get((int)0).model : new WeightedBakedModel(this.listItems);
        }
    }
}
