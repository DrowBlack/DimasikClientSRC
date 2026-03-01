package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.IHeadToggle;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;

public class ZombieVillagerModel<T extends ZombieEntity>
extends BipedModel<T>
implements IHeadToggle {
    private ModelRenderer field_217150_a;

    public ZombieVillagerModel(float p_i51058_1_, boolean p_i51058_2_) {
        super(p_i51058_1_, 0.0f, 64, p_i51058_2_ ? 32 : 64);
        if (p_i51058_2_) {
            this.bipedHead = new ModelRenderer(this, 0, 0);
            this.bipedHead.addBox(-4.0f, -10.0f, -4.0f, 8.0f, 8.0f, 8.0f, p_i51058_1_);
            this.bipedBody = new ModelRenderer(this, 16, 16);
            this.bipedBody.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, p_i51058_1_ + 0.1f);
            this.bipedRightLeg = new ModelRenderer(this, 0, 16);
            this.bipedRightLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
            this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_ + 0.1f);
            this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
            this.bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_ + 0.1f);
        } else {
            this.bipedHead = new ModelRenderer(this, 0, 0);
            this.bipedHead.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, p_i51058_1_);
            this.bipedHead.setTextureOffset(24, 0).addBox(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, p_i51058_1_);
            this.bipedHeadwear = new ModelRenderer(this, 32, 0);
            this.bipedHeadwear.addBox(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, p_i51058_1_ + 0.5f);
            this.field_217150_a = new ModelRenderer(this);
            this.field_217150_a.setTextureOffset(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, p_i51058_1_);
            this.field_217150_a.rotateAngleX = -1.5707964f;
            this.bipedHeadwear.addChild(this.field_217150_a);
            this.bipedBody = new ModelRenderer(this, 16, 20);
            this.bipedBody.addBox(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, p_i51058_1_);
            this.bipedBody.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, p_i51058_1_ + 0.05f);
            this.bipedRightArm = new ModelRenderer(this, 44, 22);
            this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_);
            this.bipedRightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
            this.bipedLeftArm = new ModelRenderer(this, 44, 22);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_);
            this.bipedLeftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            this.bipedRightLeg = new ModelRenderer(this, 0, 22);
            this.bipedRightLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
            this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_);
            this.bipedLeftLeg = new ModelRenderer(this, 0, 22);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
            this.bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, p_i51058_1_);
        }
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        ModelHelper.func_239105_a_(this.bipedLeftArm, this.bipedRightArm, ((MobEntity)entityIn).isAggressive(), this.swingProgress, ageInTicks);
    }

    @Override
    public void func_217146_a(boolean p_217146_1_) {
        this.bipedHead.showModel = p_217146_1_;
        this.bipedHeadwear.showModel = p_217146_1_;
        this.field_217150_a.showModel = p_217146_1_;
    }
}
