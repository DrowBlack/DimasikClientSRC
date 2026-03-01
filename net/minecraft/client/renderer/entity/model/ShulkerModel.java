package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

public class ShulkerModel<T extends ShulkerEntity>
extends SegmentedModel<T> {
    private final ModelRenderer base;
    private final ModelRenderer lid = new ModelRenderer(64, 64, 0, 0);
    private final ModelRenderer head;

    public ShulkerModel() {
        super(RenderType::getEntityCutoutNoCullZOffset);
        this.base = new ModelRenderer(64, 64, 0, 28);
        this.head = new ModelRenderer(64, 64, 0, 52);
        this.lid.addBox(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f);
        this.lid.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.base.addBox(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f);
        this.base.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.head.addBox(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.head.setRotationPoint(0.0f, 12.0f, 0.0f);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = ageInTicks - (float)((ShulkerEntity)entityIn).ticksExisted;
        float f1 = (0.5f + ((ShulkerEntity)entityIn).getClientPeekAmount(f)) * (float)Math.PI;
        float f2 = -1.0f + MathHelper.sin(f1);
        float f3 = 0.0f;
        if (f1 > (float)Math.PI) {
            f3 = MathHelper.sin(ageInTicks * 0.1f) * 0.7f;
        }
        this.lid.setRotationPoint(0.0f, 16.0f + MathHelper.sin(f1) * 8.0f + f3, 0.0f);
        this.lid.rotateAngleY = ((ShulkerEntity)entityIn).getClientPeekAmount(f) > 0.3f ? f2 * f2 * f2 * f2 * (float)Math.PI * 0.125f : 0.0f;
        this.head.rotateAngleX = headPitch * ((float)Math.PI / 180);
        this.head.rotateAngleY = (((ShulkerEntity)entityIn).rotationYawHead - 180.0f - ((ShulkerEntity)entityIn).renderYawOffset) * ((float)Math.PI / 180);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.base, this.lid);
    }

    public ModelRenderer getBase() {
        return this.base;
    }

    public ModelRenderer getLid() {
        return this.lid;
    }

    public ModelRenderer getHead() {
        return this.head;
    }
}
