package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SquidModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer[] legs = new ModelRenderer[8];
    private final ImmutableList<ModelRenderer> field_228296_f_;

    public SquidModel() {
        int i = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f);
        this.body.rotationPointY += 8.0f;
        for (int j = 0; j < this.legs.length; ++j) {
            this.legs[j] = new ModelRenderer(this, 48, 0);
            double d0 = (double)j * Math.PI * 2.0 / (double)this.legs.length;
            float f = (float)Math.cos(d0) * 5.0f;
            float f1 = (float)Math.sin(d0) * 5.0f;
            this.legs[j].addBox(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
            this.legs[j].rotationPointX = f;
            this.legs[j].rotationPointZ = f1;
            this.legs[j].rotationPointY = 15.0f;
            d0 = (double)j * Math.PI * -2.0 / (double)this.legs.length + 1.5707963267948966;
            this.legs[j].rotateAngleY = (float)d0;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.body);
        builder.addAll(Arrays.asList(this.legs));
        this.field_228296_f_ = builder.build();
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        for (ModelRenderer modelrenderer : this.legs) {
            modelrenderer.rotateAngleX = ageInTicks;
        }
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return this.field_228296_f_;
    }
}
