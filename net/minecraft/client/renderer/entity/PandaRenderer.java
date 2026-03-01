package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PandaHeldItemLayer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class PandaRenderer
extends MobRenderer<PandaEntity, PandaModel<PandaEntity>> {
    private static final Map<PandaEntity.Gene, ResourceLocation> field_217777_a = Util.make(Maps.newEnumMap(PandaEntity.Gene.class), p_217776_0_ -> {
        p_217776_0_.put(PandaEntity.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
        p_217776_0_.put(PandaEntity.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
        p_217776_0_.put(PandaEntity.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
        p_217776_0_.put(PandaEntity.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
        p_217776_0_.put(PandaEntity.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
        p_217776_0_.put(PandaEntity.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
        p_217776_0_.put(PandaEntity.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
    });

    public PandaRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PandaModel(9, 0.0f), 0.9f);
        this.addLayer(new PandaHeldItemLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(PandaEntity entity) {
        return field_217777_a.getOrDefault((Object)entity.func_213590_ei(), field_217777_a.get((Object)PandaEntity.Gene.NORMAL));
    }

    @Override
    protected void applyRotations(PandaEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        float f8;
        float f6;
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        if (entityLiving.rollCounter > 0) {
            float f1;
            int i = entityLiving.rollCounter;
            int j = i + 1;
            float f = 7.0f;
            float f2 = f1 = entityLiving.isChild() ? 0.3f : 0.8f;
            if (i < 8) {
                float f3 = (float)(90 * i) / 7.0f;
                float f4 = (float)(90 * j) / 7.0f;
                float f22 = this.func_217775_a(f3, f4, j, partialTicks, 8.0f);
                matrixStackIn.translate(0.0, (f1 + 0.2f) * (f22 / 90.0f), 0.0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f22));
            } else if (i < 16) {
                float f13 = ((float)i - 8.0f) / 7.0f;
                float f16 = 90.0f + 90.0f * f13;
                float f5 = 90.0f + 90.0f * ((float)j - 8.0f) / 7.0f;
                float f10 = this.func_217775_a(f16, f5, j, partialTicks, 16.0f);
                matrixStackIn.translate(0.0, f1 + 0.2f + (f1 - 0.2f) * (f10 - 90.0f) / 90.0f, 0.0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f10));
            } else if ((float)i < 24.0f) {
                float f14 = ((float)i - 16.0f) / 7.0f;
                float f17 = 180.0f + 90.0f * f14;
                float f19 = 180.0f + 90.0f * ((float)j - 16.0f) / 7.0f;
                float f11 = this.func_217775_a(f17, f19, j, partialTicks, 24.0f);
                matrixStackIn.translate(0.0, f1 + f1 * (270.0f - f11) / 90.0f, 0.0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f11));
            } else if (i < 32) {
                float f15 = ((float)i - 24.0f) / 7.0f;
                float f18 = 270.0f + 90.0f * f15;
                float f20 = 270.0f + 90.0f * ((float)j - 24.0f) / 7.0f;
                float f12 = this.func_217775_a(f18, f20, j, partialTicks, 32.0f);
                matrixStackIn.translate(0.0, f1 * ((360.0f - f12) / 90.0f), 0.0);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-f12));
            }
        }
        if ((f6 = entityLiving.func_213561_v(partialTicks)) > 0.0f) {
            matrixStackIn.translate(0.0, 0.8f * f6, 0.0);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(f6, entityLiving.rotationPitch, entityLiving.rotationPitch + 90.0f)));
            matrixStackIn.translate(0.0, -1.0f * f6, 0.0);
            if (entityLiving.func_213566_eo()) {
                float f7 = (float)(Math.cos((double)entityLiving.ticksExisted * 1.25) * Math.PI * (double)0.05f);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f7));
                if (entityLiving.isChild()) {
                    matrixStackIn.translate(0.0, 0.8f, 0.55f);
                }
            }
        }
        if ((f8 = entityLiving.func_213583_w(partialTicks)) > 0.0f) {
            float f9 = entityLiving.isChild() ? 0.5f : 1.3f;
            matrixStackIn.translate(0.0, f9 * f8, 0.0);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.lerp(f8, entityLiving.rotationPitch, entityLiving.rotationPitch + 180.0f)));
        }
    }

    private float func_217775_a(float p_217775_1_, float p_217775_2_, int p_217775_3_, float p_217775_4_, float p_217775_5_) {
        return (float)p_217775_3_ < p_217775_5_ ? MathHelper.lerp(p_217775_4_, p_217775_1_, p_217775_2_) : p_217775_1_;
    }
}
