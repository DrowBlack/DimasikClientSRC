package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.Load;
import dimasik.events.main.render.EventGameOverlay;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingManager;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.modules.render.ESP;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LightType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.Either;

public abstract class EntityRenderer<T extends Entity>
implements IEntityRenderer {
    protected final EntityRendererManager renderManager;
    public float shadowSize;
    protected float shadowOpaque = 1.0f;
    private EntityType entityType = null;
    private ResourceLocation locationTextureCustom = null;
    private static final ResourceLocation SPEAKER_ICON = new ResourceLocation("main/textures/images/icons/speaker.png");
    private static final ResourceLocation WHISPER_SPEAKER_ICON = new ResourceLocation("main/textures/images/icons/speaker_whisper.png");
    private static final ResourceLocation SPEAKER_OFF_ICON = new ResourceLocation("main/textures/images/icons/speaker_off.png");
    private static final ResourceLocation DISCONNECT_ICON = new ResourceLocation("main/textures/images/icons/disconnected.png");
    private static final ResourceLocation GROUP_ICON = new ResourceLocation("main/textures/images/icons/group.png");
    protected boolean renderName = true;
    protected boolean renderLayers = true;

    protected EntityRenderer(EntityRendererManager renderManager) {
        this.renderManager = renderManager;
    }

    public final int getPackedLight(T entityIn, float partialTicks) {
        BlockPos blockpos = new BlockPos(((Entity)entityIn).func_241842_k(partialTicks));
        return LightTexture.packLight(this.getBlockLight(entityIn, blockpos), this.func_239381_b_(entityIn, blockpos));
    }

    protected int func_239381_b_(T p_239381_1_, BlockPos p_239381_2_) {
        return ((Entity)p_239381_1_).world.getLightFor(LightType.SKY, p_239381_2_);
    }

    protected int getBlockLight(T entityIn, BlockPos partialTicks) {
        return ((Entity)entityIn).isBurning() ? 15 : ((Entity)entityIn).world.getLightFor(LightType.BLOCK, partialTicks);
    }

    public boolean shouldRender(T livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (!((Entity)livingEntityIn).isInRangeToRender3d(camX, camY, camZ)) {
            return false;
        }
        if (((Entity)livingEntityIn).ignoreFrustumCheck) {
            return true;
        }
        AxisAlignedBB axisalignedbb = ((Entity)livingEntityIn).getRenderBoundingBox().grow(0.5);
        if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0) {
            axisalignedbb = new AxisAlignedBB(((Entity)livingEntityIn).getPosX() - 2.0, ((Entity)livingEntityIn).getPosY() - 2.0, ((Entity)livingEntityIn).getPosZ() - 2.0, ((Entity)livingEntityIn).getPosX() + 2.0, ((Entity)livingEntityIn).getPosY() + 2.0, ((Entity)livingEntityIn).getPosZ() + 2.0);
        }
        return camera.isBoundingBoxInFrustum(axisalignedbb);
    }

    public Vector3d getRenderOffset(T entityIn, float partialTicks) {
        return Vector3d.ZERO;
    }

    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (!Reflector.RenderNameplateEvent_Constructor.exists()) {
            boolean renderer;
            EventGameOverlay eventGameOverlay = new EventGameOverlay(EventGameOverlay.OverlayType.Hologram);
            Load.getInstance().getEvents().call(eventGameOverlay);
            boolean bl = renderer = !eventGameOverlay.isCancelled() || !(entityIn instanceof ArmorStandEntity);
            if (this.canRenderName(entityIn) && renderer) {
                this.renderName(entityIn, ((Entity)entityIn).getDisplayName(), matrixStackIn, bufferIn, packedLightIn);
            }
        } else {
            Object object = Reflector.newInstance(Reflector.RenderNameplateEvent_Constructor, entityIn, ((Entity)entityIn).getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, Float.valueOf(partialTicks));
            Reflector.postForgeBusEvent(object);
            Object object1 = Reflector.call(object, Reflector.Event_getResult, new Object[0]);
            if (object1 != ReflectorForge.EVENT_RESULT_DENY && (object1 == ReflectorForge.EVENT_RESULT_ALLOW || this.canRenderName(entityIn))) {
                ITextComponent itextcomponent = (ITextComponent)Reflector.call(object, Reflector.RenderNameplateEvent_getContent, new Object[0]);
                this.renderName(entityIn, itextcomponent, matrixStackIn, bufferIn, packedLightIn);
            }
        }
    }

    protected boolean canRenderName(T entity) {
        return ((Entity)entity).getAlwaysRenderNameTagForRender() && ((Entity)entity).hasCustomName();
    }

    public abstract ResourceLocation getEntityTexture(T var1);

    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }

    protected void renderName(T entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        boolean flag;
        ESP nameTags = Load.getInstance().getHooks().getModuleManagers().getEsp();
        if (entityIn instanceof PlayerEntity && Load.getInstance().getHooks().getModuleManagers().getVoiceChat().isToggled() && this.shouldShowIcons()) {
            PlayerEntity player = (PlayerEntity)entityIn;
            if (entityIn == Minecraft.getInstance().player) {
                return;
            }
            if (!Minecraft.getInstance().gameSettings.hideGUI) {
                ClientPlayerStateManager manager = ClientManager.getPlayerStateManager();
                ClientVoicechat client = ClientManager.getClient();
                UUID groupId = manager.getGroup(player);
                if (client != null && client.getTalkCache().isWhispering(player)) {
                    this.renderPlayerIcon(player, displayNameIn, WHISPER_SPEAKER_ICON, matrixStackIn, bufferIn, packedLightIn);
                } else if (client != null && client.getTalkCache().isTalking(player)) {
                    this.renderPlayerIcon(player, displayNameIn, SPEAKER_ICON, matrixStackIn, bufferIn, packedLightIn);
                } else if (manager.isPlayerDisconnected(player)) {
                    this.renderPlayerIcon(player, displayNameIn, DISCONNECT_ICON, matrixStackIn, bufferIn, packedLightIn);
                } else if (groupId != null && !groupId.equals(manager.getGroupID())) {
                    this.renderPlayerIcon(player, displayNameIn, GROUP_ICON, matrixStackIn, bufferIn, packedLightIn);
                } else if (manager.isPlayerDisabled(player)) {
                    this.renderPlayerIcon(player, displayNameIn, SPEAKER_OFF_ICON, matrixStackIn, bufferIn, packedLightIn);
                }
            }
        }
        if (nameTags.isToggled() && !(entityIn instanceof ArmorStandEntity)) {
            return;
        }
        double d0 = this.renderManager.squareDistanceTo((Entity)entityIn);
        boolean bl = flag = !(d0 > 4096.0);
        if (Reflector.ForgeHooksClient_isNameplateInRenderDistance.exists()) {
            flag = Reflector.ForgeHooksClient_isNameplateInRenderDistance.callBoolean(entityIn, d0);
        }
        if (flag) {
            boolean flag1 = !((Entity)entityIn).isDiscrete();
            float f = ((Entity)entityIn).getHeight() + 0.5f;
            int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
            matrixStackIn.push();
            matrixStackIn.translate(0.0, f, 0.0);
            matrixStackIn.rotate(this.renderManager.getCameraOrientation());
            matrixStackIn.scale(-0.025f, -0.025f, 0.025f);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25f);
            int j = (int)(f1 * 255.0f) << 24;
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f2 = -fontrenderer.getStringPropertyWidth(displayNameIn) / 2;
            fontrenderer.func_243247_a(displayNameIn, f2, i, 0x20FFFFFF, false, matrix4f, bufferIn, flag1, j, packedLightIn);
            if (flag1) {
                fontrenderer.func_243247_a(displayNameIn, f2, i, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
            }
            matrixStackIn.pop();
        }
    }

    private void renderPlayerIcon(PlayerEntity player, ITextComponent component, ResourceLocation texture, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int light) {
        String text = component.getString().trim();
        if (text.matches("^\\d+$") || text.length() <= 7) {
            return;
        }
        ESP nameTags = Load.getInstance().getHooks().getModuleManagers().getEsp();
        matrixStackIn.push();
        if (nameTags.isToggled()) {
            matrixStackIn.translate(0.0, (double)player.getEyeHeight() + 0.05, 0.0);
        } else {
            matrixStackIn.translate(0.0, (double)player.getEyeHeight() + 0.5, 0.0);
        }
        matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        if (nameTags.isToggled()) {
            matrixStackIn.scale(-0.015f, -0.015f, 0.015f);
        } else {
            matrixStackIn.scale(-0.025f, -0.025f, 0.025f);
        }
        matrixStackIn.translate(0.0, -9.0, 0.0);
        float offset = Minecraft.getInstance().fontRenderer.getStringWidth(component.getString()) / 2 + 7;
        if (nameTags.isToggled()) {
            offset = Minecraft.getInstance().fontRenderer.getStringWidth(component.getString()) / 2;
        }
        IVertexBuilder builder = buffer.getBuffer(RenderType.getText(texture));
        int alpha = 32;
        if (player.isDiscrete()) {
            EntityRenderer.vertex(builder, matrixStackIn, offset, 10.0f, 0.0f, 0.0f, 1.0f, alpha, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset + 10.0f, 10.0f, 0.0f, 1.0f, 1.0f, alpha, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset + 10.0f, 0.0f, 0.0f, 1.0f, 0.0f, alpha, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset, 0.0f, 0.0f, 0.0f, 0.0f, alpha, light);
        } else {
            EntityRenderer.vertex(builder, matrixStackIn, offset, 10.0f, 0.0f, 0.0f, 1.0f, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset + 10.0f, 10.0f, 0.0f, 1.0f, 1.0f, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset + 10.0f, 0.0f, 0.0f, 1.0f, 0.0f, light);
            EntityRenderer.vertex(builder, matrixStackIn, offset, 0.0f, 0.0f, 0.0f, 0.0f, light);
            IVertexBuilder builderSeeThrough = buffer.getBuffer(RenderType.getTextSeeThrough(texture));
            EntityRenderer.vertex(builderSeeThrough, matrixStackIn, offset, 10.0f, 0.0f, 0.0f, 1.0f, alpha, light);
            EntityRenderer.vertex(builderSeeThrough, matrixStackIn, offset + 10.0f, 10.0f, 0.0f, 1.0f, 1.0f, alpha, light);
            EntityRenderer.vertex(builderSeeThrough, matrixStackIn, offset + 10.0f, 0.0f, 0.0f, 1.0f, 0.0f, alpha, light);
            EntityRenderer.vertex(builderSeeThrough, matrixStackIn, offset, 0.0f, 0.0f, 0.0f, 0.0f, alpha, light);
        }
        matrixStackIn.pop();
    }

    private boolean shouldShowIcons() {
        if (OnboardingManager.isOnboarding()) {
            return false;
        }
        if (VoicechatClient.CLIENT_CONFIG.hideIcons.get().booleanValue()) {
            return false;
        }
        if (ClientManager.getClient() != null && ClientManager.getClient().getConnection() != null && ClientManager.getClient().getConnection().isInitialized()) {
            return true;
        }
        return Minecraft.getInstance().getIntegratedServer() == null;
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int light) {
        EntityRenderer.vertex(builder, matrixStack, x, y, z, u, v, 255, light);
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int alpha, int light) {
        MatrixStack.Entry entry = matrixStack.getLast();
        Matrix4f pose = entry.getMatrix();
        Matrix3f normal = entry.getNormal();
        builder.pos(pose, x, y, z).color(255, 255, 255, alpha).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(normal, 0.0f, 0.0f, -1.0f).endVertex();
    }

    public EntityRendererManager getRenderManager() {
        return this.renderManager;
    }

    @Override
    public Either<EntityType, TileEntityType> getType() {
        return this.entityType == null ? null : Either.makeLeft(this.entityType);
    }

    @Override
    public void setType(Either<EntityType, TileEntityType> p_setType_1_) {
        this.entityType = p_setType_1_.getLeft().get();
    }

    @Override
    public ResourceLocation getLocationTextureCustom() {
        return this.locationTextureCustom;
    }

    @Override
    public void setLocationTextureCustom(ResourceLocation p_setLocationTextureCustom_1_) {
        this.locationTextureCustom = p_setLocationTextureCustom_1_;
    }

    @Generated
    public boolean isRenderName() {
        return this.renderName;
    }

    @Generated
    public void setRenderName(boolean renderName) {
        this.renderName = renderName;
    }

    @Generated
    public boolean isRenderLayers() {
        return this.renderLayers;
    }

    @Generated
    public void setRenderLayers(boolean renderLayers) {
        this.renderLayers = renderLayers;
    }
}
