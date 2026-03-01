package dimasik.managers.mods.voicechat.voice.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dimasik.Load;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.onboarding.OnboardingManager;
import dimasik.managers.mods.voicechat.intercompatibility.ClientCompatibilityManager;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.GroupChatManager;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;

public class RenderEvents {
    private static final ResourceLocation MICROPHONE_ICON = new ResourceLocation("main/textures/images/icons/microphone.png");
    private static final ResourceLocation WHISPER_MICROPHONE_ICON = new ResourceLocation("main/textures/images/icons/microphone_whisper.png");
    private static final ResourceLocation MICROPHONE_OFF_ICON = new ResourceLocation("main/textures/images/icons/microphone_off.png");
    private static final ResourceLocation SPEAKER_ICON = new ResourceLocation("main/textures/images/icons/speaker.png");
    private static final ResourceLocation WHISPER_SPEAKER_ICON = new ResourceLocation("main/textures/images/icons/speaker_whisper.png");
    private static final ResourceLocation SPEAKER_OFF_ICON = new ResourceLocation("main/textures/images/icons/speaker_off.png");
    private static final ResourceLocation DISCONNECT_ICON = new ResourceLocation("main/textures/images/icons/disconnected.png");
    private static final ResourceLocation GROUP_ICON = new ResourceLocation("main/textures/images/icons/group.png");
    private final Minecraft minecraft = Minecraft.getInstance();

    public RenderEvents() {
        ClientCompatibilityManager.INSTANCE.onRenderNamePlate(this::onRenderName);
        ClientCompatibilityManager.INSTANCE.onRenderHUD(this::onRenderHUD);
    }

    public void onRenderHUD(MatrixStack stack, float tickDelta) {
        if (!this.shouldShowIcons()) {
            return;
        }
        if (VoicechatClient.CLIENT_CONFIG.hideIcons.get().booleanValue()) {
            return;
        }
        if (!Load.getInstance().getHooks().getModuleManagers().getVoiceChat().isToggled()) {
            return;
        }
        ClientPlayerStateManager manager = ClientManager.getPlayerStateManager();
        ClientVoicechat client = ClientManager.getClient();
        if (manager.isDisconnected() && this.isStartup()) {
            return;
        }
        if (manager.isDisconnected()) {
            this.renderIcon(stack, DISCONNECT_ICON);
        } else if (manager.isDisabled()) {
            this.renderIcon(stack, SPEAKER_OFF_ICON);
        } else if (manager.isMuted() && VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals((Object)MicrophoneActivationType.VOICE)) {
            this.renderIcon(stack, MICROPHONE_OFF_ICON);
        } else if (client != null && client.getMicThread() != null) {
            if (client.getMicThread().isWhispering()) {
                this.renderIcon(stack, WHISPER_MICROPHONE_ICON);
            } else if (client.getMicThread().isTalking()) {
                this.renderIcon(stack, MICROPHONE_ICON);
            }
        }
        if (manager.getGroupID() != null && VoicechatClient.CLIENT_CONFIG.showGroupHUD.get().booleanValue()) {
            GroupChatManager.renderIcons(stack);
        }
    }

    private boolean isStartup() {
        ClientVoicechat client = ClientManager.getClient();
        return client != null && System.currentTimeMillis() - client.getStartTime() < 5000L;
    }

    private void renderIcon(MatrixStack matrixStack, ResourceLocation texture) {
        if (!Load.getInstance().getHooks().getModuleManagers().getVoiceChat().isToggled()) {
            return;
        }
        matrixStack.push();
        this.minecraft.getTextureManager().bindTexture(texture);
        int posX = this.minecraft.getMainWindow().getScaledWidth() / 2 - 7;
        int posY = -50;
        if (this.minecraft.currentScreen instanceof ChatScreen) {
            posY -= 10;
        }
        if (posX < 0) {
            matrixStack.translate(this.minecraft.getMainWindow().getScaledWidth(), 0.0, 0.0);
        }
        matrixStack.translate(0.0, this.minecraft.getMainWindow().getScaledHeight(), 0.0);
        matrixStack.translate(posX, posY, 0.0);
        float scale = VoicechatClient.CLIENT_CONFIG.hudIconScale.get().floatValue();
        matrixStack.scale(scale, scale, 1.0f);
        Screen.blit(matrixStack, posX < 0 ? -16 : 0, posY < 0 ? -16 : 0, 0.0f, 0.0f, 16, 16, 16, 16);
        matrixStack.pop();
    }

    public void onRenderName(Entity entity, ITextComponent component, MatrixStack stack, IRenderTypeBuffer vertexConsumers, int light) {
    }

    private void renderPlayerIcon(PlayerEntity player, ITextComponent component, ResourceLocation texture, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int light) {
    }

    public boolean shouldShowIcons() {
        if (OnboardingManager.isOnboarding()) {
            return false;
        }
        if (ClientManager.getClient() != null && ClientManager.getClient().getConnection() != null && ClientManager.getClient().getConnection().isInitialized()) {
            return true;
        }
        return this.minecraft.getIntegratedServer() == null;
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int light) {
        RenderEvents.vertex(builder, matrixStack, x, y, z, u, v, 255, light);
    }

    private static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int alpha, int light) {
        MatrixStack.Entry entry = matrixStack.getLast();
        Matrix4f pose = entry.getMatrix();
        Matrix3f normal = entry.getNormal();
        builder.pos(pose, x, y, z).color(255, 255, 255, alpha).tex(u, v).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(normal, 0.0f, 0.0f, -1.0f).endVertex();
    }
}
