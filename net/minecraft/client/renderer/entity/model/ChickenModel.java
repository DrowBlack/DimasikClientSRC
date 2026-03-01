package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ChickenModel<T extends Entity>
extends AgeableModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer bill;
    private final ModelRenderer chin;

    public ChickenModel() {
        int i = 16;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f, 0.0f);
        this.head.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.bill = new ModelRenderer(this, 14, 0);
        this.bill.addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, 0.0f);
        this.bill.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.chin = new ModelRenderer(this, 14, 4);
        this.chin.addBox(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f, 0.0f);
        this.chin.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.body = new ModelRenderer(this, 0, 9);
        this.body.addBox(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.body.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.rightLeg = new ModelRenderer(this, 26, 0);
        this.rightLeg.addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.rightLeg.setRotationPoint(-2.0f, 19.0f, 1.0f);
        this.leftLeg = new ModelRenderer(this, 26, 0);
        this.leftLeg.addBox(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        this.leftLeg.setRotationPoint(1.0f, 19.0f, 1.0f);
        this.rightWing = new ModelRenderer(this, 24, 13);
        this.rightWing.addBox(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.rightWing.setRotationPoint(-4.0f, 13.0f, 0.0f);
        this.leftWing = new ModelRenderer(this, 24, 13);
        this.leftWing.addBox(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f);
        this.leftWing.setRotationPoint(4.0f, 13.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.head, this.bill, this.chin);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body, this.rightLeg, this.leftLeg, this.rightWing, this.leftWing);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.bill.rotateAngleX = this.head.rotateAngleX;
        this.bill.rotateAngleY = this.head.rotateAngleY;
        this.chin.rotateAngleX = this.head.rotateAngleX;
        this.chin.rotateAngleY = this.head.rotateAngleY;
        this.body.rotateAngleX = 1.5707964f;
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662f + (float)Math.PI) * 1.4f * limbSwingAmount;
        this.rightWing.rotateAngleZ = ageInTicks;
        this.leftWing.rotateAngleZ = -ageInTicks;
    }
}
