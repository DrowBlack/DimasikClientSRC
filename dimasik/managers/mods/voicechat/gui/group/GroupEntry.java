package dimasik.managers.mods.voicechat.gui.group;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.managers.mods.voicechat.gui.GameProfileUtils;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumeSlider;
import dimasik.managers.mods.voicechat.gui.volume.PlayerVolumeEntry;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenEntryBase;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.awt.Color;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class GroupEntry
extends ListScreenEntryBase<GroupEntry> {
    protected static final ResourceLocation TALK_OUTLINE = new ResourceLocation("main/textures/images/icons/talk_outline.png");
    protected static final ResourceLocation SPEAKER_OFF = new ResourceLocation("main/textures/images/icons/speaker_small_off.png");
    protected static final int PADDING = 4;
    protected static final int BG_FILL = new Color(74, 74, 74).getRGB();
    protected static final int PLAYER_NAME_COLOR = new Color(255, 255, 255).getRGB();
    protected final ListScreenBase parent;
    protected final Minecraft minecraft;
    protected PlayerState state;
    protected final AdjustVolumeSlider volumeSlider;

    public GroupEntry(ListScreenBase parent, PlayerState state) {
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.state = state;
        this.volumeSlider = new AdjustVolumeSlider(0, 0, 100, 20, new PlayerVolumeEntry.PlayerVolumeConfigEntry(state.getUuid()));
        this.children.add(this.volumeSlider);
    }

    @Override
    public void render(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
        ClientVoicechat client;
        AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL);
        poseStack.push();
        int outlineSize = height - 8;
        poseStack.translate(left + 4, top + 4, 0.0);
        float scale = (float)outlineSize / 10.0f;
        poseStack.scale(scale, scale, scale);
        if (!this.state.isDisabled() && (client = ClientManager.getClient()) != null && client.getTalkCache().isTalking(this.state.getUuid())) {
            this.minecraft.getTextureManager().bindTexture(TALK_OUTLINE);
            Screen.blit(poseStack, 0, 0, 0.0f, 0.0f, 10, 10, 16, 16);
        }
        this.minecraft.getTextureManager().bindTexture(GameProfileUtils.getSkin(this.state.getUuid()));
        AbstractGui.blit(poseStack, 1, 1, 8, 8, 8.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        AbstractGui.blit(poseStack, 1, 1, 8, 8, 40.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.disableBlend();
        if (this.state.isDisabled()) {
            poseStack.push();
            poseStack.translate(1.0, 1.0, 0.0);
            poseStack.scale(0.5f, 0.5f, 1.0f);
            this.minecraft.getTextureManager().bindTexture(SPEAKER_OFF);
            Screen.blit(poseStack, 0, 0, 0.0f, 0.0f, 16, 16, 16, 16);
            poseStack.pop();
        }
        poseStack.pop();
        StringTextComponent name = new StringTextComponent(this.state.getName());
        this.minecraft.fontRenderer.drawString(poseStack, name.getText(), left + 4 + outlineSize + 4, top + height / 2 - this.minecraft.fontRenderer.FONT_HEIGHT / 2, PLAYER_NAME_COLOR);
        if (hovered && !ClientManager.getPlayerStateManager().getOwnID().equals(this.state.getUuid())) {
            this.volumeSlider.setWidth(Math.min(width - (4 + outlineSize + 4 + this.minecraft.fontRenderer.getStringWidth(name.getText()) + 4 + 4), 100));
            this.volumeSlider.x = left + (width - this.volumeSlider.getWidth() - 4);
            this.volumeSlider.y = top + (height - this.volumeSlider.getHeight()) / 2;
            this.volumeSlider.render(poseStack, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return List.of();
    }

    @Generated
    public void setState(PlayerState state) {
        this.state = state;
    }

    @Generated
    public PlayerState getState() {
        return this.state;
    }
}
