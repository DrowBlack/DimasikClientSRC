package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BlazeModel<T extends Entity>
extends SegmentedModel<T> {
    private final ModelRenderer[] blazeSticks;
    private final ModelRenderer blazeHead = new ModelRenderer(this, 0, 0);
    private final ImmutableList<ModelRenderer> field_228242_f_;

    public BlazeModel() {
        this.blazeHead.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.blazeSticks = new ModelRenderer[12];
        for (int i = 0; i < this.blazeSticks.length; ++i) {
            this.blazeSticks[i] = new ModelRenderer(this, 0, 16);
            this.blazeSticks[i].addBox(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(this.blazeHead);
        builder.addAll(Arrays.asList(this.blazeSticks));
        this.field_228242_f_ = builder.build();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return this.field_228242_f_;
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = ageInTicks * (float)Math.PI * -0.1f;
        for (int i = 0; i < 4; ++i) {
            this.blazeSticks[i].rotationPointY = -2.0f + MathHelper.cos(((float)(i * 2) + ageInTicks) * 0.25f);
            this.blazeSticks[i].rotationPointX = MathHelper.cos(f) * 9.0f;
            this.blazeSticks[i].rotationPointZ = MathHelper.sin(f) * 9.0f;
            f += 1.0f;
        }
        f = 0.7853982f + ageInTicks * (float)Math.PI * 0.03f;
        for (int j = 4; j < 8; ++j) {
            this.blazeSticks[j].rotationPointY = 2.0f + MathHelper.cos(((float)(j * 2) + ageInTicks) * 0.25f);
            this.blazeSticks[j].rotationPointX = MathHelper.cos(f) * 7.0f;
            this.blazeSticks[j].rotationPointZ = MathHelper.sin(f) * 7.0f;
            f += 1.0f;
        }
        f = 0.47123894f + ageInTicks * (float)Math.PI * -0.05f;
        for (int k = 8; k < 12; ++k) {
            this.blazeSticks[k].rotationPointY = 11.0f + MathHelper.cos(((float)k * 1.5f + ageInTicks) * 0.5f);
            this.blazeSticks[k].rotationPointX = MathHelper.cos(f) * 5.0f;
            this.blazeSticks[k].rotationPointZ = MathHelper.sin(f) * 5.0f;
            f += 1.0f;
        }
        this.blazeHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.blazeHead.rotateAngleX = headPitch * ((float)Math.PI / 180);
    }
}
