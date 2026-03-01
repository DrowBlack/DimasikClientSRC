package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.entity.model.OcelotModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;

public class CatModel<T extends CatEntity>
extends OcelotModel<T> {
    private float field_217155_m;
    private float field_217156_n;
    private float field_217157_o;

    public CatModel(float p_i51069_1_) {
        super(p_i51069_1_);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.field_217155_m = ((CatEntity)entityIn).func_213408_v(partialTick);
        this.field_217156_n = ((CatEntity)entityIn).func_213421_w(partialTick);
        this.field_217157_o = ((CatEntity)entityIn).func_213424_x(partialTick);
        if (this.field_217155_m <= 0.0f) {
            this.ocelotHead.rotateAngleX = 0.0f;
            this.ocelotHead.rotateAngleZ = 0.0f;
            this.ocelotFrontLeftLeg.rotateAngleX = 0.0f;
            this.ocelotFrontLeftLeg.rotateAngleZ = 0.0f;
            this.ocelotFrontRightLeg.rotateAngleX = 0.0f;
            this.ocelotFrontRightLeg.rotateAngleZ = 0.0f;
            this.ocelotFrontRightLeg.rotationPointX = -1.2f;
            this.ocelotBackLeftLeg.rotateAngleX = 0.0f;
            this.ocelotBackRightLeg.rotateAngleX = 0.0f;
            this.ocelotBackRightLeg.rotateAngleZ = 0.0f;
            this.ocelotBackRightLeg.rotationPointX = -1.1f;
            this.ocelotBackRightLeg.rotationPointY = 18.0f;
        }
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        if (((TameableEntity)entityIn).isSleeping()) {
            this.ocelotBody.rotateAngleX = 0.7853982f;
            this.ocelotBody.rotationPointY += -4.0f;
            this.ocelotBody.rotationPointZ += 5.0f;
            this.ocelotHead.rotationPointY += -3.3f;
            this.ocelotHead.rotationPointZ += 1.0f;
            this.ocelotTail.rotationPointY += 8.0f;
            this.ocelotTail.rotationPointZ += -2.0f;
            this.ocelotTail2.rotationPointY += 2.0f;
            this.ocelotTail2.rotationPointZ += -0.8f;
            this.ocelotTail.rotateAngleX = 1.7278761f;
            this.ocelotTail2.rotateAngleX = 2.670354f;
            this.ocelotFrontLeftLeg.rotateAngleX = -0.15707964f;
            this.ocelotFrontLeftLeg.rotationPointY = 16.1f;
            this.ocelotFrontLeftLeg.rotationPointZ = -7.0f;
            this.ocelotFrontRightLeg.rotateAngleX = -0.15707964f;
            this.ocelotFrontRightLeg.rotationPointY = 16.1f;
            this.ocelotFrontRightLeg.rotationPointZ = -7.0f;
            this.ocelotBackLeftLeg.rotateAngleX = -1.5707964f;
            this.ocelotBackLeftLeg.rotationPointY = 21.0f;
            this.ocelotBackLeftLeg.rotationPointZ = 1.0f;
            this.ocelotBackRightLeg.rotateAngleX = -1.5707964f;
            this.ocelotBackRightLeg.rotationPointY = 21.0f;
            this.ocelotBackRightLeg.rotationPointZ = 1.0f;
            this.state = 3;
        }
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (this.field_217155_m > 0.0f) {
            this.ocelotHead.rotateAngleZ = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleZ, -1.2707963f, this.field_217155_m);
            this.ocelotHead.rotateAngleY = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleY, 1.2707963f, this.field_217155_m);
            this.ocelotFrontLeftLeg.rotateAngleX = -1.2707963f;
            this.ocelotFrontRightLeg.rotateAngleX = -0.47079635f;
            this.ocelotFrontRightLeg.rotateAngleZ = -0.2f;
            this.ocelotFrontRightLeg.rotationPointX = -0.2f;
            this.ocelotBackLeftLeg.rotateAngleX = -0.4f;
            this.ocelotBackRightLeg.rotateAngleX = 0.5f;
            this.ocelotBackRightLeg.rotateAngleZ = -0.5f;
            this.ocelotBackRightLeg.rotationPointX = -0.3f;
            this.ocelotBackRightLeg.rotationPointY = 20.0f;
            this.ocelotTail.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail.rotateAngleX, 0.8f, this.field_217156_n);
            this.ocelotTail2.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail2.rotateAngleX, -0.4f, this.field_217156_n);
        }
        if (this.field_217157_o > 0.0f) {
            this.ocelotHead.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleX, -0.58177644f, this.field_217157_o);
        }
    }
}
