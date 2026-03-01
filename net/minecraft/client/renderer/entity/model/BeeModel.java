package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

public class BeeModel<T extends BeeEntity>
extends AgeableModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer torso;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer frontLegs;
    private final ModelRenderer middleLegs;
    private final ModelRenderer backLegs;
    private final ModelRenderer stinger;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private float bodyPitch;

    public BeeModel() {
        super(false, 24.0f, 0.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0f, 19.0f, 0.0f);
        this.torso = new ModelRenderer(this, 0, 0);
        this.torso.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body.addChild(this.torso);
        this.torso.addBox(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f, 0.0f);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.stinger.addBox(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f, 0.0f);
        this.torso.addChild(this.stinger);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.leftAntenna.setRotationPoint(0.0f, -2.0f, -5.0f);
        this.leftAntenna.addBox(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightAntenna.setRotationPoint(0.0f, -2.0f, -5.0f);
        this.rightAntenna.addBox(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.rightWing.setRotationPoint(-1.5f, -4.0f, -3.0f);
        this.rightWing.rotateAngleX = 0.0f;
        this.rightWing.rotateAngleY = -0.2618f;
        this.rightWing.rotateAngleZ = 0.0f;
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.leftWing.setRotationPoint(1.5f, -4.0f, -3.0f);
        this.leftWing.rotateAngleX = 0.0f;
        this.leftWing.rotateAngleY = 0.2618f;
        this.leftWing.rotateAngleZ = 0.0f;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addBox(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.frontLegs = new ModelRenderer(this);
        this.frontLegs.setRotationPoint(1.5f, 3.0f, -2.0f);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addBox("frontLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 1);
        this.middleLegs = new ModelRenderer(this);
        this.middleLegs.setRotationPoint(1.5f, 3.0f, 0.0f);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addBox("midLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 3);
        this.backLegs = new ModelRenderer(this);
        this.backLegs.setRotationPoint(1.5f, 3.0f, 2.0f);
        this.body.addChild(this.backLegs);
        this.backLegs.addBox("backLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 5);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = ((BeeEntity)entityIn).getBodyPitch(partialTick);
        this.stinger.showModel = !((BeeEntity)entityIn).hasStung();
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag;
        this.rightWing.rotateAngleX = 0.0f;
        this.leftAntenna.rotateAngleX = 0.0f;
        this.rightAntenna.rotateAngleX = 0.0f;
        this.body.rotateAngleX = 0.0f;
        this.body.rotationPointY = 19.0f;
        boolean bl = flag = ((Entity)entityIn).isOnGround() && ((Entity)entityIn).getMotion().lengthSquared() < 1.0E-7;
        if (flag) {
            this.rightWing.rotateAngleY = -0.2618f;
            this.rightWing.rotateAngleZ = 0.0f;
            this.leftWing.rotateAngleX = 0.0f;
            this.leftWing.rotateAngleY = 0.2618f;
            this.leftWing.rotateAngleZ = 0.0f;
            this.frontLegs.rotateAngleX = 0.0f;
            this.middleLegs.rotateAngleX = 0.0f;
            this.backLegs.rotateAngleX = 0.0f;
        } else {
            float f = ageInTicks * 2.1f;
            this.rightWing.rotateAngleY = 0.0f;
            this.rightWing.rotateAngleZ = MathHelper.cos(f) * (float)Math.PI * 0.15f;
            this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
            this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
            this.frontLegs.rotateAngleX = 0.7853982f;
            this.middleLegs.rotateAngleX = 0.7853982f;
            this.backLegs.rotateAngleX = 0.7853982f;
            this.body.rotateAngleX = 0.0f;
            this.body.rotateAngleY = 0.0f;
            this.body.rotateAngleZ = 0.0f;
        }
        if (!entityIn.func_233678_J__()) {
            this.body.rotateAngleX = 0.0f;
            this.body.rotateAngleY = 0.0f;
            this.body.rotateAngleZ = 0.0f;
            if (!flag) {
                float f1 = MathHelper.cos(ageInTicks * 0.18f);
                this.body.rotateAngleX = 0.1f + f1 * (float)Math.PI * 0.025f;
                this.leftAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03f;
                this.rightAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03f;
                this.frontLegs.rotateAngleX = -f1 * (float)Math.PI * 0.1f + 0.3926991f;
                this.backLegs.rotateAngleX = -f1 * (float)Math.PI * 0.05f + 0.7853982f;
                this.body.rotationPointY = 19.0f - MathHelper.cos(ageInTicks * 0.18f) * 0.9f;
            }
        }
        if (this.bodyPitch > 0.0f) {
            this.body.rotateAngleX = ModelUtils.func_228283_a_(this.body.rotateAngleX, 3.0915928f, this.bodyPitch);
        }
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body);
    }
}
