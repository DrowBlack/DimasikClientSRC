package net.minecraft.client.renderer.model;

import java.util.Collections;
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

public class BuiltInModel
implements IBakedModel {
    private final ItemCameraTransforms cameraTransforms;
    private final ItemOverrideList overrides;
    private final TextureAtlasSprite sprite;
    private final boolean isSideLit;

    public BuiltInModel(ItemCameraTransforms cameraTransforms, ItemOverrideList overrides, TextureAtlasSprite spite, boolean isSideLit) {
        this.cameraTransforms = cameraTransforms;
        this.overrides = overrides;
        this.sprite = spite;
        this.isSideLit = isSideLit;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isSideLit() {
        return this.isSideLit;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.sprite;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.cameraTransforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }
}
