package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.util.ResourceLocation;

public abstract class EnergyLayer<T extends Entity, M extends EntityModel<T>>
extends LayerRenderer<T, M> {
    public EnergyLayer(IEntityRenderer<T, M> p_i226038_1_) {
        super(p_i226038_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (((IChargeableMob)entitylivingbaseIn).isCharged()) {
            float f = (float)((Entity)entitylivingbaseIn).ticksExisted + partialTicks;
            EntityModel<T> entitymodel = this.func_225635_b_();
            entitymodel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            ((EntityModel)this.getEntityModel()).copyModelAttributesTo(entitymodel);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEnergySwirl(this.func_225633_a_(), this.func_225634_a_(f), f * 0.01f));
            entitymodel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            entitymodel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5f, 0.5f, 0.5f, 1.0f);
        }
    }

    protected abstract float func_225634_a_(float var1);

    protected abstract ResourceLocation func_225633_a_();

    protected abstract EntityModel<T> func_225635_b_();
}
