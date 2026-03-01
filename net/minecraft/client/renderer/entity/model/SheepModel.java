package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.SheepEntity;

public class SheepModel<T extends SheepEntity>
extends QuadrupedModel<T> {
    private float headRotationAngleX;

    public SheepModel() {
        super(12, 0.0f, false, 8.0f, 4.0f, 2.0f, 2.0f, 24);
        this.headModel = new ModelRenderer(this, 0, 0);
        this.headModel.addBox(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 8.0f, 0.0f);
        this.headModel.setRotationPoint(0.0f, 6.0f, -8.0f);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f, 0.0f);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.headModel.rotationPointY = 6.0f + ((SheepEntity)entityIn).getHeadRotationPointY(partialTick) * 9.0f;
        this.headRotationAngleX = ((SheepEntity)entityIn).getHeadRotationAngleX(partialTick);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.headModel.rotateAngleX = this.headRotationAngleX;
    }
}
