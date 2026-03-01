package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.optifine.Config;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements IEntityRenderer<T, M> {
    private static final Logger LOGGER = LogManager.getLogger();
    public M entityModel;
    protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();
    public LivingEntity renderEntity;
    public float renderLimbSwing;
    public float renderLimbSwingAmount;
    public float renderAgeInTicks;
    public float renderHeadYaw;
    public float renderHeadPitch;
    public float renderPartialTicks;
    public static final boolean animateModelLiving = Boolean.getBoolean("animate.model.living");

    public LivingRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
        super(rendererManager);
        this.entityModel = entityModelIn;
        this.shadowSize = shadowSizeIn;
    }

    public final boolean addLayer(LayerRenderer<T, M> layer) {
        return this.layerRenderers.add(layer);
    }

    @Override
    public M getEntityModel() {
        return this.entityModel;
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (!Reflector.RenderLivingEvent_Pre_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, entityIn, this, Float.valueOf(partialTicks), matrixStackIn, bufferIn, packedLightIn)) {
            boolean flag3;
            boolean flag1;
            Direction direction;
            float f7;
            if (animateModelLiving) {
                ((LivingEntity)entityIn).limbSwingAmount = 1.0f;
            }
            matrixStackIn.push();
            ((EntityModel)this.entityModel).swingProgress = this.getSwingProgress(entityIn, partialTicks);
            ((EntityModel)this.entityModel).isSitting = ((Entity)entityIn).isPassenger();
            if (Reflector.IForgeEntity_shouldRiderSit.exists()) {
                ((EntityModel)this.entityModel).isSitting = ((Entity)entityIn).isPassenger() && ((Entity)entityIn).getRidingEntity() != null && Reflector.callBoolean(((Entity)entityIn).getRidingEntity(), Reflector.IForgeEntity_shouldRiderSit, new Object[0]);
            }
            ((EntityModel)this.entityModel).isChild = ((LivingEntity)entityIn).isChild();
            float f = MathHelper.interpolateAngle(partialTicks, ((LivingEntity)entityIn).prevRenderYawOffset, ((LivingEntity)entityIn).renderYawOffset);
            float f1 = MathHelper.interpolateAngle(partialTicks, ((LivingEntity)entityIn).prevRotationYawHead, ((LivingEntity)entityIn).rotationYawHead);
            float f2 = f1 - f;
            if (((EntityModel)this.entityModel).isSitting && ((Entity)entityIn).getRidingEntity() instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)((Entity)entityIn).getRidingEntity();
                f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
                f2 = f1 - f;
                float f3 = MathHelper.wrapDegrees(f2);
                if (f3 < -85.0f) {
                    f3 = -85.0f;
                }
                if (f3 >= 85.0f) {
                    f3 = 85.0f;
                }
                f = f1 - f3;
                if (f3 * f3 > 2500.0f) {
                    f += f3 * 0.2f;
                }
                f2 = f1 - f;
            }
            float f3 = f7 = entityIn == Minecraft.getInstance().getRenderViewEntity() ? MathHelper.lerp(partialTicks, ((LivingEntity)entityIn).prevRotationPitchHead, ((LivingEntity)entityIn).rotationPitchHead) : MathHelper.lerp(partialTicks, ((LivingEntity)entityIn).prevRotationPitch, ((LivingEntity)entityIn).rotationPitch);
            if (((Entity)entityIn).getPose() == Pose.SLEEPING && (direction = ((LivingEntity)entityIn).getBedDirection()) != null) {
                float f4 = ((Entity)entityIn).getEyeHeight(Pose.STANDING) - 0.1f;
                matrixStackIn.translate((float)(-direction.getXOffset()) * f4, 0.0, (float)(-direction.getZOffset()) * f4);
            }
            float f8 = this.handleRotationFloat(entityIn, partialTicks);
            this.applyRotations(entityIn, matrixStackIn, f8, f, partialTicks);
            matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
            this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0.0, -1.501f, 0.0);
            float f9 = 0.0f;
            float f5 = 0.0f;
            if (!((Entity)entityIn).isPassenger() && ((LivingEntity)entityIn).isAlive()) {
                f9 = MathHelper.lerp(partialTicks, ((LivingEntity)entityIn).prevLimbSwingAmount, ((LivingEntity)entityIn).limbSwingAmount);
                f5 = ((LivingEntity)entityIn).limbSwing - ((LivingEntity)entityIn).limbSwingAmount * (1.0f - partialTicks);
                if (((LivingEntity)entityIn).isChild()) {
                    f5 *= 3.0f;
                }
                if (f9 > 1.0f) {
                    f9 = 1.0f;
                }
            }
            ((EntityModel)this.entityModel).setLivingAnimations(entityIn, f5, f9, partialTicks);
            ((EntityModel)this.entityModel).setRotationAngles(entityIn, f5, f9, f8, f2, f7);
            if (CustomEntityModels.isActive()) {
                this.renderEntity = entityIn;
                this.renderLimbSwing = f5;
                this.renderLimbSwingAmount = f9;
                this.renderAgeInTicks = f8;
                this.renderHeadYaw = f2;
                this.renderHeadPitch = f7;
                this.renderPartialTicks = partialTicks;
            }
            boolean flag = Config.isShaders();
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag2 = !(flag1 = this.isVisible(entityIn)) && !((Entity)entityIn).isInvisibleToPlayer(minecraft.player);
            RenderType rendertype = this.func_230496_a_(entityIn, flag1, flag2, flag3 = minecraft.isEntityGlowing((Entity)entityIn));
            if (rendertype != null) {
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
                float f6 = this.getOverlayProgress(entityIn, partialTicks);
                if (flag) {
                    if (((LivingEntity)entityIn).hurtTime > 0 || ((LivingEntity)entityIn).deathTime > 0) {
                        Shaders.setEntityColor(1.0f, 0.0f, 0.0f, 0.3f);
                    }
                    if (f6 > 0.0f) {
                        Shaders.setEntityColor(f6, f6, f6, 0.5f);
                    }
                }
                int i = LivingRenderer.getPackedOverlay(entityIn, f6);
                ((Model)this.entityModel).render(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0f, 1.0f, 1.0f, flag2 ? 0.15f : 1.0f);
            }
            if (!((Entity)entityIn).isSpectator()) {
                for (LayerRenderer<T, M> layerrenderer : this.layerRenderers) {
                    layerrenderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, f5, f9, partialTicks, f8, f2, f7);
                }
            }
            if (Config.isShaders()) {
                Shaders.setEntityColor(0.0f, 0.0f, 0.0f, 0.0f);
            }
            if (CustomEntityModels.isActive()) {
                this.renderEntity = null;
            }
            matrixStackIn.pop();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            if (Reflector.RenderLivingEvent_Post_Constructor.exists()) {
                Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, entityIn, this, Float.valueOf(partialTicks), matrixStackIn, bufferIn, packedLightIn);
            }
        }
    }

    @Nullable
    protected RenderType func_230496_a_(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        ResourceLocation resourcelocation = this.getEntityTexture(p_230496_1_);
        if (this.getLocationTextureCustom() != null) {
            resourcelocation = this.getLocationTextureCustom();
        }
        if (p_230496_3_) {
            return RenderType.getItemEntityTranslucentCull(resourcelocation);
        }
        if (p_230496_2_) {
            return ((Model)this.entityModel).getRenderType(resourcelocation);
        }
        if (((Entity)p_230496_1_).isGlowing() && !Config.getMinecraft().worldRenderer.isRenderEntityOutlines()) {
            return ((Model)this.entityModel).getRenderType(resourcelocation);
        }
        return p_230496_4_ ? RenderType.getOutline(resourcelocation) : null;
    }

    public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
        return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
    }

    protected boolean isVisible(T livingEntityIn) {
        return !((Entity)livingEntityIn).isInvisible();
    }

    private static float getFacingAngle(Direction facingIn) {
        switch (facingIn) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean func_230495_a_(T p_230495_1_) {
        return false;
    }

    protected void applyRotations(T entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        String s;
        Pose pose;
        if (this.func_230495_a_(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)((LivingEntity)entityLiving).ticksExisted * 3.25) * Math.PI * (double)0.4f);
        }
        if ((pose = ((Entity)entityLiving).getPose()) != Pose.SLEEPING) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f - rotationYaw));
        }
        if (((LivingEntity)entityLiving).deathTime > 0) {
            float f = ((float)((LivingEntity)entityLiving).deathTime + partialTicks - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
        } else if (((LivingEntity)entityLiving).isSpinAttacking()) {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0f - ((LivingEntity)entityLiving).rotationPitch));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(((float)((LivingEntity)entityLiving).ticksExisted + partialTicks) * -75.0f));
        } else if (pose == Pose.SLEEPING) {
            Direction direction = ((LivingEntity)entityLiving).getBedDirection();
            float f1 = direction != null ? LivingRenderer.getFacingAngle(direction) : rotationYaw;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0f));
        } else if ((((Entity)entityLiving).hasCustomName() || entityLiving instanceof PlayerEntity) && ("Dinnerbone".equals(s = TextFormatting.getTextWithoutFormattingCodes(((Entity)entityLiving).getName().getString())) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity)entityLiving).isWearing(PlayerModelPart.CAPE))) {
            matrixStackIn.translate(0.0, ((Entity)entityLiving).getHeight() + 0.1f, 0.0);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
        }
    }

    protected float getSwingProgress(T livingBase, float partialTickTime) {
        return ((LivingEntity)livingBase).getSwingProgress(partialTickTime);
    }

    protected float handleRotationFloat(T livingBase, float partialTicks) {
        return (float)((LivingEntity)livingBase).ticksExisted + partialTicks;
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 90.0f;
    }

    protected float getOverlayProgress(T livingEntityIn, float partialTicks) {
        return 0.0f;
    }

    protected void preRenderCallback(T entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
    }

    @Override
    protected boolean canRenderName(T entity) {
        boolean flag;
        float f;
        double d0 = this.renderManager.squareDistanceTo((Entity)entity);
        float f2 = f = ((Entity)entity).isDiscrete() ? 32.0f : 64.0f;
        if (d0 >= (double)(f * f)) {
            return false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity clientplayerentity = minecraft.player;
        boolean bl = flag = !((Entity)entity).isInvisibleToPlayer(clientplayerentity);
        if (entity != clientplayerentity) {
            Team team = ((Entity)entity).getTeam();
            Team team1 = clientplayerentity.getTeam();
            if (team != null) {
                Team.Visible team$visible = team.getNameTagVisibility();
                switch (team$visible) {
                    case ALWAYS: {
                        return flag;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    }
                }
                return true;
            }
        }
        return Minecraft.isGuiEnabled() && entity != minecraft.getRenderViewEntity() && flag && !((Entity)entity).isBeingRidden();
    }

    public List<LayerRenderer<T, M>> getLayerRenderers() {
        return this.layerRenderers;
    }
}
