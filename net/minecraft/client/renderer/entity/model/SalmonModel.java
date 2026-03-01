package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SalmonModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer bodyFront;
    private final ModelRenderer bodyRear;
    private final ModelRenderer head;
    private final ModelRenderer finRight;
    private final ModelRenderer finLeft;

    public SalmonModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 20;
        this.bodyFront = new ModelRenderer(this, 0, 0);
        this.bodyFront.addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.bodyFront.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.bodyRear = new ModelRenderer(this, 0, 13);
        this.bodyRear.addBox(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.bodyRear.setRotationPoint(0.0f, 20.0f, 8.0f);
        this.head = new ModelRenderer(this, 22, 0);
        this.head.addBox(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setRotationPoint(0.0f, 20.0f, 0.0f);
        ModelRenderer modelrenderer = new ModelRenderer(this, 20, 10);
        modelrenderer.addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f);
        modelrenderer.setRotationPoint(0.0f, 0.0f, 8.0f);
        this.bodyRear.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 2, 1);
        modelrenderer1.addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f);
        modelrenderer1.setRotationPoint(0.0f, -4.5f, 5.0f);
        this.bodyFront.addChild(modelrenderer1);
        ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 2);
        modelrenderer2.addBox(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f);
        modelrenderer2.setRotationPoint(0.0f, -4.5f, -1.0f);
        this.bodyRear.addChild(modelrenderer2);
        this.finRight = new ModelRenderer(this, -4, 0);
        this.finRight.addBox(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.finRight.setRotationPoint(-1.5f, 21.5f, 0.0f);
        this.finRight.rotateAngleZ = -0.7853982f;
        this.finLeft = new ModelRenderer(this, 0, 0);
        this.finLeft.addBox(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.finLeft.setRotationPoint(1.5f, 21.5f, 0.0f);
        this.finLeft.rotateAngleZ = 0.7853982f;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.bodyFront, this.bodyRear, this.head, this.finRight, this.finLeft);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 1.0f;
        float f1 = 1.0f;
        if (!((Entity)entityIn).isInWater()) {
            f = 1.3f;
            f1 = 1.7f;
        }
        this.bodyRear.rotateAngleY = -f * 0.25f * MathHelper.sin(f1 * 0.6f * ageInTicks);
    }
}
