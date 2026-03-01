package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class EnderDragonRenderer
extends EntityRenderer<EnderDragonEntity> {
    public static final ResourceLocation ENDERCRYSTAL_BEAM_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation field_229052_g_ = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType field_229053_h_ = RenderType.getEntityCutoutNoCull(DRAGON_TEXTURES);
    private static final RenderType field_229054_i_ = RenderType.getEntityDecal(DRAGON_TEXTURES);
    private static final RenderType field_229055_j_ = RenderType.getEyes(field_229052_g_);
    private static final RenderType field_229056_k_ = RenderType.getEntitySmoothCutout(ENDERCRYSTAL_BEAM_TEXTURES);
    private static final float field_229057_l_ = (float)(Math.sqrt(3.0) / 2.0);
    private final EnderDragonModel model = new EnderDragonModel();

    public EnderDragonRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5f;
    }

    @Override
    public void render(EnderDragonEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        float f = (float)entityIn.getMovementOffsets(7, partialTicks)[0];
        float f1 = (float)(entityIn.getMovementOffsets(5, partialTicks)[1] - entityIn.getMovementOffsets(10, partialTicks)[1]);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1 * 10.0f));
        matrixStackIn.translate(0.0, 0.0, 1.0);
        matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
        matrixStackIn.translate(0.0, -1.501f, 0.0);
        boolean flag = entityIn.hurtTime > 0;
        this.model.setLivingAnimations(entityIn, 0.0f, 0.0f, partialTicks);
        if (entityIn.deathTicks > 0) {
            float f2 = (float)entityIn.deathTicks / 200.0f;
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityAlpha(DRAGON_EXPLODING_TEXTURES, f2));
            this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(field_229054_i_);
            this.model.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.getPackedUV(0.0f, flag), 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            IVertexBuilder ivertexbuilder3 = bufferIn.getBuffer(field_229053_h_);
            this.model.render(matrixStackIn, ivertexbuilder3, packedLightIn, OverlayTexture.getPackedUV(0.0f, flag), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        IVertexBuilder ivertexbuilder4 = bufferIn.getBuffer(field_229055_j_);
        if (Config.isShaders()) {
            Shaders.beginSpiderEyes();
        }
        Config.getRenderGlobal().renderOverlayEyes = true;
        this.model.render(matrixStackIn, ivertexbuilder4, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        Config.getRenderGlobal().renderOverlayEyes = false;
        if (Config.isShaders()) {
            Shaders.endSpiderEyes();
        }
        if (entityIn.deathTicks > 0) {
            float f5 = ((float)entityIn.deathTicks + partialTicks) / 200.0f;
            float f7 = Math.min(f5 > 0.8f ? (f5 - 0.8f) / 0.2f : 0.0f, 1.0f);
            Random random = new Random(432L);
            IVertexBuilder ivertexbuilder2 = bufferIn.getBuffer(RenderType.getLightning());
            matrixStackIn.push();
            matrixStackIn.translate(0.0, -1.0, -2.0);
            int i = 0;
            while ((float)i < (f5 + f5 * f5) / 2.0f * 60.0f) {
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f + f5 * 90.0f));
                float f3 = random.nextFloat() * 20.0f + 5.0f + f7 * 10.0f;
                float f4 = random.nextFloat() * 2.0f + 1.0f + f7 * 2.0f;
                Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
                int j = (int)(255.0f * (1.0f - f7));
                EnderDragonRenderer.func_229061_a_(ivertexbuilder2, matrix4f, j);
                EnderDragonRenderer.func_229060_a_(ivertexbuilder2, matrix4f, f3, f4);
                EnderDragonRenderer.func_229062_b_(ivertexbuilder2, matrix4f, f3, f4);
                EnderDragonRenderer.func_229061_a_(ivertexbuilder2, matrix4f, j);
                EnderDragonRenderer.func_229062_b_(ivertexbuilder2, matrix4f, f3, f4);
                EnderDragonRenderer.func_229063_c_(ivertexbuilder2, matrix4f, f3, f4);
                EnderDragonRenderer.func_229061_a_(ivertexbuilder2, matrix4f, j);
                EnderDragonRenderer.func_229063_c_(ivertexbuilder2, matrix4f, f3, f4);
                EnderDragonRenderer.func_229060_a_(ivertexbuilder2, matrix4f, f3, f4);
                ++i;
            }
            matrixStackIn.pop();
        }
        matrixStackIn.pop();
        if (entityIn.closestEnderCrystal != null) {
            matrixStackIn.push();
            float f6 = (float)(entityIn.closestEnderCrystal.getPosX() - MathHelper.lerp((double)partialTicks, entityIn.prevPosX, entityIn.getPosX()));
            float f8 = (float)(entityIn.closestEnderCrystal.getPosY() - MathHelper.lerp((double)partialTicks, entityIn.prevPosY, entityIn.getPosY()));
            float f9 = (float)(entityIn.closestEnderCrystal.getPosZ() - MathHelper.lerp((double)partialTicks, entityIn.prevPosZ, entityIn.getPosZ()));
            EnderDragonRenderer.func_229059_a_(f6, f8 + EnderCrystalRenderer.func_229051_a_(entityIn.closestEnderCrystal, partialTicks), f9, partialTicks, entityIn.ticksExisted, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void func_229061_a_(IVertexBuilder p_229061_0_, Matrix4f p_229061_1_, int p_229061_2_) {
        p_229061_0_.pos(p_229061_1_, 0.0f, 0.0f, 0.0f).color(255, 255, 255, p_229061_2_).endVertex();
        p_229061_0_.pos(p_229061_1_, 0.0f, 0.0f, 0.0f).color(255, 255, 255, p_229061_2_).endVertex();
    }

    private static void func_229060_a_(IVertexBuilder p_229060_0_, Matrix4f p_229060_1_, float p_229060_2_, float p_229060_3_) {
        p_229060_0_.pos(p_229060_1_, -field_229057_l_ * p_229060_3_, p_229060_2_, -0.5f * p_229060_3_).color(255, 0, 255, 0).endVertex();
    }

    private static void func_229062_b_(IVertexBuilder p_229062_0_, Matrix4f p_229062_1_, float p_229062_2_, float p_229062_3_) {
        p_229062_0_.pos(p_229062_1_, field_229057_l_ * p_229062_3_, p_229062_2_, -0.5f * p_229062_3_).color(255, 0, 255, 0).endVertex();
    }

    private static void func_229063_c_(IVertexBuilder p_229063_0_, Matrix4f p_229063_1_, float p_229063_2_, float p_229063_3_) {
        p_229063_0_.pos(p_229063_1_, 0.0f, p_229063_2_, 1.0f * p_229063_3_).color(255, 0, 255, 0).endVertex();
    }

    public static void func_229059_a_(float p_229059_0_, float p_229059_1_, float p_229059_2_, float p_229059_3_, int p_229059_4_, MatrixStack p_229059_5_, IRenderTypeBuffer p_229059_6_, int p_229059_7_) {
        float f = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_2_ * p_229059_2_);
        float f1 = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_1_ * p_229059_1_ + p_229059_2_ * p_229059_2_);
        p_229059_5_.push();
        p_229059_5_.translate(0.0, 2.0, 0.0);
        p_229059_5_.rotate(Vector3f.YP.rotation((float)(-Math.atan2(p_229059_2_, p_229059_0_)) - 1.5707964f));
        p_229059_5_.rotate(Vector3f.XP.rotation((float)(-Math.atan2(f, p_229059_1_)) - 1.5707964f));
        IVertexBuilder ivertexbuilder = p_229059_6_.getBuffer(field_229056_k_);
        float f2 = 0.0f - ((float)p_229059_4_ + p_229059_3_) * 0.01f;
        float f3 = MathHelper.sqrt(p_229059_0_ * p_229059_0_ + p_229059_1_ * p_229059_1_ + p_229059_2_ * p_229059_2_) / 32.0f - ((float)p_229059_4_ + p_229059_3_) * 0.01f;
        int i = 8;
        float f4 = 0.0f;
        float f5 = 0.75f;
        float f6 = 0.0f;
        MatrixStack.Entry matrixstack$entry = p_229059_5_.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        for (int j = 1; j <= 8; ++j) {
            float f7 = MathHelper.sin((float)j * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float f8 = MathHelper.cos((float)j * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float f9 = (float)j / 8.0f;
            ivertexbuilder.pos(matrix4f, f4 * 0.2f, f5 * 0.2f, 0.0f).color(0, 0, 0, 255).tex(f6, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229059_7_).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            ivertexbuilder.pos(matrix4f, f4, f5, f1).color(255, 255, 255, 255).tex(f6, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229059_7_).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            ivertexbuilder.pos(matrix4f, f7, f8, f1).color(255, 255, 255, 255).tex(f9, f3).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229059_7_).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            ivertexbuilder.pos(matrix4f, f7 * 0.2f, f8 * 0.2f, 0.0f).color(0, 0, 0, 255).tex(f9, f2).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229059_7_).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }
        p_229059_5_.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EnderDragonEntity entity) {
        return DRAGON_TEXTURES;
    }

    public static class EnderDragonModel
    extends EntityModel<EnderDragonEntity> {
        private final ModelRenderer head;
        private final ModelRenderer spine;
        private final ModelRenderer jaw;
        private final ModelRenderer body;
        private ModelRenderer leftProximalWing;
        private ModelRenderer leftDistalWing;
        private ModelRenderer leftForeThigh;
        private ModelRenderer leftForeLeg;
        private ModelRenderer leftForeFoot;
        private ModelRenderer leftHindThigh;
        private ModelRenderer leftHindLeg;
        private ModelRenderer leftHindFoot;
        private ModelRenderer rightProximalWing;
        private ModelRenderer rightDistalWing;
        private ModelRenderer rightForeThigh;
        private ModelRenderer rightForeLeg;
        private ModelRenderer rightForeFoot;
        private ModelRenderer rightHindThigh;
        private ModelRenderer rightHindLeg;
        private ModelRenderer rightHindFoot;
        @Nullable
        private EnderDragonEntity dragonInstance;
        private float partialTicks;

        public EnderDragonModel() {
            this.textureWidth = 256;
            this.textureHeight = 256;
            float f = -16.0f;
            this.head = new ModelRenderer(this);
            this.head.addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 0.0f, 176, 44);
            this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 0.0f, 112, 30);
            this.head.mirror = true;
            this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.head.mirror = false;
            this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.jaw = new ModelRenderer(this);
            this.jaw.setRotationPoint(0.0f, 4.0f, -8.0f);
            this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 0.0f, 176, 65);
            this.head.addChild(this.jaw);
            this.spine = new ModelRenderer(this);
            this.spine.addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 0.0f, 192, 104);
            this.spine.addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 0.0f, 48, 0);
            this.body = new ModelRenderer(this);
            this.body.setRotationPoint(0.0f, 4.0f, 8.0f);
            this.body.addBox("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, 0.0f, 0, 0);
            this.body.addBox("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addBox("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addBox("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, 0.0f, 220, 53);
            this.leftProximalWing = new ModelRenderer(this);
            this.leftProximalWing.mirror = true;
            this.leftProximalWing.setRotationPoint(12.0f, 5.0f, 2.0f);
            this.leftProximalWing.addBox("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.leftProximalWing.addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.leftDistalWing = new ModelRenderer(this);
            this.leftDistalWing.mirror = true;
            this.leftDistalWing.setRotationPoint(56.0f, 0.0f, 0.0f);
            this.leftDistalWing.addBox("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.leftDistalWing.addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.leftProximalWing.addChild(this.leftDistalWing);
            this.leftForeThigh = new ModelRenderer(this);
            this.leftForeThigh.setRotationPoint(12.0f, 20.0f, 2.0f);
            this.leftForeThigh.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.leftForeLeg = new ModelRenderer(this);
            this.leftForeLeg.setRotationPoint(0.0f, 20.0f, -1.0f);
            this.leftForeLeg.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.leftForeThigh.addChild(this.leftForeLeg);
            this.leftForeFoot = new ModelRenderer(this);
            this.leftForeFoot.setRotationPoint(0.0f, 23.0f, 0.0f);
            this.leftForeFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.leftForeLeg.addChild(this.leftForeFoot);
            this.leftHindThigh = new ModelRenderer(this);
            this.leftHindThigh.setRotationPoint(16.0f, 16.0f, 42.0f);
            this.leftHindThigh.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.leftHindLeg = new ModelRenderer(this);
            this.leftHindLeg.setRotationPoint(0.0f, 32.0f, -4.0f);
            this.leftHindLeg.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.leftHindThigh.addChild(this.leftHindLeg);
            this.leftHindFoot = new ModelRenderer(this);
            this.leftHindFoot.setRotationPoint(0.0f, 31.0f, 4.0f);
            this.leftHindFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.leftHindLeg.addChild(this.leftHindFoot);
            this.rightProximalWing = new ModelRenderer(this);
            this.rightProximalWing.setRotationPoint(-12.0f, 5.0f, 2.0f);
            this.rightProximalWing.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.rightProximalWing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.rightDistalWing = new ModelRenderer(this);
            this.rightDistalWing.setRotationPoint(-56.0f, 0.0f, 0.0f);
            this.rightDistalWing.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.rightDistalWing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.rightProximalWing.addChild(this.rightDistalWing);
            this.rightForeThigh = new ModelRenderer(this);
            this.rightForeThigh.setRotationPoint(-12.0f, 20.0f, 2.0f);
            this.rightForeThigh.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.rightForeLeg = new ModelRenderer(this);
            this.rightForeLeg.setRotationPoint(0.0f, 20.0f, -1.0f);
            this.rightForeLeg.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.rightForeThigh.addChild(this.rightForeLeg);
            this.rightForeFoot = new ModelRenderer(this);
            this.rightForeFoot.setRotationPoint(0.0f, 23.0f, 0.0f);
            this.rightForeFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.rightForeLeg.addChild(this.rightForeFoot);
            this.rightHindThigh = new ModelRenderer(this);
            this.rightHindThigh.setRotationPoint(-16.0f, 16.0f, 42.0f);
            this.rightHindThigh.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.rightHindLeg = new ModelRenderer(this);
            this.rightHindLeg.setRotationPoint(0.0f, 32.0f, -4.0f);
            this.rightHindLeg.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.rightHindThigh.addChild(this.rightHindLeg);
            this.rightHindFoot = new ModelRenderer(this);
            this.rightHindFoot.setRotationPoint(0.0f, 31.0f, 4.0f);
            this.rightHindFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.rightHindLeg.addChild(this.rightHindFoot);
        }

        @Override
        public void setLivingAnimations(EnderDragonEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
            this.dragonInstance = entityIn;
            this.partialTicks = partialTick;
        }

        @Override
        public void setRotationAngles(EnderDragonEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            matrixStackIn.push();
            float f = MathHelper.lerp(this.partialTicks, this.dragonInstance.prevAnimTime, this.dragonInstance.animTime);
            this.jaw.rotateAngleX = (float)(Math.sin(f * ((float)Math.PI * 2)) + 1.0) * 0.2f;
            float f1 = (float)(Math.sin(f * ((float)Math.PI * 2) - 1.0f) + 1.0);
            f1 = (f1 * f1 + f1 * 2.0f) * 0.05f;
            matrixStackIn.translate(0.0, f1 - 2.0f, -3.0);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f1 * 2.0f));
            float f2 = 0.0f;
            float f3 = 20.0f;
            float f4 = -12.0f;
            float f5 = 1.5f;
            double[] adouble = this.dragonInstance.getMovementOffsets(6, this.partialTicks);
            float f6 = MathHelper.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] - this.dragonInstance.getMovementOffsets(10, this.partialTicks)[0]);
            float f7 = MathHelper.rotWrap(this.dragonInstance.getMovementOffsets(5, this.partialTicks)[0] + (double)(f6 / 2.0f));
            float f8 = f * ((float)Math.PI * 2);
            for (int i = 0; i < 5; ++i) {
                double[] adouble1 = this.dragonInstance.getMovementOffsets(5 - i, this.partialTicks);
                float f9 = (float)Math.cos((float)i * 0.45f + f8) * 0.15f;
                this.spine.rotateAngleY = MathHelper.rotWrap(adouble1[0] - adouble[0]) * ((float)Math.PI / 180) * 1.5f;
                this.spine.rotateAngleX = f9 + this.dragonInstance.getHeadPartYOffset(i, adouble, adouble1) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.spine.rotateAngleZ = -MathHelper.rotWrap(adouble1[0] - (double)f7) * ((float)Math.PI / 180) * 1.5f;
                this.spine.rotationPointY = f3;
                this.spine.rotationPointZ = f4;
                this.spine.rotationPointX = f2;
                f3 = (float)((double)f3 + Math.sin(this.spine.rotateAngleX) * 10.0);
                f4 = (float)((double)f4 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
                f2 = (float)((double)f2 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
                this.spine.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }
            this.head.rotationPointY = f3;
            this.head.rotationPointZ = f4;
            this.head.rotationPointX = f2;
            double[] adouble2 = this.dragonInstance.getMovementOffsets(0, this.partialTicks);
            this.head.rotateAngleY = MathHelper.rotWrap(adouble2[0] - adouble[0]) * ((float)Math.PI / 180);
            this.head.rotateAngleX = MathHelper.rotWrap(this.dragonInstance.getHeadPartYOffset(6, adouble, adouble2)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.rotateAngleZ = -MathHelper.rotWrap(adouble2[0] - (double)f7) * ((float)Math.PI / 180);
            this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            matrixStackIn.push();
            matrixStackIn.translate(0.0, 1.0, 0.0);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-f6 * 1.5f));
            matrixStackIn.translate(0.0, -1.0, 0.0);
            this.body.rotateAngleZ = 0.0f;
            this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            float f10 = f * ((float)Math.PI * 2);
            this.leftProximalWing.rotateAngleX = 0.125f - (float)Math.cos(f10) * 0.2f;
            this.leftProximalWing.rotateAngleY = -0.25f;
            this.leftProximalWing.rotateAngleZ = -((float)(Math.sin(f10) + 0.125)) * 0.8f;
            this.leftDistalWing.rotateAngleZ = (float)(Math.sin(f10 + 2.0f) + 0.5) * 0.75f;
            this.rightProximalWing.rotateAngleX = this.leftProximalWing.rotateAngleX;
            this.rightProximalWing.rotateAngleY = -this.leftProximalWing.rotateAngleY;
            this.rightProximalWing.rotateAngleZ = -this.leftProximalWing.rotateAngleZ;
            this.rightDistalWing.rotateAngleZ = -this.leftDistalWing.rotateAngleZ;
            this.func_229081_a_(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, f1, this.leftProximalWing, this.leftForeThigh, this.leftForeLeg, this.leftForeFoot, this.leftHindThigh, this.leftHindLeg, this.leftHindFoot);
            this.func_229081_a_(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, f1, this.rightProximalWing, this.rightForeThigh, this.rightForeLeg, this.rightForeFoot, this.rightHindThigh, this.rightHindLeg, this.rightHindFoot);
            matrixStackIn.pop();
            float f11 = -((float)Math.sin(f * ((float)Math.PI * 2))) * 0.0f;
            f8 = f * ((float)Math.PI * 2);
            f3 = 10.0f;
            f4 = 60.0f;
            f2 = 0.0f;
            adouble = this.dragonInstance.getMovementOffsets(11, this.partialTicks);
            for (int j = 0; j < 12; ++j) {
                adouble2 = this.dragonInstance.getMovementOffsets(12 + j, this.partialTicks);
                f11 = (float)((double)f11 + Math.sin((float)j * 0.45f + f8) * (double)0.05f);
                this.spine.rotateAngleY = (MathHelper.rotWrap(adouble2[0] - adouble[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.spine.rotateAngleX = f11 + (float)(adouble2[1] - adouble[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.spine.rotateAngleZ = MathHelper.rotWrap(adouble2[0] - (double)f7) * ((float)Math.PI / 180) * 1.5f;
                this.spine.rotationPointY = f3;
                this.spine.rotationPointZ = f4;
                this.spine.rotationPointX = f2;
                f3 = (float)((double)f3 + Math.sin(this.spine.rotateAngleX) * 10.0);
                f4 = (float)((double)f4 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
                f2 = (float)((double)f2 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
                this.spine.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
            }
            matrixStackIn.pop();
        }

        private void func_229081_a_(MatrixStack p_229081_1_, IVertexBuilder p_229081_2_, int p_229081_3_, int p_229081_4_, float p_229081_5_, ModelRenderer p_229081_6_, ModelRenderer p_229081_7_, ModelRenderer p_229081_8_, ModelRenderer p_229081_9_, ModelRenderer p_229081_10_, ModelRenderer p_229081_11_, ModelRenderer p_229081_12_) {
            p_229081_10_.rotateAngleX = 1.0f + p_229081_5_ * 0.1f;
            p_229081_11_.rotateAngleX = 0.5f + p_229081_5_ * 0.1f;
            p_229081_12_.rotateAngleX = 0.75f + p_229081_5_ * 0.1f;
            p_229081_7_.rotateAngleX = 1.3f + p_229081_5_ * 0.1f;
            p_229081_8_.rotateAngleX = -0.5f - p_229081_5_ * 0.1f;
            p_229081_9_.rotateAngleX = 0.75f + p_229081_5_ * 0.1f;
            p_229081_6_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_7_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
            p_229081_10_.render(p_229081_1_, p_229081_2_, p_229081_3_, p_229081_4_);
        }
    }
}
