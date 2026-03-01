package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EndermanEyesLayer;
import net.minecraft.client.renderer.entity.layers.HeldBlockLayer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class EndermanRenderer
extends MobRenderer<EndermanEntity, EndermanModel<EndermanEntity>> {
    private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation("textures/entity/enderman/enderman.png");
    private final Random rnd = new Random();

    public EndermanRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new EndermanModel(0.0f), 0.5f);
        this.addLayer(new EndermanEyesLayer<EndermanEntity>(this));
        this.addLayer(new HeldBlockLayer(this));
    }

    @Override
    public void render(EndermanEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockState blockstate = entityIn.getHeldBlockState();
        EndermanModel endermanmodel = (EndermanModel)this.getEntityModel();
        endermanmodel.isCarrying = blockstate != null;
        endermanmodel.isAttacking = entityIn.isScreaming();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public Vector3d getRenderOffset(EndermanEntity entityIn, float partialTicks) {
        if (entityIn.isScreaming()) {
            double d0 = 0.02;
            return new Vector3d(this.rnd.nextGaussian() * 0.02, 0.0, this.rnd.nextGaussian() * 0.02);
        }
        return super.getRenderOffset(entityIn, partialTicks);
    }

    @Override
    public ResourceLocation getEntityTexture(EndermanEntity entity) {
        return ENDERMAN_TEXTURES;
    }
}
