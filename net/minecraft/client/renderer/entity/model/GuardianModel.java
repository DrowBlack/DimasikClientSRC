package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class GuardianModel
extends SegmentedModel<GuardianEntity> {
    private static final float[] field_217136_a = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] field_217137_b = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] field_217138_f = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] field_217139_g = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] field_217140_h = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] field_217141_i = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private final ModelRenderer guardianBody;
    private final ModelRenderer guardianEye;
    private final ModelRenderer[] guardianSpines;
    private final ModelRenderer[] guardianTail;

    public GuardianModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.guardianSpines = new ModelRenderer[12];
        this.guardianBody = new ModelRenderer(this);
        this.guardianBody.setTextureOffset(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f);
        this.guardianBody.setTextureOffset(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f);
        this.guardianBody.setTextureOffset(0, 28).addBox(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        for (int i = 0; i < this.guardianSpines.length; ++i) {
            this.guardianSpines[i] = new ModelRenderer(this, 0, 0);
            this.guardianSpines[i].addBox(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
            this.guardianBody.addChild(this.guardianSpines[i]);
        }
        this.guardianEye = new ModelRenderer(this, 8, 0);
        this.guardianEye.addBox(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f);
        this.guardianBody.addChild(this.guardianEye);
        this.guardianTail = new ModelRenderer[3];
        this.guardianTail[0] = new ModelRenderer(this, 40, 0);
        this.guardianTail[0].addBox(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f);
        this.guardianTail[1] = new ModelRenderer(this, 0, 54);
        this.guardianTail[1].addBox(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f);
        this.guardianTail[2] = new ModelRenderer(this);
        this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f);
        this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f);
        this.guardianBody.addChild(this.guardianTail[0]);
        this.guardianTail[0].addChild(this.guardianTail[1]);
        this.guardianTail[1].addChild(this.guardianTail[2]);
        this.func_228261_a_(0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.guardianBody);
    }

    @Override
    public void setRotationAngles(GuardianEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = ageInTicks - (float)entityIn.ticksExisted;
        this.guardianBody.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.guardianBody.rotateAngleX = headPitch * ((float)Math.PI / 180);
        float f1 = (1.0f - entityIn.getSpikesAnimation(f)) * 0.55f;
        this.func_228261_a_(ageInTicks, f1);
        this.guardianEye.rotationPointZ = -8.25f;
        Entity entity = Minecraft.getInstance().getRenderViewEntity();
        if (entityIn.hasTargetedEntity()) {
            entity = entityIn.getTargetedEntity();
        }
        if (entity != null) {
            Vector3d vector3d = entity.getEyePosition(0.0f);
            Vector3d vector3d1 = entityIn.getEyePosition(0.0f);
            double d0 = vector3d.y - vector3d1.y;
            this.guardianEye.rotationPointY = d0 > 0.0 ? 0.0f : 1.0f;
            Vector3d vector3d2 = entityIn.getLook(0.0f);
            vector3d2 = new Vector3d(vector3d2.x, 0.0, vector3d2.z);
            Vector3d vector3d3 = new Vector3d(vector3d1.x - vector3d.x, 0.0, vector3d1.z - vector3d.z).normalize().rotateYaw(1.5707964f);
            double d1 = vector3d2.dotProduct(vector3d3);
            this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(d1)) * 2.0f * (float)Math.signum(d1);
        }
        this.guardianEye.showModel = true;
        float f2 = entityIn.getTailAnimation(f);
        this.guardianTail[0].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.05f;
        this.guardianTail[1].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.1f;
        this.guardianTail[1].rotationPointX = -1.5f;
        this.guardianTail[1].rotationPointY = 0.5f;
        this.guardianTail[1].rotationPointZ = 14.0f;
        this.guardianTail[2].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.15f;
        this.guardianTail[2].rotationPointX = 0.5f;
        this.guardianTail[2].rotationPointY = 0.5f;
        this.guardianTail[2].rotationPointZ = 6.0f;
    }

    private void func_228261_a_(float p_228261_1_, float p_228261_2_) {
        for (int i = 0; i < 12; ++i) {
            this.guardianSpines[i].rotateAngleX = (float)Math.PI * field_217136_a[i];
            this.guardianSpines[i].rotateAngleY = (float)Math.PI * field_217137_b[i];
            this.guardianSpines[i].rotateAngleZ = (float)Math.PI * field_217138_f[i];
            this.guardianSpines[i].rotationPointX = field_217139_g[i] * (1.0f + MathHelper.cos(p_228261_1_ * 1.5f + (float)i) * 0.01f - p_228261_2_);
            this.guardianSpines[i].rotationPointY = 16.0f + field_217140_h[i] * (1.0f + MathHelper.cos(p_228261_1_ * 1.5f + (float)i) * 0.01f - p_228261_2_);
            this.guardianSpines[i].rotationPointZ = field_217141_i[i] * (1.0f + MathHelper.cos(p_228261_1_ * 1.5f + (float)i) * 0.01f - p_228261_2_);
        }
    }
}
