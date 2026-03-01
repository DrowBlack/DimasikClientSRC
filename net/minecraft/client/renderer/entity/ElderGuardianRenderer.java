package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;

public class ElderGuardianRenderer
extends GuardianRenderer {
    public static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");

    public ElderGuardianRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, 1.2f);
    }

    @Override
    protected void preRenderCallback(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(ElderGuardianEntity.field_213629_b, ElderGuardianEntity.field_213629_b, ElderGuardianEntity.field_213629_b);
    }

    @Override
    public ResourceLocation getEntityTexture(GuardianEntity entity) {
        return GUARDIAN_ELDER_TEXTURE;
    }
}
