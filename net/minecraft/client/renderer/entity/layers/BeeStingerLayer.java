package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class BeeStingerLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends StuckInBodyLayer<T, M> {
    private static final ResourceLocation field_229131_a_ = new ResourceLocation("textures/entity/bee/bee_stinger.png");

    public BeeStingerLayer(LivingRenderer<T, M> p_i226036_1_) {
        super(p_i226036_1_);
    }

    @Override
    protected int func_225631_a_(T p_225631_1_) {
        return ((LivingEntity)p_225631_1_).getBeeStingCount();
    }

    @Override
    protected void func_225632_a_(MatrixStack p_225632_1_, IRenderTypeBuffer p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_) {
        float f = MathHelper.sqrt(p_225632_5_ * p_225632_5_ + p_225632_7_ * p_225632_7_);
        float f1 = (float)(Math.atan2(p_225632_5_, p_225632_7_) * 57.2957763671875);
        float f2 = (float)(Math.atan2(p_225632_6_, f) * 57.2957763671875);
        p_225632_1_.translate(0.0, 0.0, 0.0);
        p_225632_1_.rotate(Vector3f.YP.rotationDegrees(f1 - 90.0f));
        p_225632_1_.rotate(Vector3f.ZP.rotationDegrees(f2));
        float f3 = 0.0f;
        float f4 = 0.125f;
        float f5 = 0.0f;
        float f6 = 0.0625f;
        float f7 = 0.03125f;
        p_225632_1_.rotate(Vector3f.XP.rotationDegrees(45.0f));
        p_225632_1_.scale(0.03125f, 0.03125f, 0.03125f);
        p_225632_1_.translate(2.5, 0.0, 0.0);
        IVertexBuilder ivertexbuilder = p_225632_2_.getBuffer(RenderType.getEntityCutoutNoCull(field_229131_a_));
        for (int i = 0; i < 4; ++i) {
            p_225632_1_.rotate(Vector3f.XP.rotationDegrees(90.0f));
            MatrixStack.Entry matrixstack$entry = p_225632_1_.getLast();
            Matrix4f matrix4f = matrixstack$entry.getMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormal();
            BeeStingerLayer.func_229132_a_(ivertexbuilder, matrix4f, matrix3f, -4.5f, -1, 0.0f, 0.0f, p_225632_3_);
            BeeStingerLayer.func_229132_a_(ivertexbuilder, matrix4f, matrix3f, 4.5f, -1, 0.125f, 0.0f, p_225632_3_);
            BeeStingerLayer.func_229132_a_(ivertexbuilder, matrix4f, matrix3f, 4.5f, 1, 0.125f, 0.0625f, p_225632_3_);
            BeeStingerLayer.func_229132_a_(ivertexbuilder, matrix4f, matrix3f, -4.5f, 1, 0.0f, 0.0625f, p_225632_3_);
        }
    }

    private static void func_229132_a_(IVertexBuilder p_229132_0_, Matrix4f p_229132_1_, Matrix3f p_229132_2_, float p_229132_3_, int p_229132_4_, float p_229132_5_, float p_229132_6_, int p_229132_7_) {
        p_229132_0_.pos(p_229132_1_, p_229132_3_, p_229132_4_, 0.0f).color(255, 255, 255, 255).tex(p_229132_5_, p_229132_6_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229132_7_).normal(p_229132_2_, 0.0f, 1.0f, 0.0f).endVertex();
    }
}
