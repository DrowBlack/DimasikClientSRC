package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class LlamaSpitModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer main = new ModelRenderer(this);

    public LlamaSpitModel() {
        this(0.0f);
    }

    public LlamaSpitModel(float p_i47225_1_) {
        int i = 2;
        this.main.setTextureOffset(0, 0).addBox(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f, p_i47225_1_);
        this.main.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.main);
    }
}
