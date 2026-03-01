package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.player.EventElytra;
import dimasik.events.main.player.EventPlayerRender;
import dimasik.events.main.render.EventNameRender;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.mods.cape.wavecapes.renderlayers.CustomCapeRenderLayer;
import dimasik.modules.render.CustomModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PlayerRenderer
extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    public static final ResourceLocation RABBIT_TEXTURE = new ResourceLocation("main/textures/images/rabbit.png");
    public static final ResourceLocation DEMON_TEXTURE = new ResourceLocation("main/textures/images/demon.png");
    public static final ResourceLocation DEMON_TEXTURE_WHITE = new ResourceLocation("main/textures/images/whitedemon.png");
    public static final ResourceLocation JEFF_TEXTURE = new ResourceLocation("main/textures/images/jeff.png");
    public static final ResourceLocation FREDDY_TEXTURE = new ResourceLocation("main/textures/images/freddy.png");
    public static final ResourceLocation CHINCHILLA_TEXTURE = new ResourceLocation("main/textures/images/chinchilla.png");

    public PlayerRenderer(EntityRendererManager renderManager) {
        this(renderManager, false);
    }

    public PlayerRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
        super(renderManager, new PlayerModel(0.0f, useSmallArms), 0.5f);
        this.addLayer(new BipedArmorLayer(this, new BipedModel(0.5f), new BipedModel(1.0f)));
        this.addLayer(new HeldItemLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(this));
        this.addLayer(new ArrowLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(this));
        this.addLayer(new Deadmau5HeadLayer(this));
        this.addLayer(new HeadLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(this));
        this.addLayer(new CustomCapeRenderLayer(this));
        this.addLayer(new ElytraLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(this));
        this.addLayer(new ParrotVariantLayer<AbstractClientPlayerEntity>(this));
        this.addLayer(new SpinAttackEffectLayer<AbstractClientPlayerEntity>(this));
        this.addLayer(new BeeStingerLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        this.setModelVisibilities(entityIn);
        Load.getInstance().getEvents().call(new EventPlayerRender.Pre(this, bufferIn, entityIn));
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        Load.getInstance().getEvents().call(new EventPlayerRender.Post(this, bufferIn, entityIn));
    }

    @Override
    public Vector3d getRenderOffset(AbstractClientPlayerEntity entityIn, float partialTicks) {
        return entityIn.isCrouching() ? new Vector3d(0.0, -0.125, 0.0) : super.getRenderOffset(entityIn, partialTicks);
    }

    private void setModelVisibilities(AbstractClientPlayerEntity clientPlayer) {
        PlayerModel playermodel = (PlayerModel)this.getEntityModel();
        if (clientPlayer.isSpectator()) {
            playermodel.setVisible(false);
            playermodel.bipedHead.showModel = true;
            playermodel.bipedHeadwear.showModel = true;
        } else {
            playermodel.setVisible(true);
            playermodel.bipedHeadwear.showModel = clientPlayer.isWearing(PlayerModelPart.HAT);
            playermodel.bipedBodyWear.showModel = clientPlayer.isWearing(PlayerModelPart.JACKET);
            playermodel.bipedLeftLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.bipedRightLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.bipedLeftArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_SLEEVE);
            playermodel.bipedRightArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.isSneak = clientPlayer.isCrouching();
            BipedModel.ArmPose bipedmodel$armpose = PlayerRenderer.func_241741_a_(clientPlayer, Hand.MAIN_HAND);
            BipedModel.ArmPose bipedmodel$armpose1 = PlayerRenderer.func_241741_a_(clientPlayer, Hand.OFF_HAND);
            if (bipedmodel$armpose.func_241657_a_()) {
                BipedModel.ArmPose armPose = bipedmodel$armpose1 = clientPlayer.getHeldItemOffhand().isEmpty() ? BipedModel.ArmPose.EMPTY : BipedModel.ArmPose.ITEM;
            }
            if (clientPlayer.getPrimaryHand() == HandSide.RIGHT) {
                playermodel.rightArmPose = bipedmodel$armpose;
                playermodel.leftArmPose = bipedmodel$armpose1;
            } else {
                playermodel.rightArmPose = bipedmodel$armpose1;
                playermodel.leftArmPose = bipedmodel$armpose;
            }
        }
    }

    private static BipedModel.ArmPose func_241741_a_(AbstractClientPlayerEntity p_241741_0_, Hand p_241741_1_) {
        ItemStack itemstack = p_241741_0_.getHeldItem(p_241741_1_);
        if (itemstack.isEmpty()) {
            return BipedModel.ArmPose.EMPTY;
        }
        if (p_241741_0_.getActiveHand() == p_241741_1_ && p_241741_0_.getItemInUseCount() > 0) {
            UseAction useaction = itemstack.getUseAction();
            if (useaction == UseAction.BLOCK) {
                return BipedModel.ArmPose.BLOCK;
            }
            if (useaction == UseAction.BOW) {
                return BipedModel.ArmPose.BOW_AND_ARROW;
            }
            if (useaction == UseAction.SPEAR) {
                return BipedModel.ArmPose.THROW_SPEAR;
            }
            if (useaction == UseAction.CROSSBOW && p_241741_1_ == p_241741_0_.getActiveHand()) {
                return BipedModel.ArmPose.CROSSBOW_CHARGE;
            }
        } else if (!p_241741_0_.isSwingInProgress && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack)) {
            return BipedModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedModel.ArmPose.ITEM;
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayerEntity entity) {
        boolean others;
        CustomModel customModel = Load.getInstance().getHooks().getModuleManagers().getCustomModel();
        boolean self = customModel.getElements().getSelected("Self") && (entity == IFastAccess.mc.player || entity == IFastAccess.mc.world.getEntityByID(1337));
        boolean friend = customModel.getElements().getSelected("Friends") && Load.getInstance().getHooks().getFriendManagers().is(entity.getName().getString());
        boolean bl = others = customModel.getElements().getSelected("Others") && entity != IFastAccess.mc.player && entity != IFastAccess.mc.world.getEntityByID(1337) && !Load.getInstance().getHooks().getFriendManagers().is(entity.getName().getString());
        if (customModel.isToggled() && customModel.getMode().getSelected("Rabbit") && (self || friend || others)) {
            return RABBIT_TEXTURE;
        }
        if (customModel.isToggled() && customModel.getMode().getSelected("Jeff Killer") && (self || friend || others)) {
            return JEFF_TEXTURE;
        }
        if (customModel.isToggled() && customModel.getMode().getSelected("Demon") && (self || friend || others)) {
            return DEMON_TEXTURE;
        }
        if (customModel.isToggled() && customModel.getMode().getSelected("White Demon") && (self || friend || others)) {
            return DEMON_TEXTURE_WHITE;
        }
        if (customModel.isToggled() && customModel.getMode().getSelected("Freddy Bear") && (self || friend || others)) {
            return FREDDY_TEXTURE;
        }
        if (customModel.isToggled() && customModel.getMode().getSelected("Chinchilla") && (self || friend || others)) {
            return CHINCHILLA_TEXTURE;
        }
        return entity.getLocationSkin();
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        float f = 0.9375f;
        matrixStackIn.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderName(AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Scoreboard scoreboard;
        ScoreObjective scoreobjective;
        double d0 = this.renderManager.squareDistanceTo(entityIn);
        matrixStackIn.push();
        EventNameRender eventNameRender = new EventNameRender(EventNameRender.Type.PlayerName);
        Load.getInstance().getEvents().call(eventNameRender);
        if (d0 < 100.0 && (scoreobjective = (scoreboard = entityIn.getWorldScoreboard()).getObjectiveInDisplaySlot(2)) != null && !eventNameRender.isCancelled()) {
            Score score = scoreboard.getOrCreateScore(entityIn.getScoreboardName(), scoreobjective);
            super.renderName(entityIn, new StringTextComponent(Integer.toString(score.getScorePoints())).appendString(" ").append(scoreobjective.getDisplayName()), matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.translate(0.0, 0.25875f, 0.0);
        }
        if (!eventNameRender.isCancelled()) {
            super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        }
        matrixStackIn.pop();
    }

    public void renderRightArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn) {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, ((PlayerModel)this.entityModel).bipedRightArm, ((PlayerModel)this.entityModel).bipedRightArmwear);
    }

    public void renderLeftArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn) {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, ((PlayerModel)this.entityModel).bipedLeftArm, ((PlayerModel)this.entityModel).bipedLeftArmwear);
    }

    private void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn, ModelRenderer rendererArmIn, ModelRenderer rendererArmwearIn) {
        PlayerModel playermodel = (PlayerModel)this.getEntityModel();
        this.setModelVisibilities(playerIn);
        playermodel.swingProgress = 0.0f;
        playermodel.isSneak = false;
        playermodel.swimAnimation = 0.0f;
        playermodel.setRotationAngles(playerIn, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        rendererArmIn.rotateAngleX = 0.0f;
        rendererArmIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(playerIn.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);
        rendererArmwearIn.rotateAngleX = 0.0f;
        rendererArmwearIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(playerIn.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected void applyRotations(AbstractClientPlayerEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        float f = entityLiving.getSwimAnimation(partialTicks);
        if (entityLiving.isElytraFlying()) {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f1 = (float)entityLiving.getTicksElytraFlying() + partialTicks;
            float f2 = MathHelper.clamp(f1 * f1 / 100.0f, 0.0f, 1.0f);
            if (!entityLiving.isSpinAttacking()) {
                if (entityLiving == IFastAccess.mc.player) {
                    EventElytra eventElytra = new EventElytra(entityLiving.rotationPitch);
                    Load.getInstance().getEvents().call(eventElytra);
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f2 * (-90.0f - eventElytra.getVisualPitch())));
                } else {
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f2 * (-90.0f - entityLiving.rotationPitch)));
                }
            }
            Vector3d vector3d = entityLiving.getLook(partialTicks);
            Vector3d vector3d1 = entityLiving.getMotion();
            double d0 = Entity.horizontalMag(vector3d1);
            double d1 = Entity.horizontalMag(vector3d);
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                matrixStackIn.rotate(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0f) {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f3 = entityLiving.isInWater() ? -90.0f - entityLiving.rotationPitch : -90.0f;
            float f4 = MathHelper.lerp(f, 0.0f, f3);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f4));
            if (entityLiving.isActualySwimming()) {
                matrixStackIn.translate(0.0, -1.0, 0.3f);
            }
        } else {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }
}
