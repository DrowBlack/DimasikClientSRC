package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CreeperModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer creeperArmor;
    private final ModelRenderer body;
    private final ModelRenderer leg1;
    private final ModelRenderer leg2;
    private final ModelRenderer leg3;
    private final ModelRenderer leg4;

    public CreeperModel() {
        this(0.0f);
    }

    public CreeperModel(float p_i46366_1_) {
        int i = 6;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, p_i46366_1_);
        this.head.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.creeperArmor = new ModelRenderer(this, 32, 0);
        this.creeperArmor.addBox(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, p_i46366_1_ + 0.5f);
        this.creeperArmor.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, p_i46366_1_);
        this.body.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.leg1 = new ModelRenderer(this, 0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, p_i46366_1_);
        this.leg1.setRotationPoint(-2.0f, 18.0f, 4.0f);
        this.leg2 = new ModelRenderer(this, 0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, p_i46366_1_);
        this.leg2.setRotationPoint(2.0f, 18.0f, 4.0f);
        this.leg3 = new ModelRenderer(this, 0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, p_i46366_1_);
        this.leg3.setRotationPoint(-2.0f, 18.0f, -4.0f);
        this.leg4 = new ModelRenderer(this, 0, 16);
        this.leg4.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, p_i46366_1_);
        this.leg4.setRotationPoint(2.0f, 18.0f, -4.0f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.head, this.body, this.leg1, this.leg2, this.leg3, this.leg4);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
    }
}
