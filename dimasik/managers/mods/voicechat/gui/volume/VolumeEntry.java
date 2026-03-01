package dimasik.managers.mods.voicechat.gui.volume;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumeSlider;
import dimasik.managers.mods.voicechat.gui.volume.AdjustVolumesScreen;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenEntryBase;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class VolumeEntry
extends ListScreenEntryBase<VolumeEntry> {
    protected static final ITextComponent OTHER_VOLUME = new TranslationTextComponent("message.voicechat.other_volume");
    protected static final ITextComponent OTHER_VOLUME_DESCRIPTION = new TranslationTextComponent("message.voicechat.other_volume.description");
    protected static final ResourceLocation OTHER_VOLUME_ICON = new ResourceLocation("main/textures/images/icons/other_volume.png");
    protected static final int SKIN_SIZE = 24;
    protected static final int PADDING = 4;
    protected static final int BG_FILL = new Color(74, 74, 74).getRGB();
    protected static final int PLAYER_NAME_COLOR = new Color(255, 255, 255).getRGB();
    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final AdjustVolumesScreen screen;
    protected final AdjustVolumeSlider volumeSlider;

    public VolumeEntry(AdjustVolumesScreen screen, AdjustVolumeSlider.VolumeConfigEntry entry) {
        this.screen = screen;
        this.volumeSlider = new AdjustVolumeSlider(0, 0, 100, 20, entry);
        this.children.add(this.volumeSlider);
    }

    @Override
    public void render(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
        int skinX = left + 4;
        int skinY = top + (height - 24) / 2;
        int textX = skinX + 24 + 4;
        int textY = top + (height - this.minecraft.fontRenderer.FONT_HEIGHT) / 2;
        AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL);
        this.renderElement(poseStack, index, top, left, width, height, mouseX, mouseY, hovered, delta, skinX, skinY, textX, textY);
        this.volumeSlider.x = left + (width - this.volumeSlider.getWidth() - 4);
        this.volumeSlider.y = top + (height - this.volumeSlider.getHeight()) / 2;
        this.volumeSlider.render(poseStack, mouseX, mouseY, delta);
    }

    public abstract void renderElement(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10, int var11, int var12, int var13, int var14);
}
