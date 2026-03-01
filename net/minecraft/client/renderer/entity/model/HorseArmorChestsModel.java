package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;

public class HorseArmorChestsModel<T extends AbstractChestedHorseEntity>
extends HorseModel<T> {
    private final ModelRenderer field_199057_c = new ModelRenderer(this, 26, 21);
    private final ModelRenderer field_199058_d;

    public HorseArmorChestsModel(float p_i51068_1_) {
        super(p_i51068_1_);
        this.field_199057_c.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.field_199058_d = new ModelRenderer(this, 26, 21);
        this.field_199058_d.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        this.field_199057_c.rotateAngleY = -1.5707964f;
        this.field_199058_d.rotateAngleY = 1.5707964f;
        this.field_199057_c.setRotationPoint(6.0f, -8.0f, 0.0f);
        this.field_199058_d.setRotationPoint(-6.0f, -8.0f, 0.0f);
        this.body.addChild(this.field_199057_c);
        this.body.addChild(this.field_199058_d);
    }

    @Override
    protected void func_199047_a(ModelRenderer p_199047_1_) {
        ModelRenderer modelrenderer = new ModelRenderer(this, 0, 12);
        modelrenderer.addBox(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelrenderer.setRotationPoint(1.25f, -10.0f, 4.0f);
        ModelRenderer modelrenderer1 = new ModelRenderer(this, 0, 12);
        modelrenderer1.addBox(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelrenderer1.setRotationPoint(-1.25f, -10.0f, 4.0f);
        modelrenderer.rotateAngleX = 0.2617994f;
        modelrenderer.rotateAngleZ = 0.2617994f;
        modelrenderer1.rotateAngleX = 0.2617994f;
        modelrenderer1.rotateAngleZ = -0.2617994f;
        p_199047_1_.addChild(modelrenderer);
        p_199047_1_.addChild(modelrenderer1);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (((AbstractChestedHorseEntity)entityIn).hasChest()) {
            this.field_199057_c.showModel = true;
            this.field_199058_d.showModel = true;
        } else {
            this.field_199057_c.showModel = false;
            this.field_199058_d.showModel = false;
        }
    }
}
