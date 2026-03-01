package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends LayerRenderer<T, M> {
    public StuckInBodyLayer(LivingRenderer<T, M> p_i226041_1_) {
        super(p_i226041_1_);
    }

    protected abstract int func_225631_a_(T var1);

    protected abstract void func_225632_a_(MatrixStack var1, IRenderTypeBuffer var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int i = this.func_225631_a_(entitylivingbaseIn);
        Random random = new Random(((Entity)entitylivingbaseIn).getEntityId());
        if (i > 0) {
            for (int j = 0; j < i; ++j) {
                matrixStackIn.push();
                ModelRenderer modelrenderer = ((PlayerModel)this.getEntityModel()).getRandomModelRenderer(random);
                ModelRenderer.ModelBox modelrenderer$modelbox = modelrenderer.getRandomCube(random);
                modelrenderer.translateRotate(matrixStackIn);
                float f = random.nextFloat();
                float f1 = random.nextFloat();
                float f2 = random.nextFloat();
                float f3 = MathHelper.lerp(f, modelrenderer$modelbox.posX1, modelrenderer$modelbox.posX2) / 16.0f;
                float f4 = MathHelper.lerp(f1, modelrenderer$modelbox.posY1, modelrenderer$modelbox.posY2) / 16.0f;
                float f5 = MathHelper.lerp(f2, modelrenderer$modelbox.posZ1, modelrenderer$modelbox.posZ2) / 16.0f;
                matrixStackIn.translate(f3, f4, f5);
                f = -1.0f * (f * 2.0f - 1.0f);
                f1 = -1.0f * (f1 * 2.0f - 1.0f);
                f2 = -1.0f * (f2 * 2.0f - 1.0f);
                this.func_225632_a_(matrixStackIn, bufferIn, packedLightIn, (Entity)entitylivingbaseIn, f, f1, f2, partialTicks);
                matrixStackIn.pop();
            }
        }
    }
}
