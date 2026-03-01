package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class DolphinModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer tail;
    private final ModelRenderer tailFin;

    public DolphinModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        float f = 18.0f;
        float f1 = -8.0f;
        this.body = new ModelRenderer(this, 22, 0);
        this.body.addBox(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f);
        this.body.setRotationPoint(0.0f, 22.0f, -5.0f);
        ModelRenderer modelrenderer = new ModelRenderer(this, 51, 0);
        modelrenderer.addBox(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f);
        modelrenderer.rotateAngleX = 1.0471976f;
        this.body.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 48, 20);
        modelrenderer1.mirror = true;
        modelrenderer1.addBox(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        modelrenderer1.setRotationPoint(2.0f, -2.0f, 4.0f);
        modelrenderer1.rotateAngleX = 1.0471976f;
        modelrenderer1.rotateAngleZ = 2.0943952f;
        this.body.addChild(modelrenderer1);
        ModelRenderer modelrenderer2 = new ModelRenderer(this, 48, 20);
        modelrenderer2.addBox(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        modelrenderer2.setRotationPoint(-2.0f, -2.0f, 4.0f);
        modelrenderer2.rotateAngleX = 1.0471976f;
        modelrenderer2.rotateAngleZ = -2.0943952f;
        this.body.addChild(modelrenderer2);
        this.tail = new ModelRenderer(this, 0, 19);
        this.tail.addBox(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f);
        this.tail.setRotationPoint(0.0f, -2.5f, 11.0f);
        this.tail.rotateAngleX = -0.10471976f;
        this.body.addChild(this.tail);
        this.tailFin = new ModelRenderer(this, 19, 20);
        this.tailFin.addBox(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f);
        this.tailFin.setRotationPoint(0.0f, 0.0f, 9.0f);
        this.tailFin.rotateAngleX = 0.0f;
        this.tail.addChild(this.tailFin);
        ModelRenderer modelrenderer3 = new ModelRenderer(this, 0, 0);
        modelrenderer3.addBox(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f);
        modelrenderer3.setRotationPoint(0.0f, -4.0f, -3.0f);
        ModelRenderer modelrenderer4 = new ModelRenderer(this, 0, 13);
        modelrenderer4.addBox(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f);
        modelrenderer3.addChild(modelrenderer4);
        this.body.addChild(modelrenderer3);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.body.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.body.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        if (Entity.horizontalMag(((Entity)entityIn).getMotion()) > 1.0E-7) {
            this.body.rotateAngleX += -0.05f + -0.05f * MathHelper.cos(ageInTicks * 0.3f);
            this.tail.rotateAngleX = -0.1f * MathHelper.cos(ageInTicks * 0.3f);
            this.tailFin.rotateAngleX = -0.2f * MathHelper.cos(ageInTicks * 0.3f);
        }
    }
}
