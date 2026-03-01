package dimasik.managers.mods.voicechat.voice.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.GameProfileUtils;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.GroupPlayerIconOrientation;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class GroupChatManager {
    private static final ResourceLocation TALK_OUTLINE = new ResourceLocation("main/textures/images/textures/icons/talk_outline.png");
    private static final ResourceLocation SPEAKER_OFF_ICON = new ResourceLocation("main/textures/images/textures/icons/speaker_small_off.png");

    public static void renderIcons(MatrixStack matrixStack) {
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        List<PlayerState> groupMembers = GroupChatManager.getGroupMembers(VoicechatClient.CLIENT_CONFIG.showOwnGroupIcon.get());
        matrixStack.push();
        int posX = VoicechatClient.CLIENT_CONFIG.groupPlayerIconPosX.get();
        int posY = VoicechatClient.CLIENT_CONFIG.groupPlayerIconPosY.get();
        if (posX < 0) {
            matrixStack.translate(mc.getMainWindow().getScaledWidth(), 0.0, 0.0);
        }
        if (posY < 0) {
            matrixStack.translate(0.0, mc.getMainWindow().getScaledHeight(), 0.0);
        }
        matrixStack.translate(posX, posY, 0.0);
        float scale = VoicechatClient.CLIENT_CONFIG.groupHudIconScale.get().floatValue();
        matrixStack.scale(scale, scale, 1.0f);
        boolean vertical = VoicechatClient.CLIENT_CONFIG.groupPlayerIconOrientation.get().equals((Object)GroupPlayerIconOrientation.VERTICAL);
        for (int i = 0; i < groupMembers.size(); ++i) {
            PlayerState state = groupMembers.get(i);
            matrixStack.push();
            if (vertical) {
                if (posY < 0) {
                    matrixStack.translate(0.0, (double)i * -11.0, 0.0);
                } else {
                    matrixStack.translate(0.0, (double)i * 11.0, 0.0);
                }
            } else if (posX < 0) {
                matrixStack.translate((double)i * -11.0, 0.0, 0.0);
            } else {
                matrixStack.translate((double)i * 11.0, 0.0, 0.0);
            }
            if (client.getTalkCache().isTalking(state.getUuid())) {
                mc.getTextureManager().bindTexture(TALK_OUTLINE);
                Screen.blit(matrixStack, posX < 0 ? -10 : 0, posY < 0 ? -10 : 0, 0.0f, 0.0f, 10, 10, 16, 16);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            mc.getTextureManager().bindTexture(GameProfileUtils.getSkin(state.getUuid()));
            Screen.blit(matrixStack, posX < 0 ? -9 : 1, posY < 0 ? -9 : 1, 8.0f, 8.0f, 8, 8, 64, 64);
            Screen.blit(matrixStack, posX < 0 ? -9 : 1, posY < 0 ? -9 : 1, 40.0f, 8.0f, 8, 8, 64, 64);
            if (state.isDisabled()) {
                matrixStack.push();
                matrixStack.translate(posX < 0 ? -9.0 : 1.0, posY < 0 ? -9.0 : 1.0, 0.0);
                matrixStack.scale(0.5f, 0.5f, 1.0f);
                mc.getTextureManager().bindTexture(SPEAKER_OFF_ICON);
                Screen.blit(matrixStack, 0, 0, 0.0f, 0.0f, 16, 16, 16, 16);
                matrixStack.pop();
            }
            matrixStack.pop();
        }
        matrixStack.pop();
    }

    public static List<PlayerState> getGroupMembers() {
        return GroupChatManager.getGroupMembers(true);
    }

    public static List<PlayerState> getGroupMembers(boolean includeSelf) {
        ArrayList<PlayerState> entries = new ArrayList<PlayerState>();
        UUID group = ClientManager.getPlayerStateManager().getGroupID();
        if (group == null) {
            return entries;
        }
        for (PlayerState state : ClientManager.getPlayerStateManager().getPlayerStates(includeSelf)) {
            if (!state.hasGroup() || !state.getGroup().equals(group)) continue;
            entries.add(state);
        }
        entries.sort(Comparator.comparing(PlayerState::getName));
        return entries;
    }
}
