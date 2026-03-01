package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class ElytraModel<T extends LivingEntity>
extends AgeableModel<T> {
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

    public ElytraModel() {
        this.leftWing.addBox(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
        this.rightWing = new ModelRenderer(this, 22, 0);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, 1.0f);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.leftWing, this.rightWing);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 0.2617994f;
        float f1 = -0.2617994f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        if (((LivingEntity)entityIn).isElytraFlying()) {
            float f4 = 1.0f;
            Vector3d vector3d = ((Entity)entityIn).getMotion();
            if (vector3d.y < 0.0) {
                Vector3d vector3d1 = vector3d.normalize();
                f4 = 1.0f - (float)Math.pow(-vector3d1.y, 1.5);
            }
            f = f4 * 0.34906584f + (1.0f - f4) * f;
            f1 = f4 * -1.5707964f + (1.0f - f4) * f1;
        } else if (((Entity)entityIn).isCrouching()) {
            f = 0.69813174f;
            f1 = -0.7853982f;
            f2 = 3.0f;
            f3 = 0.08726646f;
        }
        this.leftWing.rotationPointX = 5.0f;
        this.leftWing.rotationPointY = f2;
        if (entityIn instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)entityIn;
            abstractclientplayerentity.rotateElytraX = (float)((double)abstractclientplayerentity.rotateElytraX + (double)(f - abstractclientplayerentity.rotateElytraX) * 0.1);
            abstractclientplayerentity.rotateElytraY = (float)((double)abstractclientplayerentity.rotateElytraY + (double)(f3 - abstractclientplayerentity.rotateElytraY) * 0.1);
            abstractclientplayerentity.rotateElytraZ = (float)((double)abstractclientplayerentity.rotateElytraZ + (double)(f1 - abstractclientplayerentity.rotateElytraZ) * 0.1);
            this.leftWing.rotateAngleX = abstractclientplayerentity.rotateElytraX;
            this.leftWing.rotateAngleY = abstractclientplayerentity.rotateElytraY;
            this.leftWing.rotateAngleZ = abstractclientplayerentity.rotateElytraZ;
        } else {
            this.leftWing.rotateAngleX = f;
            this.leftWing.rotateAngleZ = f1;
            this.leftWing.rotateAngleY = f3;
        }
        this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
        this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
        this.rightWing.rotationPointY = this.leftWing.rotationPointY;
        this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
        this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
    }
}
