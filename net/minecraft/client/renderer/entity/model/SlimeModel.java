package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SlimeModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer slimeBodies;
    private final ModelRenderer slimeRightEye;
    private final ModelRenderer slimeLeftEye;
    private final ModelRenderer slimeMouth;

    public SlimeModel(int slimeBodyTexOffY) {
        this.slimeBodies = new ModelRenderer(this, 0, slimeBodyTexOffY);
        this.slimeRightEye = new ModelRenderer(this, 32, 0);
        this.slimeLeftEye = new ModelRenderer(this, 32, 4);
        this.slimeMouth = new ModelRenderer(this, 32, 8);
        if (slimeBodyTexOffY > 0) {
            this.slimeBodies.addBox(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.slimeRightEye.addBox(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.slimeLeftEye.addBox(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.slimeMouth.addBox(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        } else {
            this.slimeBodies.addBox(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        }
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.slimeBodies, this.slimeRightEye, this.slimeLeftEye, this.slimeMouth);
    }
}
