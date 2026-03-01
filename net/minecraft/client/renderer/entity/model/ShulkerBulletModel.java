package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ShulkerBulletModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer renderer;

    public ShulkerBulletModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.renderer = new ModelRenderer(this);
        this.renderer.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f, 0.0f);
        this.renderer.setTextureOffset(0, 10).addBox(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0.0f);
        this.renderer.setTextureOffset(20, 0).addBox(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, 0.0f);
        this.renderer.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.renderer);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderer.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.renderer.rotateAngleX = headPitch * ((float)Math.PI / 180);
    }
}
