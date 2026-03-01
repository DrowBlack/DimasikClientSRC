package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

public class ArrowLayer<T extends LivingEntity, M extends PlayerModel<T>>
extends StuckInBodyLayer<T, M> {
    private final EntityRendererManager field_215336_a;
    private ArrowEntity field_229130_b_;

    public ArrowLayer(LivingRenderer<T, M> rendererIn) {
        super(rendererIn);
        this.field_215336_a = rendererIn.getRenderManager();
    }

    @Override
    protected int func_225631_a_(T p_225631_1_) {
        return ((LivingEntity)p_225631_1_).getArrowCountInEntity();
    }

    @Override
    protected void func_225632_a_(MatrixStack p_225632_1_, IRenderTypeBuffer p_225632_2_, int p_225632_3_, Entity p_225632_4_, float p_225632_5_, float p_225632_6_, float p_225632_7_, float p_225632_8_) {
        float f = MathHelper.sqrt(p_225632_5_ * p_225632_5_ + p_225632_7_ * p_225632_7_);
        this.field_229130_b_ = new ArrowEntity(p_225632_4_.world, p_225632_4_.getPosX(), p_225632_4_.getPosY(), p_225632_4_.getPosZ());
        this.field_229130_b_.rotationYaw = (float)(Math.atan2(p_225632_5_, p_225632_7_) * 57.2957763671875);
        this.field_229130_b_.rotationPitch = (float)(Math.atan2(p_225632_6_, f) * 57.2957763671875);
        this.field_229130_b_.prevRotationYaw = this.field_229130_b_.rotationYaw;
        this.field_229130_b_.prevRotationPitch = this.field_229130_b_.rotationPitch;
        this.field_215336_a.renderEntityStatic(this.field_229130_b_, 0.0, 0.0, 0.0, 0.0f, p_225632_8_, p_225632_1_, p_225632_2_, p_225632_3_);
    }
}
