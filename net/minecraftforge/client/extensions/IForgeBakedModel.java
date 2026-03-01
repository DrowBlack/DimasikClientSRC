package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.optifine.reflect.Reflector;

public interface IForgeBakedModel {
    default public IBakedModel getBakedModel() {
        return (IBakedModel)this;
    }

    default public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        return this.getBakedModel().getQuads(state, side, rand);
    }

    default public boolean isAmbientOcclusion(BlockState state) {
        return this.getBakedModel().isAmbientOcclusion();
    }

    default public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return (IBakedModel)Reflector.ForgeHooksClient_handlePerspective.call(new Object[]{this.getBakedModel(), cameraTransformType, mat});
    }

    default public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData) {
        return tileData;
    }

    default public TextureAtlasSprite getParticleTexture(IModelData data) {
        return this.getBakedModel().getParticleTexture();
    }

    default public boolean isLayered() {
        return false;
    }
}
