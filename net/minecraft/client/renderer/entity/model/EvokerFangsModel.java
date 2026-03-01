package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class EvokerFangsModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer base = new ModelRenderer(this, 0, 0);
    private final ModelRenderer upperJaw;
    private final ModelRenderer lowerJaw;

    public EvokerFangsModel() {
        this.base.setRotationPoint(-5.0f, 22.0f, -5.0f);
        this.base.addBox(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f);
        this.upperJaw = new ModelRenderer(this, 40, 0);
        this.upperJaw.setRotationPoint(1.5f, 22.0f, -4.0f);
        this.upperJaw.addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        this.lowerJaw = new ModelRenderer(this, 40, 0);
        this.lowerJaw.setRotationPoint(-1.5f, 22.0f, 4.0f);
        this.lowerJaw.addBox(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = limbSwing * 2.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        f = 1.0f - f * f * f;
        this.upperJaw.rotateAngleZ = (float)Math.PI - f * 0.35f * (float)Math.PI;
        this.lowerJaw.rotateAngleZ = (float)Math.PI + f * 0.35f * (float)Math.PI;
        this.lowerJaw.rotateAngleY = (float)Math.PI;
        float f1 = (limbSwing + MathHelper.sin(limbSwing * 2.7f)) * 0.6f * 12.0f;
        this.lowerJaw.rotationPointY = this.upperJaw.rotationPointY = 24.0f - f1;
        this.base.rotationPointY = this.upperJaw.rotationPointY;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.base, this.upperJaw, this.lowerJaw);
    }
}
