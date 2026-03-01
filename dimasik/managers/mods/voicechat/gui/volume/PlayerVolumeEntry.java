package dimasik.managers.mods.voicechat.gui.volume;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.gui.GameProfileUtils;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumeSlider;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumesScreen;
import dimasik.managers.mods.voicechat.gui.volume.VolumeEntry;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.Util;

public class PlayerVolumeEntry
extends VolumeEntry {
    @Nullable
    protected final PlayerState state;

    public PlayerVolumeEntry(@Nullable PlayerState state, AdjustVolumesScreen screen) {
        super(screen, new PlayerVolumeConfigEntry(state != null ? state.getUuid() : Util.DUMMY_UUID));
        this.state = state;
    }

    @Nullable
    public PlayerState getState() {
        return this.state;
    }

    @Override
    public void renderElement(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta, int skinX, int skinY, int textX, int textY) {
        if (this.state != null) {
            this.minecraft.getTextureManager().bindTexture(GameProfileUtils.getSkin(this.state.getUuid()));
            AbstractGui.blit(poseStack, skinX, skinY, 24, 24, 8.0f, 8.0f, 8, 8, 64, 64);
            RenderSystem.enableBlend();
            AbstractGui.blit(poseStack, skinX, skinY, 24, 24, 40.0f, 8.0f, 8, 8, 64, 64);
            RenderSystem.disableBlend();
            this.minecraft.fontRenderer.drawString(poseStack, this.state.getName(), textX, textY, PLAYER_NAME_COLOR);
        } else {
            this.minecraft.getTextureManager().bindTexture(OTHER_VOLUME_ICON);
            AbstractGui.blit(poseStack, skinX, skinY, 24, 24, 16.0f, 16.0f, 16, 16, 16, 16);
            this.minecraft.fontRenderer.drawString(poseStack, OTHER_VOLUME.getString(), textX, textY, PLAYER_NAME_COLOR);
            if (hovered) {
                this.screen.postRender(() -> this.screen.renderTooltip(poseStack, OTHER_VOLUME_DESCRIPTION, mouseX, mouseY));
            }
        }
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return List.of();
    }

    public static class PlayerVolumeConfigEntry
    implements AdjustVolumeSlider.VolumeConfigEntry {
        private final UUID playerUUID;

        public PlayerVolumeConfigEntry(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }

        @Override
        public void save(double value) {
            VoicechatClient.VOLUME_CONFIG.setPlayerVolume(this.playerUUID, value);
            VoicechatClient.VOLUME_CONFIG.save();
        }

        @Override
        public double get() {
            return VoicechatClient.VOLUME_CONFIG.getPlayerVolume(this.playerUUID);
        }
    }
}
